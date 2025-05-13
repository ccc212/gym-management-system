// 场馆公告组件
const AdminAnnouncementsComponent = {
    template: `
        <div class="admin-announcements-container">
            <el-card>
                <div slot="header" class="card-header">
                    <h2>场馆公告</h2>
                    <el-button type="primary" size="small" @click="createAnnouncement">
                        <i class="el-icon-plus"></i> 发布新公告
                    </el-button>
                </div>
                
                <!-- 搜索筛选区域 -->
                <el-form :inline="true" class="search-form" size="small">
                    <el-form-item label="公告类型">
                        <el-select v-model="searchForm.type" placeholder="选择公告类型" clearable @change="searchAnnouncements">
                            <el-option v-for="item in announcementTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="发布状态">
                        <el-select v-model="searchForm.status" placeholder="选择发布状态" clearable @change="searchAnnouncements">
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
                            @change="searchAnnouncements">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item label="关键词">
                        <el-input v-model="searchForm.keyword" placeholder="标题或内容关键词" @keyup.enter.native="searchAnnouncements"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="searchAnnouncements">查询</el-button>
                        <el-button @click="resetSearch">重置</el-button>
                    </el-form-item>
                </el-form>
                
                <!-- 公告列表 -->
                <el-table 
                    v-loading="loading" 
                    :data="announcementsList" 
                    style="width: 100%"
                    :header-cell-style="{background:'#f5f7fa', color:'#606266'}"
                    border>
                    <el-table-column prop="id" label="ID" width="80"></el-table-column>
                    <el-table-column prop="title" label="标题" min-width="180">
                        <template slot-scope="scope">
                            <el-tooltip :content="scope.row.title" placement="top" :disabled="scope.row.title.length < 20">
                                <span>{{ scope.row.title.length > 20 ? scope.row.title.substring(0, 20) + '...' : scope.row.title }}</span>
                            </el-tooltip>
                        </template>
                    </el-table-column>
                    <el-table-column prop="type" label="类型" width="120">
                        <template slot-scope="scope">
                            {{ getAnnouncementTypeText(scope.row.type) }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="publishDate" label="发布日期" width="120"></el-table-column>
                    <el-table-column prop="endDate" label="结束日期" width="120"></el-table-column>
                    <el-table-column prop="status" label="状态" width="100">
                        <template slot-scope="scope">
                            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="author" label="发布人" width="100"></el-table-column>
                    <el-table-column prop="createdTime" label="创建时间" width="180"></el-table-column>
                    <el-table-column label="操作" width="240">
                        <template slot-scope="scope">
                            <el-button size="mini" @click="viewAnnouncement(scope.row)">查看</el-button>
                            <el-button type="primary" size="mini" @click="editAnnouncement(scope.row)">编辑</el-button>
                            <el-button 
                                type="danger" 
                                size="mini" 
                                @click="deleteAnnouncement(scope.row)">删除</el-button>
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
            
            <!-- 新增/编辑公告弹窗 -->
            <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="650px">
                <el-form :model="announcementForm" :rules="formRules" ref="announcementForm" label-width="100px">
                    <el-form-item label="公告标题" prop="title">
                        <el-input v-model="announcementForm.title" placeholder="请输入公告标题"></el-input>
                    </el-form-item>
                    <el-form-item label="公告类型" prop="type">
                        <el-select v-model="announcementForm.type" placeholder="请选择公告类型" style="width: 100%">
                            <el-option v-for="item in announcementTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="发布日期" prop="publishDate">
                        <el-date-picker v-model="announcementForm.publishDate" type="date" placeholder="选择发布日期" value-format="yyyy-MM-dd" style="width: 100%"></el-date-picker>
                    </el-form-item>
                    <el-form-item label="结束日期" prop="endDate">
                        <el-date-picker v-model="announcementForm.endDate" type="date" placeholder="选择结束日期" value-format="yyyy-MM-dd" style="width: 100%"></el-date-picker>
                    </el-form-item>
                    <el-form-item label="发布状态" prop="status">
                        <el-select v-model="announcementForm.status" placeholder="请选择发布状态" style="width: 100%">
                            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="公告内容" prop="content">
                        <el-input type="textarea" v-model="announcementForm.content" :rows="8" placeholder="请输入公告内容"></el-input>
                    </el-form-item>
                </el-form>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="saveAnnouncement">确定</el-button>
                </span>
            </el-dialog>
            
            <!-- 查看公告详情弹窗 -->
            <el-dialog title="公告详情" :visible.sync="detailDialogVisible" width="700px">
                <div v-if="currentAnnouncement" class="announcement-detail">
                    <h2 class="announcement-title">{{ currentAnnouncement.title }}</h2>
                    <div class="announcement-meta">
                        <span class="announcement-type">{{ getAnnouncementTypeText(currentAnnouncement.type) }}</span>
                        <span>发布日期: {{ currentAnnouncement.publishDate }}</span>
                        <span>结束日期: {{ currentAnnouncement.endDate }}</span>
                        <span>状态: {{ getStatusText(currentAnnouncement.status) }}</span>
                        <span>发布人: {{ currentAnnouncement.author }}</span>
                    </div>
                    <div class="announcement-content" v-html="formatContent(currentAnnouncement.content)"></div>
                </div>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="detailDialogVisible = false">关闭</el-button>
                    <el-button type="primary" @click="editAnnouncement(currentAnnouncement)">编辑</el-button>
                </span>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            // 搜索表单
            searchForm: {
                type: '',
                status: '',
                dateRange: [],
                keyword: ''
            },
            // 公告类型选项
            announcementTypes: [
                { value: 'NOTICE', label: '通知公告' },
                { value: 'EVENT', label: '活动信息' },
                { value: 'MAINTENANCE', label: '维护信息' },
                { value: 'RULE', label: '规章制度' },
                { value: 'OTHER', label: '其他' }
            ],
            // 状态选项
            statusOptions: [
                { value: 'DRAFT', label: '草稿' },
                { value: 'PUBLISHED', label: '已发布' },
                { value: 'EXPIRED', label: '已过期' }
            ],
            // 公告列表
            announcementsList: [],
            // 加载状态
            loading: false,
            // 分页信息
            pagination: {
                currentPage: 1,
                pageSize: 10,
                total: 0
            },
            // 弹窗控制
            dialogVisible: false,
            detailDialogVisible: false,
            // 表单数据
            announcementForm: {
                id: null,
                title: '',
                type: 'NOTICE',
                publishDate: '',
                endDate: '',
                content: '',
                status: 'DRAFT',
                author: ''
            },
            // 表单验证规则
            formRules: {
                title: [
                    { required: true, message: '请输入公告标题', trigger: 'blur' },
                    { min: 2, max: 100, message: '标题长度在2到100个字符之间', trigger: 'blur' }
                ],
                type: [
                    { required: true, message: '请选择公告类型', trigger: 'change' }
                ],
                publishDate: [
                    { required: true, message: '请选择发布日期', trigger: 'change' }
                ],
                content: [
                    { required: true, message: '请输入公告内容', trigger: 'blur' }
                ]
            },
            // 当前查看/编辑的公告
            currentAnnouncement: null,
            // 是否是编辑模式
            isEdit: false
        };
    },
    computed: {
        // 弹窗标题
        dialogTitle() {
            return this.isEdit ? '编辑公告' : '发布新公告';
        }
    },
    created() {
        // 初始加载公告数据
        this.loadAnnouncementData();
    },
    methods: {
        // 加载公告数据
        loadAnnouncementData() {
            this.loading = true;

            // 模拟后端API请求
            setTimeout(() => {
                // 模拟数据
                const now = new Date();
                const yesterday = new Date(now);
                yesterday.setDate(now.getDate() - 1);

                const next10Days = new Date(now);
                next10Days.setDate(now.getDate() + 10);

                const next30Days = new Date(now);
                next30Days.setDate(now.getDate() + 30);

                this.announcementsList = [
                    { id: 1001, title: '五一假期场馆开放时间调整通知', type: 'NOTICE', publishDate: this.formatDate(yesterday), endDate: this.formatDate(next10Days), status: 'PUBLISHED', author: '管理员', createdTime: yesterday.toLocaleString(), content: '尊敬的用户：\n\n鉴于即将到来的五一劳动节假期，我们的场馆开放时间将做如下调整：\n\n1. 5月1日至5月3日：上午9:00至晚上22:00\n2. 5月4日至5月5日：上午8:00至晚上23:00\n\n请提前安排您的健身计划。祝您假期愉快！' },
                    { id: 1002, title: '游泳池维护通知', type: 'MAINTENANCE', publishDate: this.formatDate(now), endDate: this.formatDate(next10Days), status: 'PUBLISHED', author: '管理员', createdTime: now.toLocaleString(), content: '尊敬的用户：\n\n我们的游泳池将于5月8日至5月10日进行水质维护和设备检修，期间游泳池将暂停开放。\n\n给您带来的不便，敬请谅解。' },
                    { id: 1003, title: '篮球友谊赛活动', type: 'EVENT', publishDate: this.formatDate(now), endDate: this.formatDate(next30Days), status: 'DRAFT', author: '管理员', createdTime: now.toLocaleString(), content: '【篮球友谊赛】\n\n时间：5月15日 周六 14:00-17:00\n地点：篮球场A\n\n欢迎各位篮球爱好者参加，请提前报名！\n\n报名方式：前台登记或线上预约' },
                    { id: 1004, title: '场馆使用规章制度', type: 'RULE', publishDate: this.formatDate(yesterday), endDate: this.formatDate(next30Days), status: 'PUBLISHED', author: '管理员', createdTime: yesterday.toLocaleString(), content: '为了维护良好的运动环境，保障所有用户的健身体验，特制定以下规章制度：\n\n1. 入场须出示有效会员卡或预约凭证\n2. 请穿着合适的运动装备\n3. 爱护场馆设施，损坏需赔偿\n4. 禁止在场馆内吸烟、饮酒\n5. 请保持场地清洁，垃圾入箱\n\n感谢您的配合！' }
                ];

                // 根据筛选条件过滤
                this.filterAnnouncements();

                this.pagination.total = this.announcementsList.length;
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
        // 获取公告类型文本
        getAnnouncementTypeText(type) {
            const typeMap = {
                'NOTICE': '通知公告',
                'EVENT': '活动信息',
                'MAINTENANCE': '维护信息',
                'RULE': '规章制度',
                'OTHER': '其他'
            };
            return typeMap[type] || '未知';
        },
        // 获取状态文本
        getStatusText(status) {
            const statusMap = {
                'DRAFT': '草稿',
                'PUBLISHED': '已发布',
                'EXPIRED': '已过期'
            };
            return statusMap[status] || '未知';
        },
        // 获取状态类型
        getStatusType(status) {
            const typeMap = {
                'DRAFT': 'info',
                'PUBLISHED': 'success',
                'EXPIRED': 'warning'
            };
            return typeMap[status] || 'info';
        },
        // 搜索公告
        searchAnnouncements() {
            this.pagination.currentPage = 1;
            this.loadAnnouncementData();
        },
        // 重置搜索条件
        resetSearch() {
            this.searchForm = {
                type: '',
                status: '',
                dateRange: [],
                keyword: ''
            };
            this.searchAnnouncements();
        },
        // 根据条件过滤公告
        filterAnnouncements() {
            let filteredList = [...this.announcementsList];

            if (this.searchForm.type) {
                filteredList = filteredList.filter(item => {
                    return item.type === this.searchForm.type;
                });
            }

            if (this.searchForm.status) {
                filteredList = filteredList.filter(item => {
                    return item.status === this.searchForm.status;
                });
            }

            if (this.searchForm.dateRange && this.searchForm.dateRange.length === 2) {
                const startDate = this.searchForm.dateRange[0];
                const endDate = this.searchForm.dateRange[1];

                filteredList = filteredList.filter(item => {
                    return (item.publishDate >= startDate && item.publishDate <= endDate) ||
                        (item.endDate >= startDate && item.endDate <= endDate);
                });
            }

            if (this.searchForm.keyword) {
                const keyword = this.searchForm.keyword.toLowerCase();
                filteredList = filteredList.filter(item => {
                    return item.title.toLowerCase().includes(keyword) ||
                        item.content.toLowerCase().includes(keyword);
                });
            }

            this.announcementsList = filteredList;
            this.pagination.total = filteredList.length;
        },
        // 分页大小变化处理
        handleSizeChange(val) {
            this.pagination.pageSize = val;
            this.loadAnnouncementData();
        },
        // 页码变化处理
        handleCurrentChange(val) {
            this.pagination.currentPage = val;
            this.loadAnnouncementData();
        },
        // 创建新公告
        createAnnouncement() {
            this.isEdit = false;
            const today = new Date();

            this.announcementForm = {
                id: null,
                title: '',
                type: 'NOTICE',
                publishDate: this.formatDate(today),
                endDate: '',
                content: '',
                status: 'DRAFT',
                author: '管理员'
            };

            this.dialogVisible = true;

            // 在下一次DOM更新循环后，重置表单验证
            this.$nextTick(() => {
                if (this.$refs.announcementForm) {
                    this.$refs.announcementForm.clearValidate();
                }
            });
        },
        // 编辑公告
        editAnnouncement(announcement) {
            this.isEdit = true;
            this.announcementForm = JSON.parse(JSON.stringify(announcement));
            this.dialogVisible = true;

            // 确保详情弹窗关闭
            this.detailDialogVisible = false;

            // 在下一次DOM更新循环后，重置表单验证
            this.$nextTick(() => {
                if (this.$refs.announcementForm) {
                    this.$refs.announcementForm.clearValidate();
                }
            });
        },
        // 保存公告
        saveAnnouncement() {
            this.$refs.announcementForm.validate(valid => {
                if (valid) {
                    this.loading = true;

                    // 模拟保存请求
                    setTimeout(() => {
                        if (this.isEdit) {
                            // 更新现有公告
                            const index = this.announcementsList.findIndex(item => item.id === this.announcementForm.id);
                            if (index !== -1) {
                                // 更新时间戳
                                const updatedAnnouncement = {
                                    ...this.announcementForm,
                                    updatedTime: new Date().toLocaleString()
                                };
                                this.announcementsList.splice(index, 1, updatedAnnouncement);
                                this.$message.success('公告更新成功');
                            }
                        } else {
                            // 创建新公告
                            const newAnnouncement = {
                                ...this.announcementForm,
                                id: Date.now(), // 模拟生成ID
                                createdTime: new Date().toLocaleString(),
                                author: '管理员'
                            };
                            this.announcementsList.unshift(newAnnouncement);
                            this.$message.success('公告创建成功');
                        }

                        this.dialogVisible = false;
                        this.loading = false;
                    }, 500);
                }
            });
        },
        // 删除公告
        deleteAnnouncement(announcement) {
            this.$confirm('确认删除该公告吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.loading = true;

                // 模拟删除请求
                setTimeout(() => {
                    const index = this.announcementsList.findIndex(item => item.id === announcement.id);
                    if (index !== -1) {
                        this.announcementsList.splice(index, 1);
                        this.$message.success('公告删除成功');
                    }
                    this.loading = false;
                }, 500);
            }).catch(() => {
                // 取消删除
            });
        },
        // 查看公告详情
        viewAnnouncement(announcement) {
            this.currentAnnouncement = JSON.parse(JSON.stringify(announcement));
            this.detailDialogVisible = true;
        },
        // 格式化内容，将换行符转换为HTML
        formatContent(content) {
            if (!content) return '';
            return content.replace(/\n/g, '<br>');
        }
    }
};