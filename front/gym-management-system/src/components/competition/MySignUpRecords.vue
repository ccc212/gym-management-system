<template>
  <el-dialog
      title="我的报名记录"
      v-model="dialogVisible"
      width="900px"
      :before-close="handleClose"
  >
    <div class="records-container" v-loading="loading">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="个人报名" name="individual">
          <el-empty v-if="individualRecords.length === 0 && !loading" description="暂无个人报名记录"></el-empty>
          <el-table v-else :data="individualRecords" border style="width: 100%">
            <el-table-column prop="competitionName" label="赛事名称" width="150"></el-table-column>
            <el-table-column prop="competitionType" label="赛事类型" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.competitionType === '0'" type="success">乒乓球赛</el-tag>
                <el-tag v-else-if="scope.row.competitionType === '1'" type="primary">篮球赛</el-tag>
                <el-tag v-else>{{ scope.row.competitionType }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="startTime" label="赛事开始时间" width="170"></el-table-column>
            <el-table-column prop="competitionStatus" label="赛事状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.competitionStatus === '0'" type="info">未开始</el-tag>
                <el-tag v-if="scope.row.competitionStatus === '1'" type="success">正在进行</el-tag>
                <el-tag v-if="scope.row.competitionStatus === '2'" type="warning">已结束</el-tag>
                <el-tag v-if="scope.row.competitionStatus === '3'" type="danger">报名已截止</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="signUpStatus" label="报名状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.signUpStatus === 0" type="info">待审核</el-tag>
                <el-tag v-if="scope.row.signUpStatus === 1" type="success">已通过</el-tag>
                <el-tag v-if="scope.row.signUpStatus === 2" type="danger">已拒绝</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="rejectReason" label="拒绝原因" width="150">
              <template #default="scope">
                <span v-if="scope.row.rejectReason">{{ scope.row.rejectReason }}</span>
                <span v-else class="no-reason">无</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="scope">
                <el-button
                    type="danger"
                    size="small"
                    @click="cancelSignUp(scope.row, 'individual')"
                    :disabled="!canCancel(scope.row)"
                >取消报名
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
              v-if="individualTotal > 0"
              v-model:current-page="individualParams.page"
              v-model:page-size="individualParams.pageSize"
              :page-sizes="[10, 20, 50]"
              :total="individualTotal"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleIndividualSizeChange"
              @current-change="handleIndividualCurrentChange"
              class="pagination"
          />
        </el-tab-pane>

        <el-tab-pane label="团队报名" name="team">
          <el-empty v-if="teamRecords.length === 0 && !loading" description="暂无团队报名记录"></el-empty>
          <el-table v-else :data="teamRecords" border style="width: 100%">
            <el-table-column prop="competitionName" label="赛事名称" width="150"></el-table-column>
            <el-table-column prop="teamName" label="团队名称" width="120"></el-table-column>
            <el-table-column prop="competitionType" label="赛事类型" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.competitionType === '0'" type="success">乒乓球赛</el-tag>
                <el-tag v-else-if="scope.row.competitionType === '1'" type="primary">篮球赛</el-tag>
                <el-tag v-else>{{ scope.row.competitionType }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="startTime" label="赛事开始时间" width="170"></el-table-column>
            <el-table-column prop="competitionStatus" label="赛事状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.competitionStatus === '0'" type="info">未开始</el-tag>
                <el-tag v-if="scope.row.competitionStatus === '1'" type="success">正在进行</el-tag>
                <el-tag v-if="scope.row.competitionStatus === '2'" type="warning">已结束</el-tag>
                <el-tag v-if="scope.row.competitionStatus === '3'" type="danger">报名已截止</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="signUpStatus" label="报名状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope.row.signUpStatus === 0" type="info">待审核</el-tag>
                <el-tag v-if="scope.row.signUpStatus === 1" type="success">已通过</el-tag>
                <el-tag v-if="scope.row.signUpStatus === 2" type="danger">已拒绝</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="rejectReason" label="拒绝原因" width="150">
              <template #default="scope">
                <span v-if="scope.row.rejectReason">{{ scope.row.rejectReason }}</span>
                <span v-else class="no-reason">无</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="scope">
                <el-button
                    type="danger"
                    size="small"
                    @click="cancelSignUp(scope.row, 'team')"
                    :disabled="!canCancel(scope.row)"
                >取消报名
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
              v-if="teamTotal > 0"
              v-model:current-page="teamParams.page"
              v-model:page-size="teamParams.pageSize"
              :page-sizes="[10, 20, 50]"
              :total="teamTotal"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleTeamSizeChange"
              @current-change="handleTeamCurrentChange"
              class="pagination"
          />
        </el-tab-pane>
      </el-tabs>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import {onMounted, ref, watch} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {
  CompetitionSignUpTeamControllerService,
  CompetitionSignUpUserControllerService,
  TeamControllerService,
  TeamMemberRelationControllerService
} from '../../../generated';
import {userStore} from '@/store/user';

const store = userStore();

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  }
});

const emit = defineEmits(['update:modelValue', 'refresh']);

const dialogVisible = ref(props.modelValue);
const loading = ref(false);
const activeTab = ref('individual');

// 个人报名记录
const individualRecords = ref<any[]>([]);
const individualTotal = ref(0);
const individualParams = ref({
  page: 1,
  pageSize: 10
});

// 团队报名记录
const teamRecords = ref<any[]>([]);
const teamTotal = ref(0);
const teamParams = ref({
  page: 1,
  pageSize: 10
});

// 监听对话框状态变化
watch(() => props.modelValue, (newVal) => {
  dialogVisible.value = newVal;
  if (newVal) {
    // 对话框打开时加载数据
    loadData();
  }
});

// 监听对话框状态变化
watch(() => dialogVisible.value, (newVal) => {
  emit('update:modelValue', newVal);
});

// 加载数据
const loadData = () => {
  loadIndividualRecords();
  loadTeamRecords();
};

// 加载个人报名记录
const loadIndividualRecords = async () => {
  loading.value = true;
  try {
    const userId = store.getId;
    const res = await CompetitionSignUpUserControllerService.getCompetitionSignUpUserUsingGet(
        Number(userId)
    );

    if (res && res.code === 0 && res.data) {
      individualRecords.value = res.data;
    } else {
      individualRecords.value = [];
    }
  } catch (error) {
    console.error('获取个人报名记录失败:', error);
    ElMessage.error('获取个人报名记录失败');
    individualRecords.value = [];
  } finally {
    loading.value = false;
  }
};

// 加载团队报名记录
const loadTeamRecords = async () => {
  loading.value = true;
  try {
    const userId = store.getId;
    const res = await CompetitionSignUpTeamControllerService.getCompetitionSignUpTeamUsingGet(
        Number(userId)
    );

    if (res && res.code === 0 && res.data) {
      teamRecords.value = res.data;
    } else {
      teamRecords.value = [];
    }
  } catch (error) {
    console.error('获取团队报名记录失败:', error);
    ElMessage.error('获取团队报名记录失败');
    teamRecords.value = [];
  } finally {
    loading.value = false;
  }
};

// 获取用户的团队列表
const getUserTeams = async () => {
  try {
    const userId = store.getId;
    // 获取用户创建的团队（作为队长）
    const leadTeamsRes = await TeamControllerService.listTeamUsingGet(
        undefined, // departId
        store.getName, // leaderName - 查找用户是队长的团队
        1, // page
        100, // pageSize
        undefined // teamName
    );

    let userTeams = [];
    if (leadTeamsRes && leadTeamsRes.code === 0 && leadTeamsRes.data) {
      // 过滤出用户是队长的团队
      userTeams = leadTeamsRes.data.records.filter((team: any) =>
          team.leaderName === store.getName
      );
    }

    // 获取用户加入的团队（作为成员）
    try {
      const memberTeamsRes = await TeamMemberRelationControllerService.listTeamUsingGet1(
          1, // page
          100, // pageSize
          undefined, // status
          undefined, // teamId
          userId // userId
      );

      if (memberTeamsRes && memberTeamsRes.code === 0 && memberTeamsRes.data) {
        // 获取已通过的团队申请
        const approvedRelations = memberTeamsRes.data.records.filter((relation: any) => relation.status === 1);

        // 为每个团队关系获取团队详情
        for (const relation of approvedRelations) {
          // 检查是否已经在队长的团队列表中
          const exists = userTeams.some((team: any) => team.id === relation.teamId);
          if (!exists) {
            // 获取团队详情
            const teamDetailRes = await TeamControllerService.getTeamDetailUsingGet(Number(relation.teamId));
            if (teamDetailRes && teamDetailRes.code === 0 && teamDetailRes.data) {
              userTeams.push(teamDetailRes.data);
            }
          }
        }
      }
    } catch (memberError) {
      console.error('获取用户加入的团队失败:', memberError);
    }

    return userTeams;
  } catch (error) {
    console.error('获取用户团队列表失败:', error);
    ElMessage.error('获取用户团队信息失败');
    return [];
  }
};

// 判断是否可以取消报名
const canCancel = (record: any) => {
  // 只有未开始的比赛可以取消报名，注意competitionStatus可能是字符串
  return record.competitionStatus === '0' || record.competitionStatus === 0;
};

// 取消报名
const cancelSignUp = (record: any, type: 'individual' | 'team') => {
  ElMessageBox.confirm(
      '确定要取消报名吗？取消后需要重新报名。',
      '取消报名',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        try {
          let res;
          if (type === 'individual') {
            res = await CompetitionSignUpUserControllerService.deleteCompetitionSignUpUserUsingDelete(record.id);
          } else {
            res = await CompetitionSignUpTeamControllerService.deleteCompetitionSignUpTeamUsingDelete(record.id);
          }

          if (res.code === 0) {
            ElMessage.success('取消报名成功');
            // 刷新数据
            loadData();
            // 通知父组件刷新
            emit('refresh');
          } else {
            ElMessage.error(res.msg || '取消报名失败');
          }
        } catch (error) {
          console.error('取消报名失败:', error);
          ElMessage.error('取消报名失败');
        }
      })
      .catch(() => {
        // 用户取消操作，不做任何处理
      });
};

// 分页处理
const handleIndividualSizeChange = (val: number) => {
  individualParams.value.pageSize = val;
  loadIndividualRecords();
};

const handleIndividualCurrentChange = (val: number) => {
  individualParams.value.page = val;
  loadIndividualRecords();
};

const handleTeamSizeChange = (val: number) => {
  teamParams.value.pageSize = val;
  loadTeamRecords();
};

const handleTeamCurrentChange = (val: number) => {
  teamParams.value.page = val;
  loadTeamRecords();
};

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false;
};

// 初始化 - 在对话框打开时加载数据
onMounted(() => {
  if (dialogVisible.value) {
    loadData();
  }
});
</script>

<style scoped>
.records-container {
  min-height: 300px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.no-reason {
  color: #999;
  font-style: italic;
}
</style> 