// 路由配置
const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        component: LoginComponent  // 使用components.js中定义的组件
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