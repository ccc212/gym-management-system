<template>
    <div class = "logincontainer">
        <el-form ref="form" class="loginform" :model="loginModel" :rules="rules"  :inline="false" size="large">
            <el-form-item >
                <div class="logintitle">登录</div>
            </el-form-item>
            <el-form-item prop="userNumber">
                <el-input v-model="loginModel.userNumber" placeholder="请输入学号或工号"></el-input>
            </el-form-item>
            <el-form-item prop="password">
                <el-input type="password" v-model="loginModel.password" placeholder="请输入密码"></el-input>
            </el-form-item>

            <el-row :gutter="20">
                <el-col :span="12" :offset="0">
                    <el-button @click="submitForm" type="primary" class="mybtn">登录</el-button>
                </el-col>
                <el-col :span="12" :offset="0">
                    <el-button type="danger" plain class="mybtn">重置</el-button>
                </el-col>
            </el-row>
        </el-form>
    </div>
</template>

<script setup lang="ts">
import type { FormInstance } from 'element-plus';
import {reactive,ref} from 'vue';
import {loginApi} from '../../../src/api/system/user/index';
import {userStore} from '../../store/user/index';
import {useRouter} from 'vue-router';
import {menuStore} from '../../store/menu/index'


const router = useRouter()
const store = userStore()
const mstore = menuStore()

//表单ref属性
const form = ref<FormInstance>()
//表单绑定对象
const loginModel = reactive({
    userNumber:'admin',
    password:'123456',
})
//表单验证规则
const rules = reactive({
    userNumber: [{
        required: true,
        trigger:['blur','change'],
        message: '请输入工号或学号',
    },
    ],
    password: [{
        required: true,
        trigger:['blur','change'],
        message: '请输入密码',
    }
    ],
})

function submitForm() {
    form.value?.validate(async(valid) => {
        if(valid){
            let res = await loginApi(loginModel)
            if (res && res.code == 0) {
                //存储用户信息，跳转首页
                store.setId(res.data.id)
                store.setUserNumber(res.data.userNumber)
                store.setName(res.data.name)
                store.setToken(res.data.token)
                store.getInfo()
                mstore.getMenuList(router, store.getId)
                // 自动写入 userId
                localStorage.setItem('userId', res.data.id)
                //跳转
                router.push({path:'/'})
            }
        }
    })
}



</script>

<style scoped lang="scss">
.logincontainer{
    height: 100%;
    background-color: #B7CEEB;
    display: flex;
    justify-content:center;
    align-items: center;
    .loginform{
        height:320px;
        width: 450px;
        padding: auto;
        padding: 20px 30px;
        border-radius:10px;
        background-color: #fff;
        .logintitle{
            display: flex;
            justify-content: center;
            color:black;
            width: 100%;
            font-size: 24px;
            font-weight: 600;
        }
        .mybtn{
            width: 100%;
        }
    }
}
</style>