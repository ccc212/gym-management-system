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
            // 模拟API请求
            setTimeout(() => {
                const today = new Date().toISOString().split('T')[0];

                // 模拟数据
                this.todayReservations = [
                    {
                        id: 1,
                        venueInfo: { id: 1, name: '篮球场A', type: '篮球场', pricePerHour: 80 },
                        date: today,
                        startTime: '13:00',
                        endTime: '15:00',
                        numberOfPeople: 8,
                        cost: 160,
                        usageStatus: 'NOT_STARTED'
                    },
                    {
                        id: 2,
                        venueInfo: { id: 4, name: '羽毛球场A', type: '羽毛球场', pricePerHour: 40 },
                        date: today,
                        startTime: '16:00',
                        endTime: '17:00',
                        numberOfPeople: 2,
                        cost: 40,
                        usageStatus: 'NOT_STARTED'
                    },
                    {
                        id: 3,
                        venueInfo: { id: 8, name: '游泳池', type: '游泳池', pricePerHour: 30 },
                        date: today,
                        startTime: '18:00',
                        endTime: '19:30',
                        numberOfPeople: 1,
                        cost: 45,
                        usageStatus: 'NOT_STARTED'
                    }
                ];

                // 检查本地存储是否有使用中的记录
                const ongoingUsageStr = localStorage.getItem('ongoingUsage');
                if (ongoingUsageStr) {
                    try {
                        const ongoingUsage = JSON.parse(ongoingUsageStr);
                        const index = this.todayReservations.findIndex(item => item.id === ongoingUsage.id);
                        if (index !== -1) {
                            this.todayReservations[index].usageStatus = 'IN_PROGRESS';
                        }
                    } catch (e) {
                        localStorage.removeItem('ongoingUsage');
                    }
                }
            }, 500);
        },
        // 检查是否有进行中的使用
        checkOngoingUsage() {
            const ongoingUsageStr = localStorage.getItem('ongoingUsage');
            if (ongoingUsageStr) {
                try {
                    const ongoingUsage = JSON.parse(ongoingUsageStr);
                    const startTimeStr = localStorage.getItem('usageStartTime');

                    this.currentUsage = ongoingUsage;
                    this.usageStartTime = startTimeStr;

                    // 计算已使用时间
                    if (startTimeStr) {
                        const startTime = new Date(startTimeStr);
                        const now = new Date();
                        this.elapsedTime = Math.floor((now - startTime) / 1000);
                    }

                    // 启动计时器
                    this.startTimer();
                } catch (e) {
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
                this.usageStartTime = new Date().toLocaleString();
                this.elapsedTime = 0;

                // 保存到本地存储
                localStorage.setItem('ongoingUsage', JSON.stringify(this.currentUsage));
                localStorage.setItem('usageStartTime', this.usageStartTime);

                // 启动计时器
                this.startTimer();

                // 提示
                this.$message.success('已开始使用场地');
            }
        },
        // 启动计时器
        startTimer() {
            // 清除已有计时器
            this.clearTimer();

            // 启动新计时器
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
            // 显示结算弹窗
            this.settlementDialogVisible = true;
        },
        // 查看使用详情
        viewUsageDetail(usage) {
            // 模拟获取详情
            this.selectedUsage = {
                ...usage,
                actualStartTime: '14:05',
                actualEndTime: '15:55',
                duration: '1小时50分钟',
                actualCost: 147.5,
                paymentMethod: 'wechat',
                paymentTime: '2025-03-31 15:56:23'
            };

            this.usageDetailDialogVisible = true;
        },
        // 确认结算
        confirmSettlement() {
            if (!this.currentUsage) return;

            // 清除计时器
            this.clearTimer();

            // 更新状态
            const index = this.todayReservations.findIndex(item => item.id === this.currentUsage.id);
            if (index !== -1) {
                this.todayReservations[index].usageStatus = 'COMPLETED';

                // 创建完整的使用记录（实际项目中应该提交到后端）
                const usageRecord = {
                    ...this.currentUsage,
                    actualStartTime: this.usageStartTime,
                    actualEndTime: new Date().toLocaleString(),
                    duration: this.formatDuration(this.elapsedTime),
                    actualCost: this.calculateCurrentCost(),
                    paymentMethod: this.paymentMethod,
                    paymentTime: new Date().toLocaleString()
                };

                console.log('使用记录:', usageRecord);

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
            }
        },
        // 格式化时长
        formatDuration(seconds) {
            const hours = Math.floor(seconds / 3600);
            const minutes = Math.floor((seconds % 3600) / 60);
            const secs = seconds % 60;

            let result = '';
            if (hours > 0) {
                result += `${hours}小时`;
            }
            if (minutes > 0 || hours > 0) {
                result += `${minutes}分钟`;
            }
            result += `${secs}秒`;

            return result;
        },
        // 计算当前费用
        calculateCurrentCost() {
            if (!this.currentUsage) return 0;

            // 预约的时长（小时）
            const [startHours, startMinutes] = this.currentUsage.startTime.split(':').map(Number);
            const [endHours, endMinutes] = this.currentUsage.endTime.split(':').map(Number);

            const startTimeMinutes = startHours * 60 + startMinutes;
            const endTimeMinutes = endHours * 60 + endMinutes;
            const bookingDurationHours = (endTimeMinutes - startTimeMinutes) / 60;

            // 已使用时长（小时）
            const usedHours = this.elapsedTime / 3600;

            // 如果未超时，按照预约费用计算
            if (!this.isOvertime) {
                return Math.min(usedHours, bookingDurationHours) * this.currentUsage.venueInfo.pricePerHour;
            }

            // 如果超时，需要额外计算超时费用
            const normalCost = bookingDurationHours * this.currentUsage.venueInfo.pricePerHour;
            const overtimeCost = this.calculateOvertimeCost();

            return normalCost + overtimeCost;
        },
        // 计算超时费用
        calculateOvertimeCost() {
            if (!this.currentUsage || !this.isOvertime) return 0;

            // 解析结束时间
            const [endHours, endMinutes] = this.currentUsage.endTime.split(':').map(Number);
            const endTimeInSeconds = (endHours * 60 + endMinutes) * 60;

            // 计算超时秒数
            const overtimeSeconds = this.elapsedTime - endTimeInSeconds;

            // 转换为小时
            const overtimeHours = overtimeSeconds / 3600;

            // 超时费用（按小时收费，超时部分按1.5倍计算）
            return overtimeHours * this.currentUsage.venueInfo.pricePerHour * 1.5;
        },
        // 获取当前时间
        getCurrentTime() {
            const now = new Date();
            const hours = now.getHours().toString().padStart(2, '0');
            const minutes = now.getMinutes().toString().padStart(2, '0');

            return `${hours}:${minutes}`;
        },
        // 获取状态对应的样式类型
        getStatusType(status) {
            const types = {
                'NOT_STARTED': 'info',
                'IN_PROGRESS': 'primary',
                'COMPLETED': 'success'
            };
            return types[status] || 'info';
        },
        // 获取状态显示文本
        getStatusText(status) {
            const texts = {
                'NOT_STARTED': '待使用',
                'IN_PROGRESS': '使用中',
                'COMPLETED': '已完成'
            };
            return texts[status] || '未知';
        },
        // 获取支付方式显示文本
        getPaymentMethodText(method) {
            const texts = {
                'balance': '余额支付',
                'wechat': '微信支付',
                'alipay': '支付宝'
            };
            return texts[method] || '未知';
        }
    }
};