// 预约管理组件
const AdminReservationsComponent = {
    template: `
        <div class="admin-reservations-container">
            <el-card>
                <div slot="header" class="card-header">
                    <h2>预约管理</h2>
                </div>
                
                <!-- 搜索筛选区域 -->
                <el-form :inline="true" class="search-form" size="small">
                    <el-form-item label="场地类型">
                        <el-select v-model="searchForm.venueType" placeholder="选择场地类型" clearable @change="searchReservations">
                            <el-option v-for="item in venueTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="预约状态">
                        <el-select v-model="searchForm.status" placeholder="选择预约状态" clearable @change="searchReservations">
                            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="日期范围">
                        <el-date-picker
                            v-model="searchForm.dateRange"
                            type="daterange"
                            range-separator="至"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期"
                            value-format="yyyy-MM-dd"
                            @change="searchReservations">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="searchReservations">查询</el-button>
                        <el-button @click="resetSearch">重置</el-button>
                    </el-form-item>
                </el-form>
                
                <!-- 预约列表 -->
                <el-table 
                    v-loading="loading" 
                    :data="reservationList" 
                    style="width: 100%"
                    :header-cell-style="{background:'#f5f7fa', color:'#606266'}"
                    border>
                    <el-table-column prop="id" label="ID" width="80"></el-table-column>
                    <el-table-column prop="venueName" label="场地名称" width="120"></el-table-column>
                    <el-table-column prop="venueType" label="场地类型" width="100"></el-table-column>
                    <el-table-column prop="userName" label="预约用户" width="100"></el-table-column>
                    <el-table-column prop="date" label="预约日期" width="120"></el-table-column>
                    <el-table-column prop="timeRange" label="时间段" width="160"></el-table-column>
                    <el-table-column prop="numberOfPeople" label="人数" width="80" align="center"></el-table-column>
                    <el-table-column prop="status" label="状态" width="100">
                        <template slot-scope="scope">
                            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
                    <el-table-column label="操作" width="240">
                        <template slot-scope="scope">
                            <el-button type="primary" size="mini" @click="showReservationDetail(scope.row)">详情</el-button>
                            <el-button v-if="scope.row.status === 'PENDING'" type="success" size="mini" @click="approveReservation(scope.row)">批准</el-button>
                            <el-button v-if="scope.row.status === 'PENDING'" type="danger" size="mini" @click="rejectReservation(scope.row)">拒绝</el-button>
                            <el-button v-if="scope.row.status === 'APPROVED' && !isReservationStarted(scope.row)" type="warning" size="mini" @click="cancelReservation(scope.row)">取消</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                
                <!-- 分页组件 -->
                <div class="pagination-container">
                    <el-pagination
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                        :current-page="pagination.currentPage"
                        :page-sizes="[10, 20, 50, 100]"
                        :page-size="pagination.pageSize"
                        layout="total, sizes, prev, pager, next, jumper"
                        :total="pagination.total">
                    </el-pagination>
                </div>
            </el-card>
            
            <!-- 预约详情弹窗 -->
            <el-dialog title="预约详情" :visible.sync="detailDialogVisible" width="600px">
                <div v-if="selectedReservation" class="reservation-detail">
                    <el-descriptions :column="2" border>
                        <el-descriptions-item label="预约ID">{{ selectedReservation.id }}</el-descriptions-item>
                        <el-descriptions-item label="预约状态">
                            <el-tag :type="getStatusType(selectedReservation.status)">{{ getStatusText(selectedReservation.status) }}</el-tag>
                        </el-descriptions-item>
                        <el-descriptions-item label="场地名称">{{ selectedReservation.venueName }}</el-descriptions-item>
                        <el-descriptions-item label="场地类型">{{ selectedReservation.venueType }}</el-descriptions-item>
                        <el-descriptions-item label="预约用户">{{ selectedReservation.userName }}</el-descriptions-item>
                        <el-descriptions-item label="用户手机">{{ selectedReservation.userPhone || '无' }}</el-descriptions-item>
                        <el-descriptions-item label="预约日期">{{ selectedReservation.date }}</el-descriptions-item>
                        <el-descriptions-item label="时间段">{{ selectedReservation.timeRange }}</el-descriptions-item>
                        <el-descriptions-item label="预约人数">{{ selectedReservation.numberOfPeople }} 人</el-descriptions-item>
                        <el-descriptions-item label="费用">{{ selectedReservation.cost }} 元</el-descriptions-item>
                        <el-descriptions-item label="创建时间" :span="2">{{ selectedReservation.createTime }}</el-descriptions-item>
                        <el-descriptions-item label="修改时间" :span="2">{{ selectedReservation.updateTime || '无' }}</el-descriptions-item>
                        <el-descriptions-item label="备注" :span="2">{{ selectedReservation.remarks || '无' }}</el-descriptions-item>
                    </el-descriptions>
                    
                    <!-- 操作记录 -->
                    <div class="operation-history" v-if="selectedReservation.operationHistory && selectedReservation.operationHistory.length > 0">
                        <h3>操作记录</h3>
                        <el-timeline>
                            <el-timeline-item
                                v-for="(activity, index) in selectedReservation.operationHistory"
                                :key="index"
                                :timestamp="activity.time"
                                :type="getOperationHistoryType(activity.type)">
                                {{ activity.content }}
                                <div class="history-operator">操作人: {{ activity.operator }}</div>
                            </el-timeline-item>
                        </el-timeline>
                    </div>
                    
                    <!-- 审核操作 -->
                    <div class="approval-section" v-if="selectedReservation.status === 'PENDING'">
                        <h3>预约审核</h3>
                        <el-form :model="approvalForm" ref="approvalForm" label-width="80px">
                            <el-form-item label="审核意见" prop="comment">
                                <el-input type="textarea" v-model="approvalForm.comment" placeholder="请输入审核意见"></el-input>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="success" @click="doApprove">批准预约</el-button>
                                <el-button type="danger" @click="doReject">拒绝预约</el-button>
                            </el-form-item>
                        </el-form>
                    </div>
                </div>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="detailDialogVisible = false">关闭</el-button>
                </span>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            // 搜索表单
            searchForm: {
                venueType: '',
                status: '',
                dateRange: []
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
            // 预约状态选项
            statusOptions: [
                { value: 'PENDING', label: '待审核' },
                { value: 'CONFIRMED', label: '已确认' },
                { value: 'CANCELED', label: '已取消' },
                { value: 'COMPLETED', label: '已完成' },
                { value: 'REJECTED', label: '已拒绝' }
            ],
            // 预约列表
            reservationList: [],
            // 加载状态
            loading: false,
            // 分页信息
            pagination: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },
            // 详情弹窗可见性
            detailDialogVisible: false,
            // 选中的预约
            selectedReservation: null,
            // 审核表单
            approvalForm: {
                comment: ''
            }
        };
    },
    created() {
        // 初始加载预约数据
        this.loadReservationData();
    },
    methods: {
        // 加载预约数据
        loadReservationData() {
            this.loading = true;
            
            // 构建查询参数
            const params = new URLSearchParams({
                page: this.pagination.currentPage,
                size: this.pagination.pageSize
            });
            
            // 添加场地类型过滤
            if (this.searchForm.venueType) {
                params.append('venueType', this.searchForm.venueType);
            }
            
            // 添加状态过滤
            if (this.searchForm.status) {
                params.append('status', this.searchForm.status);
            }
            
            // 添加日期范围过滤
            if (this.searchForm.dateRange && this.searchForm.dateRange.length === 2) {
                params.append('startDate', this.searchForm.dateRange[0]);
                params.append('endDate', this.searchForm.dateRange[1]);
            }
            
            console.log('查询参数:', params.toString());
            
            // 发送请求到后端
            axios.get(`/api/reservations?${params.toString()}`)
                .then(response => {
                    console.log('API Response:', response.data);
                    if (response.data && response.data.records) {
                        this.reservationList = response.data.records.map(reservation => {
                            console.log('Processing reservation:', reservation);
                            console.log('User info:', reservation.userInfo);
                            return {
                                id: reservation.id,
                                venueName: reservation.venueInfo ? reservation.venueInfo.name : '未知',
                                venueType: reservation.venueInfo ? reservation.venueInfo.type : '未知',
                                userName: reservation.userInfo ? reservation.userInfo.username : '未知',
                                date: reservation.date || '未知',
                                timeRange: `${reservation.startTime} - ${reservation.endTime}`,
                                numberOfPeople: reservation.numberOfPeople,
                                status: reservation.status,
                                createTime: reservation.createdTime,
                                cost: reservation.cost,
                                remarks: reservation.remarks
                            };
                        });
                        this.pagination.total = response.data.total;
                        console.log('Processed List:', this.reservationList);
                    }
                })
                .catch(error => {
                    console.error('Error loading reservations:', error);
                    this.$message.error('加载预约数据失败');
                })
                .finally(() => {
                    this.loading = false;
                });
        },
        // 搜索预约
        searchReservations() {
            this.pagination.currentPage = 1;
            this.loadReservationData();
        },
        // 重置搜索条件
        resetSearch() {
            this.searchForm = {
                venueType: '',
                status: '',
                dateRange: []
            };
            this.searchReservations();
        },
        // 根据条件过滤预约
        filterReservations() {
            let filteredList = [...this.reservationList];

            if (this.searchForm.venueType) {
                // 将英文类型转换为对应的中文类型名
                const typeMap = {
                    'basketball': '篮球场',
                    'football': '足球场',
                    'badminton': '羽毛球场',
                    'tennis': '网球场',
                    'swimming': '游泳池',
                    'table_tennis': '乒乓球室'
                };

                filteredList = filteredList.filter(reservation => {
                    return reservation.venueType === typeMap[this.searchForm.venueType];
                });
            }

            if (this.searchForm.status) {
                filteredList = filteredList.filter(reservation => {
                    return reservation.status === this.searchForm.status;
                });
            }

            if (this.searchForm.dateRange && this.searchForm.dateRange.length === 2) {
                const startDate = this.searchForm.dateRange[0];
                const endDate = this.searchForm.dateRange[1];

                filteredList = filteredList.filter(reservation => {
                    return reservation.date >= startDate && reservation.date <= endDate;
                });
            }

            this.reservationList = filteredList;
            this.pagination.total = filteredList.length;
        },
        // 分页大小变化处理
        handleSizeChange(val) {
            this.pagination.pageSize = val;
            this.loadReservationData();
        },
        // 页码变化处理
        handleCurrentChange(val) {
            this.pagination.currentPage = val;
            this.loadReservationData();
        },
        // 获取状态文本
        getStatusText(status) {
            const statusMap = {
                'PENDING': '待审核',
                'CONFIRMED': '已确认',
                'CANCELED': '已取消',
                'COMPLETED': '已完成',
                'REJECTED': '已拒绝'
            };
            return statusMap[status] || '未知';
        },
        // 获取状态类型
        getStatusType(status) {
            const typeMap = {
                'PENDING': 'warning',
                'CONFIRMED': 'success',
                'CANCELED': 'info',
                'COMPLETED': 'success',
                'REJECTED': 'danger'
            };
            return typeMap[status] || 'info';
        },
        // 获取操作历史类型
        getOperationHistoryType(type) {
            const typeMap = {
                'CREATE': 'primary',
                'APPROVE': 'success',
                'REJECT': 'danger',
                'CANCEL': 'info',
                'COMPLETE': 'success',
                'NO_SHOW': 'warning'
            };
            return typeMap[type] || 'info';
        },
        // 判断预约是否已开始
        isReservationStarted(reservation) {
            const now = new Date();
            const reservationDate = new Date(reservation.date);

            // 判断是否是今天或者过去的日期
            if (reservationDate.getDate() !== now.getDate() ||
                reservationDate.getMonth() !== now.getMonth() ||
                reservationDate.getFullYear() !== now.getFullYear()) {
                return reservationDate < now;
            }

            // 如果是今天，判断时间是否已经开始
            const startTime = reservation.timeRange.split(' - ')[0];
            const [hours, minutes] = startTime.split(':').map(Number);

            const reservationTime = new Date();
            reservationTime.setHours(hours, minutes, 0, 0);

            return now >= reservationTime;
        },
        // 显示预约详情
        showReservationDetail(reservation) {
            this.selectedReservation = JSON.parse(JSON.stringify(reservation));
            this.approvalForm.comment = '';
            this.detailDialogVisible = true;
        },
        // 批准预约
        approveReservation(reservation) {
            this.selectedReservation = JSON.parse(JSON.stringify(reservation));
            this.approvalForm.comment = '';
            this.detailDialogVisible = true;

            // 聚焦到审核部分
            this.$nextTick(() => {
                const approvalSection = document.querySelector('.approval-section');
                if (approvalSection) {
                    approvalSection.scrollIntoView({ behavior: 'smooth' });
                }
            });
        },
        // 执行批准操作
        async doApprove() {
            if (!this.selectedReservation) return;

            this.loading = true;
            try {
                const response = await axios.put(`/api/reservations/${this.selectedReservation.id}/approve`, {
                    comment: this.approvalForm.comment
                });

                if (response.data) {
                    this.$message.success('预约审核通过');
                    this.detailDialogVisible = false;
                    this.loadReservationData(); // 重新加载数据
                }
            } catch (error) {
                console.error('批准预约失败:', error);
                this.$message.error('批准预约失败');
            } finally {
                this.loading = false;
            }
        },
        // 拒绝预约
        rejectReservation(reservation) {
            this.selectedReservation = JSON.parse(JSON.stringify(reservation));
            this.approvalForm.comment = '';
            this.detailDialogVisible = true;

            // 聚焦到审核部分
            this.$nextTick(() => {
                const approvalSection = document.querySelector('.approval-section');
                if (approvalSection) {
                    approvalSection.scrollIntoView({ behavior: 'smooth' });
                }
            });
        },
        // 执行拒绝操作
        async doReject() {
            if (!this.selectedReservation) return;

            if (!this.approvalForm.comment) {
                this.$message.warning('请输入拒绝原因');
                return;
            }

            this.loading = true;
            try {
                const response = await axios.put(`/api/reservations/${this.selectedReservation.id}/reject`, {
                    comment: this.approvalForm.comment
                });

                if (response.data) {
                    this.$message.success('预约已拒绝');
                    this.detailDialogVisible = false;
                    this.loadReservationData(); // 重新加载数据
                }
            } catch (error) {
                console.error('拒绝预约失败:', error);
                this.$message.error('拒绝预约失败');
            } finally {
                this.loading = false;
            }
        },
        // 取消预约
        async cancelReservation(reservation) {
            try {
                const { value } = await this.$prompt('请输入取消原因', '取消预约', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                inputType: 'textarea',
                inputPattern: /\S+/,
                inputErrorMessage: '取消原因不能为空'
                });

                this.loading = true;
                const response = await axios.put(`/api/reservations/${reservation.id}/cancel`, {
                    comment: value
                });

                if (response.data) {
                    this.$message.success('预约已取消');
                    this.loadReservationData(); // 重新加载数据
                }
            } catch (error) {
                if (error !== 'cancel') { // 用户取消输入的情况
                    console.error('取消预约失败:', error);
                    this.$message.error('取消预约失败');
                }
            } finally {
                    this.loading = false;
            }
        }
    }
};