// 定义登录组件（确保它是全局变量）
const LoginComponent = {
    template: `
    <div class="login-container">
        <el-card class="login-card">
            <div slot="header" class="card-header">
                <h2>登录体育场馆管理系统</h2>
            </div>
            <el-form :model="loginForm" :rules="rules" ref="loginForm">
                <el-form-item prop="username">
                    <el-input v-model="loginForm.username" prefix-icon="el-icon-user" placeholder="用户名"></el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input v-model="loginForm.password" prefix-icon="el-icon-lock" type="password" placeholder="密码"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleLogin" style="width: 100%">登录</el-button>
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
        handleLogin() {
            this.$refs.loginForm.validate(valid => {
                if (valid) {
                    // 模拟登录成功
                    localStorage.setItem('token', 'demo-token');
                    let user;
                    if (this.loginForm.username === 'admin' && this.loginForm.password === 'admin123') {
                        user = {
                            id: 1,
                            name: '管理员',
                            role: 'ADMIN'
                        };
                    } else {
                        user = {
                            id: 2,
                            name: '测试用户',
                            role: 'USER'
                        };
                    }
                    localStorage.setItem('currentUser', JSON.stringify(user));
                    this.$router.push(user.role === 'ADMIN' ? '/admin/venues' : '/user/venues');
                    // 通知父组件更新用户信息
                    this.$root.currentUser = user;
                }
            });
        }
    }
};