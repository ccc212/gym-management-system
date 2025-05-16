import axios from 'axios'
import { ElMessage } from 'element-plus'
import { userStore } from '@/store/user/index'

// 创建axios实例
const service = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
    config => {
        const store = userStore()
        const token = store.getToken
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config
    },
    error => {
        console.error('请求错误:', error)
        return Promise.reject(error)
    }
)

// 响应拦截器
service.interceptors.response.use(
    response => {
        const res = response.data
        if (res.code !== 0) {
            ElMessage.error(res.message || '请求失败')
            return Promise.reject(new Error(res.message || '请求失败'))
        }
        return res
    },
    error => {
        console.error('响应错误:', error)
        if (error.response?.status === 401) {
            const store = userStore()
            store.logout()
            window.location.href = '/login'
        }
        ElMessage.error(error.response?.data?.msg || '请求出现了问题')
        return Promise.reject(error)
    }
)

export default service 