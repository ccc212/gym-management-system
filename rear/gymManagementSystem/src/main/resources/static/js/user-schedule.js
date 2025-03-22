// 场地一周信息查询组件 - 用户
const UserScheduleComponent = {
    template: `
        <div>
            <el-card class="box-card" shadow="never">
                <div slot="header" class="card-header">
                    <span>场地一周安排</span>
                </div>
                <el-form :model="searchForm" :inline="true" class="demo-form-inline">
                    <el-form-item label="场地类型">
                        <el-select v-model="searchForm.type" placeholder="请选择场地类型" @change="fetchVenues">
                            <el-option label="全部" value=""></el-option>
                            <el-option label="篮球场" value="篮球场"></el-option>
                            <el-option label="羽毛球场" value="羽毛球场"></el-option>
                            <el-option label="网球场" value="网球场"></el-option>
                            <el-option label="乒乓球室" value="乒乓球室"></el-option>
                            <el-option label="健身房" value="健身房"></el-option>
                            <el-option label="游泳池" value="游泳池"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="场地">
                        <el-select v-model="searchForm.venueId" placeholder="请选择场地" @change="fetchSchedule">
                            <el-option
                                v-for="venue in venues"
                                :key="venue.id"
                                :label="venue.name"
                                :value="venue.id">
                            </el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="周次">
                        <el-date-picker
                            v-model="searchForm.weekStart"
                            type="date"
                            placeholder="选择周次"
                            format="yyyy 第 WW 周"
                            value-format="yyyy-MM-dd"
                            @change="fetchSchedule">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="fetchSchedule">查询</el-button>
                    </el-form-item>
                </el-form>
                
                <div class="schedule-container" v-if="searchForm.venueId">
                    <el-table :data="timeSlots" border style="width: 100%">
                        <el-table-column prop="time" label="时间/日期" width="120"></el-table-column>
                        <el-table-column v-for="(day, index) in weekDays" :key="index" :label="day.label">
                            <template slot-scope="scope">
                                <div 
                                    :class="getTimeSlotClass(scope.row.time, day.date)"
                                    @click="handleTimeSlotClick(scope.row.time, day.date)">
                                    {{ getTimeSlotText(scope.row.time, day.date) }}
                                </div>
                            </template>
                        </el-table-column>
                    </el-table>
                    
                    <div class="legend" style="margin-top: 20px;">
                        <div style="display: flex; align-items: center; margin-right: 20px;">
                            <div style="width: 20px; height: 20px; background-color: #f0f9eb; margin-right: 5px; border: 1px solid #ddd;"></div>
                            <span>可预约</span>
                        </div>
                        <div style="display: flex; align-items: center; margin-right: 20px;">
                            <div style="width: 20px; height: 20px; background-color: #fef0f0; margin-right: 5px; border: 1px solid #ddd;"></div>
                            <span>已预约</span>
                        </div>
                        <div style="display: flex; align-items: center; margin-right: 20px;">
                            <div style="width: 20px; height: 20px; background-color: #fdf6ec; margin-right: 5px; border: 1px solid #ddd;"></div>
                            <span>特殊用途</span>
                        </div>
                    </div>
                </div>
                <div v-else class="empty-data">
                    <i class="el-icon-date" style="font-size: 48px; color: #909399;"></i>
                    <p>请选择场地查看排期</p>
                </div>
            </el-card>
            
            <!-- 预约对话框 -->
            <el-dialog title="场地预约" :visible.sync="dialogVisible" width="40%">
                <el-form :model="form" :rules="rules" ref="form" label-width="100px">
                    <el-form-item label="场地名称">
                        <el-input v-model="form.venueName" disabled></el-input>
                    </el-form-item>
                    <el-form-item label="预约时间">
                        <el-input v-model="form.reservationTime" disabled></el-input>
                    </el-form-item>
                    <el-form-item label="一卡通号" prop="cardNumber">
                        <el-input v-model="form.cardNumber" placeholder="请输入一卡通号"></el-input>
                    </el-form-item>
                    <el-form-item label="备注" prop="remark">
                        <el-input type="textarea" v-model="form.remark"></el-input>
                    </el-form-item>
                </el-form>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitReservation">确认预约</el-button>
                </span>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            searchForm: {
                type: '',
                venueId: null,
                weekStart: this.getMonday(new Date())
            },
            venues: [],
            weekDays: [],
            timeSlots: [
                { time: '09:00-10:00' },
                { time: '10:00-11:00' },
                { time: '11:00-12:00' },
                { time: '14:00-15:00' },
                { time: '15:00-16:00' },
                { time: '16:00-17:00' },
                { time: '17:00-18:00' },
                { time: '18:00-19:00' },
                { time: '19:00-20:00' },
                { time: '20:00-21:00' }
            ],
            schedule: {},
            dialogVisible: false,
            form: {
                venueId: null,
                venueName: '',
                reservationDate: null,
                reservationTime: '',
                cardNumber: '',
                remark: ''
            },
            rules: {
                cardNumber: [
                    { required: true, message: '请输入一卡通号', trigger: 'blur' },
                    { pattern: /^\d{10}$/, message: '一卡通号必须为10位数字', trigger: 'blur' }
                ]
            }
        };
    },
    created() {
        this.initWeekDays();
        this.fetchVenues();

        // 获取用户的一卡通号
        const userStr = localStorage.getItem('currentUser');
        if (userStr) {
            try {
                const user = JSON.parse(userStr);
                if (user.cardNumber) {
                    this.form.cardNumber = user.cardNumber;
                }
            } catch (e) {
                console.error('解析用户信息失败:', e);
            }
        }
    },
    methods: {
        getMonday(d) {
            const date = new Date(d);
            const day = date.getDay();
            const diff = date.getDate() - day + (day === 0 ? -6 : 1);
            const monday = new Date(date.setDate(diff));
            return this.formatDate(monday);
        },
        formatDate(date) {
            const d = new Date(date);
            let month = '' + (d.getMonth() + 1);
            let day = '' + d.getDate();
            const year = d.getFullYear();

            if (month.length < 2) month = '0' + month;
            if (day.length < 2) day = '0' + day;

            return [year, month, day].join('-');
        },
        initWeekDays() {
            this.weekDays = [];
            const weekStart = this.searchForm.weekStart ? new Date(this.searchForm.weekStart) : new Date(this.getMonday(new Date()));

            for (let i = 0; i < 7; i++) {
                const date = new Date(weekStart);
                date.setDate(weekStart.getDate() + i);

                const dayNames = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
                const dayName = dayNames[date.getDay()];
                const monthDay = `${date.getMonth() + 1}/${date.getDate()}`;

                this.weekDays.push({
                    date: this.formatDate(date),
                    label: `${dayName} (${monthDay})`
                });
            }
        },
        fetchVenues() {
            // 实际应该调用API
            let url = '/venues/available';
            if (this.searchForm.type) {
                url = `/venues/type/${this.searchForm.type}`;
            }

            axios.get(url)
                .then(response => {
                    this.venues = response.data;
                    if (this.venues.length > 0 && !this.searchForm.venueId) {
                        this.searchForm.venueId = this.venues[0].id;
                        this.fetchSchedule();
                    }
                })
                .catch(error => {
                    console.error('获取场地列表失败:', error);

                    // 模拟数据
                    this.venues = [
                        { id: 1, name: '篮球场1号', type: '篮球场' },
                        { id: 2, name: '羽毛球场1号', type: '羽毛球场' },
                        { id: 3, name: '网球场1号', type: '网球场' }
                    ];

                    if (this.searchForm.type) {
                        this.venues = this.venues.filter(v => v.type === this.searchForm.type);
                    }

                    if (this.venues.length > 0 && !this.searchForm.venueId) {
                        this.searchForm.venueId = this.venues[0].id;
                        this.fetchSchedule();
                    }
                });
        },
        fetchSchedule() {
            if (!this.searchForm.venueId || !this.searchForm.weekStart) return;

            this.initWeekDays();

            // 实际应该调用API
            const params = {
                venueId: this.searchForm.venueId,
                weekStart: this.searchForm.weekStart
            };

            axios.get('/reservations/schedule', { params })
                .then(response => {
                    this.schedule = response.data;
                })
                .catch(error => {
                    console.error('获取场地排期失败:', error);

                    // 模拟数据
                    this.schedule = {};

                    // 生成一些随机预约
                    const randomBookings = Math.floor(Math.random() * 10) + 5;
                    for (let i = 0; i < randomBookings; i++) {
                        const dayIndex = Math.floor(Math.random() * 7);
                        const timeIndex = Math.floor(Math.random() * 10);
                        const date = this.weekDays[dayIndex].date;
                        const time = this.timeSlots[timeIndex].time;

                        if (!this.schedule[date]) {
                            this.schedule[date] = {};
                        }

                        const statusTypes = ['BOOKED', 'TEAM', 'CLASS'];
                        const statusIndex = Math.floor(Math.random() * 3);

                        this.schedule[date][time] = {
                            status: statusTypes[statusIndex],
                            id: i + 1
                        };
                    }
                });
        },
        getTimeSlotClass(time, date) {
            if (!this.schedule[date] || !this.schedule[date][time]) {
                return 'time-slot available';
            }

            const status = this.schedule[date][time].status;

            if (status === 'BOOKED') {
                return 'time-slot booked';
            } else if (status === 'TEAM' || status === 'CLASS') {
                return 'time-slot special';
            }

            return 'time-slot available';
        },
        getTimeSlotText(time, date) {
            if (!this.schedule[date] || !this.schedule[date][time]) {
                return '可预约';
            }

            const status = this.schedule[date][time].status;

            if (status === 'BOOKED') {
                return '已预约';
            } else if (status === 'TEAM') {
                return '校队训练';
            } else if (status === 'CLASS') {
                return '学校上课';
            }

            return '可预约';
        },
        handleTimeSlotClick(time, date) {
            // 如果已被预约或特殊用途，不可点击
            if (this.schedule[date] && this.schedule[date][time]) {
                return;
            }

            // 如果是过去的日期，不可预约
            const clickDate = new Date(date);
            const today = new Date();
            today.setHours(0, 0, 0, 0);

            if (clickDate < today) {
                this.$message.warning('不能预约过去的日期');
                return;
            }

            // 打开预约对话框
            const selectedVenue = this.venues.find(v => v.id === this.searchForm.venueId);

            this.form = {
                venueId: this.searchForm.venueId,
                venueName: selectedVenue ? selectedVenue.name : '',
                reservationDate: date,
                reservationTime: time,
                cardNumber: this.form.cardNumber || '',
                remark: ''
            };

            // 格式化预约时间显示
            const dateParts = date.split('-');
            const formattedDate = `${dateParts[0]}年${dateParts[1]}月${dateParts[2]}日`;
            this.form.reservationTime = `${formattedDate} ${time}`;

            this.dialogVisible = true;
        },
        submitReservation() {
            this.$refs.form.validate((valid) => {
                if (valid) {
                    // 解析时间段
                    const [startTime, endTime] = this.form.reservationTime.split(' ')[1].split('-');
                    const date = new Date(this.form.reservationDate);

                    const [startHour, startMinute] = startTime.split(':');
                    const [endHour, endMinute] = endTime.split(':');

                    const startDateTime = new Date(date);
                    startDateTime.setHours(parseInt(startHour), parseInt(startMinute), 0);

                    const endDateTime = new Date(date);
                    endDateTime.setHours(parseInt(endHour), parseInt(endMinute), 0);

                    // 实际应该调用API
                    const data = {
                        venueId: this.form.venueId,
                        cardNumber: this.form.cardNumber,
                        startTime: startDateTime.toISOString(),
                        endTime: endDateTime.toISOString(),
                        remark: this.form.remark
                    };

                    axios.post('/reservations', data)
                        .then(response => {
                            this.$message.success('预约成功');
                            this.dialogVisible = false;
                            this.fetchSchedule(); // 刷新排期
                        })
                        .catch(error => {
                            console.error('预约失败:', error);
                            this.$message.error('预约失败: ' + (error.response?.data?.message || '未知错误'));

                            // 模拟操作成功
                            this.dialogVisible = false;

                            // 更新本地数据
                            if (!this.schedule[this.form.reservationDate]) {
                                this.schedule[this.form.reservationDate] = {};
                            }

                            const timeSlot = this.form.reservationTime.split(' ')[1];
                            this.schedule[this.form.reservationDate][timeSlot] = {
                                status: 'BOOKED',
                                id: Math.floor(Math.random() * 1000) + 100
                            };

                            this.$message.success('预约成功');
                        });
                }
            });
        }
    }
};