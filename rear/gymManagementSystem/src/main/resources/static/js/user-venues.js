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
                    <el-table-column prop="location" label="场地位置"></el-table-column>
                    <el-table-column prop="capacity" label="容纳人数" width="120"></el-table-column>
                    <el-table-column prop="pricePerHour" label="每小时价格" width="120">
                        <template slot-scope="scope">
                            {{ scope.row.pricePerHour }} 元
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="180">
                        <template slot-scope="scope">
                            <el-button type="primary" size="mini" @click="showTimeSlots(scope.row)">预约</el-button>
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
                            <el-col :span="6" v-for="timeSlot in timeSlots" :key="timeSlot.id">
                                <div 
                                    :class="['time-slot', {
                                        'available': timeSlot.status === 'AVAILABLE',
                                        'booked': timeSlot.status === 'BOOKED',
                                        'special': timeSlot.status === 'SPECIAL',
                                        'selected': selectedTimeSlot && selectedTimeSlot.id === timeSlot.id
                                    }]"
                                    @click="selectTimeSlot(timeSlot)">
                                    {{ timeSlot.startTime }} - {{ timeSlot.endTime }}
                                </div>
                            </el-col>
                        </el-row>
                    </div>
                    
                    <el-divider></el-divider>
                    
                    <el-form :model="bookingForm" :rules="bookingRules" ref="bookingForm" label-width="100px" size="small">
                        <el-form-item label="预约人数" prop="numberOfPeople">
                            <el-input-number v-model="bookingForm.numberOfPeople" :min="1" :max="selectedVenue.capacity"></el-input-number>
                        </el-form-item>
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
                        <p><strong>位置:</strong> {{ selectedVenue.location }}</p>
                        <p><strong>容纳人数:</strong> {{ selectedVenue.capacity }} 人</p>
                        <p><strong>每小时费用:</strong> {{ selectedVenue.pricePerHour }} 元</p>
                        <p><strong>设施:</strong> {{ selectedVenue.facilities || '无特殊设施' }}</p>
                        <p><strong>描述:</strong> {{ selectedVenue.description || '暂无描述' }}</p>
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
                numberOfPeople: 1,
                remarks: ''
            },
            // 表单验证规则
            bookingRules: {
                numberOfPeople: [
                    { required: true, message: '请输入预约人数', trigger: 'blur' }
                ]
            },
            // 场馆详情弹窗
            venueDetailDialogVisible: false
        };
    },
    created() {
        // 初始加载场馆数据
        this.loadVenueData();
    },
    methods: {
        // 加载场馆数据
        loadVenueData() {
            this.loading = true;

            // 模拟后端API请求
            setTimeout(() => {
                // 模拟数据，实际项目中应通过API获取
                this.venueList = [
                    { id: 1, name: '篮球场A', type: '篮球场', location: '体育馆一楼', capacity: 20, pricePerHour: 80, facilities: '照明设备、更衣室', description: '标准篮球场，适合5v5比赛' },
                    { id: 2, name: '篮球场B', type: '篮球场', location: '体育馆一楼', capacity: 20, pricePerHour: 80, facilities: '照明设备、更衣室', description: '标准篮球场，适合5v5比赛' },
                    { id: 3, name: '足球场A', type: '足球场', location: '户外运动区', capacity: 30, pricePerHour: 200, facilities: '照明设备、更衣室、淋浴', description: '7人制足球场，人造草皮' },
                    { id: 4, name: '羽毛球场A', type: '羽毛球场', location: '体育馆二楼', capacity: 4, pricePerHour: 40, facilities: '空调、照明', description: '标准羽毛球场地' },
                    { id: 5, name: '羽毛球场B', type: '羽毛球场', location: '体育馆二楼', capacity: 4, pricePerHour: 40, facilities: '空调、照明', description: '标准羽毛球场地' },
                    { id: 6, name: '羽毛球场C', type: '羽毛球场', location: '体育馆二楼', capacity: 4, pricePerHour: 40, facilities: '空调、照明', description: '标准羽毛球场地' },
                    { id: 7, name: '网球场A', type: '网球场', location: '户外运动区', capacity: 4, pricePerHour: 60, facilities: '照明设备', description: '硬地网球场' },
                    { id: 8, name: '游泳池', type: '游泳池', location: '体育馆负一楼', capacity: 50, pricePerHour: 30, facilities: '更衣室、淋浴、储物柜', description: '25米标准泳道，6条泳道' },
                    { id: 9, name: '乒乓球室A', type: '乒乓球室', location: '体育馆三楼', capacity: 4, pricePerHour: 20, facilities: '空调、照明', description: '配备2张标准乒乓球桌' },
                    { id: 10, name: '乒乓球室B', type: '乒乓球室', location: '体育馆三楼', capacity: 4, pricePerHour: 20, facilities: '空调、照明', description: '配备2张标准乒乓球桌' }
                ];
                this.pagination.total = this.venueList.length;
                this.loading = false;

                // 根据筛选条件过滤
                this.filterVenues();
            }, 500);
        },
        // 搜索场馆
        searchVenues() {
            this.pagination.currentPage = 1;
            this.loadVenueData();
        },
        // 重置搜索条件
        resetSearch() {
            this.searchForm = {
                venueType: '',
                date: new Date().toISOString().split('T')[0]
            };
            this.searchVenues();
        },
        // 根据条件过滤场馆
        filterVenues() {
            if (this.searchForm.venueType) {
                // 将中文类型转换为对应的英文value值
                const typeMap = {
                    '篮球场': 'basketball',
                    '足球场': 'football',
                    '羽毛球场': 'badminton',
                    '网球场': 'tennis',
                    '游泳池': 'swimming',
                    '乒乓球室': 'table_tennis'
                };

                // 反向映射，从value获取中文类型
                const reverseTypeMap = {};
                Object.keys(typeMap).forEach(key => {
                    reverseTypeMap[typeMap[key]] = key;
                });

                this.venueList = this.venueList.filter(venue => {
                    return venue.type === reverseTypeMap[this.searchForm.venueType];
                });
            }

            this.pagination.total = this.venueList.length;
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
                numberOfPeople: 1,
                remarks: ''
            };

            // 模拟加载时间段数据
            this.loadTimeSlots();

            this.timeSlotDialogVisible = true;
        },
        // 加载时间段数据
        loadTimeSlots() {
            // 模拟数据，实际项目中应通过API获取
            const slots = [];
            const startHour = 8; // 早上8点开始
            const endHour = 22;  // 晚上10点结束

            for (let hour = startHour; hour < endHour; hour++) {
                // 创建整点和半点的时间段
                for (let minute of [0, 30]) {
                    const startTime = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
                    const endHourMin = minute === 0 ? `${hour}:30` : `${hour + 1}:00`;
                    const endTime = endHourMin.split(':').map(num => num.toString().padStart(2, '0')).join(':');

                    // 随机状态，实际应从后端获取
                    const statuses = ['AVAILABLE', 'BOOKED', 'SPECIAL'];
                    const randomStatus = Math.random() < 0.7 ? 'AVAILABLE' : statuses[Math.floor(Math.random() * 3)];

                    slots.push({
                        id: `${hour}-${minute}`,
                        startTime,
                        endTime,
                        status: randomStatus
                    });
                }
            }

            this.timeSlots = slots;
        },
        // 选择时间段
        selectTimeSlot(timeSlot) {
            if (timeSlot.status !== 'AVAILABLE') {
                this.$message.warning('该时间段不可预约');
                return;
            }

            this.selectedTimeSlot = timeSlot;
        },
        // 计算预估费用
        calculateEstimatedCost() {
            if (!this.selectedTimeSlot || !this.selectedVenue) return 0;

            // 解析时间计算小时数
            const startTime = this.selectedTimeSlot.startTime.split(':');
            const endTime = this.selectedTimeSlot.endTime.split(':');

            const startMinutes = parseInt(startTime[0]) * 60 + parseInt(startTime[1]);
            const endMinutes = parseInt(endTime[0]) * 60 + parseInt(endTime[1]);

            // 计算小时数（取整到0.5小时）
            const hours = (endMinutes - startMinutes) / 60;

            // 计算费用
            return (this.selectedVenue.pricePerHour * hours).toFixed(2);
        },
        // 确认预约
        confirmBooking() {
            if (!this.selectedTimeSlot) {
                this.$message.warning('请选择预约时间段');
                return;
            }

            this.$refs.bookingForm.validate(valid => {
                if (valid) {
                    // 组装预约数据
                    const bookingData = {
                        venueId: this.selectedVenue.id,
                        date: this.searchForm.date,
                        startTime: this.selectedTimeSlot.startTime,
                        endTime: this.selectedTimeSlot.endTime,
                        numberOfPeople: this.bookingForm.numberOfPeople,
                        remarks: this.bookingForm.remarks,
                        estimatedCost: this.calculateEstimatedCost()
                    };

                    // 模拟提交预约请求
                    this.loading = true;
                    setTimeout(() => {
                        this.loading = false;
                        this.timeSlotDialogVisible = false;

                        // 提示预约成功
                        this.$message.success('预约成功！');

                        // 可以跳转到我的预约页面
                        // this.$router.push('/user/myreservations');
                    }, 1000);

                    console.log('提交预约数据:', bookingData);
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