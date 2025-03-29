// 失约处理组件 - 管理员
const AdminNoshowsComponent = {
    template: `
        <div>
            <div class="table-operations">
                <el-button type="primary" @click="fetchData">刷新</el-button>
            </div>
            
            <el-table :data="noshows" border style="width: 100%">
                <el-table-column prop="id" label="ID" width="80"></el-table-column>
                <el-table-column prop="venue.name" label="场地名称"></el-table-column>
                <el-table-column prop="cardNumber" label="一卡通号"></el-table-column>
                <el-table-column prop="startTime" label="预约时间">
                    <template slot-scope="scope">
                        {{ formatDateTime(scope.row.startTime) }} - {{ formatDateTime(scope.row.endTime) }}
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态">
                    <template slot-scope="scope">
                        <el-tag type="danger">未到场</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="200">
                    <template slot-scope="scope">
                        <el-button size="mini" @click="handleProcess(scope.row)">处理</el-button>
                    </template>
                </el-table-column>
            </el-table>
            
            <!-- 失约处理对话框 -->
            <el-dialog title="失约处理" :visible.sync="dialogVisible" width="30%">
                <el-form :model="form" ref="form" label-width="100px">
                    <el-form-item label="一卡通号">
                        <el-input v-model="form.cardNumber" disabled></el-input>
                    </el-form-item>
                    <el-form-item label="场地">
                        <el-input v-model="form.venueName" disabled></el-input>
                    </el-form-item>
                    <el-form-item label="预约时间">
                        <el-input v-model="form.reservationTime" disabled></el-input>
                    </el-form-item>
                    <el-form-item label="处理方式" prop="actionType">
                        <el-select v-model="form.actionType" placeholder="请选择处理方式" style="width: 100%">
                            <el-option label="违约警告" value="WARNING"></el-option>
                            <el-option label="暂停预约权限1周" value="SUSPEND_1_WEEK"></el-option>
                            <el-option label="暂停预约权限1个月" value="SUSPEND_1_MONTH"></el-option>
                            <el-option label="暂停预约权限3个月" value="SUSPEND_3_MONTHS"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="备注" prop="remark">
                        <el-input type="textarea" v-model="form.remark"></el-input>
                    </el-form-item>
                </el-form>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitForm">确认</el-button>
                </span>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            noshows: [],
            dialogVisible: false,
            form: {
                id: null,
                cardNumber: '',
                venueName: '',
                reservationTime: '',
                actionType: '',
                remark: ''
            }
        };
    },
    created() {
        this.fetchData();
    },
    methods: {
        formatDateTime(dateStr) {
            if (!dateStr) return '';
            const date = new Date(dateStr);
            return date.toLocaleString('zh-CN', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
            });
        },
        fetchData() {
            // 实际应该调用API
            axios.get('/reservations/noshows')
                .then(response => {
                    this.noshows = response.data;
                })
                .catch(error => {
                    console.error('获取失约记录失败:', error);
                    this.$message.error('获取失约记录失败');

                    // 模拟数据
                    this.noshows = [
                        {
                            id: 6,
                            venue: { id: 1, name: '篮球场1号' },
                            cardNumber: '2021001004',
                            startTime: '2023-05-19T14:00:00',
                            endTime: '2023-05-19T16:00:00',
                            status: 'NO_SHOW'
                        },
                        {
                            id: 7,
                            venue: { id: 2, name: '羽毛球场1号' },
                            cardNumber: '2021001005',
                            startTime: '2023-05-19T16:00:00',
                            endTime: '2023-05-19T18:00:00',
                            status: 'NO_SHOW'
                        }
                    ];
                });
        },
        handleProcess(row) {
            this.form = {
                id: row.id,
                cardNumber: row.cardNumber,
                venueName: row.venue.name,
                reservationTime: `${this.formatDateTime(row.startTime)} - ${this.formatDateTime(row.endTime)}`,
                actionType: '',
                remark: ''
            };
            this.dialogVisible = true;
        },
        submitForm() {
            if (!this.form.actionType) {
                this.$message.warning('请选择处理方式');
                return;
            }

            // 实际应该调用API
            const data = {
                reservationId: this.form.id,
                actionType: this.form.actionType,
                remark: this.form.remark
            };

            axios.post('/reservations/noshow/process', data)
                .then(response => {
                    this.$message.success('处理成功');
                    this.dialogVisible = false;
                    this.fetchData();
                })
                .catch(error => {
                    console.error('处理失约记录失败:', error);
                    this.$message.error('处理失约记录失败');

                    // 模拟操作成功
                    this.noshows = this.noshows.filter(item => item.id !== this.form.id);
                    this.dialogVisible = false;
                    this.$message.success('处理成功');
                });
        }
    }
};

// 场馆公告组件
const AdminAnnouncementsComponent = {
    template: `
        <div>
            <div class="table-operations">
                <el-button type="primary" @click="showAddDialog">新建公告</el-button>
                <el-button type="danger" :disabled="!multipleSelection.length" @click="handleBatchDelete">批量删除</el-button>
            </div>
            
            <el-table :data="announcements" border style="width: 100%" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55"></el-table-column>
                <el-table-column prop="id" label="ID" width="80"></el-table-column>
                <el-table-column prop="title" label="标题"></el-table-column>
                <el-table-column prop="publishTime" label="发布时间">
                    <template slot-scope="scope">
                        {{ new Date(scope.row.publishTime).toLocaleString() }}
                    </template>
                </el-table-column>
                <el-table-column prop="expireTime" label="过期时间">
                    <template slot-scope="scope">
                        {{ scope.row.expireTime ? new Date(scope.row.expireTime).toLocaleString() : '永久有效' }}
                    </template>
                </el-table-column>
                <el-table-column prop="active" label="状态">
                    <template slot-scope="scope">
                        <el-tag :type="scope.row.active ? 'success' : 'info'">
                            {{ scope.row.active ? '活跃' : '已下线' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="200">
                    <template slot-scope="scope">
                        <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
                        <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            
            <!-- 新建/编辑公告对话框 -->
            <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="50%">
                <el-form :model="form" :rules="rules" ref="form" label-width="100px">
                    <el-form-item label="标题" prop="title">
                        <el-input v-model="form.title"></el-input>
                    </el-form-item>
                    <el-form-item label="内容" prop="content">
                        <el-input type="textarea" :rows="5" v-model="form.content"></el-input>
                    </el-form-item>
                    <el-form-item label="过期时间" prop="expireTime">
                        <el-date-picker
                            v-model="form.expireTime"
                            type="datetime"
                            placeholder="选择过期时间"
                            style="width: 100%">
                        </el-date-picker>
                    </el-form-item>
                    <el-form-item label="状态" prop="active">
                        <el-switch v-model="form.active"></el-switch>
                    </el-form-item>
                </el-form>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitForm">确定</el-button>
                </span>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            announcements: [],
            multipleSelection: [],
            dialogVisible: false,
            dialogTitle: '新建公告',
            isEdit: false,
            form: this.getInitialForm(),
            rules: {
                title: [
                    { required: true, message: '请输入公告标题', trigger: 'blur' }
                ],
                content: [
                    { required: true, message: '请输入公告内容', trigger: 'blur' }
                ]
            }
        };
    },
    created() {
        this.fetchData();
    },
    methods: {
        getInitialForm() {
            return {
                id: null,
                title: '',
                content: '',
                publishTime: new Date(),
                expireTime: null,
                active: true
            };
        },
        fetchData() {
            // 实际应该调用API
            axios.get('/announcements')
                .then(response => {
                    this.announcements = response.data;
                })
                .catch(error => {
                    console.error('获取公告列表失败:', error);
                    this.$message.error('获取公告列表失败');

                    // 模拟数据
                    this.announcements = [
                        {
                            id: 1,
                            title: '体育馆端午节开放公告',
                            content: '端午节期间（6月10日-6月12日）体育馆开放时间调整为上午9:00-下午5:00。',
                            publishTime: '2023-06-01T09:00:00',
                            expireTime: '2023-06-13T00:00:00',
                            active: true
                        },
                        {
                            id: 2,
                            title: '游泳池维修公告',
                            content: '游泳池因设备维修，将于6月15日-6月20日暂停开放，给您带来的不便敬请谅解。',
                            publishTime: '2023-06-05T10:00:00',
                            expireTime: '2023-06-21T00:00:00',
                            active: true
                        }
                    ];
                });
        },
        handleSelectionChange(val) {
            this.multipleSelection = val;
        },
        showAddDialog() {
            this.dialogTitle = '新建公告';
            this.isEdit = false;
            this.form = this.getInitialForm();
            this.dialogVisible = true;
        },
        handleEdit(row) {
            this.dialogTitle = '编辑公告';
            this.isEdit = true;
            this.form = {
                id: row.id,
                title: row.title,
                content: row.content,
                publishTime: new Date(row.publishTime),
                expireTime: row.expireTime ? new Date(row.expireTime) : null,
                active: row.active
            };
            this.dialogVisible = true;
        },
        handleDelete(row) {
            this.$confirm('确认删除该公告?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                // 实际应该调用API
                axios.delete(`/announcements/${row.id}`)
                    .then(() => {
                        this.$message.success('删除成功');
                        this.fetchData();
                    })
                    .catch(error => {
                        console.error('删除公告失败:', error);
                        this.$message.error('删除公告失败');

                        // 模拟操作成功
                        this.announcements = this.announcements.filter(item => item.id !== row.id);
                        this.$message.success('删除成功');
                    });
            }).catch(() => {});
        },
        handleBatchDelete() {
            this.$confirm(`确认删除选中的 ${this.multipleSelection.length} 个公告?`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                // 实际应该批量调用API
                const ids = this.multipleSelection.map(item => item.id);

                // 模拟操作成功
                this.announcements = this.announcements.filter(item => !ids.includes(item.id));
                this.$message.success('批量删除成功');
            }).catch(() => {});
        },
        submitForm() {
            this.$refs.form.validate((valid) => {
                if (valid) {
                    const data = {
                        id: this.form.id,
                        title: this.form.title,
                        content: this.form.content,
                        publishTime: this.form.publishTime.toISOString(),
                        expireTime: this.form.expireTime ? this.form.expireTime.toISOString() : null,
                        active: this.form.active
                    };

                    if (this.isEdit) {
                        // 编辑公告
                        axios.put(`/announcements/${this.form.id}`, data)
                            .then(response => {
                                this.$message.success('更新成功');
                                this.dialogVisible = false;
                                this.fetchData();
                            })
                            .catch(error => {
                                console.error('更新公告失败:', error);
                                this.$message.error('更新公告失败');

                                // 模拟操作成功
                                const index = this.announcements.findIndex(a => a.id === this.form.id);
                                if (index !== -1) {
                                    this.announcements.splice(index, 1, data);
                                }
                                this.dialogVisible = false;
                                this.$message.success('更新成功');
                            });
                    } else {
                        // 新建公告
                        axios.post('/announcements', data)
                            .then(response => {
                                this.$message.success('创建成功');
                                this.dialogVisible = false;
                                this.fetchData();
                            })
                            .catch(error => {
                                console.error('创建公告失败:', error);
                                this.$message.error('创建公告失败');

                                // 模拟操作成功
                                data.id = this.announcements.length > 0 ? Math.max(...this.announcements.map(a => a.id)) + 1 : 1;
                                this.announcements.push(data);
                                this.dialogVisible = false;
                                this.$message.success('创建成功');
                            });
                    }
                }
            });
        }
    }
};