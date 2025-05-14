<template>
  <el-dialog
    title="团队报名"
    v-model="dialogVisible"
    width="800px"
    :before-close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      v-loading="loading"
    >
      <el-form-item label="团队名称" prop="teamInfo.teamName">
        <el-input v-model="form.teamInfo.teamName" placeholder="请输入团队名称" disabled></el-input>
      </el-form-item>
      <el-form-item label="所属部门" prop="teamInfo.departId">
        <el-select v-model="form.teamInfo.departId" placeholder="请选择部门" disabled>
          <el-option
            v-for="item in departmentOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="领队姓名" prop="teamInfo.leaderName">
        <el-input v-model="form.teamInfo.leaderName" placeholder="请输入领队姓名" disabled></el-input>
      </el-form-item>
      <el-form-item label="联系电话" prop="teamInfo.leaderPhone">
        <el-input v-model="form.teamInfo.leaderPhone" placeholder="请输入联系电话"></el-input>
      </el-form-item>
      <el-form-item label="比赛项目" prop="teamInfo.competitionItemId">
        <el-select v-model="form.teamInfo.competitionItemId" placeholder="请选择比赛项目">
          <el-option
            v-for="item in competitionItems"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>

      <!-- 团队成员表 -->
      <el-form-item label="参赛成员" prop="selectedMemberIds">
        <div class="member-selection-header">
          <span style="margin-right: 20px">请选择参加此次比赛的团队成员</span>
          <div class="selection-actions">
            <el-button type="primary" size="small" @click="selectAllMembers">全选</el-button>
            <el-button type="info" size="small" @click="deselectAllMembers">取消全选</el-button>
          </div>
        </div>
        
        <el-table ref="memberTableRef" :data="form.members" border @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column label="姓名" prop="name" width="120"></el-table-column>
          <el-table-column label="学工号" prop="userNumber" width="150"></el-table-column>
          <el-table-column label="性别" width="80">
            <template #default="scope">
              {{ scope.row.sex === '0' ? '男' : scope.row.sex === '1' ? '女' : '' }}
            </template>
          </el-table-column>
          <el-table-column label="联系电话" prop="phone" width="150"></el-table-column>
          <el-table-column label="备注">
            <template #default="scope">
              <span v-if="isLeader(scope.row)" class="member-role">队长</span>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="selected-count">已选择 {{ form.selectedMemberIds.length }} 名成员</div>
      </el-form-item>

      <el-form-item label="备注" prop="teamInfo.remark">
        <el-input
          v-model="form.teamInfo.remark"
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
import { ref, reactive, onMounted, computed, nextTick, watch } from 'vue';
import { ElMessage } from 'element-plus';
import type { FormInstance } from 'element-plus';
import { DepartmentControllerService } from '../../../generated/services/DepartmentControllerService';
import { CompetitionSignUpTeamControllerService } from '../../../generated/services/CompetitionSignUpTeamControllerService';
import { TeamControllerService } from '../../../generated/services/TeamControllerService';
import { UserControllerService } from '../../../generated/services/UserControllerService';
import { competitionItemStore } from '@/store/competition';
import { userStore } from '@/store/user';
import type { AddCompetitionSignUpTeamDTO } from '../../../generated/models/AddCompetitionSignUpTeamDTO';

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  competition: {
    type: Object,
    required: true
  },
  selectedTeam: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(['update:modelValue', 'success']);

const dialogVisible = ref(props.modelValue);
const formRef = ref<FormInstance>();
const memberTableRef = ref(null);
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
const form = reactive<{
  teamInfo: AddCompetitionSignUpTeamDTO;
  members: Array<{
    id: string | number;
    name: string;
    userNumber: string;
    sex: string;
    phone: string;
  }>;
  selectedMemberIds: Array<string | number>;
}>({
  teamInfo: {
    competitionId: undefined,
    competitionItemId: undefined,
    departId: undefined,
    teamName: '',
    leaderName: '',
    leaderPhone: '',
    teamId: undefined,
    remark: '',
    memberIds: [] // 用于存储选中的成员ID
  },
  members: [],
  selectedMemberIds: []
});

// 表单验证规则
const rules = {
  'teamInfo.teamName': [{ required: true, message: '请输入团队名称', trigger: 'blur' }],
  'teamInfo.departId': [{ required: true, message: '请选择所属部门', trigger: 'change' }],
  'teamInfo.leaderName': [{ required: true, message: '请输入领队姓名', trigger: 'blur' }],
  'teamInfo.leaderPhone': [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  'teamInfo.competitionItemId': [{ required: true, message: '请选择比赛项目', trigger: 'change' }],
  selectedMemberIds: [{ required: true, type: 'array', message: '请选择参赛成员', trigger: 'change', min: 1 }]
};

// 判断是否是队长
const isLeader = (member) => {
  return member.name === form.teamInfo.leaderName;
};

// 处理成员选择变化
const handleSelectionChange = (selectedRows) => {
  form.selectedMemberIds = selectedRows.map(row => row.id);
  form.teamInfo.memberIds = form.selectedMemberIds;
};

// 全选成员
const selectAllMembers = () => {
  form.members.forEach(member => {
    if (memberTableRef.value) {
      memberTableRef.value.toggleRowSelection(member, true);
    }
  });
  form.selectedMemberIds = form.members.map(member => member.id);
  form.teamInfo.memberIds = form.selectedMemberIds;
};

// 取消全选
const deselectAllMembers = () => {
  if (memberTableRef.value) {
    memberTableRef.value.clearSelection();
  }
  form.selectedMemberIds = [];
  form.teamInfo.memberIds = [];
};

// 获取用户信息和预填团队信息
const loadUserInfo = async () => {
  try {
    loading.value = true;
    const userId = store.getId;
    if (!userId) {
      ElMessage.warning('未获取到用户ID，请重新登录');
      return;
    }

    // 如果有选择的团队，优先使用团队信息
    if (props.selectedTeam) {
      form.teamInfo.teamName = props.selectedTeam.teamName || '';
      form.teamInfo.leaderName = props.selectedTeam.leaderName || '';
      form.teamInfo.leaderPhone = props.selectedTeam.leaderPhone || '';
      form.teamInfo.departId = props.selectedTeam.departId;
      form.teamInfo.teamId = props.selectedTeam.id;
      
      // 如果有团队成员，也加载团队成员
      if (props.selectedTeam.members && props.selectedTeam.members.length > 0) {
        form.members = props.selectedTeam.members.map((member: any) => ({
          id: member.id,
          name: member.name || '',
          userNumber: member.userNumber || '',
          sex: member.sex || '0',
          phone: member.phone || ''
        }));
        
        // 默认全选所有成员
        form.selectedMemberIds = form.members.map(member => member.id);
        form.teamInfo.memberIds = form.selectedMemberIds;
        
        // 等待DOM更新后，默认选中所有行
        nextTick(() => {
          selectAllMembers();
        });
      } else {
        // 获取团队详情
        try {
          const teamRes = await TeamControllerService.getTeamDetailUsingGet(Number(props.selectedTeam.id));
          if (teamRes && teamRes.code === 0 && teamRes.data && teamRes.data.members) {
            form.members = teamRes.data.members.map((member: any) => ({
              id: member.id,
              name: member.name || '',
              userNumber: member.userNumber || '',
              sex: member.sex || '0',
              phone: member.phone || ''
            }));
            
            // 默认全选所有成员
            form.selectedMemberIds = form.members.map(member => member.id);
            form.teamInfo.memberIds = form.selectedMemberIds;
            
            // 等待DOM更新后，默认选中所有行
            nextTick(() => {
              selectAllMembers();
            });
          }
        } catch (teamError) {
          console.error('获取团队详情失败:', teamError);
        }
      }
      
      return;
    }

    // 如果没有选择的团队，使用用户信息
    const res = await UserControllerService.getInfoUsingGet(Number(userId));
    if (res && res.code === 0 && res.data) {
      const userInfo = res.data;
      // 填充领队信息
      form.teamInfo.leaderName = userInfo.name || '';
      form.teamInfo.leaderPhone = userInfo.phone || '';
      form.teamInfo.departId = userInfo.departId;
      
      // 添加自己为第一个团队成员
      if (form.members.length === 0) {
        form.members.push({
          id: userInfo.id,
          name: userInfo.name || '',
          userNumber: userInfo.userNumber || '',
          sex: userInfo.sex || '0',
          phone: userInfo.phone || ''
        });
        
        // 默认选中自己
        form.selectedMemberIds = [userInfo.id];
        form.teamInfo.memberIds = form.selectedMemberIds;
        
        // 等待DOM更新后，默认选中自己
        nextTick(() => {
          selectAllMembers();
        });
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
    ElMessage.error('获取用户信息失败，请重新登录');
  } finally {
    loading.value = false;
  }
};

// 监听对话框打开状态，确保正确加载和选中数据
watch(() => dialogVisible.value, (newVal) => {
  if (newVal) {
    nextTick(() => {
      // 当对话框打开时，重新尝试选中所有成员
      if (form.members.length > 0 && form.selectedMemberIds.length > 0) {
        selectAllMembers();
      }
    });
  }
});

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
    
    // 验证是否选择了成员
    if (form.selectedMemberIds.length === 0) {
      ElMessage.warning('请至少选择一名参赛成员');
      return;
    }

    form.teamInfo.competitionId = props.competition.id;
    form.teamInfo.memberIds = form.selectedMemberIds;
    
    loading.value = true;
    
    // 提交团队报名信息
    const res = await CompetitionSignUpTeamControllerService.addCompetitionSignUpTeamUsingPost(form.teamInfo);
    
    if (res && res.code === 0) {
      ElMessage.success('报名成功');
      // 发送成功事件，包含团队ID和比赛ID信息
      emit('success', {
        teamId: form.teamInfo.teamId,
        competitionId: form.teamInfo.competitionId
      });
      handleClose();
    } else {
      ElMessage.error(res?.msg || '报名失败');
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
  form.members = [];
  form.selectedMemberIds = [];
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

.member-selection-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.selection-actions {
  display: flex;
  gap: 10px;
}

.selected-count {
  margin-top: 10px;
  text-align: right;
  color: #409EFF;
  font-weight: bold;
}

.member-role {
  display: inline-block;
  padding: 2px 6px;
  background-color: #67C23A;
  color: white;
  border-radius: 4px;
  font-size: 12px;
}
</style> 