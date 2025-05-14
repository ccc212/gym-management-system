<template>
  <el-dialog
    :title="title || '创建团队'"
    v-model="dialogVisible"
    width="500px"
    :before-close="handleClose"
    append-to-body
  >
    <el-form 
      ref="createTeamFormRef" 
      :model="createTeamForm" 
      :rules="createTeamRules"
      label-width="100px"
    >
      <el-form-item label="团队名称" prop="teamName">
        <el-input v-model="createTeamForm.teamName" placeholder="请输入团队名称"></el-input>
      </el-form-item>
      <el-form-item label="联系电话" prop="leaderPhone">
        <el-input v-model="createTeamForm.leaderPhone" placeholder="请输入联系电话"></el-input>
      </el-form-item>
      <el-form-item label="所属部门" prop="departId">
        <el-select v-model="createTeamForm.departId" placeholder="请选择部门" style="width: 100%">
          <el-option
            v-for="item in departmentOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreateTeam" :loading="submitting">创建</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue';
import { ElMessage, FormInstance } from 'element-plus';
import { TeamControllerService } from '../../../generated/services/TeamControllerService';
import { DepartmentControllerService } from '../../../generated/services/DepartmentControllerService';
import { userStore } from '@/store/user';

const store = userStore();

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  title: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['update:modelValue', 'created', 'cancel']);

const dialogVisible = ref(props.modelValue);

// 监听对话框状态变化
watch(() => props.modelValue, (newVal) => {
  dialogVisible.value = newVal;
});

watch(() => dialogVisible.value, (newVal) => {
  emit('update:modelValue', newVal);
});

// 创建团队相关
const createTeamFormRef = ref<FormInstance>();
const createTeamForm = reactive({
  teamName: '',
  leaderName: '',
  leaderPhone: '',
  departId: undefined as number | undefined
});
const departmentOptions = ref<{value: string | number, label: string}[]>([]);
const submitting = ref(false);

// 创建团队表单验证规则
const createTeamRules = {
  teamName: [{ required: true, message: '请输入团队名称', trigger: 'blur' }],
  leaderPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  departId: [{ required: true, message: '请选择所属部门', trigger: 'change' }]
};

// 获取部门选项
const loadDepartments = async () => {
  try {
    const res = await DepartmentControllerService.selectListUsingGet();
    if (res && res.code === 0 && res.data) {
      departmentOptions.value = res.data.map((item: any) => ({
        value: item.value,
        label: item.label
      }));
    }
  } catch (error) {
    console.error('获取部门列表失败:', error);
  }
};

// 加载创建团队表单的用户信息
const loadCreateTeamFormUserInfo = () => {
  const userInfo = store.getUserInfo;
  if (userInfo) {
    createTeamForm.leaderName = userInfo.name || '';
    createTeamForm.leaderPhone = userInfo.phone || '';
    createTeamForm.departId = userInfo.departId;
  }
};

// 提交创建团队
const submitCreateTeam = async () => {
  if (!createTeamFormRef.value) return;
  
  try {
    await createTeamFormRef.value.validate();
    submitting.value = true;
    
    // 设置领队信息
    const formData = {
      ...createTeamForm,
      leaderName: store.getName || createTeamForm.leaderName,
      memberIds: [store.getId] // 添加当前用户为团队成员
    };
    
    const res = await TeamControllerService.addTeamUsingPost(formData);
    
    if (res && res.code === 0 && res.data) {
      const newTeamId = res.data;
      ElMessage.success('团队创建成功');
      dialogVisible.value = false;
      
      // 获取新创建的团队信息
      const teamRes = await TeamControllerService.getTeamUsingGet(newTeamId);
      if (teamRes && teamRes.code === 0 && teamRes.data) {
        // 将新创建的团队信息发送给父组件
        emit('created', teamRes.data);
      }
      
      // 重置表单
      createTeamForm.teamName = '';
      createTeamForm.leaderPhone = '';
    } else {
      ElMessage.error(res?.msg || '创建团队失败');
    }
  } catch (error) {
    console.error('创建团队失败:', error);
    ElMessage.error('创建团队失败，请检查表单信息');
  } finally {
    submitting.value = false;
  }
};

// 关闭对话框
const handleClose = () => {
  emit('cancel');
  dialogVisible.value = false;
};

// 重置表单
const resetForm = () => {
  if (createTeamFormRef.value) {
    createTeamFormRef.value.resetFields();
  }
  loadCreateTeamFormUserInfo();
};

// 初始化
onMounted(() => {
  loadDepartments();
  loadCreateTeamFormUserInfo();
});

// 对外暴露的方法
defineExpose({
  resetForm
});
</script>

<style scoped>
.dialog-footer {
  padding-top: 20px;
  text-align: right;
}
</style> 