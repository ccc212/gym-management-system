// 登录组件
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
                    const user = {
                        id: 1,
                        name: '测试用户',
                        role: 'USER'
                    };
                    localStorage.setItem('currentUser', JSON.stringify(user));
                    this.$router.push('/user/venues');
                    // 通知父组件更新用户信息
                    this.$root.currentUser = user;
                }
            });
        }
    }
};

// 路由配置
const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        component: LoginComponent
    },
    // 场地管理员路由
    {
        path: '/admin/venues',
        component: AdminVenuesComponent,
        meta: { requiresAuth: true, role: 'ADMIN' }
    },
    {
        path: '/admin/reservations',
        component: AdminReservationsComponent,
        meta: { requiresAuth: true, role: 'ADMIN' }
    },
    {
        path: '/admin/special',
        component: AdminSpecialComponent,
        meta: { requiresAuth: true, role: 'ADMIN' }
    },
    {
        path: '/admin/noshows',
        component: AdminNoshowsComponent,
        meta: { requiresAuth: true, role: 'ADMIN' }
    },
    {
        path: '/admin/announcements',
        component: AdminAnnouncementsComponent,
        meta: { requiresAuth: true, role: 'ADMIN' }
    },
    // 普通用户路由
    {
        path: '/user/venues',
        component: UserVenuesComponent,
        meta: { requiresAuth: true, role: 'USER' }
    },
    {
        path: '/user/myreservations',
        component: UserReservationsComponent,
        meta: { requiresAuth: true, role: 'USER' }
    },
    {
        path: '/user/usage',
        component: UserUsageComponent,
        meta: { requiresAuth: true, role: 'USER' }
    },
    {
        path: '/user/schedule',
        component: UserScheduleComponent,
        meta: { requiresAuth: true, role: 'USER' }
    },
    {
        path: '/user/prices',
        component: UserPricesComponent,
        meta: { requiresAuth: true, role: 'USER' }
    }
];

// 创建路由实例
const router = new VueRouter({
    routes
});

// 路由守卫
router.beforeEach((to, from, next) => {
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));

    if (requiresAuth && !currentUser) {
        next('/login');
    } else if (requiresAuth && to.meta.role && to.meta.role !== currentUser.role) {
        next('/'); // 权限不足，重定向到主页
    } else {
        next();
    }
});