<template>
  <el-dialog
    title="选择参赛团队"
    v-model="dialogVisible"
    width="500px"
    :before-close="handleClose"
  >
    <div v-if="localTeamOptions.length > 0">
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
              v-for="item in localTeamOptions"
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
        <el-button type="success" @click="showJoinTeamDialog = true">加入团队</el-button>
      </div>
    </el-empty>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="confirmSelect" v-if="localTeamOptions.length > 0">确认</el-button>
      </span>
    </template>
  </el-dialog>
  
  <!-- 使用创建团队组件 -->
  <CreateTeamDialog 
    v-model="showCreateTeamDialog"
    @created="handleTeamCreated"
    @cancel="showCreateTeamDialog = false"
  />
  
  <!-- 使用加入团队组件 -->
  <JoinTeamDialog 
    v-model="showJoinTeamDialog"
    :joined-team-ids="joinedTeamIds"
    @joined="handleTeamJoined"
    @cancel="showJoinTeamDialog = false"
  />
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { userStore } from '@/store/user';
import { useRouter } from 'vue-router';
import CreateTeamDialog from '@/components/team/CreateTeamDialog.vue';
import JoinTeamDialog from '@/components/team/JoinTeamDialog.vue';

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

// 本地团队选项，用于实时更新UI状态
const localTeamOptions = ref([...props.teamOptions]);

// 监听父组件传入的teamOptions变化
watch(() => props.teamOptions, (newVal) => {
  localTeamOptions.value = [...newVal];
}, { immediate: true });

// 已加入的团队ID列表
const joinedTeamIds = computed(() => {
  return localTeamOptions.value.map((team: any) => team.value);
});

// 对话框控制
const showCreateTeamDialog = ref(false);
const showJoinTeamDialog = ref(false);

// 监听selectedTeamId的变化，确保同步更新到selectedId
watch(() => props.selectedTeamId, (newVal) => {
  selectedId.value = newVal;
}, { immediate: true });

// 监听对话框状态变化
watch(() => props.modelValue, (newVal) => {
  dialogVisible.value = newVal;
});

// 监听对话框状态变化
watch(() => dialogVisible.value, (newVal) => {
  emit('update:modelValue', newVal);
});

// 处理团队创建成功
const handleTeamCreated = (team: any) => {
  // 创建团队选项格式
  const newTeam = {
    value: team.id,
    label: team.teamName
  };
  
  // 添加到本地选项中
  localTeamOptions.value.push(newTeam);
  
  // 添加到父组件中
  emit('teamCreated', newTeam);
  
  // 选中新创建的团队
  selectedId.value = newTeam.value;
  
  // 关闭创建团队对话框
  showCreateTeamDialog.value = false;
  
  // 显示创建成功提示
  ElMessage.success(`已创建团队"${team.teamName}"并选中`);
};

// 处理团队加入申请成功
const handleTeamJoined = (team: any) => {
  // 这里不需要立即添加到选项中，因为需要队长审批
  ElMessage.info('申请已发送，审核通过后团队将出现在您的列表中');
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