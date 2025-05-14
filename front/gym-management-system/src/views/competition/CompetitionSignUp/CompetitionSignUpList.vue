<template>
  <el-main v-loading="loading">
    <!-- 搜索区域 -->
    <el-form :model="searchParm" :inline="true" size="default">
      <el-form-item>
        <el-input v-model="searchParm.name" placeholder="请输入赛事名称"></el-input>
      </el-form-item>
      <el-form-item>
        <el-select v-model="searchParm.type" placeholder="请选择类型" style="width: 150px" clearable>
          <el-option
              v-for="item in typeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button icon="Search" @click="searchBtn">搜索</el-button>
        <el-button icon="Refresh" type="danger" @click="resetBtn">重置</el-button>
        <el-button type="primary" @click="showMySignUpRecords = true">我的报名</el-button>
      </el-form-item>
    </el-form>

    <!-- 赛事列表 -->
    <el-table :data="tableList" border stripe>
      <el-table-column prop="name" label="赛事名称" width="150"></el-table-column>
      <el-table-column prop="type" label="类型" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.type === 0" type="success">乒乓球赛</el-tag>
          <el-tag v-else-if="scope.row.type === 1" type="primary">篮球赛</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isTeamCompetition" label="赛事形式" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.isTeamCompetition === 0">个人赛</el-tag>
          <el-tag v-else-if="scope.row.isTeamCompetition === 1" type="warning">团队赛</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="signUpDeadline" label="报名截止时间" width="180"></el-table-column>
      <el-table-column prop="startTime" label="开始时间" width="180"></el-table-column>
      <el-table-column prop="endTime" label="结束时间" width="180"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="info">未开始</el-tag>
          <el-tag v-if="scope.row.status === 1" type="success">正在进行</el-tag>
          <el-tag v-if="scope.row.status === 2" type="warning">已结束</el-tag>
          <el-tag v-if="scope.row.status === 3" type="danger">报名已截止</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button type="primary" size="small" @click="viewDetails(scope.row)">查看详情</el-button>
          <el-button
              v-if="!isSignedUp(scope.row.id)"
              type="success"
              size="small"
              @click="signUp(scope.row)"
              :disabled="!canSignUp(scope.row)"
          >报名
          </el-button>
          <el-button
              v-else
              type="danger"
              size="small"
              @click="cancelSignUp(scope.row)"
          >取消报名
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
        v-model:current-page="searchParm.page"
        v-model:page-size="searchParm.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
    />

    <!-- 详情对话框 -->
    <competition-details
        v-if="showDetails"
        v-model="showDetails"
        :competition="currentCompetition"
    />

    <!-- 个人报名对话框 -->
    <individual-sign-up
        v-if="showSignUp && !selectedCompetition?.isTeamCompetition"
        v-model="showSignUp"
        :competition="currentCompetition"
        @success="handleSignUpSuccess"
    />

    <!-- 团队报名对话框 -->
    <team-sign-up
        v-if="showSignUp && selectedCompetition?.isTeamCompetition"
        v-model="showSignUp"
        :competition="selectedCompetition"
        :selected-team="selectedTeam"
        @success="handleSignUpSuccess"
    />

    <!-- 团队选择对话框 -->
    <team-select
        v-if="showTeamSelect"
        v-model="showTeamSelect"
        :team-options="teamOptions"
        :selected-team-id="selectedTeamId"
        @confirm="confirmTeamSelect"
        @cancel="cancelTeamSelect"
        @team-created="handleTeamCreated"
    />

    <!-- 我的报名记录对话框 -->
    <my-sign-up-records
        v-model="showMySignUpRecords"
        @refresh="loadSignUpStatus"
    />
  </el-main>
</template>

<script setup lang="ts">
import {computed, onMounted, reactive, ref} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {
  CompetitionControllerService,
  CompetitionSignUpTeamControllerService,
  CompetitionSignUpUserControllerService, CompetitionVO,
  TeamControllerService,
  TeamMemberRelationControllerService
} from '../../../../generated';
import CompetitionDetails from '../../../components/competition/CompetitionDetails.vue';
import IndividualSignUp from '../../../components/competition/IndividualSignUp.vue';
import TeamSignUp from '../../../components/competition/TeamSignUp.vue';
import TeamSelect from '../../../components/competition/TeamSelect.vue';
import MySignUpRecords from '../../../components/competition/MySignUpRecords.vue';
import moment from 'moment';
import {userStore} from '@/store/user';

const store = userStore();

// 类型选项
const typeOptions = [
  {value: 0, label: '乒乓球赛'},
  {value: 1, label: '篮球赛'}
];

// 搜索参数
const searchParm = reactive({
  page: 1,
  pageSize: 10,
  name: '',
  type: null,
});

// 数据列表
const tableList = ref<CompetitionVO[]>([]);
const total = ref(0);

// 对话框控制
const showDetails = ref(false);
const showSignUp = ref(false);
const selectedCompetition = ref<CompetitionVO | null>(null);
const showMySignUpRecords = ref(false);

// 报名状态
const signUpStatus = ref<number[]>([]);
// 存储用户报名记录ID
const signUpRecords = ref<Map<number, number>>(new Map());
// 存储用户报名的团队ID
const teamSignUpRecords = ref<Map<number, number>>(new Map());

// 团队选择相关
const showTeamSelect = ref(false);
const teamOptions = ref<{ value: string | number, label: string }[]>([]);
const selectedTeamId = ref<number | undefined>(undefined);
const selectedTeam = ref<any>(null);

// 添加loading状态
const loading = ref(false);

// 获取列表数据
const loadData = async () => {
  try {
    loading.value = true;

    // 同时发起获取比赛列表和报名状态的请求
    const competitionPromise = CompetitionControllerService.listCompetitionUsingGet(
        undefined,
        undefined,
        undefined,
        undefined,
        searchParm.name,
        searchParm.page,
        searchParm.pageSize,
        undefined,
        searchParm.type || undefined
    );

    const statusPromise = loadSignUpStatus();

    // 等待所有请求完成
    const [res] = await Promise.all([competitionPromise, statusPromise]);

    if (res?.data) {
      tableList.value = res.data.records ?? [];
      total.value = res.data.total ?? 0;
    }
  } catch (error) {
    console.error('获取数据失败:', error);
    ElMessage.error('获取数据失败');
  } finally {
    loading.value = false;
  }
};

// 获取用户报名状态
const loadSignUpStatus = async () => {
  try {
    // 重置状态
    signUpStatus.value = [];
    signUpRecords.value.clear();
    teamSignUpRecords.value.clear();

    // 同时发起两个请求，不用等待第一个完成再发起第二个
    const userId = store.getId;

    // 发起个人报名查询
    const userPromise = CompetitionSignUpUserControllerService.listCompetitionSignUpUserUsingGet(
        undefined,
        undefined,
        undefined,
        undefined,
        1,
        100,
        undefined,
        userId
    );

    // 获取用户的团队ID列表
    const userTeamsPromise = getUserTeams();

    // 等待用户团队列表加载完成
    const userTeams = await userTeamsPromise;
    const userTeamIds = userTeams.map(team => team.id);

    // 如果用户有团队，发起团队报名查询
    let teamPromise = Promise.resolve({data: {records: []}});
    if (userTeamIds.length > 0) {
      teamPromise = CompetitionSignUpTeamControllerService.listCompetitionUsingGet1(
          undefined,
          1,
          100,
          undefined,
          undefined
      );
    }

    // 等待所有请求完成
    const [userRes, teamRes] = await Promise.all([userPromise, teamPromise]);

    // 处理个人报名记录
    if (userRes.data?.records) {
      userRes.data.records.forEach((item: any) => {
        signUpStatus.value.push(item.competitionId);
        signUpRecords.value.set(item.competitionId, item.id);
      });
    }

    // 处理团队报名记录
    if (teamRes.data?.records && userTeamIds.length > 0) {
      teamRes.data.records.forEach((teamSignUp: any) => {
        // 检查这个团队报名记录是否对应用户的某个团队
        if (userTeamIds.includes(teamSignUp.teamId)) {
          signUpStatus.value.push(teamSignUp.competitionId);
          teamSignUpRecords.value.set(teamSignUp.competitionId, teamSignUp.id);
        }
      });
    }

    console.log('报名状态已更新:', signUpStatus.value);
  } catch (error) {
    console.error('获取报名状态失败:', error);
    ElMessage.error('获取报名状态失败');
  }
};

// 检查是否已报名
const isSignedUp = (competitionId: number) => {
  return signUpStatus.value.includes(competitionId);
};

// 取消报名
const cancelSignUp = (row: any) => {
  // 根据赛事类型判断是个人报名还是团队报名
  const isTeamCompetition = row.isTeamCompetition === 1;
  let signUpId;

  if (isTeamCompetition) {
    signUpId = teamSignUpRecords.value.get(row.id);
  } else {
    signUpId = signUpRecords.value.get(row.id);
  }

  if (!signUpId) {
    ElMessage.error('未找到报名记录');
    return;
  }

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
          if (isTeamCompetition) {
            res = await CompetitionSignUpTeamControllerService.deleteCompetitionSignUpTeamUsingDelete(Number(signUpId));
          } else {
            res = await CompetitionSignUpUserControllerService.deleteCompetitionSignUpUserUsingDelete(Number(signUpId));
          }

          if (res.code === 0) {
            ElMessage.success('取消报名成功');
            // 刷新数据
            signUpStatus.value = [];
            signUpRecords.value.clear();
            teamSignUpRecords.value.clear();
            loadData();
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

// 查看详情
const viewDetails = (row: any) => {
  selectedCompetition.value = row;
  showDetails.value = true;
};

// 报名
const signUp = async (row: any) => {
  selectedCompetition.value = row;

  // 如果是团队赛事，先检查用户是否在团队中
  if (row.isTeamCompetition === 1) {
    try {
      // 查询用户参与的团队
      const userTeams = await getUserTeams();

      if (userTeams.length === 0) {
        // 用户没有团队，打开团队选择对话框，显示创建团队选项
        teamOptions.value = [];
        showTeamSelect.value = true;
      } else if (userTeams.length === 1) {
        // 用户只有一个团队，直接使用
        selectedTeam.value = userTeams[0];
        showSignUp.value = true;
      } else {
        // 用户有多个团队，显示选择团队对话框
        teamOptions.value = userTeams.map((team: any) => ({
          value: team.id,
          label: team.teamName
        }));
        showTeamSelect.value = true;
      }
    } catch (error) {
      console.error('获取团队信息失败:', error);
      ElMessage.error('获取团队信息失败，请稍后再试');
    }
  } else {
    // 个人赛事直接显示报名对话框
    showSignUp.value = true;
  }
};

// 获取用户的团队列表
const getUserTeams = async () => {
  try {
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
      const userId = store.getId;
      const memberTeamsRes = await TeamMemberRelationControllerService.listTeamUsingGet1(
          1, // page
          100, // pageSize
          undefined, // teamId
          undefined, // teamName
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

// 处理团队选择确认
const confirmTeamSelect = async (teamId: number) => {
  if (!teamId) {
    ElMessage.warning('请选择一个团队');
    return;
  }

  selectedTeamId.value = teamId;
  console.log('收到团队选择确认，ID:', teamId);

  try {
    // 获取选择的团队详情，包括团队成员信息
    const res = await TeamControllerService.getTeamDetailUsingGet(teamId);
    if (res && res.code === 0 && res.data) {
      // 设置selectedTeam并关闭团队选择对话框，打开报名对话框
      selectedTeam.value = res.data;
      showTeamSelect.value = false;
      showSignUp.value = true;
    } else {
      ElMessage.error(res?.msg || '获取团队详情失败');
    }
  } catch (error) {
    console.error('获取团队详情失败:', error);
    ElMessage.error('获取团队详情失败，请稍后再试');
  }
};

// 取消团队选择
const cancelTeamSelect = () => {
  showTeamSelect.value = false;
  selectedTeamId.value = undefined;
};

// 报名成功处理
const handleSignUpSuccess = (result: any) => {
  showSignUp.value = false;  // 关闭报名对话框

  // 如果是团队报名，直接更新状态
  if (selectedCompetition.value && selectedCompetition.value.isTeamCompetition === 1) {
    // 正在处理的比赛ID
    const competitionId = selectedCompetition.value.id;

    // 更新报名状态
    if (!signUpStatus.value.includes(competitionId)) {
      signUpStatus.value.push(competitionId);

      // 如果结果对象中包含ID信息，使用它，否则使用0
      if (result && result.competitionId) {
        teamSignUpRecords.value.set(competitionId, result.id || 0);
      } else {
        // 临时设置一个ID，等loadData执行时会被正确的ID替换
        teamSignUpRecords.value.set(competitionId, 0);
      }
    }
  }

  // 重置选中的团队
  selectedTeam.value = null;

  // 刷新数据
  loadData();
};

// 搜索
const searchBtn = () => {
  searchParm.page = 1;
  loadData();
};

// 重置
const resetBtn = () => {
  searchParm.name = '';
  searchParm.type = null;
  searchParm.page = 1;
  loadData();
};

// 分页处理
const handleSizeChange = (val: number) => {
  searchParm.pageSize = val;
  loadData();
};

const handleCurrentChange = (val: number) => {
  searchParm.page = val;
  loadData();
};

// 判断是否可以报名
const canSignUp = (row: any) => {
  const now = moment();
  const deadline = moment(row.signUpDeadline);
  return now.isBefore(deadline);
};

const currentCompetition = computed(() => {
  return selectedCompetition.value || {};
});

// 处理团队创建成功
const handleTeamCreated = (newTeam: any) => {
  // 添加新团队到选项中
  teamOptions.value.push(newTeam);
  selectedTeamId.value = newTeam.value;
};

// 初始化
onMounted(() => {
  loadData();
});
</script>

<style scoped>
</style>