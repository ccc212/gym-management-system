<template>
    <el-dropdown placement="bottom-start">
    <span class="el-dropdown-link">
        操作
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item @click="updateBtn">修改密码</el-dropdown-item>
        <el-dropdown-item @click="loginOutBtn">退出登录</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>

  <!-- 修改密码弹窗 -->
  <el-dialog title="修该密码" v-model="openPassword" width="500px" append-to-body>
      <el-form ref="form" :model="upModel" :rules="rules" label-width="80px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input type="password" v-model="upModel.oldPassword" placeholder="请输入原密码"></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="password">
          <el-input type="password" v-model="upModel.password" placeholder="请输入新密码"></el-input>
        </el-form-item>
        <el-form-item label="确定密码" prop="confirm">
          <el-input type="password" v-model="upModel.confirm" placeholder="请再次输入新密码"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFormEdit">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
</template>

<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import{ref,reactive} from 'vue'
import {ElMessage} from 'element-plus'
import {updatePasswordApi} from '../../api/system/user/index'
import {useRouter} from 'vue-router'
import {userStore} from '../../store/user/index'
import useInstance from '../../hooks/useInstance'
import {tabStore} from '../../store/tabs/index'



const{global} = useInstance()
const store = userStore()
const router = useRouter()
const tStore = tabStore()
const form = ref<FormInstance>()

const openPassword = ref(false)

//表单对象
const upModel = reactive({
    id:'',
    oldPassword:'',
    password:'', 
    confirm:'',
})


const rules = reactive({
  oldPassword: [
   {
     required: true, 
     message: '请输入原密码', 
     trigger: 'blur',
   },
  ],
  password: [
    {
      required: true,
      message:'请输入新密码',
      trigger: 'blur',
    },
  ],
  confirm: [
    {
      required: true,
      message:'请再次输入新密码',
      trigger: 'blur',
    },
  ]
})


// 修改密码弹窗
function updateBtn(){
    openPassword.value = true
}

//修改密码弹窗提交
function submitFormEdit(){
  upModel.id = store.getId
    form.value?.validate(async(valid) => {
      if(valid){
        //判断新密码和确定密码是否一致
        if(upModel.password !== upModel.confirm){
          ElMessage.error('新密码和确定密码不一致')
          return;
        }
        // 这里可以添加提交表单的逻辑
        let res = await updatePasswordApi(upModel)
        if(res && res.code === 0){
          tStore.clearTab()
          store.setToken('')
          router.push({path:'/login'})
          ElMessage.success(res.msg)
          //清空缓存
          localStorage.clear();
        }
      }
    });
};

//取消按钮
function cancel(){
    openPassword.value = false
}

//退出登录
async function loginOutBtn(){
  const confirm = await global.$myconfirm("是否退出登录")
  if(confirm){
    tStore.clearTab()
    store.setToken('')
    localStorage.clear();
    router.replace({path:'/login'})
  }
}


</script>

<style scoped lang="scss">
.el-dropdown-link:focus{
    outline:none;
    cursor:pointer;
}

</style>