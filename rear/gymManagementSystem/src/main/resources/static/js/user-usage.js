// 场地使用组件 - 用户
const UserUsageComponent = {
    template: `
        <div>
            <el-card class="box-card" shadow="never">
                <div slot="header" class="card-header">
                    <span>场地使用</span>
                </div>
                <div v-if="!activeUsage">
                    <el-table :data="reservations" border style="width: 100%">
                        <el-table-column prop="id" label="ID" width="80"></el-table-column>
                        <el-table-column prop="venue.name" label="场地名称"></el-table-column>
                        <el-table-column prop="startTime" label="预约时间">
                            <template slot-scope="scope">
                                {{ new Date(scope.row.startTime).toLocaleString() }} - {{ new Date(scope.row.endTime).toLocaleString() }}
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" width="150">
                            <template slot-scope="scope">
                                <el-button 
                                    size="mini" 
                                    type="primary" 
                                    @click="startUsage(scope.row)"
                                    :disabled="!isReservationActive(scope.row)">
                                    开始使用
                                </el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>
                <div v-else class="timer-container">
                    <h2>当前使用场地: {{ activeUsage.venue.name }}</h2>
                    <div class="timer-display">
                        {{ formatTime(elapsedTime) }}
                    </div>
                    <div class="progress-info">
                        <el-progress :percentage="progressPercentage" :color="progressColor"></el-progress>
                        <div class="time-info">
                            <span>已使用时间: {{ formatTime(elapsedTime) }}</span>
                            <span>剩余时间: {{ formatTime(remainingTime) }}</span>
                        </div>
                    </div>
                    <div class="cost-display">
                        当前产生费用: {{ currentCost }} 元
                    </div>
                    <div class="timer-actions">
                        <el-button type="danger" @click="endUsage" :loading="submitting">结束使用</el-button>
                    </div>
                    <div v-if="usageEnded" class="payment-section">
                        <h3>使用结算</h3>
                        <p>使用时长: {{ formatTime(finalTime) }}</p>
                        <p>应付金额: {{ finalCost }} 元</p>
                        <el-button type="primary" @click="payUsage" :loading="paying">一卡通付费</el-button>
                    </div>
                </div>
            </el-card>
        </div>
    `,
    data() {
        return {
            reservations: [],
            activeUsage: null,
            elapsedTime: 0, // 秒
            remainingTime: 0, // 秒
            timerInterval: null,
            currentCost: 0,
            submitting: false,
            usageEnded: false,
            finalTime: 0,
            finalCost: 0,
            paying: false,
            startTime: null
        };
    },
    computed: {
        progressPercentage() {
            if (!this.activeUsage) return 0;
            const totalTime = this.elapsedTime + this.remainingTime;
            return totalTime === 0 ? 0 : Math.min(100, Math.round((this.elapsedTime / totalTime) * 100));
        },
        progressColor() {
            if (this.progressPercentage < 70) return '#67c23a';
            if (this.progressPercentage < 90) return '#e6a23c';
            return '#f56c6c';
        }
    },
    created() {
        this.fetchReservations();

        // 检查是否有正在进行的使用
        this.fetchActiveUsage();
    },
    beforeDestroy() {
        if (this.timerInterval) {
            clearInterval(this.timerInterval);
        }
    },
    methods: {
        fetchReservations() {
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
            axios.get(`/reservations/active?cardNumber=${cardNumber}`)
                .then(response => {
                    this.reservations = response.data;
                })
                .catch(error => {
                    console.error('获取活跃预约失败:', error);

                    // 模拟数据
                    this.reservations = [
                        {
                            id: 8,
                            venue: {
                                id: 1,
                                name: '篮球场1号',
                                pricePerHour: 60
                            },
                            cardNumber: cardNumber,
                            startTime: new Date(new Date().getTime() - 30 * 60 * 1000).toISOString(), // 30分钟前
                            endTime: new Date(new Date().getTime() + 90 * 60 * 1000).toISOString(), // 90分钟后
                            status: 'BOOKED'
                        }
                    ];
                });
        },
        fetchActiveUsage() {
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
            axios.get(`/usages/active?cardNumber=${cardNumber}`)
                .then(response => {
                    if (response.data) {
                        this.activeUsage = response.data;
                        this.startTime = new Date(this.activeUsage.startTime);
                        this.startTimer();
                    }
                })
                .catch(error => {
                    console.error('获取活跃使用记录失败:', error);
                    // 默认不设置活跃使用
                });
        },
        isReservationActive(reservation) {
            const now = new Date();
            const start = new Date(reservation.startTime);
            const end = new Date(reservation.endTime);

            return start <= now && now <= end && reservation.status === 'BOOKED';
        },
        startUsage(reservation) {
            if (this.activeUsage) {
                this.$message.warning('您已有正在使用的场地，请先结束当前使用');
                return;
            }

            // 获取当前用户的一卡通号
            const userStr = localStorage.getItem('currentUser');
            let cardNumber = reservation.cardNumber;
            if (userStr) {
                try {
                    const user = JSON.parse(userStr);
                    if (user.cardNumber) {
                        cardNumber = user.cardNumber;
                    }
                } catch (e) {
                    console.error('解析用户信息失败:', e);
                }
            }

            // 实际应该调用API
            const data = {
                venueId: reservation.venue.id,
                cardNumber: cardNumber,
                reservationId: reservation.id
            };

            axios.post('/usages/start', data)
                .then(response => {
                    this.$message.success('开始使用场地');
                    this.activeUsage = response.data;
                    this.startTime = new Date();
                    this.startTimer();
                })
                .catch(error => {
                    console.error('开始场地使用失败:', error);
                    this.$message.error('开始场地使用失败');

                    // 模拟操作成功
                    this.activeUsage = {
                        id: 1,
                        venue: reservation.venue,
                        reservation: reservation,
                        cardNumber: cardNumber,
                        startTime: new Date().toISOString(),
                        endTime: null,
                        cost: null,
                        paid: false
                    };
                    this.startTime = new Date();
                    this.startTimer();
                    this.$message.success('开始使用场地');
                });
        },
        startTimer() {
            // 计算已经过去的时间（如果从数据库恢复状态）
            if (this.startTime) {
                const now = new Date();
                this.elapsedTime = Math.floor((now - this.startTime) / 1000);
            }

            // 计算剩余时间
            if (this.activeUsage.reservation) {
                const endTime = new Date(this.activeUsage.reservation.endTime);
                const now = new Date();
                this.remainingTime = Math.max(0, Math.floor((endTime - now) / 1000));
            } else {
                // 默认2小时
                this.remainingTime = 2 * 60 * 60;
            }

            // 启动计时器
            this.timerInterval = setInterval(() => {
                this.elapsedTime++;
                if (this.remainingTime > 0) {
                    this.remainingTime--;
                }
                this.calculateCost();
            }, 1000);
        },
        calculateCost() {
            if (!this.activeUsage || !this.activeUsage.venue.pricePerHour) return;

            const hours = this.elapsedTime / 3600;
            const hourlyRate = parseFloat(this.activeUsage.venue.pricePerHour);
            this.currentCost = (hours * hourlyRate).toFixed(2);
        },
        formatTime(seconds) {
            const hours = Math.floor(seconds / 3600);
            const minutes = Math.floor((seconds % 3600) / 60);
            const secs = seconds % 60;

            return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
        },
        endUsage() {
            if (!this.activeUsage) return;

            this.submitting = true;

            // 停止计时器
            if (this.timerInterval) {
                clearInterval(this.timerInterval);
                this.timerInterval = null;
            }

            // 实际应该调用API
            axios.post(`/usages/${this.activeUsage.id}/end`)
                .then(response => {
                    this.submitting = false;
                    this.usageEnded = true;
                    this.finalTime = this.elapsedTime;
                    this.finalCost = this.currentCost;

                    // 更新activeUsage
                    this.activeUsage = response.data;
                })
                .catch(error => {
                    console.error('结束场地使用失败:', error);
                    this.$message.error('结束场地使用失败');

                    // 模拟操作成功
                    this.submitting = false;
                    this.usageEnded = true;
                    this.finalTime = this.elapsedTime;
                    this.finalCost = this.currentCost;
                    this.$message.success('结束使用场地');
                });
        },
        payUsage() {
            if (!this.usageEnded || !this.activeUsage) return;

            this.paying = true;

            // 实际应该调用API
            axios.post(`/usages/${this.activeUsage.id}/pay`)
                .then(response => {
                    this.paying = false;
                    this.$message.success('付费成功');

                    // 重置状态
                    setTimeout(() => {
                        this.activeUsage = null;
                        this.elapsedTime = 0;
                        this.remainingTime = 0;
                        this.currentCost = 0;
                        this.usageEnded = false;
                        this.finalTime = 0;
                        this.finalCost = 0;
                        this.fetchReservations();
                    }, 1500);
                })
                .catch(error => {
                    console.error('场地使用付费失败:', error);
                    this.$message.error('场地使用付费失败');

                    // 模拟操作成功
                    this.paying = false;
                    this.$message.success('付费成功');

                    // 重置状态
                    setTimeout(() => {
                        this.activeUsage = null;
                        this.elapsedTime = 0;
                        this.remainingTime = 0;
                        this.currentCost = 0;
                        this.usageEnded = false;
                        this.finalTime = 0;
                        this.finalCost = 0;
                        this.fetchReservations();
                    }, 1500);
                });
        }
    }
};