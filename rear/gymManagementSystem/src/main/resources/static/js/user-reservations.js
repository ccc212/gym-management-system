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
                               <el-option label="待使用" value="PENDING"></el-option>
                               <el-option label="已使用" value="USED"></el-option>
                               <el-option label="已取消" value="CANCELED"></el-option>
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

            // 模拟后端API请求
            setTimeout(() => {
                // 模拟数据，实际项目中应通过API获取
                this.reservationList = [
                    {
                        id: 1,
                        venueInfo: { id: 1, name: '篮球场A', type: '篮球场' },
                        date: '2025-04-01',
                        startTime: '13:00',
                        endTime: '15:00',
                        numberOfPeople: 8,
                        cost: 160,
                        status: 'PENDING',
                        createdTime: '2025-03-31 10:30:00',
                        remarks: '团队活动'
                    },
                    {
                        id: 2,
                        venueInfo: { id: 4, name: '羽毛球场A', type: '羽毛球场' },
                        date: '2025-04-02',
                        startTime: '09:00',
                        endTime: '10:00',
                        numberOfPeople: 2,
                        cost: 40,
                        status: 'PENDING',
                        createdTime: '2025-03-31 09:15:00',
                        remarks: ''
                    },
                    {
                        id: 3,
                        venueInfo: { id: 8, name: '游泳池', type: '游泳池' },
                        date: '2025-03-30',
                        startTime: '16:00',
                        endTime: '17:30',
                        numberOfPeople: 1,
                        cost: 45,
                        status: 'USED',
                        createdTime: '2025-03-29 14:20:00',
                        remarks: ''
                    },
                    {
                        id: 4,
                        venueInfo: { id: 3, name: '足球场A', type: '足球场' },
                        date: '2025-03-29',
                        startTime: '15:00',
                        endTime: '17:00',
                        numberOfPeople: 14,
                        cost: 400,
                        status: 'CANCELED',
                        createdTime: '2025-03-28 09:30:00',
                        cancelReason: '天气原因',
                        remarks: '学院足球队训练'
                    }
                ];

                this.pagination.total = this.reservationList.length;
                this.loading = false;

                // 根据筛选条件过滤
                this.filterReservations();
            }, 500);
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

            // 模拟API请求
            this.loading = true;
            setTimeout(() => {
                // 更新本地数据
                const index = this.reservationList.findIndex(item => item.id === this.selectedReservation.id);
                if (index !== -1) {
                    this.reservationList[index].status = 'CANCELED';
                    this.reservationList[index].cancelReason = this.cancelForm.reason;
                }

                this.loading = false;
                this.cancelDialogVisible = false;

                // 显示成功提示
                this.$message.success('预约已成功取消');

                // 刷新数据
                this.loadReservationData();
            }, 500);
        },
        // 获取状态对应的样式类型
        getStatusType(status) {
            const types = {
                'PENDING': 'primary',
                'USED': 'success',
                'CANCELED': 'info'
            };
            return types[status] || 'info';
        },
        // 获取状态显示文本
        getStatusText(status) {
            const texts = {
                'PENDING': '待使用',
                'USED': '已使用',
                'CANCELED': '已取消'
            };
            return texts[status] || '未知';
        },
        // 格式化日期
        formatDate(dateStr) {
            if (!dateStr) return '';

            const date = new Date(dateStr);
            return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
        },
        // 格式化日期时间
        formatDateTime(dateTimeStr) {
            if (!dateTimeStr) return '';

            return dateTimeStr.replace(/-/g, '年').replace(' ', '日 ').replace(/:/g, '时') + '分';
        }
    }
};