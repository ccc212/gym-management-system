// 场地使用组件
const UserUsageComponent = {
    template: `
        <div class="venue-usage-container">
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-card>
                        <div slot="header" class="card-header">
                            <h2>今日可用预约</h2>
                        </div>
                        
                        <div v-if="todayReservations.length === 0" class="empty-data">
                            <el-empty description="没有今日可用的预约"></el-empty>
                        </div>
                        
                        <div v-else>
                            <el-table 
                                :data="todayReservations" 
                                style="width: 100%"
                                :header-cell-style="{background:'#f5f7fa', color:'#606266'}"
                                border>
                                <el-table-column prop="venueInfo.name" label="场地名称" width="120"></el-table-column>
                                <el-table-column label="预约时间" width="180">
                                    <template slot-scope="scope">
                                        {{ scope.row.startTime }} - {{ scope.row.endTime }}
                                    </template>
                                </el-table-column>
                                <el-table-column prop="status" label="状态" width="100">
                                    <template slot-scope="scope">
                                        <el-tag :type="getStatusType(scope.row.usageStatus)">{{ getStatusText(scope.row.usageStatus) }}</el-tag>
                                    </template>
                                </el-table-column>
                                <el-table-column label="操作" width="160">
                                    <template slot-scope="scope">
                                        <el-button 
                                            v-if="scope.row.usageStatus === 'NOT_STARTED'" 
                                            type="primary" 
                                            size="mini" 
                                            @click="startUsage(scope.row)">开始使用</el-button>
                                        <el-button 
                                            v-if="scope.row.usageStatus === 'IN_PROGRESS'" 
                                            type="danger" 
                                            size="mini" 
                                            @click="endUsage(scope.row)">结束使用</el-button>
                                        <el-button 
                                            v-if="scope.row.usageStatus === 'COMPLETED'" 
                                            type="info" 
                                            size="mini" 
                                            @click="viewUsageDetail(scope.row)">查看详情</el-button>
                                    </template>
                                </el-table-column>
                            </el-table>
                        </div>
                    </el-card>
                </el-col>
                
                <el-col :span="12">
                    <el-card v-if="currentUsage" class="usage-timer-card">
                        <div slot="header" class="card-header">
                            <h2>正在使用</h2>
                        </div>
                        
                        <div class="usage-info">
                            <h3>{{ currentUsage.venueInfo.name }}</h3>
                            <p>预约时间: {{ currentUsage.startTime }} - {{ currentUsage.endTime }}</p>
                            
                            <div class="timer-container">
                                <div class="timer-label">已使用时间</div>
                                <div class="timer-display">{{ formatDuration(elapsedTime) }}</div>
                                
                                <div class="cost-container">
                                    <div class="cost-label">当前费用</div>
                                    <div class="cost-display">¥ {{ calculateCurrentCost().toFixed(2) }}</div>
                                </div>
                                
                                <el-alert
                                    v-if="isOvertime"
                                    title="您已超出预约时间"
                                    type="warning"
                                    :closable="false"
                                    show-icon>
                                    <span slot="description">
                                        超时使用将按正常价格收费，请尽快结束使用。
                                    </span>
                                </el-alert>
                                
                                <div class="timer-actions">
                                    <el-button type="danger" @click="endUsage(currentUsage)">结束使用</el-button>
                                </div>
                            </div>
                        </div>
                    </el-card>
                    
                    <el-card v-else class="no-usage-card">
                        <div slot="header" class="card-header">
                            <h2>场地使用</h2>
                        </div>
                        
                        <div class="empty-usage">
                            <el-empty description="您当前没有正在使用的场地">
                                <el-button type="primary" @click="$router.push('/user/venues')">去预约场地</el-button>
                            </el-empty>
                        </div>
                    </el-card>
                </el-col>
            </el-row>
            
            <!-- 使用结算弹窗 -->
            <el-dialog title="场地使用结算" :visible.sync="settlementDialogVisible" width="500px">
                <div v-if="currentUsage" class="settlement-info">
                    <h3>{{ currentUsage.venueInfo.name }}</h3>
                    <el-descriptions :column="1" border>
                        <el-descriptions-item label="场地类型">{{ currentUsage.venueInfo.type }}</el-descriptions-item>
                        <el-descriptions-item label="预约时间">{{ currentUsage.startTime }} - {{ currentUsage.endTime }}</el-descriptions-item>
                        <el-descriptions-item label="实际使用时间">{{ usageStartTime }} - {{ getCurrentTime() }}</el-descriptions-item>
                        <el-descriptions-item label="使用时长">{{ formatDuration(elapsedTime) }}</el-descriptions-item>
                        <el-descriptions-item label="标准费用">{{ currentUsage.cost }} 元</el-descriptions-item>
                        <el-descriptions-item label="实际费用" class="highlight-cost">
                            {{ calculateCurrentCost().toFixed(2) }} 元
                            <span v-if="isOvertime" class="overtime-tag">
                                (含超时费用 {{ calculateOvertimeCost().toFixed(2) }} 元)
                            </span>
                        </el-descriptions-item>
                    </el-descriptions>
                    
                    <div class="payment-options">
                        <h4>支付方式</h4>
                        <el-radio-group v-model="paymentMethod">
                            <el-radio label="balance">余额支付</el-radio>
                            <el-radio label="wechat">微信支付</el-radio>
                            <el-radio label="alipay">支付宝</el-radio>
                        </el-radio-group>
                    </div>
                </div>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="settlementDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="confirmSettlement">确认支付</el-button>
                </span>
            </el-dialog>
            
            <!-- 使用详情弹窗 -->
            <el-dialog title="使用详情" :visible.sync="usageDetailDialogVisible" width="500px">
                <div v-if="selectedUsage" class="usage-detail">
                    <h3>{{ selectedUsage.venueInfo.name }}</h3>
                    <el-descriptions :column="1" border>
                        <el-descriptions-item label="场地类型">{{ selectedUsage.venueInfo.type }}</el-descriptions-item>
                        <el-descriptions-item label="预约时间">{{ selectedUsage.startTime }} - {{ selectedUsage.endTime }}</el-descriptions-item>
                        <el-descriptions-item label="实际使用时间">{{ selectedUsage.actualStartTime }} - {{ selectedUsage.actualEndTime }}</el-descriptions-item>
                        <el-descriptions-item label="使用时长">{{ selectedUsage.duration }}</el-descriptions-item>
                        <el-descriptions-item label="应付费用">{{ selectedUsage.cost }} 元</el-descriptions-item>
                        <el-descriptions-item label="实付费用">{{ selectedUsage.actualCost }} 元</el-descriptions-item>
                        <el-descriptions-item label="支付方式">{{ getPaymentMethodText(selectedUsage.paymentMethod) }}</el-descriptions-item>
                        <el-descriptions-item label="支付时间">{{ selectedUsage.paymentTime }}</el-descriptions-item>
                    </el-descriptions>
                </div>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="usageDetailDialogVisible = false">关闭</el-button>
                </span>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            // 今日可用预约
            todayReservations: [],
            // 当前使用的场地
            currentUsage: null,
            // 使用开始时间
            usageStartTime: '',
            // 计时器
            timer: null,
            // 已经过时间（秒）
            elapsedTime: 0,
            // 结算弹窗
            settlementDialogVisible: false,
            // 支付方式
            paymentMethod: 'balance',
            // 使用详情弹窗
            usageDetailDialogVisible: false,
            // 选中的使用记录
            selectedUsage: null
        };
    },
    computed: {
        // 是否超时
        isOvertime() {
            if (!this.currentUsage) return false;

            // 解析结束时间
            const [hours, minutes] = this.currentUsage.endTime.split(':').map(Number);
            const endTimeInMinutes = hours * 60 + minutes;

            // 获取当前时间
            const now = new Date();
            const currentTimeInMinutes = now.getHours() * 60 + now.getMinutes();

            return currentTimeInMinutes > endTimeInMinutes;
        }
    },
    created() {
        // 加载今日预约
        this.loadTodayReservations();

        // 检查是否有进行中的使用
        this.checkOngoingUsage();
    },
    beforeDestroy() {
        // 清除计时器
        this.clearTimer();
    },
    methods: {
        // 加载今日预约
        loadTodayReservations() {
            console.log('开始加载今日预约...');
            
            // 获取当前用户ID
            const currentUser = JSON.parse(localStorage.getItem('currentUser'));
            const userId = currentUser && currentUser.id ? currentUser.id : null;
            if (!userId) {
                this.$message.error('请先登录');
                return;
            }
            
            // 获取今日预约
            axios.get(`/api/reservations/user/${userId}`, {
                params: {
                    page: 1,
                    size: 100,
                    status: 'PENDING,CONFIRMED,IN_USE'  // 获取所有可用状态的预约
                }
            })
            .then(response => {
                console.log('获取到预约数据:', response.data);
                
                if (response.data && response.data.records) {
                    // 处理预约数据
                    this.todayReservations = response.data.records
                        .filter(reservation => {
                            // 确保日期是Date对象
                            if (typeof reservation.date === 'string') {
                                reservation.date = new Date(reservation.date);
                            }
                            
                            // 只显示今天的预约
                            const today = new Date();
                            const reservationDate = new Date(reservation.date);
                            return reservationDate.toDateString() === today.toDateString();
                        })
                        .map(reservation => {
                            // 添加使用状态
                            const usageStatus = this.getUsageStatus(reservation);
                            console.log(`预约 ${reservation.id} 的使用状态: ${usageStatus}`);
                            
                            return {
                                ...reservation,
                                usageStatus
                            };
                        });
                    
                    console.log('处理后的预约数据:', this.todayReservations);
                    
                    // 检查本地存储是否有使用中的记录
                    const ongoingUsageStr = localStorage.getItem('ongoingUsage');
                    if (ongoingUsageStr) {
                        try {
                            const ongoingUsage = JSON.parse(ongoingUsageStr);
                            const index = this.todayReservations.findIndex(item => item.id === ongoingUsage.id);
                            if (index !== -1) {
                                console.log(`找到进行中的预约: ${ongoingUsage.id}`);
                                this.todayReservations[index].usageStatus = 'IN_PROGRESS';
                            }
                        } catch (e) {
                            console.error('解析进行中的使用记录失败:', e);
                            localStorage.removeItem('ongoingUsage');
                        }
                    }
                } else {
                    console.error('预约数据格式不正确:', response.data);
                    this.$message.error('获取预约列表失败：数据格式不正确');
                }
            })
            .catch(error => {
                console.error('加载今日预约失败:', error);
                if (error.response) {
                    console.error('错误响应:', error.response.data);
                    this.$message.error('加载今日预约失败: ' + (error.response.data.msg || error.message));
                } else {
                    this.$message.error('加载今日预约失败: ' + error.message);
                }
            });
        },
        
        // 获取使用状态
        getUsageStatus(reservation) {
            const now = new Date();
            
            // 解析日期和时间
            const [hours, minutes] = reservation.startTime.split(':').map(Number);
            const [endHours, endMinutes] = reservation.endTime.split(':').map(Number);
            
            // 将日期字符串转换为Date对象
            const reservationDate = new Date(reservation.date);
            
            // 设置开始时间和结束时间
            const startTime = new Date(reservationDate);
            startTime.setHours(hours, minutes, 0, 0);
            
            const endTime = new Date(reservationDate);
            endTime.setHours(endHours, endMinutes, 0, 0);
            
            // 检查是否有进行中的使用记录
            const ongoingUsageStr = localStorage.getItem('ongoingUsage');
            if (ongoingUsageStr) {
                try {
                    const ongoingUsage = JSON.parse(ongoingUsageStr);
                    if (ongoingUsage.id === reservation.id) {
                        return 'IN_PROGRESS';
                    }
                } catch (e) {
                    localStorage.removeItem('ongoingUsage');
                }
            }
            
            // 检查预约状态
            if (reservation.status === 'COMPLETED') {
                return 'COMPLETED';
            }
            
            // 根据时间判断状态
            if (now < startTime) {
                return 'NOT_STARTED';
            } else if (now >= endTime) {
                return 'COMPLETED';
            } else {
                return 'NOT_STARTED';
            }
        },
        // 检查是否有进行中的使用
        checkOngoingUsage() {
            const ongoingUsageStr = localStorage.getItem('ongoingUsage');
            const usageStartTime = localStorage.getItem('usageStartTime');
            
            if (ongoingUsageStr && usageStartTime) {
                try {
                    const ongoingUsage = JSON.parse(ongoingUsageStr);
                    const startTime = new Date(usageStartTime);
                    const now = new Date();
                    
                    // 计算已经过的时间（秒）
                    this.elapsedTime = Math.floor((now - startTime) / 1000);
                    
                    // 设置当前使用
                    this.currentUsage = ongoingUsage;
                    this.usageStartTime = usageStartTime;
                    
                    // 启动计时器
                    this.startTimer();
                } catch (e) {
                    console.error('解析进行中的使用记录失败:', e);
                    localStorage.removeItem('ongoingUsage');
                    localStorage.removeItem('usageStartTime');
                }
            }
        },
        // 开始使用场地
        startUsage(reservation) {
            // 检查是否已有使用中的场地
            if (this.currentUsage) {
                this.$confirm('您已有正在使用的场地，需要先结束当前使用才能开始新的使用。', '提示', {
                    confirmButtonText: '去结束使用',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    // 滚动到当前使用的卡片
                    const timerCard = document.querySelector('.usage-timer-card');
                    if (timerCard) {
                        timerCard.scrollIntoView({ behavior: 'smooth' });
                    }
                }).catch(() => {});
                return;
            }

            // 更新状态
            const index = this.todayReservations.findIndex(item => item.id === reservation.id);
            if (index !== -1) {
                this.todayReservations[index].usageStatus = 'IN_PROGRESS';
                this.currentUsage = this.todayReservations[index];

                // 记录开始时间
                const now = new Date();
                this.usageStartTime = now.toLocaleString();
                this.elapsedTime = 0;

                // 保存到本地存储
                localStorage.setItem('ongoingUsage', JSON.stringify({
                    ...this.currentUsage,
                    startTime: this.usageStartTime
                }));
                localStorage.setItem('usageStartTime', this.usageStartTime);

                // 启动计时器
                this.startTimer();

                // 提示
                this.$message.success('已开始使用场地');
            }
        },
        // 启动计时器
        startTimer() {
            if (this.timer) {
                clearInterval(this.timer);
            }
            this.timer = setInterval(() => {
                this.elapsedTime++;
            }, 1000);
        },
        // 清除计时器
        clearTimer() {
            if (this.timer) {
                clearInterval(this.timer);
                this.timer = null;
            }
        },
        // 结束使用
        endUsage(reservation) {
            // 检查是否是当前使用中的场地
            if (!this.currentUsage || this.currentUsage.id !== reservation.id) {
                this.$message.error('只能结束当前正在使用的场地');
                return;
            }

            // 显示结算弹窗
            this.settlementDialogVisible = true;
        },
        // 查看使用详情
        viewUsageDetail(usage) {
            // 获取使用详情
            axios.get(`/api/reservations/${usage.id}/usage`)
                .then(response => {
                    if (response.data && response.data.code === 200) {
                        this.selectedUsage = {
                            ...usage,
                            ...response.data.data
                        };
                        this.usageDetailDialogVisible = true;
                    } else {
                        this.$message.error(response.data.msg || '获取使用详情失败');
                    }
                })
                .catch(error => {
                    console.error('获取使用详情失败:', error);
                    this.$message.error('获取使用详情失败: ' + error.message);
                });
        },
        // 确认结算
        confirmSettlement() {
            if (!this.currentUsage) return;

            // 清除计时器
            this.clearTimer();

            // 格式化时间
            const formatTime = (date) => {
                const d = new Date(date);
                return d.toLocaleTimeString('zh-CN', {
                    hour: '2-digit',
                    minute: '2-digit',
                    second: '2-digit',
                    hour12: false
                });
            };

            // 准备结算数据
            const now = new Date();
            const settlementData = {
                reservationId: this.currentUsage.id,
                actualStartTime: formatTime(this.usageStartTime),
                actualEndTime: formatTime(now),
                duration: this.formatDuration(this.elapsedTime),
                actualCost: this.calculateCurrentCost().toFixed(2),
                paymentMethod: this.paymentMethod
            };

            console.log('提交结算数据:', settlementData);

            // 提交结算
            axios.post('/api/reservations/settle', settlementData)
                .then(response => {
                    console.log('结算响应:', response.data);
                    if (response.data && response.data.code === 200) {
                        // 更新状态
                        const index = this.todayReservations.findIndex(item => item.id === this.currentUsage.id);
                        if (index !== -1) {
                            this.todayReservations[index].usageStatus = 'COMPLETED';
                            this.todayReservations[index].actualStartTime = settlementData.actualStartTime;
                            this.todayReservations[index].actualEndTime = settlementData.actualEndTime;
                            this.todayReservations[index].duration = settlementData.duration;
                            this.todayReservations[index].actualCost = settlementData.actualCost;
                            this.todayReservations[index].paymentMethod = settlementData.paymentMethod;
                        }

                        // 清除本地存储
                        localStorage.removeItem('ongoingUsage');
                        localStorage.removeItem('usageStartTime');

                        // 重置状态
                        this.currentUsage = null;
                        this.elapsedTime = 0;
                        this.usageStartTime = '';

                        // 关闭弹窗
                        this.settlementDialogVisible = false;

                        // 提示
                        this.$message.success('结算成功！');
                    } else {
                        this.$message.error(response.data.msg || '结算失败');
                    }
                })
                .catch(error => {
                    console.error('结算失败:', error);
                    if (error.response && error.response.data) {
                        console.error('错误详情:', error.response.data);
                        this.$message.error('结算失败: ' + (error.response.data.msg || error.message));
                    } else {
                        this.$message.error('结算失败: ' + error.message);
                    }
                });
        },
        // 格式化时长
        formatDuration(seconds) {
            const hours = Math.floor(seconds / 3600);
            const minutes = Math.floor((seconds % 3600) / 60);
            const remainingSeconds = seconds % 60;
            
            const parts = [];
            if (hours > 0) parts.push(`${hours}小时`);
            if (minutes > 0) parts.push(`${minutes}分钟`);
            if (remainingSeconds > 0) parts.push(`${remainingSeconds}秒`);
            
            return parts.join(' ') || '0秒';
        },
        // 计算当前费用
        calculateCurrentCost() {
            if (!this.currentUsage) return 0;
            
            const pricePerHour = this.currentUsage.venueInfo.pricePerHour;
            const hours = this.elapsedTime / 3600; // 转换为小时
            
            // 如果超时，加收超时费用
            if (this.isOvertime) {
                const normalHours = this.getNormalHours();
                const overtimeHours = hours - normalHours;
                return pricePerHour * normalHours + this.calculateOvertimeCost();
            }
            
            return pricePerHour * hours;
        },
        // 计算超时费用
        calculateOvertimeCost() {
            if (!this.currentUsage || !this.isOvertime) return 0;
            
            const pricePerHour = this.currentUsage.venueInfo.pricePerHour;
            const normalHours = this.getNormalHours();
            const totalHours = this.elapsedTime / 3600;
            const overtimeHours = totalHours - normalHours;
            
            // 超时费用按1.5倍计算
            return pricePerHour * overtimeHours * 1.5;
        },
        // 获取正常使用时长（小时）
        getNormalHours() {
            if (!this.currentUsage) return 0;
            
            const [startHours, startMinutes] = this.currentUsage.startTime.split(':').map(Number);
            const [endHours, endMinutes] = this.currentUsage.endTime.split(':').map(Number);
            
            return (endHours - startHours) + (endMinutes - startMinutes) / 60;
        },
        // 获取当前时间
        getCurrentTime() {
            const now = new Date();
            return now.toLocaleString();
        },
        // 获取状态文本
        getStatusText(status) {
            const statusMap = {
                'NOT_STARTED': '未开始',
                'IN_PROGRESS': '使用中',
                'COMPLETED': '已完成'
            };
            return statusMap[status] || '未知';
        },
        // 获取状态对应的样式类型
        getStatusType(status) {
            const typeMap = {
                'NOT_STARTED': 'info',
                'IN_PROGRESS': 'success',
                'COMPLETED': 'warning'
            };
            return typeMap[status] || 'info';
        },
        // 获取支付方式文本
        getPaymentMethodText(method) {
            const methodMap = {
                'balance': '余额支付',
                'wechat': '微信支付',
                'alipay': '支付宝'
            };
            return methodMap[method] || '未知';
        }
    }
};