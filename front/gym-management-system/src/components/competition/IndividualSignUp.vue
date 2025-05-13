<template>
  <el-dialog
    title="个人报名"
    v-model="dialogVisible"
    width="600px"
    :before-close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      v-loading="loading"
    >
      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" placeholder="请输入姓名"></el-input>
      </el-form-item>
      <el-form-item label="学工号" prop="userNumber">
        <el-input v-model="form.userNumber" placeholder="请输入学工号" :disabled="true"></el-input>
      </el-form-item>
      <el-form-item label="性别" prop="sex">
        <el-radio-group v-model="form.sex">
          <el-radio label="0">男</el-radio>
          <el-radio label="1">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="联系电话" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入联系电话"></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱"></el-input>
      </el-form-item>
      <el-form-item label="所属部门" prop="departId">
        <el-select v-model="form.departId" placeholder="请选择部门">
          <el-option
            v-for="item in departmentOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="比赛项目" prop="competitionItemId">
        <el-select v-model="form.competitionItemId" placeholder="请选择比赛项目">
          <el-option
            v-for="item in competitionItems"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="form.remark"
          type="textarea"
          placeholder="请输入备注信息"
        ></el-input>
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="loading">提交报名</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import type { FormInstance } from 'element-plus';
import { DepartmentControllerService } from '../../../generated/services/DepartmentControllerService';
import { CompetitionSignUpUserControllerService } from '../../../generated/services/CompetitionSignUpUserControllerService';
import { UserControllerService } from '../../../generated/services/UserControllerService';
import { competitionItemStore } from '@/store/competition';
import { userStore } from '@/store/user';
import type { AddCompetitionSignUpUserDTO } from '../../../generated/models/AddCompetitionSignUpUserDTO';
import router from "@/router";

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  competition: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['update:modelValue', 'success']);

const dialogVisible = ref(props.modelValue);
const formRef = ref<FormInstance>();
const departmentOptions = ref([]);
const compItemStore = competitionItemStore();
const store = userStore();
const loading = ref(false);

// 获取当前比赛的比赛项目
const competitionItems = computed(() => {
  // 如果store中有数据且类型匹配，则直接使用
  const storeItems = compItemStore.getItemsByType(props.competition.type);
  if (storeItems && storeItems.length > 0) {
    return storeItems;
  }
  // 否则返回competition中的items
  return props.competition.items || [];
});

// 表单数据
const form = reactive<AddCompetitionSignUpUserDTO>({
  competitionId: undefined,
  competitionItemId: undefined,
  departId: undefined,
  name: '',
  userNumber: '',
  sex: '0',
  phone: '',
  email: '',
  userStuorfac: '',
  userId: undefined,
  remark: ''
});

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  userNumber: [{ required: true, message: '请输入学工号', trigger: 'blur' }],
  sex: [{ required: true, message: '请选择性别', trigger: 'change' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  departId: [{ required: true, message: '请选择所属部门', trigger: 'change' }],
  competitionItemId: [{ required: true, message: '请选择比赛项目', trigger: 'change' }]
};

// 获取用户信息
const loadUserInfo = async () => {
  try {
    loading.value = true;
    const userId = store.getId;
    if (!userId) {
      ElMessage.warning('未获取到用户ID，请重新登录');
      await router.push('/login');
      return;
    }

    const res = await UserControllerService.getListUsingGet5(
        1,
        '',
        1,
        store.getUserNumber,
        ''
    );
    if (res.data?.records) {
      const userInfo = res.data.records[0];
      // 填充表单数据
      form.name = userInfo.name || '';
      form.userNumber = userInfo.userNumber || '';
      form.sex = userInfo.sex === '女' ? '1' : '0';
      form.phone = userInfo.phone || '';
      form.email = userInfo.email || '';
      form.departId = userInfo.departId;
      form.userId = userInfo.id;
      form.userStuorfac = userInfo.userType || '';
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
    ElMessage.error('获取用户信息失败，请重新登录');
  } finally {
    loading.value = false;
  }
};

// 获取部门列表
const loadDepartments = async () => {
  try {
    const res = await DepartmentControllerService.getListUsingGet(
        1,
        '',
        10000
    );
    if (res?.data) {
      departmentOptions.value = res.data.records.map((item: any) => ({
        value: item.id,
        label: item.departName
      }));
    }
  } catch (error) {
    console.error('获取部门列表失败:', error);
    ElMessage.error('获取部门列表失败');
  }
};

// 获取赛事项目列表
const loadCompetitionItems = async () => {
  if (compItemStore.getItemsByType(props.competition.type).length === 0) {
    try {
      await compItemStore.fetchCompetitionItems({
        type: props.competition.type,
        isTeamCompetition: props.competition.isTeamCompetition
      });
    } catch (error) {
      console.error('获取赛事项目列表失败:', error);
    }
  }
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    form.competitionId = props.competition.id;
    
    loading.value = true;
    const res = await CompetitionSignUpUserControllerService.addCompetitionSignUpUserUsingPost(form);
    
    if (res.code === 0) {
      ElMessage.success('报名成功');
      emit('success');
      handleClose();
    } else {
      ElMessage.error(res.msg || '报名失败');
    }
  } catch (error) {
    console.error('报名失败:', error);
    ElMessage.error('报名失败，请检查表单信息');
  } finally {
    loading.value = false;
  }
};

const handleClose = () => {
  dialogVisible.value = false;
  emit('update:modelValue', false);
  formRef.value?.resetFields();
};

onMounted(() => {
  loadUserInfo();
  loadDepartments();
  loadCompetitionItems();
});
</script>

<style scoped>
.dialog-footer {
  padding-top: 20px;
  text-align: right;
}
</style> 