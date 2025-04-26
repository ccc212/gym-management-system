import axios from "axios";
import type {AxiosInstance,InternalAxiosRequestConfig,AxiosResponse,AxiosRequestConfig} from "axios";
import {ElMessage} from 'element-plus'
import { userStore } from "../store/user";


//配置axios
const config = {
    /* baseURL: 'http://localhost:5678', */
    baseURL: '/api',
    timeout: 10000
}

//返回值类型
export interface Result<T = any>{
    code:number,
    msg:string,
    data:T
}

class Http{
    //axios实例
    private instance:AxiosInstance
    //初始化
    constructor(configs:AxiosRequestConfig){
        //创建axios实例
        this.instance = axios.create(configs)
        //配置请求拦截器
        this.interceptors()
    }

    //请求发送，返回之后处理
    private interceptors(){
        //请求发送之前的处理:请求头携带token
        this.instance.interceptors.request.use((config:InternalAxiosRequestConfig)=>{
            let store = userStore();
            
            let token = store.getToken;//获取token
            if(token){
                config.headers['token'] = token;
            }
            return config
       }),(error:any)=>{
        error.data = {}
        error.data.msg = '服务器出现了异常'
        return error;
       }

       //请求返回之后的处理
       this.instance.interceptors.response.use((res:AxiosResponse)=>{
        if(res.status === 200){
            return res.data
        }else{
            ElMessage.error(res.data.msg || '请求出现了问题')
            return Promise.reject(res.data.msg || '请求出现了问题')
        }
       }),(error:any)=>{
        console.log('错误')
        error.data = {}
        if(error && error.response){
            switch(error.response.status){
                case 400:
                    error.data.msg = '请求错误';
                    ElMessage.error(error.data.msg)
                    break
                case 401:
                    error.data.msg = '未授权';
                    ElMessage.error(error.data.msg)
                    break
                case 403:
                    error.data.msg = '拒绝访问';
                    ElMessage.error(error.data.msg)
                    break
                case 404:
                    error.data.msg = '请求地址出错';
                    ElMessage.error(error.data.msg)
                    break
                case 408:
                    error.data.msg = '请求超时';
                    ElMessage.error(error.data.msg)
                    break
                case 500:
                    error.data.msg = '接口错误';
                    ElMessage.error(error.data.msg)
                    break
                case 501:
                    error.data.msg = '网络未实现';
                    ElMessage.error(error.data.msg)
                    break
                case 502:
                    error.data.msg = '网络错误';
                    ElMessage.error(error.data.msg)
                    break
                case 503:
                    error.data.msg = '服务不可用';
                    ElMessage.error(error.data.msg)
                    break
                case 504:
                    error.data.msg = '网络超时';
                    ElMessage.error(error.data.msg)
                    break
                case 505:
                    error.data.msg = 'http版本不支持该请求';
                    ElMessage.error(error.data.msg)
                    break
                default:
                    error.data.msg = '请求出现了问题${error.response.status}';
                    ElMessage.error(error.data.msg)
            }
        } else{
            error.data.msg = '连接到服务器失败'
            ElMessage.error(error.data.msg)
        }
        return Promise.reject(error)
       }
    }

    //post请求
    post<T = Result>(url:string,data?:object):Promise<T>{
        return this.instance.post(url,data)
    }

    //put请求
    put<T = Result>(url:string,data?:object):Promise<T>{
        return this.instance.put(url,data)
    }

    //get请求
    get<T = Result>(url:string,params?:object):Promise<T>{
        return this.instance.get(url,{params})
    }

    //delete请求
    delete<T = Result>(url:string):Promise<T>{
        return this.instance.delete(url)
    }

    //图片上传
    upload<T = Result>(url:string,params?:object):Promise<T>{
        return this.instance.post(url,params,{
            headers:{
                'Content-Type':'multipart/form-data'
            }
        })
    }
}

export default new Http(config)
