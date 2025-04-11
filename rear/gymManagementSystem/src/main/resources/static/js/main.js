// API基础URL
const API_BASE_URL = '/api';

// Axios全局配置
axios.defaults.baseURL = API_BASE_URL;
axios.defaults.headers.common['Content-Type'] = 'application/json';

// 添加请求拦截器，设置token等
axios.interceptors.request.use(config => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
}, error => {
    return Promise.reject(error);
});

// 添加响应拦截器，处理错误等
axios.interceptors.response.use(response => {
    return response;
}, error => {
    if (error.response && error.response.status === 401) {
        // 未授权，跳转到登录页面
        localStorage.removeItem('token');
        localStorage.removeItem('currentUser');
        router.push('/login');
    }
    return Promise.reject(error);
});

// 全局Vue实例
const app = new Vue({
    el: '#app',
    router,  // 使用router.js中定义的router实例
    data() {
        return {
            currentUser: null,
        };
    },
    created() {
        // 从本地存储中恢复用户信息
        const userStr = localStorage.getItem('currentUser');
        if (userStr) {
            try {
                this.currentUser = JSON.parse(userStr);
            } catch (e) {
                localStorage.removeItem('currentUser');
            }
        }
    },
    methods: {
        handleCommand(command) {
            if (command === 'logout') {
                this.logout();
            }
        },
        logout() {
            localStorage.removeItem('token');
            localStorage.removeItem('currentUser');
            this.currentUser = null;
            this.$message.success('已成功退出登录');
            this.$router.push('/login');
        }
    }
});