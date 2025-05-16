<template>
    <div class="register-container">
        <div class="register-box">
            <h1 class="system-title">体育场馆管理系统</h1>
            <el-form ref="form" class="register-form" :model="registerModel" :rules="rules" :inline="false" size="large">
                <el-form-item>
                    <div class="register-title">用户注册</div>
                </el-form-item>
                <el-form-item prop="userNumber">
                    <el-input v-model="registerModel.userNumber" placeholder="请输入学号或工号">
                        <template #prefix>
                            <el-icon><User /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="name">
                    <el-input v-model="registerModel.name" placeholder="请输入姓名">
                        <template #prefix>
                            <el-icon><UserFilled /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input type="password" v-model="registerModel.password" placeholder="请输入密码">
                        <template #prefix>
                            <el-icon><Lock /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="confirmPassword">
                    <el-input type="password" v-model="registerModel.confirmPassword" placeholder="请确认密码">
                        <template #prefix>
                            <el-icon><Lock /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="phone">
                    <el-input v-model="registerModel.phone" placeholder="请输入手机号码">
                        <template #prefix>
                            <el-icon><Phone /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>

                <el-row :gutter="20">
                    <el-col :span="12" :offset="0">
                        <el-button @click="submitForm" type="primary" class="mybtn">注册</el-button>
                    </el-col>
                    <el-col :span="12" :offset="0">
                        <el-button @click="resetForm" type="danger" plain class="mybtn">重置</el-button>
                    </el-col>
                </el-row>

                <div class="login-link">
                    已有账号？<router-link to="/login">立即登录</router-link>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import { reactive, ref } from 'vue'
import { registerApi } from '../../../src/api/system/user/index'
import { useRouter } from 'vue-router'
import { User, UserFilled, Lock, Phone } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 表单ref属性
const form = ref<FormInstance>()

// 表单绑定对象
const registerModel = reactive({
    userNumber: '',
    name: '',
    password: '',
    confirmPassword: '',
    phone: ''
})

// 表单验证规则
const validatePass = (rule: any, value: string, callback: any) => {
    if (value === '') {
        callback(new Error('请输入密码'))
    } else {
        if (registerModel.confirmPassword !== '') {
            form.value?.validateField('confirmPassword')
        }
        callback()
    }
}

const validatePass2 = (rule: any, value: string, callback: any) => {
    if (value === '') {
        callback(new Error('请再次输入密码'))
    } else if (value !== registerModel.password) {
        callback(new Error('两次输入密码不一致!'))
    } else {
        callback()
    }
}

const rules = reactive({
    userNumber: [{
        required: true,
        trigger: ['blur', 'change'],
        message: '请输入学号或工号'
    }],
    name: [{
        required: true,
        trigger: ['blur', 'change'],
        message: '请输入姓名'
    }],
    password: [{
        required: true,
        validator: validatePass,
        trigger: ['blur', 'change']
    }],
    confirmPassword: [{
        required: true,
        validator: validatePass2,
        trigger: ['blur', 'change']
    }],
    phone: [{
        required: true,
        trigger: ['blur', 'change'],
        message: '请输入手机号码'
    }, {
        pattern: /^1[3-9]\d{9}$/,
        message: '请输入正确的手机号码',
        trigger: ['blur', 'change']
    }]
})

// 提交表单
function submitForm() {
    form.value?.validate(async (valid) => {
        if (valid) {
            try {
                const res = await registerApi(registerModel)
                if (res && res.code === 0) {
                    ElMessage.success('注册成功')
                    router.push('/login')
                } else {
                    ElMessage.error(res.message || '注册失败')
                }
            } catch (error) {
                console.error('注册失败:', error)
                ElMessage.error('注册失败，请稍后重试')
            }
        }
    })
}

// 重置表单
function resetForm() {
    form.value?.resetFields()
}
</script>

<style scoped lang="scss">
.register-container {
    height: 100vh;
    background-color: #B7CEEB;
    display: flex;
    justify-content: center;
    align-items: center;

    .register-box {
        width: 450px;
        padding: 30px;
        border-radius: 10px;
        background-color: #fff;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

        .system-title {
            text-align: center;
            color: #409EFF;
            margin-bottom: 30px;
            font-size: 28px;
        }

        .register-form {
            .register-title {
                display: flex;
                justify-content: center;
                color: #303133;
                width: 100%;
                font-size: 24px;
                font-weight: 600;
                margin-bottom: 20px;
            }

            .mybtn {
                width: 100%;
            }

            .login-link {
                margin-top: 20px;
                text-align: center;
                color: #606266;

                a {
                    color: #409EFF;
                    text-decoration: none;

                    &:hover {
                        text-decoration: underline;
                    }
                }
            }
        }
    }
}
</style> 