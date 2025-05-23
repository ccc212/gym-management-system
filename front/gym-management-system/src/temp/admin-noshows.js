// 失约处理组件
const AdminNoshowsComponent = {
    template: `
        <div class="admin-noshows-container">
            <el-card>
                <div slot="header" class="card-header">
                    <h2>失约处理</h2>
                    <el-button type="primary" size="small" @click="createTestData">创建测试数据</el-button>
                </div>
                
                <!-- 搜索筛选区域 -->
                <el-form :inline="true" class="search-form" size="small">
                    <el-form-item label="场地类型">
                        <el-select v-model="searchForm.venueType" placeholder="选择场地类型" clearable @change="searchNoshows">
                            <el-option v-for="item in venueTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="处理状态">
                        <el-select v-model="searchForm.status" placeholder="选择处理状态" clearable @change="searchNoshows">
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
                            @change="searchNoshows">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="searchNoshows">查询</el-button>
                        <el-button @click="resetSearch">重置</el-button>
                    </el-form-item>
                </el-form>
                
                <!-- 失约列表 -->
                <el-table 
                    v-loading="loading" 
                    :data="noshowsList" 
                    style="width: 100%"
                    :header-cell-style="{background:'#f5f7fa', color:'#606266'}"
                    border>
                    <el-table-column prop="id" label="ID" width="80"></el-table-column>
                    <el-table-column prop="reservationId" label="预约ID" width="80"></el-table-column>
                    <el-table-column prop="userName" label="用户" width="100"></el-table-column>
                    <el-table-column prop="venueName" label="场地名称" width="120"></el-table-column>
                    <el-table-column prop="venueType" label="场地类型" width="100"></el-table-column>
                    <el-table-column prop="date" label="预约日期" width="110"></el-table-column>
                    <el-table-column prop="timeRange" label="时间段" width="150"></el-table-column>
                    <el-table-column prop="status" label="状态" width="100">
                        <template slot-scope="scope">
                            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="penalty" label="违约金" width="100"></el-table-column>
                    <el-table-column prop="createdTime" label="创建时间" width="180"></el-table-column>
                    <el-table-column label="操作" width="220">
                        <template slot-scope="scope">
                            <el-button v-if="scope.row.status === 'PENDING'" type="success" size="mini" @click="handleNoshow(scope.row, 'EXCUSED')">免责</el-button>
                            <el-button v-if="scope.row.status === 'PENDING'" type="danger" size="mini" @click="handleNoshow(scope.row, 'PENALIZED')">处罚</el-button>
                            <el-button type="primary" size="mini" @click="viewNoshowDetail(scope.row)">详情</el-button>
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
            
            <!-- 失约处理弹窗 -->
            <el-dialog :title="'处理失约 - ' + (currentAction === 'EXCUSED' ? '免责' : '处罚')" :visible.sync="handleDialogVisible" width="500px">
                <el-form v-if="currentNoshow" :model="handleForm" ref="handleForm" label-width="100px">
                    <el-form-item label="用户" class="no-edit">
                        <el-input v-model="currentNoshow.userName" disabled></el-input>
                    </el-form-item>
                    <el-form-item label="预约信息" class="no-edit">
                        <el-input v-model="noshowInfo" disabled></el-input>
                    </el-form-item>
                    <el-form-item v-if="currentAction === 'PENALIZED'" label="处罚金额" prop="penalty">
                        <el-input-number v-model="handleForm.penalty" :min="0" :precision="2" :step="10"></el-input-number>
                    </el-form-item>
                    <el-form-item v-if="currentAction === 'PENALIZED'" label="限制预约" prop="restrictDays">
                        <el-select v-model="handleForm.restrictDays" placeholder="选择限制天数">
                            <el-option label="不限制" :value="0"></el-option>
                            <el-option label="限制3天" :value="3"></el-option>
                            <el-option label="限制7天" :value="7"></el-option>
                            <el-option label="限制15天" :value="15"></el-option>
                            <el-option label="限制30天" :value="30"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="处理原因" prop="reason">
                        <el-input type="textarea" v-model="handleForm.reason" placeholder="请输入处理原因"></el-input>
                    </el-form-item>
                    <el-form-item label="通知用户" prop="notifyUser">
                        <el-switch v-model="handleForm.notifyUser"></el-switch>
                    </el-form-item>
                </el-form>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="handleDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitHandle">确定</el-button>
                </span>
            </el-dialog>
            
            <!-- 详情弹窗 -->
            <el-dialog title="失约详情" :visible.sync="detailDialogVisible" width="600px">
                <div v-if="currentNoshow" class="noshow-detail">
                    <el-descriptions :column="2" border>
                        <el-descriptions-item label="失约ID">{{ currentNoshow.id }}</el-descriptions-item>
                        <el-descriptions-item label="预约ID">{{ currentNoshow.reservationId }}</el-descriptions-item>
                        <el-descriptions-item label="用户姓名">{{ currentNoshow.userName }}</el-descriptions-item>
                        <el-descriptions-item label="用户手机">{{ currentNoshow.userPhone }}</el-descriptions-item>
                        <el-descriptions-item label="场地名称">{{ currentNoshow.venueName }}</el-descriptions-item>
                        <el-descriptions-item label="场地类型">{{ currentNoshow.venueType }}</el-descriptions-item>
                        <el-descriptions-item label="预约日期">{{ currentNoshow.date }}</el-descriptions-item>
                        <el-descriptions-item label="时间段">{{ currentNoshow.timeRange }}</el-descriptions-item>
                        <el-descriptions-item label="预约人数">{{ currentNoshow.numberOfPeople }} 人</el-descriptions-item>
                        <el-descriptions-item label="预约费用">{{ currentNoshow.cost }} 元</el-descriptions-item>
                        <el-descriptions-item label="失约状态">
                            <el-tag :type="getStatusType(currentNoshow.status)">{{ getStatusText(currentNoshow.status) }}</el-tag>
                        </el-descriptions-item>
                        <el-descriptions-item label="违约金">{{ currentNoshow.penalty || '无' }}</el-descriptions-item>
                        <el-descriptions-item label="限制预约">{{ currentNoshow.restrictDays ? currentNoshow.restrictDays + '天' : '无' }}</el-descriptions-item>
                        <el-descriptions-item label="处理时间">{{ currentNoshow.handleTime || '未处理' }}</el-descriptions-item>
                        <el-descriptions-item label="处理人">{{ currentNoshow.handler || '未处理' }}</el-descriptions-item>
                        <el-descriptions-item label="处理原因" :span="2">{{ currentNoshow.reason || '未处理' }}</el-descriptions-item>
                    </el-descriptions>
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
            // 失约状态选项
            statusOptions: [
                { value: 'PENDING', label: '待处理' },
                { value: 'EXCUSED', label: '已免责' },
                { value: 'PENALIZED', label: '已处罚' }
            ],
            // 失约列表
            noshowsList: [],
            // 加载状态
            loading: false,
            // 分页信息
            pagination: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },
            // 处理弹窗可见性
            handleDialogVisible: false,
            // 详情弹窗可见性
            detailDialogVisible: false,
            // 当前选中的失约记录
            currentNoshow: null,
            // 当前处理动作
            currentAction: '',
            // 处理表单
            handleForm: {
                penalty: 0,
                restrictDays: 0,
                reason: '',
                notifyUser: true
            }
        };
    },
    computed: {
        // 格式化的失约信息
        noshowInfo() {
            if (!this.currentNoshow) return '';
            return `${this.currentNoshow.venueName} / ${this.currentNoshow.date} / ${this.currentNoshow.timeRange}`;
        }
    },
    created() {
        // 初始加载失约数据
        this.loadNoshowsData();
    },
    methods: {
        // 加载失约数据
        loadNoshowsData() {
            this.loading = true;

            // 构建查询参数
            const params = {
                page: this.pagination.currentPage,
                size: this.pagination.pageSize
            };

            if (this.searchForm.venueType) {
                params.venueType = this.searchForm.venueType;
            }
            if (this.searchForm.status) {
                params.status = this.searchForm.status;
            }
            if (this.searchForm.dateRange && this.searchForm.dateRange.length === 2) {
                params.startDate = this.searchForm.dateRange[0];
                params.endDate = this.searchForm.dateRange[1];
            }

            // 调用后端API
            axios.get('/api/noshows', { params })
                .then(response => {
                    this.noshowsList = response.data.records;
                    this.pagination.total = response.data.total;
                })
                .catch(error => {
                    this.$message.error('加载失约数据失败：' + error.message);
                })
                .finally(() => {
                    this.loading = false;
                });
        },
        // 搜索失约记录
        searchNoshows() {
            this.pagination.currentPage = 1;
            this.loadNoshowsData();
        },
        // 重置搜索条件
        resetSearch() {
            this.searchForm = {
                venueType: '',
                status: '',
                dateRange: []
            };
            this.searchNoshows();
        },
        // 分页大小变化处理
        handleSizeChange(val) {
            this.pagination.pageSize = val;
            this.loadNoshowsData();
        },
        // 页码变化处理
        handleCurrentChange(val) {
            this.pagination.currentPage = val;
            this.loadNoshowsData();
        },
        // 获取状态文本
        getStatusText(status) {
            const statusMap = {
                'PENDING': '待处理',
                'EXCUSED': '已免责',
                'PENALIZED': '已处罚'
            };
            return statusMap[status] || '未知';
        },
        // 获取状态类型
        getStatusType(status) {
            const typeMap = {
                'PENDING': 'warning',
                'EXCUSED': 'success',
                'PENALIZED': 'danger'
            };
            return typeMap[status] || 'info';
        },
        // 处理失约
        handleNoshow(noshow, action) {
            this.currentNoshow = JSON.parse(JSON.stringify(noshow));
            this.currentAction = action;

            this.handleForm = {
                penalty: action === 'PENALIZED' ? noshow.cost : 0,
                restrictDays: action === 'PENALIZED' ? 7 : 0,
                reason: '',
                notifyUser: true
            };

            this.handleDialogVisible = true;
        },
        // 提交处理
        submitHandle() {
            if (!this.currentNoshow) {
                this.$message.warning('数据错误，请重试');
                return;
            }

            if (this.currentAction === 'PENALIZED' && !this.handleForm.reason) {
                this.$message.warning('处罚必须填写处理原因');
                return;
            }

            this.loading = true;

            // 构建处理数据
            const handleData = {
                status: this.currentAction,
                handler: '管理员', // TODO: 从用户会话中获取当前管理员信息
                reason: this.handleForm.reason,
                notifyUser: this.handleForm.notifyUser
            };

            if (this.currentAction === 'PENALIZED') {
                handleData.penalty = this.handleForm.penalty;
                handleData.restrictDays = this.handleForm.restrictDays;
            }

            // 调用后端API
            axios.post(`/api/noshows/${this.currentNoshow.id}/handle`, handleData)
                .then(response => {
                    // 更新当前选中项
                    this.currentNoshow = response.data;
                    
                    // 更新列表中的数据
                    const index = this.noshowsList.findIndex(item => item.id === this.currentNoshow.id);
                    if (index !== -1) {
                        this.noshowsList.splice(index, 1, this.currentNoshow);
                    }

                    this.$message.success('处理成功');
                    this.handleDialogVisible = false;
                })
                .catch(error => {
                    this.$message.error('处理失败：' + error.message);
                })
                .finally(() => {
                    this.loading = false;
                });
        },
        // 查看失约详情
        viewNoshowDetail(noshow) {
            this.loading = true;
            axios.get(`/api/noshows/${noshow.id}`)
                .then(response => {
                    this.currentNoshow = response.data;
                    this.detailDialogVisible = true;
                })
                .catch(error => {
                    this.$message.error('获取详情失败：' + error.message);
                })
                .finally(() => {
                    this.loading = false;
                });
        },
        // 创建测试数据
        createTestData() {
            this.loading = true;
            axios.post('/api/noshows/test/create')
                .then(response => {
                    this.$message.success('创建测试数据成功');
                    this.loadNoshowsData();
                })
                .catch(error => {
                    this.$message.error('创建测试数据失败：' + error.message);
                })
                .finally(() => {
                    this.loading = false;
                });
        }
    }
};