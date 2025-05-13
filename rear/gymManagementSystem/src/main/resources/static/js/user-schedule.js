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
                        <el-descriptions-item label="场地类型">
                            {{ getVenueTypeLabel(selectedSchedule.type) }}
                        </el-descriptions-item>
                        <el-descriptions-item label="日期">{{ selectedSchedule.date }}</el-descriptions-item>
                        <el-descriptions-item label="时间段">{{ selectedSchedule.timeSlot }}</el-descriptions-item>
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
            venueTypes: [],
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
            return this.venues.filter(venue => venue.type === this.searchForm.venueType);
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
            console.log('开始加载场馆数据...');
            
            // 先获取场地类型
            axios.get('/api/venues/types')
                .then(response => {
                    console.log('获取到场地类型:', response.data);
                    if (Array.isArray(response.data)) {
                        this.venueTypes = response.data.map(type => ({
                            value: type,
                            label: this.getVenueTypeLabel(type)
                        }));
                        console.log('处理后的场地类型:', this.venueTypes);
                    } else {
                        console.error('场地类型数据格式错误');
                        this.$message.error('场地类型数据格式错误');
                    }
                    
                    // 获取场馆列表
                    return axios.get('/api/venues');
                })
                .then(response => {
                    console.log('获取到场馆列表:', response.data);
                    if (response.data && response.data.records) {
                        this.venues = response.data.records;
                        console.log('处理后的场馆列表:', this.venues);
                    } else {
                        this.venues = [];
                    }
                })
                .catch(error => {
                    console.error('加载场馆数据失败:', error);
                    this.$message.error('加载场馆数据失败: ' + error.message);
                })
                .finally(() => {
                    this.loading = false;
                });
        },
        // 初始化时间段
        initTimeSlots() {
            const timeSlots = [];
            let startTime = new Date();
            startTime.setHours(8, 0, 0, 0);  // 从早上8点开始
            
            const endTime = new Date();
            endTime.setHours(22, 0, 0, 0);  // 到晚上10点结束
            
            while (startTime < endTime) {
                const endTimeSlot = new Date(startTime.getTime() + 60 * 60000);  // 加1小时
                timeSlots.push({
                    id: timeSlots.length + 1,
                    timeRange: `${this.formatTime(startTime)} - ${this.formatTime(endTimeSlot)}`
                });
                startTime = endTimeSlot;
            }
            
            this.timeSlots = timeSlots;
            console.log('初始化时间段:', this.timeSlots);
        },
        // 格式化时间
        formatTime(date) {
            const hours = date.getHours().toString().padStart(2, '0');
            const minutes = date.getMinutes().toString().padStart(2, '0');
            return `${hours}:${minutes}`;
        },
        // 加载场地安排数据
        loadScheduleData() {
            this.loading = true;
            
            // 构建查询参数
            const params = {
                startDate: this.formatDate(this.currentWeekStart),
                endDate: this.formatDate(new Date(this.currentWeekStart.getTime() + 6 * 24 * 60 * 60 * 1000))
            };
            
            if (this.searchForm.venueType) {
                params.venueType = this.searchForm.venueType;
            }
            
            if (this.searchForm.venueId) {
                params.venueId = this.searchForm.venueId;
            }
            
            console.log("获取场地安排，参数：", params);
            
            // 发送请求获取场地安排数据
            axios.get('/api/venue-schedules/weekly', { params })
                .then(response => {
                    if (response.data && response.data.code === 200) {
                        console.log("获取到场地安排数据:", response.data.data);
                        this.scheduleData = response.data.data || [];
                        
                        // 打印每个时间段的数据
                        this.timeSlots.forEach(timeSlot => {
                            this.weekDays.forEach(day => {
                                const schedule = this.getScheduleForTimeAndDay(timeSlot, day);
                                if (schedule) {
                                    console.log(`时间段 ${timeSlot.timeRange} 在 ${day.date} 的安排:`, schedule);
                                }
                            });
                        });
                    } else {
                        this.$message.error(response.data.msg || '获取场地安排失败');
                    }
                })
                .catch(error => {
                    console.error("获取场地安排失败：", error);
                    this.$message.error('获取场地安排失败：' + (error.response?.data?.msg || error.message));
                })
                .finally(() => {
                    this.loading = false;
                });
        },
        // 搜索场地安排
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
        // 获取指定时间和日期的场地安排
        getScheduleForTimeAndDay(timeSlot, day) {
            console.log('查找场地安排:', {
                timeSlot: timeSlot.timeRange,
                date: day.date,
                scheduleData: this.scheduleData
            });
            
            const schedule = this.scheduleData.find(schedule => 
                schedule.date === day.date && 
                schedule.timeSlot === timeSlot.timeRange
            );
            
            if (schedule) {
                console.log('找到场地安排:', {
                    date: day.date,
                    timeSlot: timeSlot.timeRange,
                    schedule: schedule
                });
            } else {
                console.log('未找到场地安排:', {
                    date: day.date,
                    timeSlot: timeSlot.timeRange
                });
            }
            
            return schedule;
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
            console.log('显示场地详情:', {
                schedule,
                type: schedule.type,
                typeLabel: this.getVenueTypeLabel(schedule.type)
            });
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
        // 获取场地类型标签
        getVenueTypeLabel(type) {
            const typeMap = {
                'basketball': '篮球场',
                'football': '足球场',
                'badminton': '羽毛球场',
                'tennis': '网球场',
                'swimming': '游泳池',
                'table_tennis': '乒乓球室'
            };
            console.log('转换场地类型:', { type, result: typeMap[type] || type });
            return typeMap[type] || type;
        },
        // 获取状态文本
        getStatusText(status) {
            const statusMap = {
                'AVAILABLE': '可预约',
                'BOOKED': '已预约',
                'IN_USE': '使用中',
                'SPECIAL': '特殊场地',
                'MAINTENANCE': '维护中',
                'PAST': '已过期',
                'PENDING': '待确认'
            };
            return statusMap[status] || status;
        },
        // 获取状态类型（用于显示不同颜色）
        getStatusType(status) {
            const typeMap = {
                'AVAILABLE': 'success',
                'BOOKED': 'warning',
                'IN_USE': 'danger',
                'SPECIAL': 'info',
                'MAINTENANCE': 'info',
                'PAST': '',
                'PENDING': 'warning'
            };
            return typeMap[status] || '';
        }
    }
};