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
                            <span style="display: none;">{{ console.log('场地状态:', scope.row.status) }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="220">
                        <template slot-scope="scope">
                            <el-button type="primary" size="mini" @click="editVenue(scope.row)">编辑</el-button>
                            <el-button type="danger" size="mini" @click="confirmDeleteVenue(scope.row)">删除</el-button>
                            <el-button v-if="scope.row.status === 'NORMAL'" type="warning" size="mini" @click="setVenueStatus(scope.row, 'MAINTENANCE')">维护</el-button>
                            <el-button v-if="scope.row.status === 'MAINTENANCE'" type="success" size="mini" @click="setVenueStatus(scope.row, 'NORMAL')">恢复</el-button>
                            <span style="display: none;">{{ console.log('按钮状态判断:', scope.row.status === 'NORMAL') }}</span>
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
                { value: '篮球场', label: '篮球场' },
                { value: '足球场', label: '足球场' },
                { value: '羽毛球场', label: '羽毛球场' },
                { value: '网球场', label: '网球场' },
                { value: '游泳池', label: '游泳池' },
                { value: '乒乓球室', label: '乒乓球室' }
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
        async loadVenueData() {
            this.loading = true;
            try {
                const response = await axios.get('/api/venues', {
                    params: {
                        page: this.pagination.currentPage,
                        size: this.pagination.pageSize,
                        type: this.searchForm.venueType,
                        status: this.searchForm.status
                    }
                });

                if (response.data && response.data.code === 200) {
                    this.venueList = response.data.data.records || [];
                    this.pagination.total = response.data.data.total || 0;
                } else {
                    this.venueList = [];
                    this.pagination.total = 0;
                }
            } catch (error) {
                console.error('加载场馆数据失败:', error);
                this.$message.error('加载场馆数据失败');
                this.venueList = [];
                this.pagination.total = 0;
            } finally {
                this.loading = false;
            }
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
                filteredList = filteredList.filter(venue => {
                    return venue.type === this.searchForm.venueType;
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
            // 修改深拷贝方式
            this.venueForm = {
                id: venue.id,
                name: venue.name,
                type: venue.type,
                location: venue.location,
                capacity: venue.capacity,
                pricePerHour: venue.pricePerHour,
                peakHourPrice: venue.peakHourPrice || 0,
                facilities: venue.facilities || '',
                description: venue.description || '',
                imageUrl: venue.imageUrl || '',
                status: venue.status
            };
            this.venueDialogVisible = true;

            // 在下一个事件循环中重置表单校验结果
            this.$nextTick(() => {
                this.$refs.venueForm && this.$refs.venueForm.clearValidate();
            });
        },
        // 保存场地
        async saveVenue() {
            this.$refs.venueForm.validate(async valid => {
                if (valid) {
                    this.loading = true;
                    try {
                        const venueData = {
                            ...this.venueForm,
                            pricePerHour: Number(this.venueForm.pricePerHour),
                            peakHourPrice: Number(this.venueForm.peakHourPrice),
                            capacity: Number(this.venueForm.capacity)
                        };

                        if (this.dialogType === 'add') {
                            // 新增场地
                            const response = await axios.post('/api/venues', venueData);
                            if (response.data && response.data.code === 200) {
                                this.$message.success('添加场地成功');
                                this.loadVenueData(); // 重新加载数据
                            } else {
                                this.$message.error(response.data.message || '添加场地失败');
                            }
                        } else {
                            // 编辑场地
                            const response = await axios.put(`/api/venues/${this.venueForm.id}`, venueData);
                            if (response.data && response.data.code === 200) {
                                this.$message.success('更新场地成功');
                                this.loadVenueData(); // 重新加载数据
                            } else {
                                this.$message.error(response.data.message || '更新场地失败');
                            }
                        }
                        this.venueDialogVisible = false;
                    } catch (error) {
                        console.error('保存场地失败:', error);
                        this.$message.error(error.response?.data?.message || '保存场地失败');
                    } finally {
                        this.loading = false;
                    }
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
        async deleteVenue(venue) {
            this.loading = true;
            try {
                const response = await axios.delete(`/api/venues/${venue.id}`);
                if (response.status === 200) {
                    this.$message.success('删除场地成功');
                    this.loadVenueData(); // 重新加载数据
                }
            } catch (error) {
                console.error('删除场地失败:', error);
                this.$message.error('删除场地失败');
            } finally {
                this.loading = false;
            }
        },
        // 设置场地状态
        async setVenueStatus(venue, status) {
            this.loading = true;
            try {
                const response = await axios.put(`/api/venues/${venue.id}/status`, { status });
                if (response.data && response.data.code === 200) {
                    this.$message.success(`更新场地状态为${this.getStatusText(status)}成功`);
                    this.loadVenueData(); // 重新加载数据
                } else {
                    this.$message.error(response.data.message || '更新场地状态失败');
                }
            } catch (error) {
                console.error('更新场地状态失败:', error);
                this.$message.error(error.response?.data?.message || '更新场地状态失败');
            } finally {
                this.loading = false;
            }
        }
    }
};