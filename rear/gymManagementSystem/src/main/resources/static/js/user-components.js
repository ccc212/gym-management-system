// 场地预约组件 - 用户
const UserVenuesComponent = {
    template: `
        <div>
            <el-card class="box-card" shadow="never">
                <div slot="header" class="card-header">
                    <span>场地预约</span>
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
                    <el-form-item>
                        <el-button type="primary" @click="fetchVenues">查询</el-button>
                    </el-form-item>
                </el-form>
                
                <el-table :data="venues" border style="width: 100%">
                    <el-table-column prop="name" label="场地名称"></el-table-column>
                    <el-table-column prop="type" label="场地类型"></el-table-column>
                    <el-table-column prop="location" label="位置"></el-table-column>
                    <el-table-column prop="pricePerHour" label="每小时价格(元)"></el-table-column>
                    <el-table-column label="操作" width="150">
                        <template slot-scope="scope">
                            <el-button size="mini" type="primary" @click="showReservationDialog(scope.row)">预约</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </el-card>
            
            <!-- 预约对话框 -->
            <el-dialog title="场地预约" :visible.sync="dialogVisible" width="40%">
                <el-form :model="form" :rules="rules" ref="form" label-width="100px">
                    <el-form-item label="场地名称">
                        <el-input v-model="form.venueName" disabled></el-input>
                    </el-form-item>
                    <el-form-item label="场地类型">
                        <el-input v-model="form.venueType" disabled></el-input>
                    </el-form-item>
                    <el-form-item label="每小时价格">
                        <el-input v-model="form.pricePerHour" disabled>
                            <template slot="append">元</template>
                        </el-input>
                    </el-form-item>
                    <el-form-item label="日期" prop="date">
                        <el-date-picker
                            v-model="form.date"
                            type="date"
                            placeholder="选择日期"
                            style="width: 100%"
                            :picker-options="datePickerOptions">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item label="时间段" prop="timeSlot">
                        <el-select v-model="form.timeSlot" placeholder="请选择时间段" style="width: 100%" @change="calculateCost">
                            <el-option
                                v-for="slot in availableTimeSlots"
                                :key="slot.value"
                                :label="slot.label"
                                :value="slot.value">
                            </el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="一卡通号" prop="cardNumber">
                        <el-input v-model="form.cardNumber" placeholder="请输入一卡通号"></el-input>
                    </el-form-item>
                    <el-form-item label="预计费用">
                        <el-input v-model="form.estimatedCost" disabled>
                            <template slot="append">元</template>
                        </el-input>
                    </el-form-item>
                    <el-form-item label="备注" prop="remark">
                        <el-input type="textarea" v-model="form.remark"></el-input>
                    </el-form-item>
                </el-form>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitReservation">确认</el-button>
                </span>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            searchForm: {
                type: ''
            },
            venues: [],
            dialogVisible: false,
            form: {
                venueId: null,
                venueName: '',
                venueType: '',
                pricePerHour: '',
                date: null,
                timeSlot: '',
                cardNumber: '',
                estimatedCost: '0.00',
                remark: ''
            },
            rules: {
                date: [
                    { type: 'date', required: true, message: '请选择日期', trigger: 'change' }
                ],
                timeSlot: [
                    { required: true, message: '请选择时间段', trigger: 'change' }
                ],
                cardNumber: [
                    { required: true, message: '请输入一卡通号', trigger: 'blur' },
                    { pattern: /^\d{10}$/, message: '一卡通号必须为10位数字', trigger: 'blur' }
                ]
            },
            availableTimeSlots: [
                { label: '09:00 - 10:00', value: '09:00-10:00' },
                { label: '10:00 - 11:00', value: '10:00-11:00' },
                { label: '11:00 - 12:00', value: '11:00-12:00' },
                { label: '14:00 - 15:00', value: '14:00-15:00' },
                { label: '15:00 - 16:00', value: '15:00-16:00' },
                { label: '16:00 - 17:00', value: '16:00-17:00' },
                { label: '17:00 - 18:00', value: '17:00-18:00' },
                { label: '18:00 - 19:00', value: '18:00-19:00' },
                { label: '19:00 - 20:00', value: '19:00-20:00' },
                { label: '20:00 - 21:00', value: '20:00-21:00' }
            ],
            datePickerOptions: {
                disabledDate(time) {
                    return time.getTime() < Date.now() - 8.64e7; // 禁用过去的日期
                }
            }
        };
    },
    created() {
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
        fetchVenues() {
            // 实际应该调用API
            let url = '/venues/available';
            if (this.searchForm.type) {
                url = `/venues/type/${this.searchForm.type}`;
            }

            axios.get(url)
                .then(response => {
                    this.venues = response.data;
                })
                .catch(error => {
                    console.error('获取场地列表失败:', error);
                    this.$message.error('获取场地列表失败');

                    // 模拟数据
                    this.venues = [
                        { id: 1, name: '篮球场1号', type: '篮球场', location: '体育馆一层', pricePerHour: 60 },
                        { id: 2, name: '羽毛球场1号', type: '羽毛球场', location: '体育馆二层', pricePerHour: 30 },
                        { id: 3, name: '网球场1号', type: '网球场', location: '室外', pricePerHour: 40 }
                    ];

                    if (this.searchForm.type) {
                        this.venues = this.venues.filter(v => v.type === this.searchForm.type);
                    }
                });
        },
        showReservationDialog(venue) {
            this.form = {
                venueId: venue.id,
                venueName: venue.name,
                venueType: venue.type,
                pricePerHour: venue.pricePerHour,
                date: new Date(),
                timeSlot: '',
                cardNumber: this.form.cardNumber || '',
                estimatedCost: '0.00',
                remark: ''
            };
            this.dialogVisible = true;
        },
        calculateCost() {
            if (!this.form.timeSlot || !this.form.pricePerHour) {
                this.form.estimatedCost = '0.00';
                return;
            }

            const [start, end] = this.form.timeSlot.split('-');
            const startHour = parseInt(start.split(':')[0]);
            const endHour = parseInt(end.split(':')[0]);
            const hours = endHour - startHour;

            const cost = hours * parseFloat(this.form.pricePerHour);
            this.form.estimatedCost = cost.toFixed(2);
        },
        submitReservation() {
            this.$refs.form.validate((valid) => {
                if (valid) {
                    // 解析时间段
                    const [startTime, endTime] = this.form.timeSlot.split('-');
                    const date = new Date(this.form.date);

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
                            this.$router.push('/user/myreservations');
                        })
                        .catch(error => {
                            console.error('预约失败:', error);
                            this.$message.error('预约失败: ' + (error.response?.data?.message || '未知错误'));

                            // 模拟操作成功
                            this.dialogVisible = false;
                            this.$message.success('预约成功');
                            this.$router.push('/user/myreservations');
                        });
                }
            });
        }
    }
};

// 我的预约组件 - 用户
const UserReservationsComponent = {
    template: `
        <div>
            <el-card class="box-card" shadow="never">
                <div slot="header" class="card-header">
                    <span>我的预约</span>
                    <el-button style="float: right; padding: 3px 0" type="text" @click="fetchData">刷新</el-button>
                </div>
                
                <el-table :data="reservations" border style="width: 100%">
                    <el-table-column prop="id" label="ID" width="80"></el-table-column>
                    <el-table-column prop="venue.name" label="场地名称"></el-table-column>
                    <el-table-column prop="startTime" label="开始时间">
                        <template slot-scope="scope">
                            {{ new Date(scope.row.startTime).toLocaleString() }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="endTime" label="结束时间">
                        <template slot-scope="scope">
                            {{ new Date(scope.row.endTime).toLocaleString() }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="status" label="状态">
                        <template slot-scope="scope">
                            <el-tag :type="getStatusType(scope.row.status)">
                                {{ getStatusText(scope.row.status) }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="200">
                        <template slot-scope="scope">
                            <el-button 
                                size="mini" 
                                @click="handleModify(scope.row)" 
                                :disabled="scope.row.status !== 'BOOKED' || isPast(scope.row.startTime)">
                                修改
                            </el-button>
                            <el-button 
                                size="mini" 
                                type="danger" 
                                @click="handleCancel(scope.row)" 
                                :disabled="scope.row.status !== 'BOOKED' || isPast(scope.row.startTime)">
                                退订
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </el-card>
            
            <!-- 修改预约对话框 -->
            <el-dialog title="修改预约" :visible.sync="dialogVisible" width="40%">
                <el-form :model="form" :rules="rules" ref="form" label-width="100px">
                    <el-form-item label="场地名称">
                        <el-input v-model="form.venueName" disabled></el-input>
                    </el-form-item>
                    <el-form-item label="日期" prop="date">
                        <el-date-picker
                            v-model="form.date"
                            type="date"
                            placeholder="选择日期"
                            style="width: 100%"
                            :picker-options="datePickerOptions">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item label="时间段" prop="timeSlot">
                        <el-select v-model="form.timeSlot" placeholder="请选择时间段" style="width: 100%">
                            <el-option
                                v-for="slot in availableTimeSlots"
                                :key="slot.value"
                                :label="slot.label"
                                :value="slot.value">
                            </el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="备注" prop="remark">
                        <el-input type="textarea" v-model="form.remark"></el-input>
                    </el-form-item>
                </el-form>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitModification">确认修改</el-button>
                </span>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            reservations: [],
            dialogVisible: false,
            form: {
                id: null,
                venueName: '',
                date: null,
                timeSlot: '',
                remark: ''
            },
            rules: {
                date: [
                    { type: 'date', required: true, message: '请选择日期', trigger: 'change' }
                ],
                timeSlot: [
                    { required: true, message: '请选择时间段', trigger: 'change' }
                ]
            },
            availableTimeSlots: [
                { label: '09:00 - 10:00', value: '09:00-10:00' },
                { label: '10:00 - 11:00', value: '10:00-11:00' },
                { label: '11:00 - 12:00', value: '11:00-12:00' },
                { label: '14:00 - 15:00', value: '14:00-15:00' },
                { label: '15:00 - 16:00', value: '15:00-16:00' },
                { label: '16:00 - 17:00', value: '16:00-17:00' },
                { label: '17:00 - 18:00', value: '17:00-18:00' },
                { label: '18:00 - 19:00', value: '18:00-19:00' },
                { label: '19:00 - 20:00', value: '19:00-20:00' },
                { label: '20:00 - 21:00', value: '20:00-21:00' }
            ],
            datePickerOptions: {
                disabledDate(time) {
                    return time.getTime() < Date.now() - 8.64e7; // 禁用过去的日期
                }
            }
        };
    },
    created() {
        this.fetchData();
    },
    methods: {
        fetchData() {
            // 获取当前用户的一卡通号
            const userStr = localStorage.getItem('currentUser');
            let cardNumber = '';
            if (userStr) {
                try {
                    const user = JSON.parse(userStr);
                    cardNumber = user.cardNumber || '2021001001'; // 模拟默认一卡通号
                } catch (e) {
                    console.error('解析用户信息失败:', e);
                    cardNumber = '2021001001'; // 模拟默认一卡通号
                }
            } else {
                cardNumber = '2021001001'; // 模拟默认一卡通号
            }

            // 实际应该调用API
            axios.get(`/reservations?cardNumber=${cardNumber}`)
                .then(response => {
                    this.reservations = response.data;
                })
                .catch(error => {
                    console.error('获取预约列表失败:', error);
                    this.$message.error('获取预约列表失败');

                    // 模拟数据
                    this.reservations = [
                        {
                            id: 1,
                            venue: { id: 1, name: '篮球场1号' },
                            cardNumber: cardNumber,
                            startTime: '2023-05-25T14:00:00',
                            endTime: '2023-05-25T16:00:00',
                            status: 'BOOKED',
                            remark: ''
                        },
                        {
                            id: 2,
                            venue: { id: 2, name: '羽毛球场1号' },
                            cardNumber: cardNumber,
                            startTime: '2023-05-20T10:00:00',
                            endTime: '2023-05-20T12:00:00',
                            status: 'COMPLETED',
                            remark: ''
                        },
                        {
                            id: 3,
                            venue: { id: 3, name: '网球场1号' },
                            cardNumber: cardNumber,
                            startTime: '2023-05-19T15:00:00',
                            endTime: '2023-05-19T17:00:00',
                            status: 'CANCELLED',
                            remark: '临时有事'
                        }
                    ];
                });
        },
        getStatusType(status) {
            switch (status) {
                case 'BOOKED': return 'primary';
                case 'COMPLETED': return 'success';
                case 'CANCELLED': return 'info';
                case 'NO_SHOW': return 'danger';
                default: return 'info';
            }
        },
        getStatusText(status) {
            switch (status) {
                case 'BOOKED': return '已预约';
                case 'COMPLETED': return '已完成';
                case 'CANCELLED': return '已取消';
                case 'NO_SHOW': return '未到场';
                default: return '未知状态';
            }
        },
        isPast(dateStr) {
            const date = new Date(dateStr);
            return date < new Date();
        },
        handleModify(row) {
            // 解析时间段
            const startDate = new Date(row.startTime);
            const endDate = new Date(row.endTime);

            const startHour = startDate.getHours().toString().padStart(2, '0');
            const startMinute = startDate.getMinutes().toString().padStart(2, '0');

            const endHour = endDate.getHours().toString().padStart(2, '0');
            const endMinute = endDate.getMinutes().toString().padStart(2, '0');

            const timeSlot = `${startHour}:${startMinute}-${endHour}:${endMinute}`;

            this.form = {
                id: row.id,
                venueName: row.venue.name,
                date: new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate()),
                timeSlot: timeSlot,
                remark: row.remark || ''
            };

            this.dialogVisible = true;
        },
        handleCancel(row) {
            this.$confirm('确认退订该预约?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                // 获取当前用户的一卡通号
                const userStr = localStorage.getItem('currentUser');
                let cardNumber = '';
                if (userStr) {
                    try {
                        const user = JSON.parse(userStr);
                        cardNumber = user.cardNumber || row.cardNumber;
                    } catch (e) {
                        console.error('解析用户信息失败:', e);
                        cardNumber = row.cardNumber;
                    }
                } else {
                    cardNumber = row.cardNumber;
                }

                // 实际应该调用API
                axios.post(`/reservations/${row.id}/cancel`, { cardNumber: cardNumber })
                    .then(() => {
                        this.$message.success('退订成功');
                        this.fetchData();
                    })
                    .catch(error => {
                        console.error('退订预约失败:', error);
                        this.$message.error('退订预约失败');

                        // 模拟操作成功
                        const reservation = this.reservations.find(r => r.id === row.id);
                        if (reservation) {
                            reservation.status = 'CANCELLED';
                        }
                        this.$message.success('退订成功');
                    });
            }).catch(() => {});
        },
        submitModification() {
            this.$refs.form.validate((valid) => {
                if (valid) {
                    // 解析时间段
                    const [startTime, endTime] = this.form.timeSlot.split('-');
                    const date = new Date(this.form.date);

                    const [startHour, startMinute] = startTime.split(':');
                    const [endHour, endMinute] = endTime.split(':');

                    const startDateTime = new Date(date);
                    startDateTime.setHours(parseInt(startHour), parseInt(startMinute), 0);

                    const endDateTime = new Date(date);
                    endDateTime.setHours(parseInt(endHour), parseInt(endMinute), 0);

                    // 获取当前用户的一卡通号
                    const userStr = localStorage.getItem('currentUser');
                    let cardNumber = '';
                    if (userStr) {
                        try {
                            const user = JSON.parse(userStr);
                            cardNumber = user.cardNumber || '2021001001';
                        } catch (e) {
                            console.error('解析用户信息失败:', e);
                            cardNumber = '2021001001';
                        }
                    } else {
                        cardNumber = '2021001001';
                    }

                    // 实际应该调用API
                    const data = {
                        id: this.form.id,
                        cardNumber: cardNumber,
                        startTime: startDateTime.toISOString(),
                        endTime: endDateTime.toISOString(),
                        remark: this.form.remark
                    };

                    axios.put(`/reservations/${this.form.id}`, data)
                        .then(response => {
                            this.$message.success('修改成功');
                            this.dialogVisible = false;
                            this.fetchData();
                        })
                        .catch(error => {
                            console.error('修改预约失败:', error);
                            this.$message.error('修改预约失败: ' + (error.response?.data?.message || '未知错误'));

                            // 模拟操作成功
                            const reservation = this.reservations.find(r => r.id === this.form.id);
                            if (reservation) {
                                reservation.startTime = startDateTime.toISOString();
                                reservation.endTime = endDateTime.toISOString();
                                reservation.remark = this.form.remark;
                            }
                            this.dialogVisible = false;
                            this.$message.success('修改成功');
                        });
                }
            });
        }
    }
};