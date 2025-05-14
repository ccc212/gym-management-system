<template>
  <el-dialog
    :title="title || '加入团队'"
    v-model="dialogVisible"
    width="500px"
    :before-close="handleClose"
    append-to-body
  >
    <el-form
      ref="joinTeamFormRef"
      :model="joinTeamForm"
      label-width="100px"
    >
      <el-form-item label="搜索团队">
        <div class="search-container">
          <el-input
            v-model="joinTeamForm.searchText"
            placeholder="请输入团队名称或ID"
            clearable
            @keyup.enter="searchTeams"
          ></el-input>
          <el-button type="primary" @click="searchTeams" :loading="searching">搜索</el-button>
        </div>
      </el-form-item>

      <el-table
        v-if="availableTeams.length > 0"
        :data="availableTeams"
        style="width: 100%"
        border
        height="250px"
      >
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="teamName" label="团队名称" width="120"></el-table-column>
        <el-table-column prop="leaderName" label="领队"></el-table-column>
        <el-table-column fixed="right" label="操作" width="80">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              @click="joinTeam(scope.row)"
              :loading="submitting"
            >加入
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-else-if="joinTeamForm.searchText && !searching" description="未找到相关团队"></el-empty>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue';
import { ElMessage, FormInstance } from 'element-plus';
import { TeamControllerService } from '../../../generated/services/TeamControllerService';
import { TeamMemberRelationControllerService } from '../../../generated/services/TeamMemberRelationControllerService';
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
  },
  // 已加入的团队ID列表，用于过滤搜索结果
  joinedTeamIds: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['update:modelValue', 'cancel']);

const dialogVisible = ref(props.modelValue);

// 监听对话框状态变化
watch(() => props.modelValue, (newVal) => {
  dialogVisible.value = newVal;
});

watch(() => dialogVisible.value, (newVal) => {
  emit('update:modelValue', newVal);
});

// 加入团队相关
const joinTeamFormRef = ref<FormInstance>();
const joinTeamForm = reactive({
  searchText: ''
});
const availableTeams = ref<any[]>([]);
const searching = ref(false);
const submitting = ref(false);

// 搜索可加入的团队
const searchTeams = async () => {
  if (!joinTeamForm.searchText) {
    ElMessage.warning('请输入搜索内容');
    return;
  }

  searching.value = true;
  try {
    const res = await TeamControllerService.listTeamUsingGet(
        undefined, // departId
        undefined, // leaderName
        1, // page
        100, // pageSize
        joinTeamForm.searchText // teamName
    );

    if (res && res.code === 0 && res.data && res.data.records) {
      // 过滤掉已经加入的团队
      availableTeams.value = res.data.records.filter((team: any) => 
        !props.joinedTeamIds.includes(team.id)
      );
    } else {
      availableTeams.value = [];
    }
  } catch (error) {
    console.error('搜索团队失败:', error);
    ElMessage.error('搜索团队失败，请稍后再试');
  } finally {
    searching.value = false;
  }
};

// 加入团队
const joinTeam = async (team: any) => {
  submitting.value = true;
  try {
    const userId = Number(store.getId);
    if (!userId) {
      ElMessage.warning('未获取到用户ID，请重新登录');
      return;
    }

    // 创建加入团队申请 - 使用TeamMemberRelation控制器
    const teamMemberRelationDTO = {
      teamId: Number(team.id),
      userId: userId,
      status: 0 // 待审核状态
    };

    const res = await TeamMemberRelationControllerService.addTeamMemberRelationUsingPost(teamMemberRelationDTO);

    if (res && res.code === 0) {
      ElMessage.success('已提交加入申请，请等待队长审批');
      dialogVisible.value = false;

      // 清空搜索结果
      joinTeamForm.searchText = '';
      availableTeams.value = [];
    } else {
      ElMessage.error(res?.msg || '申请加入团队失败');
    }
  } catch (error) {
    console.error('申请加入团队失败:', error);
    ElMessage.error('申请加入团队失败，请稍后再试');
  } finally {
    submitting.value = false;
  }
};

// 关闭对话框
const handleClose = () => {
  emit('cancel');
  dialogVisible.value = false;
};

// 重置表单和搜索结果
const resetForm = () => {
  joinTeamForm.searchText = '';
  availableTeams.value = [];
};

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

.search-container {
  display: flex;
  gap: 10px;
}
</style> 