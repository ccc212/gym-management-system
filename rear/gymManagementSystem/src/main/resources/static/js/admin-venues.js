// 场地管理组件
const AdminVenuesComponent = {
    template: `
        <div class="admin-venues-container">
            <el-card>
                <div slot="header" class="card-header">
                    <h2>场地管理</h2>
                    <el-button type="primary" size="small" @click="showAddVenueDialog">添加场地</el-button>
                </div>
                
                <!-- 搜索筛选区域 -->
                <el-form :inline="true" class="search-form" size="small">
                    <el-form-item label="场地类型">
                        <el-select v-model="searchForm.venueType" placeholder="选择场地类型" clearable @change="searchVenues">
                            <el-option v-for="item in venueTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="场地状态">
                        <el-select v-model="searchForm.status" placeholder="选择场地状态" clearable @change="searchVenues">
                            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="searchVenues">查询</el-button>
                        <el-button @click="resetSearch">重置</el-button>
                    </el-form-item>
                </el-form>
                
                <!-- 场地列表 -->
                <el-table 
                    v-loading="loading" 
                    :data="venueList" 
                    style="width: 100%"
                    :header-cell-style="{background:'#f5f7fa', color:'#606266'}"
                    border>
                    <el-table-column prop="id" label="ID" width="60"></el-table-column>
                    <el-table-column prop="name" label="场地名称" width="150"></el-table-column>
                    <el-table-column prop="type" label="场地类型" width="120"></el-table-column>
                    <el-table-column prop="location" label="场地位置"></el-table-column>
                    <el-table-column prop="capacity" label="容纳人数" width="100"></el-table-column>
                    <el-table-column prop="pricePerHour" label="每小时价格" width="120">
                        <template slot-scope="scope">
                            {{ scope.row.pricePerHour }} 元
                        </template>
                    </el-table-column>
                    <el-table-column prop="status" label="状态" width="100">
                        <template slot-scope="scope">
                            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="220">
                        <template slot-scope="scope">
                            <el-button type="primary" size="mini" @click="editVenue(scope.row)">编辑</el-button>
                            <el-button type="danger" size="mini" @click="confirmDeleteVenue(scope.row)">删除</el-button>
                            <el-button v-if="scope.row.status === 'NORMAL'" type="warning" size="mini" @click="setVenueStatus(scope.row, 'MAINTENANCE')">维护</el-button>
                            <el-button v-if="scope.row.status === 'MAINTENANCE'" type="success" size="mini" @click="setVenueStatus(scope.row, 'NORMAL')">恢复</el-button>
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
            
            <!-- 添加/编辑场地弹窗 -->
            <el-dialog :title="dialogType === 'add' ? '添加场地' : '编辑场地'" :visible.sync="venueDialogVisible" width="600px">
                <el-form :model="venueForm" :rules="venueRules" ref="venueForm" label-width="100px">
                    <el-form-item label="场地名称" prop="name">
                        <el-input v-model="venueForm.name" placeholder="请输入场地名称"></el-input>
                    </el-form-item>
                    <el-form-item label="场地类型" prop="type">
                        <el-select v-model="venueForm.type" placeholder="请选择场地类型" style="width: 100%">
                            <el-option v-for="item in venueTypeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="场地位置" prop="location">
                        <el-input v-model="venueForm.location" placeholder="请输入场地位置"></el-input>
                    </el-form-item>
                    <el-form-item label="容纳人数" prop="capacity">
                        <el-input-number v-model="venueForm.capacity" :min="1" :max="1000" style="width: 100%"></el-input-number>
                    </el-form-item>
                    <el-form-item label="每小时价格" prop="pricePerHour">
                        <el-input-number v-model="venueForm.pricePerHour" :min="0" :precision="2" :step="10" style="width: 100%"></el-input-number>
                    </el-form-item>
                    <el-form-item label="高峰时段价格" prop="peakHourPrice">
                        <el-input-number v-model="venueForm.peakHourPrice" :min="0" :precision="2" :step="10" style="width: 100%"></el-input-number>
                    </el-form-item>
                    <el-form-item label="设施" prop="facilities">
                        <el-input v-model="venueForm.facilities" type="textarea" placeholder="场地配套设施描述"></el-input>
                    </el-form-item>
                    <el-form-item label="场地描述" prop="description">
                        <el-input v-model="venueForm.description" type="textarea" placeholder="场地详细描述"></el-input>
                    </el-form-item>
                    <el-form-item label="场地图片" prop="imageUrl">
                        <el-input v-model="venueForm.imageUrl" placeholder="场地图片URL"></el-input>
                    </el-form-item>
                    <el-form-item label="场地状态" prop="status">
                        <el-select v-model="venueForm.status" placeholder="请选择场地状态" style="width: 100%">
                            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                </el-form>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="venueDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="saveVenue">确定</el-button>
                </span>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            // 搜索表单
            searchForm: {
                venueType: '',
                status: ''
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
            // 场地状态选项
            statusOptions: [
                { value: 'NORMAL', label: '正常' },
                { value: 'MAINTENANCE', label: '维护中' },
                { value: 'SPECIAL', label: '特殊场地' }
            ],
            // 场馆类型选项（中文）
            venueTypeOptions: [
                { value: '篮球场', label: '篮球场' },
                { value: '足球场', label: '足球场' },
                { value: '羽毛球场', label: '羽毛球场' },
                { value: '网球场', label: '网球场' },
                { value: '游泳池', label: '游泳池' },
                { value: '乒乓球室', label: '乒乓球室' }
            ],
            // 场馆列表
            venueList: [],
            // 加载状态
            loading: false,
            // 分页信息
            pagination: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },
            // 场地弹窗可见性
            venueDialogVisible: false,
            // 弹窗类型（add/edit）
            dialogType: 'add',
            // 场地表单
            venueForm: {
                id: null,
                name: '',
                type: '',
                location: '',
                capacity: 10,
                pricePerHour: 50,
                peakHourPrice: 80,
                facilities: '',
                description: '',
                imageUrl: '',
                status: 'NORMAL'
            },
            // 表单验证规则
            venueRules: {
                name: [
                    { required: true, message: '请输入场地名称', trigger: 'blur' },
                    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
                ],
                type: [
                    { required: true, message: '请选择场地类型', trigger: 'change' }
                ],
                location: [
                    { required: true, message: '请输入场地位置', trigger: 'blur' }
                ],
                capacity: [
                    { required: true, message: '请输入容纳人数', trigger: 'blur' }
                ],
                pricePerHour: [
                    { required: true, message: '请输入每小时价格', trigger: 'blur' }
                ]
            }
        };
    },
    created() {
        // 初始加载场馆数据
        this.loadVenueData();
    },
    methods: {
        // 加载场馆数据
        loadVenueData() {
            this.loading = true;

            // 模拟后端API请求
            setTimeout(() => {
                // 模拟数据，实际项目中应通过API获取
                this.venueList = [
                    { id: 1, name: '篮球场A', type: '篮球场', location: '体育馆一楼', capacity: 20, pricePerHour: 80, peakHourPrice: 120, facilities: '照明设备、更衣室', description: '标准篮球场，适合5v5比赛', imageUrl: '', status: 'NORMAL' },
                    { id: 2, name: '篮球场B', type: '篮球场', location: '体育馆一楼', capacity: 20, pricePerHour: 80, peakHourPrice: 120, facilities: '照明设备、更衣室', description: '标准篮球场，适合5v5比赛', imageUrl: '', status: 'MAINTENANCE' },
                    { id: 3, name: '足球场A', type: '足球场', location: '户外运动区', capacity: 30, pricePerHour: 200, peakHourPrice: 300, facilities: '照明设备、更衣室、淋浴', description: '7人制足球场，人造草皮', imageUrl: '', status: 'NORMAL' },
                    { id: 4, name: '羽毛球场A', type: '羽毛球场', location: '体育馆二楼', capacity: 4, pricePerHour: 40, peakHourPrice: 60, facilities: '空调、照明', description: '标准羽毛球场地', imageUrl: '', status: 'NORMAL' },
                    { id: 5, name: '羽毛球场B', type: '羽毛球场', location: '体育馆二楼', capacity: 4, pricePerHour: 40, peakHourPrice: 60, facilities: '空调、照明', description: '标准羽毛球场地', imageUrl: '', status: 'NORMAL' },
                    { id: 6, name: '羽毛球场C', type: '羽毛球场', location: '体育馆二楼', capacity: 4, pricePerHour: 40, peakHourPrice: 60, facilities: '空调、照明', description: '标准羽毛球场地', imageUrl: '', status: 'NORMAL' },
                    { id: 7, name: '网球场A', type: '网球场', location: '户外运动区', capacity: 4, pricePerHour: 60, peakHourPrice: 90, facilities: '照明设备', description: '硬地网球场', imageUrl: '', status: 'NORMAL' },
                    { id: 8, name: '游泳池', type: '游泳池', location: '体育馆负一楼', capacity: 50, pricePerHour: 30, peakHourPrice: 40, facilities: '更衣室、淋浴、储物柜', description: '25米标准泳道，6条泳道', imageUrl: '', status: 'NORMAL' },
                    { id: 9, name: '乒乓球室A', type: '乒乓球室', location: '体育馆三楼', capacity: 4, pricePerHour: 20, peakHourPrice: 30, facilities: '空调、照明', description: '配备2张标准乒乓球桌', imageUrl: '', status: 'NORMAL' },
                    { id: 10, name: '乒乓球室B', type: '乒乓球室', location: '体育馆三楼', capacity: 4, pricePerHour: 20, peakHourPrice: 30, facilities: '空调、照明', description: '配备2张标准乒乓球桌', imageUrl: '', status: 'SPECIAL' }
                ];

                // 根据筛选条件过滤
                this.filterVenues();

                this.pagination.total = this.venueList.length;
                this.loading = false;
            }, 500);
        },
        // 搜索场馆
        searchVenues() {
            this.pagination.currentPage = 1;
            this.loadVenueData();
        },
        // 重置搜索条件
        resetSearch() {
            this.searchForm = {
                venueType: '',
                status: ''
            };
            this.searchVenues();
        },
        // 根据条件过滤场馆
        filterVenues() {
            let filteredList = [...this.venueList];

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

                filteredList = filteredList.filter(venue => {
                    return venue.type === typeMap[this.searchForm.venueType];
                });
            }

            if (this.searchForm.status) {
                filteredList = filteredList.filter(venue => {
                    return venue.status === this.searchForm.status;
                });
            }

            this.venueList = filteredList;
            this.pagination.total = filteredList.length;
        },
        // 分页大小变化处理
        handleSizeChange(val) {
            this.pagination.pageSize = val;
            this.loadVenueData();
        },
        // 页码变化处理
        handleCurrentChange(val) {
            this.pagination.currentPage = val;
            this.loadVenueData();
        },
        // 获取状态文本
        getStatusText(status) {
            const statusMap = {
                'NORMAL': '正常',
                'MAINTENANCE': '维护中',
                'SPECIAL': '特殊场地'
            };
            return statusMap[status] || '未知';
        },
        // 获取状态类型
        getStatusType(status) {
            const typeMap = {
                'NORMAL': 'success',
                'MAINTENANCE': 'warning',
                'SPECIAL': 'danger'
            };
            return typeMap[status] || 'info';
        },
        // 显示添加场地弹窗
        showAddVenueDialog() {
            this.dialogType = 'add';
            this.venueForm = {
                id: null,
                name: '',
                type: '',
                location: '',
                capacity: 10,
                pricePerHour: 50,
                peakHourPrice: 80,
                facilities: '',
                description: '',
                imageUrl: '',
                status: 'NORMAL'
            };
            this.venueDialogVisible = true;

            // 在下一个事件循环中重置表单校验结果
            this.$nextTick(() => {
                this.$refs.venueForm && this.$refs.venueForm.clearValidate();
            });
        },
        // 编辑场地
        editVenue(venue) {
            this.dialogType = 'edit';
            this.venueForm = JSON.parse(JSON.stringify(venue)); // 深拷贝，避免直接修改列表数据
            this.venueDialogVisible = true;

            // 在下一个事件循环中重置表单校验结果
            this.$nextTick(() => {
                this.$refs.venueForm && this.$refs.venueForm.clearValidate();
            });
        },
        // 保存场地
        saveVenue() {
            this.$refs.venueForm.validate(valid => {
                if (valid) {
                    this.loading = true;

                    // 模拟保存请求
                    setTimeout(() => {
                        if (this.dialogType === 'add') {
                            // 模拟新增操作
                            const maxId = Math.max(...this.venueList.map(v => v.id), 0);
                            this.venueForm.id = maxId + 1;
                            this.venueList.unshift(this.venueForm);
                            this.$message.success('添加场地成功');
                        } else {
                            // 模拟编辑操作
                            const index = this.venueList.findIndex(v => v.id === this.venueForm.id);
                            if (index !== -1) {
                                this.venueList.splice(index, 1, this.venueForm);
                                this.$message.success('更新场地成功');
                            }
                        }

                        this.venueDialogVisible = false;
                        this.loading = false;
                    }, 500);
                }
            });
        },
        // 确认删除场地
        confirmDeleteVenue(venue) {
            this.$confirm(`确定要删除场地 "${venue.name}" 吗？`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.deleteVenue(venue);
            }).catch(() => {
                // 取消删除操作
            });
        },
        // 删除场地
        deleteVenue(venue) {
            this.loading = true;

            // 模拟删除请求
            setTimeout(() => {
                const index = this.venueList.findIndex(v => v.id === venue.id);
                if (index !== -1) {
                    this.venueList.splice(index, 1);
                    this.$message.success('删除场地成功');
                }
                this.loading = false;
            }, 500);
        },
        // 设置场地状态
        setVenueStatus(venue, status) {
            this.loading = true;

            // 模拟状态更新请求
            setTimeout(() => {
                const index = this.venueList.findIndex(v => v.id === venue.id);
                if (index !== -1) {
                    this.venueList[index].status = status;
                    this.$message.success(`更新场地状态为${this.getStatusText(status)}成功`);
                }
                this.loading = false;
            }, 500);
        }
    }
};