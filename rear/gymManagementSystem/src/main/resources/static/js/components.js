// 登录组件
const LoginComponent = {
    template: `
        <div class="login-container">
            <el-card class="login-card">
                <div slot="header" class="card-header">
                    <h2>体育场馆管理系统登录</h2>
                </div>
                <el-form :model="loginForm" :rules="rules" ref="loginForm" label-width="80px">
                    <el-form-item label="用户名" prop="username">
                        <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
                    </el-form-item>
                    <el-form-item label="密码" prop="password">
                        <el-input type="password" v-model="loginForm.password" placeholder="请输入密码"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="submitForm('loginForm')" style="width: 100%">登录</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
        </div>
    `,
    data() {
        return {
            loginForm: {
                username: '',
                password: ''
            },
            rules: {
                username: [
                    { required: true, message: '请输入用户名', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: '请输入密码', trigger: 'blur' }
                ]
            }
        };
    },
    methods: {
        submitForm(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    // 简化登录逻辑，实际应该调用API
                    if (this.loginForm.username === 'admin') {
                        const user = {
                            id: 1,
                            name: '管理员',
                            username: 'admin',
                            role: 'ADMIN'
                        };
                        localStorage.setItem('currentUser', JSON.stringify(user));
                        localStorage.setItem('token', 'admin-token');
                        this.$root.currentUser = user;
                        this.$router.push('/admin/venues');
                    } else {
                        const user = {
                            id: 2,
                            name: '用户' + this.loginForm.username,
                            username: this.loginForm.username,
                            role: 'USER'
                        };
                        localStorage.setItem('currentUser', JSON.stringify(user));
                        localStorage.setItem('token', 'user-token');
                        this.$root.currentUser = user;
                        this.$router.push('/user/venues');
                    }
                }
            });
        }
    }
};

// 场地管理组件 - 管理员
const AdminVenuesComponent = {
    template: `
        <div>
            <div class="table-operations">
                <el-button type="primary" @click="showAddDialog">新建</el-button>
                <el-button type="danger" :disabled="!multipleSelection.length" @click="handleBatchDelete">删除</el-button>
            </div>
            
            <el-table :data="venues" border style="width: 100%" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55"></el-table-column>
                <el-table-column prop="id" label="ID" width="80"></el-table-column>
                <el-table-column prop="name" label="场地名称"></el-table-column>
                <el-table-column prop="type" label="场地类型"></el-table-column>
                <el-table-column prop="location" label="位置"></el-table-column>
                <el-table-column prop="capacity" label="容量"></el-table-column>
                <el-table-column prop="pricePerHour" label="每小时价格(元)"></el-table-column>
                <el-table-column prop="isAvailable" label="是否可用">
                    <template slot-scope="scope">
                        <el-tag :type="scope.row.isAvailable ? 'success' : 'danger'">
                            {{ scope.row.isAvailable ? '可用' : '不可用' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="150">
                    <template slot-scope="scope">
                        <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
                        <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            
            <!-- 新建/编辑场地对话框 -->
            <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="40%">
                <el-form :model="form" :rules="rules" ref="form" label-width="100px">
                    <el-form-item label="场地名称" prop="name">
                        <el-input v-model="form.name"></el-input>
                    </el-form-item>
                    <el-form-item label="场地类型" prop="type">
                        <el-select v-model="form.type" placeholder="请选择场地类型" style="width: 100%">
                            <el-option label="篮球场" value="篮球场"></el-option>
                            <el-option label="羽毛球场" value="羽毛球场"></el-option>
                            <el-option label="网球场" value="网球场"></el-option>
                            <el-option label="乒乓球室" value="乒乓球室"></el-option>
                            <el-option label="健身房" value="健身房"></el-option>
                            <el-option label="游泳池" value="游泳池"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="位置" prop="location">
                        <el-input v-model="form.location"></el-input>
                    </el-form-item>
                    <el-form-item label="容量" prop="capacity">
                        <el-input-number v-model="form.capacity" :min="1" style="width: 100%"></el-input-number>
                    </el-form-item>
                    <el-form-item label="每小时价格" prop="pricePerHour">
                        <el-input-number v-model="form.pricePerHour" :min="0" :precision="2" :step="0.1" style="width: 100%"></el-input-number>
                    </el-form-item>
                    <el-form-item label="是否可用" prop="isAvailable">
                        <el-switch v-model="form.isAvailable"></el-switch>
                    </el-form-item>
                    <el-form-item label="描述" prop="description">
                        <el-input type="textarea" v-model="form.description"></el-input>
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
            venues: [],
            multipleSelection: [],
            dialogVisible: false,
            dialogTitle: '新建场地',
            isEdit: false,
            form: this.getInitialForm(),
            rules: {
                name: [
                    { required: true, message: '请输入场地名称', trigger: 'blur' },
                ],
                type: [
                    { required: true, message: '请选择场地类型', trigger: 'change' },
                ],
                pricePerHour: [
                    { required: true, message: '请输入每小时价格', trigger: 'blur' }
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
                name: '',
                type: '',
                location: '',
                capacity: 1,
                pricePerHour: 0,
                isAvailable: true,
                description: ''
            };
        },
        fetchData() {
            // 实际应该调用API
            axios.get('/venues')
                .then(response => {
                    this.venues = response.data;
                })
                .catch(error => {
                    console.error('获取场地列表失败:', error);
                    this.$message.error('获取场地列表失败');

                    // 模拟数据，实际应从后端获取
                    this.venues = [
                        { id: 1, name: '篮球场1号', type: '篮球场', location: '体育馆一层', capacity: 20, pricePerHour: 60, isAvailable: true, description: '标准篮球场' },
                        { id: 2, name: '羽毛球场1号', type: '羽毛球场', location: '体育馆二层', capacity: 6, pricePerHour: 30, isAvailable: true, description: '标准羽毛球场' },
                        { id: 3, name: '网球场1号', type: '网球场', location: '室外', capacity: 4, pricePerHour: 40, isAvailable: true, description: '室外网球场' }
                    ];
                });
        },
        handleSelectionChange(val) {
            this.multipleSelection = val;
        },
        showAddDialog() {
            this.dialogTitle = '新建场地';
            this.isEdit = false;
            this.form = this.getInitialForm();
            this.dialogVisible = true;
        },
        handleEdit(row) {
            this.dialogTitle = '编辑场地';
            this.isEdit = true;
            this.form = JSON.parse(JSON.stringify(row));
            this.dialogVisible = true;
        },
        handleDelete(row) {
            this.$confirm('确认删除该场地?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                // 实际应该调用API
                axios.delete(`/venues/${row.id}`)
                    .then(() => {
                        this.$message.success('删除成功');
                        this.fetchData();
                    })
                    .catch(error => {
                        console.error('删除场地失败:', error);
                        this.$message.error('删除场地失败');

                        // 模拟操作成功
                        this.venues = this.venues.filter(item => item.id !== row.id);
                        this.$message.success('删除成功');
                    });
            }).catch(() => {});
        },
        handleBatchDelete() {
            this.$confirm(`确认删除选中的 ${this.multipleSelection.length} 个场地?`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                // 实际应该批量调用API
                const ids = this.multipleSelection.map(item => item.id);

                // 模拟操作成功
                this.venues = this.venues.filter(item => !ids.includes(item.id));
                this.$message.success('批量删除成功');
            }).catch(() => {});
        },
        submitForm() {
            this.$refs.form.validate((valid) => {
                if (valid) {
                    if (this.isEdit) {
                        // 编辑场地
                        axios.put(`/venues/${this.form.id}`, this.form)
                            .then(response => {
                                this.$message.success('更新成功');
                                this.dialogVisible = false;
                                this.fetchData();
                            })
                            .catch(error => {
                                console.error('更新场地失败:', error);
                                this.$message.error('更新场地失败');

                                // 模拟操作成功
                                const index = this.venues.findIndex(v => v.id === this.form.id);
                                if (index !== -1) {
                                    this.venues.splice(index, 1, this.form);
                                }
                                this.dialogVisible = false;
                                this.$message.success('更新成功');
                            });
                    } else {
                        // 新建场地
                        axios.post('/venues', this.form)
                            .then(response => {
                                this.$message.success('创建成功');
                                this.dialogVisible = false;
                                this.fetchData();
                            })
                            .catch(error => {
                                console.error('创建场地失败:', error);
                                this.$message.error('创建场地失败');

                                // 模拟操作成功
                                this.form.id = this.venues.length > 0 ? Math.max(...this.venues.map(v => v.id)) + 1 : 1;
                                this.venues.push(this.form);
                                this.dialogVisible = false;
                                this.$message.success('创建成功');
                            });
                    }
                }
            });
        }
    }
};