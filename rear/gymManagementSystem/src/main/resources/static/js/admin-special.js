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
            // 搜索表单
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
            // 所有场馆列表
            venues: [],
            // 特殊场地安排列表
            specialArrangements: [],
            // 加载状态
            loading: false,
            // 分页信息
            pagination: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },
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
            availableTimeSlots: []
        };
    },
    computed: {
        // 按类型筛选的场馆列表
        filteredVenues() {
            if (!this.searchForm.venueType) {
                return this.venues;
            }

            // 将英文类型转换为对应的中文类型名
            const typeMap = {
                'basketball': '篮球场',
                'football': '足球场',
                'badminton': '羽毛球场',
                'tennis': '网球场',
                'swimming': '游泳池',
                'table_tennis': '乒乓球室'
            };

            return this.venues.filter(venue => {
                return venue.type === typeMap[this.searchForm.venueType];
            });
        },
        // 按类型分组的场馆列表（用于下拉选择）
        venueGroups() {
            const groups = [];

            // 场馆类型分组
            this.venueTypes.forEach(type => {
                const venuesOfType = this.venues.filter(venue => {
                    return venue.type === type.label;
                });

                if (venuesOfType.length > 0) {
                    groups.push({
                        label: type.label,
                        venues: venuesOfType
                    });
                }
            });

            return groups;
        }
    },
    created() {
        // 加载场馆数据
        this.loadVenueData();

        // 初始加载特殊场地数据
        this.loadSpecialArrangementData();
    },
    methods: {
        // 加载场馆数据
        loadVenueData() {
            this.loading = true;

            // 模拟后端API请求
            setTimeout(() => {
                // 模拟数据，实际项目中应通过API获取
                this.venues = [
                    { id: 1, name: '篮球场A', type: '篮球场', location: '体育馆一楼' },
                    { id: 2, name: '篮球场B', type: '篮球场', location: '体育馆一楼' },
                    { id: 3, name: '足球场A', type: '足球场', location: '户外运动区' },
                    { id: 4, name: '羽毛球场A', type: '羽毛球场', location: '体育馆二楼' },
                    { id: 5, name: '羽毛球场B', type: '羽毛球场', location: '体育馆二楼' },
                    { id: 6, name: '羽毛球场C', type: '羽毛球场', location: '体育馆二楼' },
                    { id: 7, name: '网球场A', type: '网球场', location: '户外运动区' },
                    { id: 8, name: '游泳池', type: '游泳池', location: '体育馆负一楼' },
                    { id: 9, name: '乒乓球室A', type: '乒乓球室', location: '体育馆三楼' },
                    { id: 10, name: '乒乓球室B', type: '乒乓球室', location: '体育馆三楼' }
                ];

                this.loading = false;
            }, 300);
        },
        // 加载特殊场地数据
        loadSpecialArrangementData() {
            this.loading = true;

            // 模拟后端API请求
            setTimeout(() => {
                // 模拟数据，实际项目中应通过API获取
                const now = new Date();
                const today = this.formatDate(now);
                const tomorrow = this.formatDate(new Date(now.getTime() + 86400000));
                const nextWeek = this.formatDate(new Date(now.getTime() + 7 * 86400000));

                this.specialArrangements = [
                    { id: 1, venueId: 1, venueName: '篮球场A', venueType: '篮球场', date: today, timeRange: '14:00 - 16:00', purpose: '校队训练', remarks: '校篮球队例行训练', createdBy: '管理员', createdTime: '2023-03-25 10:00:00' },
                    { id: 2, venueId: 3, venueName: '足球场A', venueType: '足球场', date: tomorrow, timeRange: '09:00 - 12:00', purpose: '比赛活动', remarks: '院系足球联赛', createdBy: '管理员', createdTime: '2023-03-26 14:30:00' },
                    { id: 3, venueId: 4, venueName: '羽毛球场A', venueType: '羽毛球场', date: nextWeek, timeRange: '18:00 - 20:00', purpose: '体育课程', remarks: '羽毛球选修课', createdBy: '管理员', createdTime: '2023-03-27 09:15:00' },
                    { id: 4, venueId: 7, venueName: '网球场A', venueType: '网球场', date: tomorrow, timeRange: '16:00 - 18:00', purpose: '维护保养', remarks: '场地检修', createdBy: '管理员', createdTime: '2023-03-28 11:20:00' },
                    { id: 5, venueId: 8, venueName: '游泳池', venueType: '游泳池', date: today, timeRange: '全天', purpose: '临时关闭', remarks: '水质检测', createdBy: '管理员', createdTime: '2023-03-29 08:00:00' }
                ];

                // 根据筛选条件过滤
                this.filterSpecialArrangements();

                this.pagination.total = this.specialArrangements.length;
                this.loading = false;
            }, 500);
        },
        // 格式化日期
        formatDate(date) {
            const year = date.getFullYear();
            const month = (date.getMonth() + 1).toString().padStart(2, '0');
            const day = date.getDate().toString().padStart(2, '0');
            return `${year}-${month}-${day}`;
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
                // 将英文类型转换为对应的中文类型名
                const typeMap = {
                    'basketball': '篮球场',
                    'football': '足球场',
                    'badminton': '羽毛球场',
                    'tennis': '网球场',
                    'swimming': '游泳池',
                    'table_tennis': '乒乓球室'
                };

                filteredList = filteredList.filter(item => {
                    return item.venueType === typeMap[this.searchForm.venueType];
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
        loadAvailableTimeSlots() {
            if (!this.specialForm.venueId || !this.specialForm.date) {
                this.availableTimeSlots = [];
                return;
            }

            this.loading = true;

            // 模拟加载时间段
            setTimeout(() => {
                const slots = [];
                const startHour = 8; // 早上8点开始
                const endHour = 22;  // 晚上10点结束

                for (let hour = startHour; hour < endHour; hour++) {
                    // 创建整点和半点的时间段
                    for (let minute of [0, 30]) {
                        const startTime = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
                        const endHourMin = minute === 0 ? `${hour}:30` : `${hour + 1}:00`;
                        const endTime = endHourMin.split(':').map(num => num.toString().padStart(2, '0')).join(':');
                        const timeRange = `${startTime} - ${endTime}`;

                        // 随机生成预约和特殊安排状态，实际应从后端获取
                        const isBooked = Math.random() < 0.3;
                        const isSpecial = Math.random() < 0.2;

                        slots.push({
                            id: `${hour}-${minute}`,
                            timeRange,
                            isBooked,
                            isSpecial
                        });
                    }
                }

                // 添加全天选项
                slots.unshift({
                    id: 'all-day',
                    timeRange: '全天',
                    isBooked: false,
                    isSpecial: false
                });

                this.availableTimeSlots = slots;
                this.loading = false;
            }, 300);
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
        saveSpecialArrangement() {
            this.$refs.specialForm.validate(valid => {
                if (valid) {
                    this.loading = true;

                    // 模拟保存请求
                    setTimeout(() => {
                        // 获取场地信息
                        const venue = this.venues.find(v => v.id === this.specialForm.venueId);

                        // 获取时间段文本
                        let timeRange;
                        if (this.specialForm.timeSlots.includes('all-day')) {
                            timeRange = '全天';
                        } else {
                            // 获取选中的时间段并排序
                            const selectedSlots = this.availableTimeSlots.filter(slot =>
                                this.specialForm.timeSlots.includes(slot.id)
                            );

                            // 如果只选了一个时间段
                            if (selectedSlots.length === 1) {
                                timeRange = selectedSlots[0].timeRange;
                            } else {
                                // 对于多个时间段，合并显示
                                // 简化处理，实际应合并连续的时间段
                                const startTime = selectedSlots[0].timeRange.split(' - ')[0];
                                const endTime = selectedSlots[selectedSlots.length - 1].timeRange.split(' - ')[1];
                                timeRange = `${startTime} - ${endTime}`;
                            }
                        }

                        const arrangement = {
                            id: this.specialForm.id || this.specialArrangements.length + 1,
                            venueId: this.specialForm.venueId,
                            venueName: venue.name,
                            venueType: venue.type,
                            date: this.specialForm.date,
                            timeRange: timeRange,
                            purpose: this.specialForm.purpose,
                            remarks: this.specialForm.remarks,
                            createdBy: '管理员',
                            createdTime: new Date().toLocaleString()
                        };

                        if (this.dialogType === 'add') {
                            // 添加新安排
                            this.specialArrangements.unshift(arrangement);
                            this.$message.success('添加特殊安排成功');
                        } else {
                            // 更新现有安排
                            const index = this.specialArrangements.findIndex(a => a.id === this.specialForm.id);
                            if (index !== -1) {
                                this.specialArrangements.splice(index, 1, arrangement);
                                this.$message.success('更新特殊安排成功');
                            }
                        }

                        this.specialDialogVisible = false;
                        this.loading = false;
                    }, 500);
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
        deleteSpecialArrangement(arrangement) {
            this.loading = true;

            // 模拟删除请求
            setTimeout(() => {
                const index = this.specialArrangements.findIndex(a => a.id === arrangement.id);
                if (index !== -1) {
                    this.specialArrangements.splice(index, 1);
                    this.$message.success('删除特殊安排成功');
                }
                this.loading = false;
            }, 500);
        }
    }
};