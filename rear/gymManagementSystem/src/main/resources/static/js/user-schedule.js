// 场地安排组件
const UserScheduleComponent = {
    template: `
        <div class="venue-schedule-container">
            <el-card>
                <div slot="header" class="card-header">
                    <h2>场地安排</h2>
                </div>
                
                <!-- 搜索筛选区域 -->
                <el-form :inline="true" class="search-form" size="small">
                    <el-form-item label="场地类型">
                        <el-select v-model="searchForm.venueType" placeholder="选择场地类型" clearable @change="searchSchedule">
                            <el-option v-for="item in venueTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="场地名称">
                        <el-select v-model="searchForm.venueId" placeholder="选择场地" clearable @change="searchSchedule">
                            <el-option v-for="venue in filteredVenues" :key="venue.id" :label="venue.name" :value="venue.id"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="searchSchedule">查询安排</el-button>
                        <el-button @click="resetSearch">重置</el-button>
                    </el-form-item>
                </el-form>
                
                <!-- 日期导航 -->
                <div class="date-navigation">
                    <el-button icon="el-icon-arrow-left" @click="changeWeek(-1)"></el-button>
                    <span class="date-range">{{ formatDateRange(currentWeekStart, 6) }}</span>
                    <el-button icon="el-icon-arrow-right" @click="changeWeek(1)"></el-button>
                    <el-button type="text" @click="resetToCurrentWeek">回到本周</el-button>
                </div>
                
                <!-- 日历表格 -->
                <div class="schedule-calendar" v-loading="loading">
                    <table class="schedule-table">
                        <thead>
                            <tr>
                                <th class="time-column">时间段</th>
                                <th v-for="(day, index) in weekDays" :key="index">
                                    {{ day.dayOfWeek }}<br>
                                    <small>{{ day.date }}</small>
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="timeSlot in timeSlots" :key="timeSlot.id">
                                <td class="time-column">{{ timeSlot.timeRange }}</td>
                                <td v-for="(day, dayIndex) in weekDays" :key="dayIndex" 
                                    :class="getCellClass(getScheduleForTimeAndDay(timeSlot, day))">
                                    <div v-if="getScheduleForTimeAndDay(timeSlot, day)" 
                                         class="schedule-cell-content"
                                         @click="showScheduleDetail(getScheduleForTimeAndDay(timeSlot, day))">
                                        {{ getScheduleForTimeAndDay(timeSlot, day).venueName }}
                                        <div class="schedule-status">{{ getStatusText(getScheduleForTimeAndDay(timeSlot, day).status) }}</div>
                                    </div>
                                    <div v-else class="schedule-cell-empty"></div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                
                <!-- 图例说明 -->
                <div class="schedule-legend">
                    <div class="legend-item">
                        <div class="legend-color available"></div>
                        <div class="legend-text">可预约</div>
                    </div>
                    <div class="legend-item">
                        <div class="legend-color booked"></div>
                        <div class="legend-text">已预约</div>
                    </div>
                    <div class="legend-item">
                        <div class="legend-color in-use"></div>
                        <div class="legend-text">使用中</div>
                    </div>
                    <div class="legend-item">
                        <div class="legend-color special"></div>
                        <div class="legend-text">特殊场地</div>
                    </div>
                    <div class="legend-item">
                        <div class="legend-color maintenance"></div>
                        <div class="legend-text">维护中</div>
                    </div>
                </div>
            </el-card>
            
            <!-- 场地详情弹窗 -->
            <el-dialog title="场地安排详情" :visible.sync="scheduleDetailVisible" width="500px">
                <div v-if="selectedSchedule" class="schedule-detail">
                    <h3>{{ selectedSchedule.venueName }}</h3>
                    <el-descriptions :column="1" border>
                        <el-descriptions-item label="场地类型">{{ selectedSchedule.venueType }}</el-descriptions-item>
                        <el-descriptions-item label="日期">{{ selectedSchedule.date }}</el-descriptions-item>
                        <el-descriptions-item label="时间段">{{ selectedSchedule.timeRange }}</el-descriptions-item>
                        <el-descriptions-item label="状态">
                            <el-tag :type="getStatusType(selectedSchedule.status)">
                                {{ getStatusText(selectedSchedule.status) }}
                            </el-tag>
                        </el-descriptions-item>
                        <el-descriptions-item v-if="selectedSchedule.bookedBy" label="预约人">
                            {{ selectedSchedule.bookedBy }}
                        </el-descriptions-item>
                        <el-descriptions-item v-if="selectedSchedule.numberOfPeople" label="使用人数">
                            {{ selectedSchedule.numberOfPeople }} 人
                        </el-descriptions-item>
                        <el-descriptions-item v-if="selectedSchedule.remarks" label="备注">
                            {{ selectedSchedule.remarks }}
                        </el-descriptions-item>
                    </el-descriptions>
                    
                    <div class="schedule-actions" v-if="selectedSchedule.status === 'AVAILABLE'">
                        <el-button type="primary" @click="goToBooking">去预约</el-button>
                    </div>
                </div>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="scheduleDetailVisible = false">关闭</el-button>
                </span>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            // 搜索表单
            searchForm: {
                venueType: '',
                venueId: ''
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
            venues: [],
            // 当前周的开始日期
            currentWeekStart: new Date(),
            // 时间段列表
            timeSlots: [],
            // 场地安排数据
            scheduleData: [],
            // 加载状态
            loading: false,
            // 选中的安排详情
            selectedSchedule: null,
            // 详情弹窗可见性
            scheduleDetailVisible: false
        };
    },
    computed: {
        // 根据类型筛选的场馆列表
        filteredVenues() {
            if (!this.searchForm.venueType) {
                return this.venues;
            }

            // 将英文类型值转换为中文类型名
            const typeMap = {
                'basketball': '篮球场',
                'football': '足球场',
                'badminton': '羽毛球场',
                'tennis': '网球场',
                'swimming': '游泳池',
                'table_tennis': '乒乓球室'
            };

            const selectedType = typeMap[this.searchForm.venueType];
            return this.venues.filter(venue => venue.type === selectedType);
        },
        // 本周的日期列表
        weekDays() {
            const days = [];
            const weekStart = new Date(this.currentWeekStart);
            const dayNames = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];

            for (let i = 0; i < 7; i++) {
                const date = new Date(weekStart);
                date.setDate(weekStart.getDate() + i);

                days.push({
                    date: this.formatDate(date),
                    dayOfWeek: dayNames[date.getDay()],
                    fullDate: date
                });
            }

            return days;
        }
    },
    created() {
        // 设置当前周的起始日期为本周日
        this.setWeekStartToSunday();

        // 加载场馆数据
        this.loadVenueData();

        // 初始化时间段
        this.initTimeSlots();

        // 加载本周场地安排
        this.loadScheduleData();
    },
    methods: {
        // 设置周起始日为周日
        setWeekStartToSunday() {
            const today = new Date();
            const day = today.getDay(); // 0是周日，1是周一，以此类推

            // 如果不是周日，倒回到本周日
            if (day !== 0) {
                today.setDate(today.getDate() - day);
            }

            today.setHours(0, 0, 0, 0);
            this.currentWeekStart = today;
        },
        // 加载场馆数据
        loadVenueData() {
            this.loading = true;

            // 模拟API请求
            setTimeout(() => {
                // 模拟数据
                this.venues = [
                    { id: 1, name: '篮球场A', type: '篮球场', location: '体育馆一楼' },
                    { id: 2, name: '篮球场B', type: '篮球场', location: '体育馆一楼' },
                    { id: 3, name: '足球场A', type: '足球场', location: '户外运动区' },
                    { id: 4, name: '羽毛球场A', type: '羽毛球场', location: '体育馆二楼' },
                    { id: 5, name: '羽毛球场B', type: '羽毛球场', location: '体育馆二楼' },
                    { id: 6, name: '羽毛球场C', type: '羽毛球场', location: '体育馆二楼' },
                    { id: 7, name: '网球场A', type: '网球场', location: '户外运动区' },
                    { id: 8, name: '游泳池', type: '游泳池', location: '体育馆负一楼' },
                    { id: 9, name: '乒乓球室A', type: '乒乓球室', location: '体育馆三楼' },
                    { id: 10, name: '乒乓球室B', type: '乒乓球室', location: '体育馆三楼' }
                ];

                this.loading = false;
            }, 300);
        },
        // 初始化时间段
        initTimeSlots() {
            const slots = [];
            const startHour = 8; // 早上8点开始
            const endHour = 22;  // 晚上10点结束

            for (let hour = startHour; hour < endHour; hour++) {
                // 创建整点和半点的时间段
                for (let minute of [0, 30]) {
                    const startTime = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
                    const endHourMin = minute === 0 ? `${hour}:30` : `${hour + 1}:00`;
                    const endTime = endHourMin.split(':').map(num => num.toString().padStart(2, '0')).join(':');

                    slots.push({
                        id: `${hour}-${minute}`,
                        timeRange: `${startTime} - ${endTime}`
                    });
                }
            }

            this.timeSlots = slots;
        },
        // 加载场地安排数据
        loadScheduleData() {
            this.loading = true;

            // 模拟API请求
            setTimeout(() => {
                // 生成本周的日期范围
                const dateRange = this.weekDays.map(day => day.date);

                // 模拟数据
                this.scheduleData = [];

                // 为每个场地生成一些随机安排
                this.venues.forEach(venue => {
                    dateRange.forEach(date => {
                        this.timeSlots.forEach(timeSlot => {
                            // 随机生成不同状态的安排，实际应该从后端获取
                            if (Math.random() > 0.7) { // 30%的概率有安排
                                const statuses = ['AVAILABLE', 'BOOKED', 'IN_USE', 'SPECIAL', 'MAINTENANCE'];
                                const randomStatus = statuses[Math.floor(Math.random() * statuses.length)];

                                const schedule = {
                                    id: `${venue.id}-${date}-${timeSlot.id}`,
                                    venueId: venue.id,
                                    venueName: venue.name,
                                    venueType: venue.type,
                                    date: date,
                                    timeRange: timeSlot.timeRange,
                                    status: randomStatus
                                };

                                // 如果是已预约状态，添加预约人信息
                                if (randomStatus === 'BOOKED' || randomStatus === 'IN_USE') {
                                    schedule.bookedBy = '测试用户';
                                    schedule.numberOfPeople = Math.floor(Math.random() * 10) + 1;
                                    schedule.remarks = Math.random() > 0.5 ? '临时预约' : '';
                                }

                                this.scheduleData.push(schedule);
                            }
                        });
                    });
                });

                // 根据筛选条件过滤
                this.filterSchedule();

                this.loading = false;
            }, 500);
        },        // 搜索场地安排
        searchSchedule() {
            this.loadScheduleData();
        },
        // 重置搜索条件
        resetSearch() {
            this.searchForm = {
                venueType: '',
                venueId: ''
            };
            this.searchSchedule();
        },
        // 根据条件过滤场地安排
        filterSchedule() {
            if (this.searchForm.venueType || this.searchForm.venueId) {
                let filteredData = [...this.scheduleData];

                if (this.searchForm.venueId) {
                    filteredData = filteredData.filter(item => item.venueId === this.searchForm.venueId);
                } else if (this.searchForm.venueType) {
                    // 将英文类型值转换为中文类型名
                    const typeMap = {
                        'basketball': '篮球场',
                        'football': '足球场',
                        'badminton': '羽毛球场',
                        'tennis': '网球场',
                        'swimming': '游泳池',
                        'table_tennis': '乒乓球室'
                    };

                    const selectedType = typeMap[this.searchForm.venueType];
                    filteredData = filteredData.filter(item => item.venueType === selectedType);
                }

                this.scheduleData = filteredData;
            }
        },
        // 改变周次
        changeWeek(offset) {
            const newDate = new Date(this.currentWeekStart);
            newDate.setDate(newDate.getDate() + (offset * 7));
            this.currentWeekStart = newDate;

            // 重新加载日程数据
            this.loadScheduleData();
        },
        // 重置到当前周
        resetToCurrentWeek() {
            this.setWeekStartToSunday();
            this.loadScheduleData();
        },
        // 根据时间段和日期获取安排数据
        getScheduleForTimeAndDay(timeSlot, day) {
            return this.scheduleData.find(schedule =>
                schedule.timeRange === timeSlot.timeRange &&
                schedule.date === day.date
            );
        },
        // 获取单元格的CSS类
        getCellClass(schedule) {
            if (!schedule) return 'schedule-cell-empty';

            const statusClassMap = {
                'AVAILABLE': 'schedule-cell-available',
                'BOOKED': 'schedule-cell-booked',
                'IN_USE': 'schedule-cell-in-use',
                'SPECIAL': 'schedule-cell-special',
                'MAINTENANCE': 'schedule-cell-maintenance'
            };

            return `schedule-cell ${statusClassMap[schedule.status] || ''}`;
        },
        // 显示安排详情
        showScheduleDetail(schedule) {
            this.selectedSchedule = schedule;
            this.scheduleDetailVisible = true;
        },
        // 转到预约页面
        goToBooking() {
            if (this.selectedSchedule) {
                this.$router.push({
                    path: '/user/venues',
                    query: {
                        venueId: this.selectedSchedule.venueId,
                        date: this.selectedSchedule.date
                    }
                });
                this.scheduleDetailVisible = false;
            }
        },
        // 格式化日期
        formatDate(date) {
            const year = date.getFullYear();
            const month = (date.getMonth() + 1).toString().padStart(2, '0');
            const day = date.getDate().toString().padStart(2, '0');
            return `${year}-${month}-${day}`;
        },
        // 格式化日期范围
        formatDateRange(startDate, days) {
            const start = new Date(startDate);
            const end = new Date(startDate);
            end.setDate(start.getDate() + days);

            return `${this.formatDate(start)} 至 ${this.formatDate(end)}`;
        },
        // 获取状态文本
        getStatusText(status) {
            const texts = {
                'AVAILABLE': '可预约',
                'BOOKED': '已预约',
                'IN_USE': '使用中',
                'SPECIAL': '特殊场地',
                'MAINTENANCE': '维护中'
            };
            return texts[status] || '未知';
        },
        // 获取状态对应的Tag类型
        getStatusType(status) {
            const types = {
                'AVAILABLE': 'success',
                'BOOKED': 'warning',
                'IN_USE': 'primary',
                'SPECIAL': 'danger',
                'MAINTENANCE': 'info'
            };
            return types[status] || 'info';
        }
    }
};