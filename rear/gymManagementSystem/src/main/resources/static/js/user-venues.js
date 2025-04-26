// 场地预约组件
const UserVenuesComponent = {
    template: `
        <div class="venue-booking-container">
            <el-card>
                <div slot="header" class="card-header">
                    <h2>场地预约</h2>
                </div>
                
                <!-- 搜索筛选区域 -->
                <el-form :inline="true" class="search-form" size="small">
                    <el-form-item label="场地类型">
                        <el-select v-model="searchForm.venueType" placeholder="选择场地类型" clearable @change="searchVenues">
                            <el-option v-for="item in venueTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="日期">
                        <el-date-picker
                            v-model="searchForm.date"
                            type="date"
                            placeholder="选择日期"
                            value-format="yyyy-MM-dd"
                            :picker-options="datePickerOptions"
                            @change="searchVenues">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="searchVenues">查询场地</el-button>
                        <el-button @click="resetSearch">重置</el-button>
                    </el-form-item>
                </el-form>
                
                <!-- 场地列表 -->
                <el-table 
                    v-loading="loading" 
                    :data="venueList" 
                    style="width: 100%"
                    :header-cell-style="{background:'#f5f7fa', color:'#606266'}"
                    border>
                    <el-table-column prop="name" label="场地名称" width="150"></el-table-column>
                    <el-table-column prop="type" label="场地类型" width="120"></el-table-column>
                    <el-table-column prop="description" label="场地描述"></el-table-column>
                    <el-table-column prop="pricePerHour" label="价格" width="120">
                        <template slot-scope="scope">
                            {{ scope.row.pricePerHour }} 元/小时
                        </template>
                    </el-table-column>
                   
                    <el-table-column prop="status" label="状态" width="120">
                        <template slot-scope="scope">
                            <el-tag :type="scope.row.isAvailable ? 'success' : 'danger'">
                                {{ scope.row.isAvailable ? '可用' : '不可用' }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="180">
                        <template slot-scope="scope">
                            <el-button type="primary" size="mini" @click="showTimeSlots(scope.row)" :disabled="!scope.row.isAvailable">预约</el-button>
                            <el-button type="info" size="mini" @click="showVenueDetails(scope.row)">详情</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                
                <!-- 分页组件 -->
                <div class="pagination-container">
                    <el-pagination
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                        :current-page="pagination.currentPage"
                        :page-sizes="[5, 10, 20, 50]"
                        :page-size="pagination.pageSize"
                        layout="total, sizes, prev, pager, next, jumper"
                        :total="pagination.total">
                    </el-pagination>
                </div>
            </el-card>
            
            <!-- 时间段选择弹窗 -->
            <el-dialog title="选择预约时间段" :visible.sync="timeSlotDialogVisible" width="650px">
                <div v-if="selectedVenue">
                    <h4>场地: {{ selectedVenue.name }}</h4>
                    <p>日期: {{ formatDate(searchForm.date) }}</p>
                    
                    <el-divider content-position="left">可用时间段</el-divider>
                    
                    <div class="time-slots-container">
                        <el-row :gutter="10">
                            <el-col :span="6" v-for="timeSlot in timeSlots" :key="timeSlot.startTime">
                                <div 
                                    :class="['time-slot', {
                                        'available': timeSlot.status === 'AVAILABLE',
                                        'booked': timeSlot.status === 'BOOKED',
                                        'special': timeSlot.status === 'SPECIAL',
                                        'selected': selectedTimeSlot && selectedTimeSlot.startTime === timeSlot.startTime
                                    }]"
                                    @click="timeSlot.status === 'AVAILABLE' && selectTimeSlot(timeSlot)">
                                    {{ timeSlot.startTime }} - {{ timeSlot.endTime }}
                                    <div class="time-slot-price" v-if="timeSlot.status === 'AVAILABLE'">
                                        {{ timeSlot.price }}元
                                    </div>
                                </div>
                            </el-col>
                        </el-row>
                    </div>
                    
                    <el-divider></el-divider>
                    
                    <el-form :model="bookingForm" :rules="bookingRules" ref="bookingForm" label-width="100px" size="small">
                        <el-form-item label="备注信息" prop="remarks">
                            <el-input type="textarea" v-model="bookingForm.remarks" placeholder="请输入预约备注信息"></el-input>
                        </el-form-item>
                    </el-form>
                    
                    <div class="booking-summary" v-if="selectedTimeSlot">
                        <p>预约时间: {{ selectedTimeSlot.startTime }} - {{ selectedTimeSlot.endTime }}</p>
                        <p>预计费用: {{ calculateEstimatedCost() }} 元</p>
                    </div>
                </div>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="timeSlotDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="confirmBooking" :disabled="!selectedTimeSlot">确认预约</el-button>
                </span>
            </el-dialog>
            
            <!-- 场地详情弹窗 -->
            <el-dialog title="场地详情" :visible.sync="venueDetailDialogVisible" width="500px">
                <div v-if="selectedVenue" class="venue-detail">
                    <div class="venue-image">
                        <img :src="selectedVenue.imageUrl || '/img/default-venue.jpg'" alt="场地图片">
                    </div>
                    <div class="venue-info">
                        <h3>{{ selectedVenue.name }}</h3>
                        <p><strong>类型:</strong> {{ selectedVenue.type }}</p>
                        <p><strong>描述:</strong> {{ selectedVenue.description || '暂无描述' }}</p>
                        <p><strong>价格:</strong> {{ selectedVenue.pricePerHour }} 元/小时</p>
                        <p><strong>状态:</strong> 
                            <el-tag :type="selectedVenue.isAvailable ? 'success' : 'danger'">
                                {{ selectedVenue.isAvailable ? '可用' : '不可用' }}
                            </el-tag>
                        </p>
                    </div>
                </div>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            // 搜索表单
            searchForm: {
                venueType: '',
                date: new Date().toISOString().split('T')[0] // 默认今天
            },
            // 日期选择器配置
            datePickerOptions: {
                disabledDate(time) {
                    // 禁用过去的日期
                    return time.getTime() < Date.now() - 8.64e7;
                }
            },
            // 场馆类型选项
            venueTypes: [
                { value: 'basketball', label: '篮球场' },
                { value: 'football', label: '足球场' },
                { value: 'badminton', label: '羽毛球场' },
                { value: 'tennis', label: '网球场' },
                { value: 'swimming', label: '游泳池' },
                { value: 'table_tennis', label: '乒乓球室' }
            ],
            // 场馆列表
            venueList: [],
            // 加载状态
            loading: false,
            // 分页信息
            pagination: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },
            // 时间段选择弹窗
            timeSlotDialogVisible: false,
            // 选中的场馆
            selectedVenue: null,
            // 可用时间段
            timeSlots: [],
            // 选中的时间段
            selectedTimeSlot: null,
            // 预约表单
            bookingForm: {
                remarks: ''
            },
            // 表单验证规则
            bookingRules: {
                remarks: [
                    { required: false, message: '请输入预约备注信息', trigger: 'blur' }
                ]
            },
            // 场馆详情弹窗
            venueDetailDialogVisible: false,
            apiBaseUrl: '/api/venues', // 添加API基础URL
        };
    },
    created() {
        // 初始加载场馆数据
        this.loadVenueData();
    },
    methods: {
        // 加载场馆数据
        async loadVenueData() {
            this.loading = true;
            try {
                const response = await axios.get(`${this.apiBaseUrl}`, {
                    params: {
                        page: this.pagination.currentPage,
                        size: this.pagination.pageSize
                    }
                });
                this.venueList = response.data.records;
                this.pagination.total = response.data.total;
            } catch (error) {
                this.$message.error('加载场地数据失败');
                console.error('加载场地数据失败:', error);
            } finally {
                this.loading = false;
            }
        },
        // 搜索场馆
        async searchVenues() {
            this.loading = true;
            try {
                const params = {
                    page: this.pagination.currentPage,
                    size: this.pagination.pageSize
                };
                
                if (this.searchForm.venueType) {
                    params.type = this.searchForm.venueType;
                }
                
                const response = await axios.get(`${this.apiBaseUrl}`, { params });
                this.venueList = response.data.records;
                this.pagination.total = response.data.total;
            } catch (error) {
                this.$message.error('搜索场地失败');
                console.error('搜索场地失败:', error);
            } finally {
                this.loading = false;
            }
        },
        // 重置搜索条件
        resetSearch() {
            this.searchForm = {
                venueType: '',
                date: new Date().toISOString().split('T')[0]
            };
            this.searchVenues();
        },
        // 分页大小变化处理
        handleSizeChange(val) {
            this.pagination.pageSize = val;
            this.loadVenueData();
        },
        // 页码变化处理
        handleCurrentChange(val) {
            this.pagination.currentPage = val;
            this.loadVenueData();
        },
        // 显示时间段选择弹窗
        showTimeSlots(venue) {
            this.selectedVenue = venue;
            this.selectedTimeSlot = null;
            this.bookingForm = {
                remarks: ''
            };

            // 加载时间段数据
            this.loadTimeSlots();

            this.timeSlotDialogVisible = true;
        },
        // 加载时间段数据
        async loadTimeSlots() {
            try {
                const date = this.searchForm.date;
                const response = await axios.get(`${this.apiBaseUrl}/${this.selectedVenue.id}/time-slots`, {
                    params: { date: date }
                });
                
                console.log('获取到的时间段数据:', response.data);
                
                if (response.data && Array.isArray(response.data)) {
                    this.timeSlots = response.data.map(slot => ({
                        ...slot,
                        startTime: slot.startTime.format ? slot.startTime.format('HH:mm') : slot.startTime,
                        endTime: slot.endTime.format ? slot.endTime.format('HH:mm') : slot.endTime,
                        price: Number(slot.price).toFixed(2)
                    }));
                    console.log('处理后的时间段数据:', this.timeSlots);
                } else {
                    this.timeSlots = [];
                    this.$message.warning('没有可用的时间段');
                }
            } catch (error) {
                console.error('加载时间段失败:', error);
                this.$message.error('加载时间段失败: ' + error.message);
                this.timeSlots = [];
            }
        },
        // 选择时间段
        selectTimeSlot(timeSlot) {
            if (timeSlot.status === 'AVAILABLE') {
                this.selectedTimeSlot = timeSlot;
                this.bookingForm.startTime = timeSlot.startTime;
                this.bookingForm.endTime = timeSlot.endTime;
            }
        },
        // 计算预估费用
        calculateEstimatedCost() {
            if (this.selectedTimeSlot) {
                return this.selectedTimeSlot.price;
            }
            return 0;
        },
        // 确认预约
        confirmBooking() {
            if (!this.selectedTimeSlot) {
                this.$message.warning('请选择预约时间段');
                return;
            }

            this.$refs.bookingForm.validate(valid => {
                if (valid) {
                    // 构建日期时间字符串
                    const startDateTime = `${this.searchForm.date}T${this.selectedTimeSlot.startTime}:00`;
                    const endDateTime = `${this.searchForm.date}T${this.selectedTimeSlot.endTime}:00`;
                    
                    // 获取当前用户的一卡通号码
                    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
                    const cardNumber = currentUser.cardNumber || '12345678'; // 默认卡号
                    
                    // 调用后端预约API
                    this.loading = true;
                    axios.post('/api/reservations', null, {
                        params: {
                            venueId: this.selectedVenue.id,
                            cardNumber: cardNumber,
                            startTime: startDateTime,
                            endTime: endDateTime,
                            remarks: this.bookingForm.remarks
                        }
                    })
                    .then(response => {
                        this.timeSlotDialogVisible = false;
                        this.$message.success('预约成功！');
                        // 可以跳转到我的预约页面
                        // this.$router.push('/user/myreservations');
                    })
                    .catch(error => {
                        console.error('预约失败:', error);
                        let errorMsg = '预约失败，请稍后重试';
                        if (error.response && error.response.data && error.response.data.message) {
                            errorMsg = error.response.data.message;
                        }
                        this.$message.error(errorMsg);
                    })
                    .finally(() => {
                        this.loading = false;
                    });
                }
            });
        },
        // 显示场馆详情
        showVenueDetails(venue) {
            this.selectedVenue = venue;
            this.venueDetailDialogVisible = true;
        },
        // 格式化日期
        formatDate(dateStr) {
            if (!dateStr) return '';

            const date = new Date(dateStr);
            return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
        }
    }
};