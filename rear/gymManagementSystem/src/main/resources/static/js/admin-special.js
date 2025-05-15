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
                    :data="specialArrangements" 
                    style="width: 100%"
                    :header-cell-style="{background:'#f5f7fa', color:'#606266'}"
                    border>
                    <el-table-column prop="id" label="ID" width="80"></el-table-column>
                    <el-table-column prop="venueName" label="场地名称" width="150"></el-table-column>
                    <el-table-column prop="venueType" label="场地类型" width="120"></el-table-column>
                    <el-table-column prop="date" label="日期" width="120"></el-table-column>
                    <el-table-column prop="timeRange" label="时间段" width="160"></el-table-column>
                    <el-table-column prop="purpose" label="用途"></el-table-column>
                    <el-table-column prop="createdBy" label="创建人" width="120"></el-table-column>
                    <el-table-column prop="createdTime" label="创建时间" width="180"></el-table-column>
                    <el-table-column label="操作" width="150">
                        <template slot-scope="scope">
                            <el-button type="primary" size="mini" @click="editSpecialArrangement(scope.row)">编辑</el-button>
                            <el-button type="danger" size="mini" @click="confirmDeleteSpecial(scope.row)">删除</el-button>
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
            <el-dialog :title="dialogType === 'add' ? '添加特殊安排' : '编辑特殊安排'" :visible.sync="specialDialogVisible" width="600px">
                <el-form :model="specialForm" :rules="specialRules" ref="specialForm" label-width="100px">
                    <el-form-item label="场地" prop="venueId">
                        <el-select v-model="specialForm.venueId" placeholder="请选择场地" style="width: 100%" @change="handleVenueChange">
                            <el-option-group
                                v-for="venueGroup in venueGroups"
                                :key="venueGroup.label"
                                :label="venueGroup.label">
                                <el-option
                                    v-for="venue in venueGroup.venues"
                                    :key="venue.id"
                                    :label="venue.name"
                                    :value="venue.id">
                                </el-option>
                            </el-option-group>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="日期" prop="date">
                        <el-date-picker
                            v-model="specialForm.date"
                            type="date"
                            placeholder="选择日期"
                            value-format="yyyy-MM-dd"
                            style="width: 100%">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item label="时间段" prop="timeSlots">
                        <el-select v-model="specialForm.timeSlots" multiple placeholder="请选择时间段" style="width: 100%">
                            <el-option
                                v-for="timeSlot in availableTimeSlots"
                                :key="timeSlot.id"
                                :label="timeSlot.timeRange"
                                :value="timeSlot.id"
                                :disabled="timeSlot.isBooked">
                                <span style="float: left">{{ timeSlot.timeRange }}</span>
                                <span v-if="timeSlot.isBooked" style="float: right; color: #F56C6C; font-size: 13px">已预约</span>
                                <span v-else-if="timeSlot.isSpecial" style="float: right; color: #E6A23C; font-size: 13px">特殊安排</span>
                            </el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="用途" prop="purpose">
                        <el-select v-model="specialForm.purpose" placeholder="请选择用途" style="width: 100%">
                            <el-option label="校队训练" value="校队训练"></el-option>
                            <el-option label="体育课程" value="体育课程"></el-option>
                            <el-option label="比赛活动" value="比赛活动"></el-option>
                            <el-option label="维护保养" value="维护保养"></el-option>
                            <el-option label="临时关闭" value="临时关闭"></el-option>
                            <el-option label="其他" value="其他"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="备注" prop="remarks">
                        <el-input v-model="specialForm.remarks" type="textarea" placeholder="请输入备注说明"></el-input>
                    </el-form-item>
                    <el-form-item label="通知用户" prop="notifyUsers">
                        <el-switch v-model="specialForm.notifyUsers"></el-switch>
                        <span class="form-tip">设置为特殊场地后，是否通知已预约的用户</span>
                    </el-form-item>
                </el-form>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="specialDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="saveSpecialArrangement">确定</el-button>
                </span>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            venues: [], // 所有场馆列表
            specialArrangements: [],
            loading: false,
            total: 0,
            currentPage: 1,
            pageSize: 10,
            searchForm: {
                venueType: '',
                venueId: '',
                dateRange: []
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
            // 按类型分组的场馆
            venueGroups: [],
            // 特殊场地弹窗可见性
            specialDialogVisible: false,
            // 弹窗类型（add/edit）
            dialogType: 'add',
            // 特殊场地表单
            specialForm: {
                id: null,
                venueId: '',
                date: '',
                timeSlots: [],
                purpose: '',
                remarks: '',
                notifyUsers: true
            },
            // 表单验证规则
            specialRules: {
                venueId: [
                    { required: true, message: '请选择场地', trigger: 'change' }
                ],
                date: [
                    { required: true, message: '请选择日期', trigger: 'change' }
                ],
                timeSlots: [
                    { required: true, message: '请选择至少一个时间段', trigger: 'change' }
                ],
                purpose: [
                    { required: true, message: '请选择用途', trigger: 'change' }
                ]
            },
            // 可用时间段
            availableTimeSlots: [],
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
                    this.venues = response.data.data || [];
                    this.processVenueData();
                }
            } catch (error) {
                console.error('获取场地列表失败:', error);
                this.venues = [];
            }
        },
        
        // 处理场地数据
        processVenueData() {
            // 按类型分组场地
            this.venueGroups = this.venueTypes.map(type => {
                const venuesOfType = this.venues.filter(venue => venue.type === type.value);
                return {
                    label: type.label,
                    venues: venuesOfType
                };
            }).filter(group => group.venues.length > 0);
        },
        // 加载特殊场地数据
        async loadSpecialArrangementData() {
            this.loading = true;
            try {
                const params = {
                    page: this.pagination.currentPage,
                    size: this.pagination.pageSize
                };

                if (this.searchForm.venueType) {
                    params.venueType = this.searchForm.venueType;
                }
                if (this.searchForm.venueId) {
                    params.venueId = this.searchForm.venueId;
                }
                if (this.searchForm.dateRange && this.searchForm.dateRange.length === 2) {
                    params.startDate = this.searchForm.dateRange[0];
                    params.endDate = this.searchForm.dateRange[1];
                }

                const response = await axios.get(this.apiBaseUrl, { params });
                this.specialArrangements = response.data.records;
                this.pagination.total = response.data.total;
            } catch (error) {
                this.$message.error('加载特殊场地数据失败');
                console.error('加载特殊场地数据失败:', error);
            } finally {
                this.loading = false;
            }
        },
        // 搜索特殊场地安排
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
        // 根据条件过滤特殊场地安排
        filterSpecialArrangements() {
            let filteredList = [...this.specialArrangements];

            if (this.searchForm.venueType) {
                filteredList = filteredList.filter(item => {
                    return item.venueType === this.searchForm.venueType;
                });
            }

            if (this.searchForm.venueId) {
                filteredList = filteredList.filter(item => {
                    return item.venueId === this.searchForm.venueId;
                });
            }

            if (this.searchForm.dateRange && this.searchForm.dateRange.length === 2) {
                const startDate = this.searchForm.dateRange[0];
                const endDate = this.searchForm.dateRange[1];

                filteredList = filteredList.filter(item => {
                    return item.date >= startDate && item.date <= endDate;
                });
            }

            this.specialArrangements = filteredList;
            this.pagination.total = filteredList.length;
        },
        // 分页大小变化处理
        handleSizeChange(val) {
            this.pagination.pageSize = val;
            this.loadSpecialArrangementData();
        },
        // 页码变化处理
        handleCurrentChange(val) {
            this.pagination.currentPage = val;
            this.loadSpecialArrangementData();
        },
        // 显示添加特殊场地弹窗
        showAddSpecialDialog() {
            this.dialogType = 'add';
            this.specialForm = {
                id: null,
                venueId: '',
                date: '',
                timeSlots: [],
                purpose: '',
                remarks: '',
                notifyUsers: true
            };
            this.availableTimeSlots = [];
            this.specialDialogVisible = true;

            // 在下一个事件循环中重置表单校验结果
            this.$nextTick(() => {
                this.$refs.specialForm && this.$refs.specialForm.clearValidate();
            });
        },
        // 处理场地变化
        handleVenueChange() {
            if (this.specialForm.venueId && this.specialForm.date) {
                this.loadAvailableTimeSlots();
            }
        },
        // 加载可用时间段
        async loadAvailableTimeSlots() {
            if (!this.specialForm.venueId || !this.specialForm.date) {
                this.availableTimeSlots = [];
                return;
            }

            this.loading = true;
            try {
                const response = await axios.get(`${this.apiBaseUrl}/time-slots`, {
                    params: {
                        venueId: this.specialForm.venueId,
                        date: this.specialForm.date
                    }
                });
                if (response.data.code === 200) {
                    this.availableTimeSlots = response.data.data || [];
                } else {
                    this.$message.error(response.data.message || '加载时间段数据失败');
                }
            } catch (error) {
                this.$message.error('加载时间段数据失败');
                console.error('加载时间段数据失败:', error);
            } finally {
                this.loading = false;
            }
        },
        // 编辑特殊场地安排
        editSpecialArrangement(arrangement) {
            this.dialogType = 'edit';

            // 将时间段转换为id数组
            let timeSlots = [];
            if (arrangement.timeRange === '全天') {
                timeSlots = ['all-day'];
            } else {
                // 这里简化处理，实际应该根据时间范围找到对应的时间段id
                const parts = arrangement.timeRange.split(' - ');
                if (parts.length === 2) {
                    const startTime = parts[0];
                    const [hours, minutes] = startTime.split(':').map(Number);
                    timeSlots = [`${hours}-${minutes}`];
                }
            }

            this.specialForm = {
                id: arrangement.id,
                venueId: arrangement.venueId,
                date: arrangement.date,
                timeSlots: timeSlots,
                purpose: arrangement.purpose,
                remarks: arrangement.remarks || '',
                notifyUsers: true
            };

            // 加载可用时间段
            this.loadAvailableTimeSlots();

            this.specialDialogVisible = true;

            // 在下一个事件循环中重置表单校验结果
            this.$nextTick(() => {
                this.$refs.specialForm && this.$refs.specialForm.clearValidate();
            });
        },
        // 保存特殊场地安排
        async saveSpecialArrangement() {
            this.$refs.specialForm.validate(async valid => {
                if (valid) {
                    this.loading = true;
                    try {
                        const requestData = {
                            venueId: this.specialForm.venueId,
                            date: this.specialForm.date,
                            timeSlots: this.specialForm.timeSlots,
                            purpose: this.specialForm.purpose,
                            remarks: this.specialForm.remarks,
                            notifyUsers: this.specialForm.notifyUsers
                        };

                        if (this.dialogType === 'add') {
                            await axios.post(this.apiBaseUrl, requestData);
                            this.$message.success('添加特殊安排成功');
                        } else {
                            await axios.put(`${this.apiBaseUrl}/${this.specialForm.id}`, requestData);
                            this.$message.success('更新特殊安排成功');
                        }

                        this.specialDialogVisible = false;
                        this.loadSpecialArrangementData();
                    } catch (error) {
                        const errorMsg = error.response?.data?.message || '保存特殊安排失败';
                        this.$message.error(errorMsg);
                        console.error('保存特殊安排失败:', error);
                    } finally {
                        this.loading = false;
                    }
                }
            });
        },
        // 确认删除特殊安排
        confirmDeleteSpecial(arrangement) {
            this.$confirm(`确定要删除"${arrangement.venueName}"在"${arrangement.date}"的特殊安排吗？`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.deleteSpecialArrangement(arrangement);
            }).catch(() => {
                // 取消删除操作
            });
        },
        // 删除特殊安排
        async deleteSpecialArrangement(arrangement) {
            this.loading = true;
            try {
                await axios.delete(`${this.apiBaseUrl}/${arrangement.id}`);
                this.$message.success('删除特殊安排成功');
                this.loadSpecialArrangementData();
            } catch (error) {
                const errorMsg = error.response?.data?.message || '删除特殊安排失败';
                this.$message.error(errorMsg);
                console.error('删除特殊安排失败:', error);
            } finally {
                this.loading = false;
            }
        }
    }
};