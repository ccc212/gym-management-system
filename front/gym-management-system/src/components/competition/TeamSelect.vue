<template>
  <el-dialog
    title="选择参赛团队"
    v-model="dialogVisible"
    width="500px"
    :before-close="handleClose"
  >
    <div v-if="teamOptions.length > 0">
      <div class="dialog-tip">
        <el-alert
          title="请选择要用于本次比赛报名的团队"
          type="info"
          description="选择团队后，报名表格将自动填充团队成员信息"
          show-icon
          :closable="false"
        />
      </div>
      <el-form label-width="100px">
        <el-form-item label="选择团队">
          <el-select v-model="selectedId" placeholder="请选择团队" style="width: 100%">
            <el-option
              v-for="item in teamOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    
    <el-empty v-else description="您还没有加入任何团队">
      <div class="team-tip">
        <el-alert
          title="参加团队比赛需要先创建或加入一个团队"
          type="warning"
          description="创建团队后，您将成为团队队长，可以添加其他成员参与比赛"
          show-icon
          :closable="false"
        />
      </div>
      <div class="team-actions">
        <el-button type="primary" @click="showCreateTeamDialog = true">创建团队</el-button>
      </div>
    </el-empty>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="confirmSelect" v-if="teamOptions.length > 0">确认</el-button>
      </span>
    </template>
  </el-dialog>
  
  <!-- 创建团队对话框 -->
  <el-dialog
    title="创建团队"
    v-model="showCreateTeamDialog"
    width="500px"
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
        <el-button @click="showCreateTeamDialog = false">取消</el-button>
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
import { useRouter } from 'vue-router';

const store = userStore();

const router = useRouter();

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  teamOptions: {
    type: Array,
    required: true
  },
  selectedTeamId: {
    type: Number,
    default: ''
  }
});

const emit = defineEmits(['update:modelValue', 'confirm', 'cancel', 'teamCreated']);

const dialogVisible = ref(props.modelValue);
const selectedId = ref(props.selectedTeamId);

// 监听selectedTeamId的变化，确保同步更新到selectedId
watch(() => props.selectedTeamId, (newVal) => {
  selectedId.value = newVal;
}, { immediate: true });

// 创建团队相关
const showCreateTeamDialog = ref(false);
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

// 监听对话框状态变化
watch(() => props.modelValue, (newVal) => {
  dialogVisible.value = newVal;
});

// 监听对话框状态变化
watch(() => dialogVisible.value, (newVal) => {
  emit('update:modelValue', newVal);
});

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
      showCreateTeamDialog.value = false;
      
      // 获取新创建的团队信息
      const teamRes = await TeamControllerService.getTeamUsingGet(Number(newTeamId));
      if (teamRes && teamRes.code === 0 && teamRes.data) {
        // 将新创建的团队添加到选项中并选中
        const newTeam = {
          value: teamRes.data.id,
          label: teamRes.data.teamName
        };
        
        // 添加到选项中
        emit('teamCreated', newTeam);
        
        // 选中新创建的团队
        selectedId.value = newTeam.value;
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

// 确认选择
const confirmSelect = () => {
  if (!selectedId.value) {
    ElMessage.warning('请选择一个团队');
    return;
  }
  console.log('确认选择：', selectedId.value);
  emit('confirm', selectedId.value);
};

// 关闭对话框
const handleClose = () => {
  emit('cancel');
};

// 初始化
onMounted(() => {
  loadDepartments();
  loadCreateTeamFormUserInfo();
});
</script>

<style scoped>
.dialog-footer {
  padding-top: 20px;
  text-align: right;
}

.team-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 10px;
}

.dialog-tip, .team-tip {
  margin-bottom: 20px;
}
</style> 