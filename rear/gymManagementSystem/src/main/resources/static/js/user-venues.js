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
                            <el-col :span="8" v-for="timeSlot in timeSlots" :key="timeSlot.startTime">
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
                                        {{ timeSlot.price }}元/小时
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
            venues: [],
            loading: false,
            searchForm: {
                venueType: '',
                date: this.getTodayDate() // 默认日期为今天
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
            pagination: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },
            // 场馆列表
            venueList: [],
            // 日期选择器配置
            datePickerOptions: {
                disabledDate(time) {
                    // 禁用过去的日期
                    return time.getTime() < Date.now() - 8.64e7;
                }
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
        this.loadVenues();
    },
    methods: {
        // 获取今天的日期
        getTodayDate() {
            const today = new Date();
            const year = today.getFullYear();
            const month = (today.getMonth() + 1).toString().padStart(2, '0');
            const day = today.getDate().toString().padStart(2, '0');
            return `${year}-${month}-${day}`;
        },
        // 格式化日期显示
        formatDate(dateStr) {
            if (!dateStr) return '';
            
            // 如果是字符串格式的日期，先转换为Date对象
            const date = typeof dateStr === 'string' ? new Date(dateStr) : dateStr;
            
            if (!(date instanceof Date) || isNaN(date)) {
                return '';
            }
            
            const year = date.getFullYear();
            const month = (date.getMonth() + 1).toString().padStart(2, '0');
            const day = date.getDate().toString().padStart(2, '0');
            return `${year}-${month}-${day}`;
        },
        // 格式化日期显示（带年月日）
        formatDateWithChinese(dateStr) {
            if (!dateStr) return '';
            
            // 如果是字符串格式的日期，先转换为Date对象
            const date = typeof dateStr === 'string' ? new Date(dateStr) : dateStr;
            
            if (!(date instanceof Date) || isNaN(date)) {
                return '';
            }
            
            return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
        },
        // 加载场地数据
        async loadVenues() {
            this.loading = true;
            try {
                const params = {
                    page: this.pagination.currentPage,
                    size: this.pagination.pageSize
                };
                
                if (this.searchForm.venueType) {
                    params.type = this.searchForm.venueType;
                }
                
                const response = await axios.get('/api/venues', { params });
                if (response.data.code === 200) {
                    const pageData = response.data.data;
                    this.venueList = pageData.records || [];
                    // 转换场地类型为中文显示
                    this.venueList.forEach(venue => {
                        if (venue.type) {
                            const typeOption = this.venueTypes.find(t => t.value === venue.type);
                            if (typeOption) {
                                venue.type = typeOption.label;
                            }
                        }
                    });
                    this.pagination.total = pageData.total || 0;
                } else {
                    this.$message.error(response.data.message || '加载场地数据失败');
                }
            } catch (error) {
                console.error('加载场地数据失败:', error);
                this.$message.error('加载场地数据失败');
            } finally {
                this.loading = false;
            }
        },
        // 搜索场地
        searchVenues() {
            this.pagination.currentPage = 1;
            this.loadVenues();
        },
        // 重置搜索条件
        resetSearch() {
            this.searchForm = {
                venueType: '',
                date: ''
            };
            this.searchVenues();
        },
        // 分页大小变化处理
        handleSizeChange(val) {
            this.pagination.pageSize = val;
            this.loadVenues();
        },
        // 页码变化处理
        handleCurrentChange(val) {
            this.pagination.currentPage = val;
            this.loadVenues();
        },
        // 显示时间段选择弹窗
        showTimeSlots(venue) {
            if (!this.searchForm.date) {
                this.$message.warning('请先选择日期');
                return;
            }
            
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
                if (!date) {
                    this.$message.warning('请先选择日期');
                    return;
                }
                
                console.log('加载时间段，日期:', date);
                
                const response = await axios.get(`${this.apiBaseUrl}/${this.selectedVenue.id}/time-slots`, {
                    params: { date: date }
                });
                
                console.log('获取到的时间段数据:', response.data);
                
                if (response.data.code === 200 && Array.isArray(response.data.data)) {
                    this.timeSlots = response.data.data.map(slot => {
                        // 确保时间格式正确
                        const startTime = typeof slot.startTime === 'string' ? slot.startTime : 
                                       (slot.startTime.format ? slot.startTime.format('HH:mm') : '00:00');
                        const endTime = typeof slot.endTime === 'string' ? slot.endTime : 
                                     (slot.endTime.format ? slot.endTime.format('HH:mm') : '00:00');
                        
                        return {
                            ...slot,
                            startTime: startTime,
                            endTime: endTime,
                            price: Number(slot.price || this.selectedVenue.pricePerHour).toFixed(2),
                            status: slot.status || 'AVAILABLE'
                        };
                    });
                    console.log('处理后的时间段数据:', this.timeSlots);
                } else {
                    this.timeSlots = [];
                    this.$message.warning(response.data.msg || '没有可用的时间段');
                }
            } catch (error) {
                console.error('加载时间段失败:', error);
                this.$message.error('加载时间段失败: ' + (error.response?.data?.msg || error.message));
                this.timeSlots = [];
            }
        },
        // 选择时间段
        selectTimeSlot(timeSlot) {
            if (timeSlot.status === 'AVAILABLE') {
                console.log('选择的时间段原始数据:', timeSlot);
                this.selectedTimeSlot = {
                    ...timeSlot,
                    startTime: timeSlot.startTime,
                    endTime: timeSlot.endTime,
                    price: Number(timeSlot.price || this.selectedVenue.pricePerHour).toFixed(2)
                };
                console.log('处理后的选中时间段:', this.selectedTimeSlot);
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
                    // 获取当前用户
                    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
                    const userId = currentUser && currentUser.id ? currentUser.id : null;
                    const cardNumber = currentUser && currentUser.cardNumber ? currentUser.cardNumber : '12345678';

                    if (!userId) {
                        this.$message.error('请先登录');
                        return;
                    }

                    // 计算时间差（小时）
                    const startTime = this.selectedTimeSlot.startTime;
                    const endTime = this.selectedTimeSlot.endTime;
                    const [startHour, startMinute] = startTime.split(':').map(Number);
                    const [endHour, endMinute] = endTime.split(':').map(Number);
                    const duration = (endHour - startHour) + (endMinute - startMinute) / 60;

                    // 构建预约请求数据
                    const bookingData = {
                        venueId: this.selectedVenue.id,
                        userId: userId,
                        cardNumber: cardNumber,
                        startTime: `${this.searchForm.date}T${startTime}:00`,
                        endTime: `${this.searchForm.date}T${endTime}:00`,
                        numberOfPeople: 1,
                        remarks: this.bookingForm.remarks || '',
                        status: 'PENDING',
                        cost: Number((this.selectedVenue.pricePerHour * duration).toFixed(2)),
                        duration: duration
                    };

                    console.log('预约数据:', bookingData);

                    // 发送预约请求
                    axios.post(`${this.apiBaseUrl}/${this.selectedVenue.id}/reserve`, bookingData)
                        .then(response => {
                            if (response.data.code === 200) {
                                this.$message.success('预约成功');
                                this.timeSlotDialogVisible = false;
                                this.loadVenues(); // 重新加载场馆列表
                            } else {
                                this.$message.error(response.data.msg || '预约失败');
                            }
                        })
                        .catch(error => {
                            console.error('预约失败:', error);
                            this.$message.error('预约失败，请稍后重试');
                        });
                }
            });
        },
        // 显示场馆详情
        showVenueDetails(venue) {
            this.selectedVenue = venue;
            this.venueDetailDialogVisible = true;
        },
    }
};