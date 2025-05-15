// 特殊场地管理组件
const AdminSpecialComponent = {
    template: `
        <div class="admin-special-container">
            <el-card>
                <div slot="header" class="card-header">
                    <h2>特殊场地管理</h2>
                    <el-button type="primary" size="small" @click="showAddSpecialDialog">添加特殊安排</el-button>
                </div>
                
                <!-- 搜索筛选区域 -->
                <el-form :inline="true" class="search-form" size="small">
                    <el-form-item label="场地类型">
                        <el-select v-model="searchForm.venueType" placeholder="选择场地类型" clearable @change="searchSpecialArrangements">
                            <el-option v-for="item in venueTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="场地名称">
                        <el-select v-model="searchForm.venueId" placeholder="选择场地" clearable @change="searchSpecialArrangements">
                            <el-option v-for="venue in filteredVenues" :key="venue.id" :label="venue.name" :value="venue.id"></el-option>
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
                            @change="searchSpecialArrangements">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="searchSpecialArrangements">查询</el-button>
                        <el-button @click="resetSearch">重置</el-button>
                    </el-form-item>
                </el-form>
                
                <!-- 特殊场地列表 -->
                <el-table 
                    v-loading="loading" 
                    :data="specialArrangementList" 
                    style="width: 100%"
                    :header-cell-style="{background:'#f5f7fa', color:'#606266'}"
                    border>
                    <el-table-column prop="venueName" label="场地名称" width="150"></el-table-column>
                    <el-table-column prop="venueType" label="场地类型" width="120"></el-table-column>
                    <el-table-column prop="date" label="日期" width="120"></el-table-column>
                    <el-table-column prop="timeSlot" label="时间段" width="150"></el-table-column>
                    <el-table-column prop="status" label="状态" width="100">
                        <template slot-scope="scope">
                            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="remarks" label="备注" min-width="200"></el-table-column>
                    <el-table-column label="操作" width="150">
                        <template slot-scope="scope">
                            <el-button type="primary" size="mini" @click="editSpecialArrangement(scope.row)">编辑</el-button>
                            <el-button type="danger" size="mini" @click="deleteSpecialArrangement(scope.row)">删除</el-button>
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
            
            <!-- 添加/编辑特殊场地弹窗 -->
            <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
                <el-form :model="specialForm" ref="specialForm" label-width="100px">
                    <el-form-item label="场地" prop="venueId" required>
                        <el-select v-model="specialForm.venueId" placeholder="选择场地" style="width: 100%">
                            <el-option
                                v-for="venue in filteredVenues"
                                :key="venue.id"
                                :label="venue.name"
                                :value="venue.id">
                            </el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="日期" prop="date" required>
                        <el-date-picker
                            v-model="specialForm.date"
                            type="date"
                            placeholder="选择日期"
                            value-format="yyyy-MM-dd"
                            style="width: 100%">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item label="时间段" required>
                        <el-time-picker
                            v-model="specialForm.startTime"
                            placeholder="开始时间"
                            format="HH:mm"
                            value-format="HH:mm"
                            style="width: 45%">
                        </el-time-picker>
                        <span style="margin: 0 10px">至</span>
                        <el-time-picker
                            v-model="specialForm.endTime"
                            placeholder="结束时间"
                            format="HH:mm"
                            value-format="HH:mm"
                            style="width: 45%">
                        </el-time-picker>
                    </el-form-item>
                    <el-form-item label="状态" prop="status" required>
                        <el-select v-model="specialForm.status" placeholder="选择状态" style="width: 100%">
                            <el-option label="特殊场地" value="SPECIAL"></el-option>
                            <el-option label="维护中" value="MAINTENANCE"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="备注" prop="remarks">
                        <el-input type="textarea" v-model="specialForm.remarks" rows="3"></el-input>
                    </el-form-item>
                </el-form>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="saveSpecialArrangement">确定</el-button>
                </span>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            venues: [], // 所有场馆列表
            specialArrangementList: [],
            loading: false,
            pagination: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },
            searchForm: {
                venueType: '',
                venueId: '',
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
            // 特殊场地弹窗可见性
            dialogVisible: false,
            // 弹窗类型（add/edit）
            dialogTitle: '',
            // 特殊场地表单
            specialForm: {
                id: null,
                venueId: null,
                date: '',
                startTime: '',
                endTime: '',
                status: 'SPECIAL',
                remarks: ''
            },
            apiBaseUrl: '/api/special-arrangements',
            venueApiUrl: '/api/venues'
        };
    },
    computed: {
        // 按类型筛选的场馆列表
        filteredVenues() {
            if (!this.searchForm.venueType) {
                return this.venues;
            }
            return this.venues.filter(venue => venue.type === this.searchForm.venueType);
        }
    },
    created() {
        // 加载场馆数据
        this.fetchVenues();

        // 初始加载特殊场地数据
        this.loadSpecialArrangementData();
    },
    methods: {
        // 获取场地列表
        async fetchVenues() {
            try {
                const response = await axios.get(this.venueApiUrl);
                if (response.data.code === 200) {
                    this.venues = response.data.data.records || [];
                    // 转换场地类型为中文显示
                    this.venues.forEach(venue => {
                        if (venue.type) {
                            const typeOption = this.venueTypes.find(t => t.value === venue.type);
                            if (typeOption) {
                                venue.type = typeOption.label;
                            }
                        }
                    });
                    console.log('获取到的场地列表:', this.venues);
                } else {
                    console.error('获取场地列表失败:', response.data.msg);
                    this.$message.error('获取场地列表失败');
                }
            } catch (error) {
                console.error('获取场地列表失败:', error);
                this.$message.error('获取场地列表失败');
                this.venues = [];
            }
        },
        // 加载特殊场地数据
        loadSpecialArrangementData() {
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
            
            // 添加场地ID过滤
            if (this.searchForm.venueId) {
                params.append('venueId', this.searchForm.venueId);
            }
            
            // 添加日期范围过滤
            if (this.searchForm.dateRange && this.searchForm.dateRange.length === 2) {
                params.append('startDate', this.searchForm.dateRange[0]);
                params.append('endDate', this.searchForm.dateRange[1]);
            }
            
            // 发送请求到后端
            axios.get(`${this.apiBaseUrl}?${params.toString()}`)
                .then(response => {
                    if (response.data && response.data.data) {
                        this.specialArrangementList = response.data.data.records || [];
                        this.pagination.total = response.data.data.total || 0;
                    }
                })
                .catch(error => {
                    console.error('加载特殊场地数据失败:', error);
                    this.$message.error('加载特殊场地数据失败');
                })
                .finally(() => {
                    this.loading = false;
                });
        },
        // 搜索特殊场地
        searchSpecialArrangements() {
            this.pagination.currentPage = 1;
            this.loadSpecialArrangementData();
        },
        // 重置搜索条件
        resetSearch() {
            this.searchForm = {
                venueType: '',
                venueId: '',
                dateRange: []
            };
            this.searchSpecialArrangements();
        },
        // 获取状态文本
        getStatusText(status) {
            const statusMap = {
                'SPECIAL': '特殊场地',
                'MAINTENANCE': '维护中'
            };
            return statusMap[status] || '未知';
        },
        // 获取状态类型
        getStatusType(status) {
            const typeMap = {
                'SPECIAL': 'warning',
                'MAINTENANCE': 'danger'
            };
            return typeMap[status] || 'info';
        },
        // 编辑特殊场地
        editSpecialArrangement(row) {
            this.dialogTitle = '编辑特殊场地';
            this.specialForm = {
                id: row.id,
                venueId: row.venueId,
                date: row.date,
                startTime: row.startTime,
                endTime: row.endTime,
                status: row.status,
                remarks: row.remarks
            };
            this.dialogVisible = true;
        },
        // 删除特殊场地
        deleteSpecialArrangement(row) {
            this.$confirm('确定要删除这个特殊场地安排吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                axios.delete(`${this.apiBaseUrl}/${row.id}`)
                    .then(() => {
                        this.$message.success('删除成功');
                        this.loadSpecialArrangementData();
                    })
                    .catch(error => {
                        console.error('删除特殊场地失败:', error);
                        this.$message.error('删除失败');
                    });
            }).catch(() => {});
        },
        // 保存特殊场地
        saveSpecialArrangement() {
            if (!this.specialForm.venueId || !this.specialForm.date || 
                !this.specialForm.startTime || !this.specialForm.endTime || 
                !this.specialForm.status) {
                this.$message.warning('请填写完整信息');
                return;
            }
            
            const data = {
                venueId: this.specialForm.venueId,
                date: this.specialForm.date,
                startTime: this.specialForm.startTime,
                endTime: this.specialForm.endTime,
                status: this.specialForm.status,
                remarks: this.specialForm.remarks
            };
            
            const request = this.specialForm.id
                ? axios.put(`${this.apiBaseUrl}/${this.specialForm.id}`, data)
                : axios.post(this.apiBaseUrl, data);
            
            request.then(() => {
                this.$message.success(this.specialForm.id ? '更新成功' : '创建成功');
                this.dialogVisible = false;
                this.loadSpecialArrangementData();
            }).catch(error => {
                console.error('保存特殊场地失败:', error);
                this.$message.error('保存失败');
            });
        },
        // 显示添加特殊场地弹窗
        showAddSpecialDialog() {
            this.dialogTitle = '添加特殊场地';
            this.specialForm = {
                id: null,
                venueId: null,
                date: '',
                startTime: '',
                endTime: '',
                status: 'SPECIAL',
                remarks: ''
            };
            this.dialogVisible = true;
        },
        // 处理页码改变
        handleCurrentChange(val) {
            this.pagination.currentPage = val;
            this.loadSpecialArrangementData();
        },
        // 处理每页条数改变
        handleSizeChange(val) {
            this.pagination.pageSize = val;
            this.pagination.currentPage = 1;
            this.loadSpecialArrangementData();
        }
    }
};