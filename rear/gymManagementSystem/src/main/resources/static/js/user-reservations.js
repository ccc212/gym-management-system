// 我的预约组件
const UserReservationsComponent = {
    template: `
           <div class="my-reservations-container">
               <el-card>
                   <div slot="header" class="card-header">
                       <h2>我的预约</h2>
                   </div>

                   <!-- 筛选区域 -->
                   <el-form :inline="true" class="search-form" size="small">
                       <el-form-item label="预约状态">
                           <el-select v-model="searchForm.status" placeholder="选择状态" clearable @change="searchReservations">
                               <el-option label="全部" value=""></el-option>
                               <el-option label="待确认" value="PENDING"></el-option>
                               <el-option label="已确认" value="CONFIRMED"></el-option>
                               <el-option label="使用中" value="IN_USE"></el-option>
                               <el-option label="已完成" value="COMPLETED"></el-option>
                               <el-option label="已取消" value="CANCELED"></el-option>
                               <el-option label="已拒绝" value="REJECTED"></el-option>
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
                       <el-table-column prop="venueInfo.name" label="场地名称" width="150"></el-table-column>
                       <el-table-column prop="venueInfo.type" label="场地类型" width="110"></el-table-column>
                       <el-table-column label="预约时间" width="180">
                           <template slot-scope="scope">
                               {{ formatDate(scope.row.date) }}<br>
                               {{ scope.row.startTime }} - {{ scope.row.endTime }}
                           </template>
                       </el-table-column>
                       <el-table-column prop="numberOfPeople" label="人数" width="80"></el-table-column>
                       <el-table-column prop="cost" label="费用" width="100">
                           <template slot-scope="scope">
                               {{ scope.row.cost }} 元
                           </template>
                       </el-table-column>
                       <el-table-column prop="status" label="状态" width="100">
                           <template slot-scope="scope">
                               <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
                           </template>
                       </el-table-column>
                       <el-table-column label="操作" width="160">
                           <template slot-scope="scope">
                               <el-button type="primary" size="mini" @click="showReservationDetail(scope.row)">详情</el-button>
                               <el-button 
                                   v-if="scope.row.status === 'PENDING'" 
                                   type="danger" 
                                   size="mini" 
                                   @click="cancelReservation(scope.row)">取消</el-button>
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

               <!-- 预约详情弹窗 -->
               <el-dialog title="预约详情" :visible.sync="detailDialogVisible" width="500px">
                   <div v-if="selectedReservation" class="reservation-detail">
                       <h3>预约信息</h3>
                       <el-descriptions :column="1" border>
                           <el-descriptions-item label="场地名称">{{ selectedReservation.venueInfo.name }}</el-descriptions-item>
                           <el-descriptions-item label="场地类型">{{ selectedReservation.venueInfo.type }}</el-descriptions-item>
                           <el-descriptions-item label="预约日期">{{ formatDate(selectedReservation.date) }}</el-descriptions-item>
                           <el-descriptions-item label="预约时间">{{ selectedReservation.startTime }} - {{ selectedReservation.endTime }}</el-descriptions-item>
                           <el-descriptions-item label="预约人数">{{ selectedReservation.numberOfPeople }} 人</el-descriptions-item>
                           <el-descriptions-item label="费用">{{ selectedReservation.cost }} 元</el-descriptions-item>
                           <el-descriptions-item label="预约状态">
                               <el-tag :type="getStatusType(selectedReservation.status)">
                                   {{ getStatusText(selectedReservation.status) }}
                               </el-tag>
                           </el-descriptions-item>
                           <el-descriptions-item label="预约时间">{{ formatDateTime(selectedReservation.createdTime) }}</el-descriptions-item>
                           <el-descriptions-item label="备注" :span="1">
                               {{ selectedReservation.remarks || '无' }}
                           </el-descriptions-item>
                       </el-descriptions>
                   </div>
                   <span slot="footer" class="dialog-footer">
                       <el-button @click="detailDialogVisible = false">关闭</el-button>
                       <el-button 
                           v-if="selectedReservation && selectedReservation.status === 'PENDING'" 
                           type="danger" 
                           @click="cancelReservation(selectedReservation, true)">取消预约</el-button>
                   </span>
               </el-dialog>

               <!-- 取消预约弹窗 -->
               <el-dialog title="取消预约" :visible.sync="cancelDialogVisible" width="400px">
                   <div>
                       <p>确定要取消这个预约吗？</p>
                       <el-form :model="cancelForm" ref="cancelForm" label-width="100px">
                           <el-form-item label="取消原因" prop="reason">
                               <el-input 
                                   type="textarea" 
                                   v-model="cancelForm.reason"
                                   placeholder="请输入取消原因（可选）">
                               </el-input>
                           </el-form-item>
                       </el-form>
                   </div>
                   <span slot="footer" class="dialog-footer">
                       <el-button @click="cancelDialogVisible = false">取消</el-button>
                       <el-button type="danger" @click="confirmCancelReservation">确定取消</el-button>
                   </span>
               </el-dialog>
           </div>
       `,
    data() {
        return {
            // 搜索表单
            searchForm: {
                status: '',
                dateRange: []
            },
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
            // 预约详情弹窗
            detailDialogVisible: false,
            // 选中的预约
            selectedReservation: null,
            // 取消预约弹窗
            cancelDialogVisible: false,
            // 取消表单
            cancelForm: {
                reason: ''
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
            
            // 获取当前用户ID（从localStorage或其他地方）
            const currentUser = JSON.parse(localStorage.getItem('currentUser'));
            const userId = currentUser && currentUser.id ? currentUser.id : null;
            if (!userId) {
                this.$message.error('请先登录');
                this.loading = false;
                return;
            }

            console.log('当前用户ID:', userId);

            // 构建查询参数
            const params = new URLSearchParams({
                page: this.pagination.currentPage,
                size: this.pagination.pageSize
            });
            
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
            axios.get(`/api/reservations/user/${userId}?${params.toString()}`)
                .then(response => {
                    console.log('获取预约列表响应:', response.data);
                    if (response.data && response.data.records) {
                        const { records, total, current, size } = response.data;
                        console.log('预约记录数:', records.length);
                        this.reservationList = records.map(reservation => {
                            // 处理日期
                            if (typeof reservation.date === 'string') {
                                // 如果是字符串，直接使用
                                reservation.date = reservation.date;
                            } else if (reservation.date instanceof Date) {
                                // 如果是Date对象，转换为字符串
                                reservation.date = reservation.date.toISOString().split('T')[0];
                            }
                            console.log('处理预约记录:', reservation);
                            return reservation;
                        });
                        this.pagination.total = total;
                        this.pagination.currentPage = current;
                        this.pagination.pageSize = size;
                    } else {
                        console.error('预约数据格式不正确:', response.data);
                        this.$message.error('获取预约列表失败：数据格式不正确');
                    }
                })
                .catch(error => {
                    console.error('获取预约列表失败:', error);
                    if (error.response) {
                        console.error('错误响应:', error.response.data);
                    }
                    this.$message.error('获取预约列表失败：' + (error.response?.data?.message || error.message));
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
                status: '',
                dateRange: []
            };
            this.searchReservations();
        },
        // 根据条件过滤预约
        filterReservations() {
            let filteredList = [...this.reservationList];

            // 按状态过滤
            if (this.searchForm.status) {
                filteredList = filteredList.filter(item => item.status === this.searchForm.status);
            }

            // 按日期范围过滤
            if (this.searchForm.dateRange && this.searchForm.dateRange.length === 2) {
                const startDate = new Date(this.searchForm.dateRange[0]);
                const endDate = new Date(this.searchForm.dateRange[1]);

                filteredList = filteredList.filter(item => {
                    const itemDate = new Date(item.date);
                    return itemDate >= startDate && itemDate <= endDate;
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
        // 显示预约详情
        showReservationDetail(reservation) {
            this.selectedReservation = reservation;
            this.detailDialogVisible = true;
        },
        // 取消预约
        cancelReservation(reservation, fromDetail = false) {
            this.selectedReservation = reservation;
            this.cancelForm = { reason: '' };
            this.cancelDialogVisible = true;

            // 如果是从详情页打开，关闭详情弹窗
            if (fromDetail) {
                this.detailDialogVisible = false;
            }
        },
        // 确认取消预约
        confirmCancelReservation() {
            if (!this.selectedReservation) return;
            
            this.loading = true;
            
            // 发送取消请求到后端
            axios.put(`/api/reservations/${this.selectedReservation.id}/cancel`, null, {
                params: {
                    reason: this.cancelForm.reason
                }
            })
            .then(response => {
                this.$message.success('预约已成功取消');
                this.cancelDialogVisible = false;
                this.loadReservationData(); // 重新加载数据
            })
            .catch(error => {
                console.error('取消预约失败:', error);
                this.$message.error('取消预约失败，请稍后重试');
            })
            .finally(() => {
                this.loading = false;
            });
        },
        // 获取状态文本
        getStatusText(status) {
            const statusMap = {
                'PENDING': '待确认',
                'CONFIRMED': '已确认',
                'CANCELED': '已取消',
                'COMPLETED': '已完成',
                'REJECTED': '已拒绝'
            };
            return statusMap[status] || '未知';
        },
        // 获取状态对应的样式类型
        getStatusType(status) {
            const typeMap = {
                'PENDING': 'warning',
                'CONFIRMED': 'primary',
                'CANCELED': 'danger',
                'COMPLETED': 'info',
                'REJECTED': 'danger'
            };
            return typeMap[status] || 'info';
        },
        // 格式化日期
        formatDate(dateStr) {
            if (!dateStr) return '';

            try {
                // 如果是Date对象，转换为字符串
                if (dateStr instanceof Date) {
                    dateStr = dateStr.toISOString().split('T')[0];
                }
                
                // 解析日期字符串
                const [year, month, day] = dateStr.split('-');
                return `${year}年${parseInt(month)}月${parseInt(day)}日`;
            } catch (error) {
                console.error('日期格式化错误:', error);
                return dateStr;
            }
        },
        // 格式化日期时间
        formatDateTime(dateTimeStr) {
            if (!dateTimeStr) return '';

            return dateTimeStr.replace(/-/g, '年').replace(' ', '日 ').replace(/:/g, '时') + '分';
        }
    }
};