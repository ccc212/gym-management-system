<template>
  <el-main>
    <div class="page-header">
      <h2>我的团队</h2>
      <div class="page-actions">
        <el-button type="primary" @click="showCreateTeamDialog = true">创建团队</el-button>
        <el-button type="success" @click="showJoinTeamDialog = true">加入团队</el-button>
        <el-button type="info" @click="showJoinRequestsDialog = true">查看申请状态</el-button>
      </div>
    </div>
    <el-card class="team-info-card" v-for="team in userTeams" :key="team.id">
      <template #header>
        <div class="card-header">
          <h3>{{ team.teamName }}</h3>
          <div class="team-actions">
            <el-button
                v-if="isTeamLeader(team)"
                type="warning"
                size="small"
                @click="showTeamMemberRequests(team)">
              成员申请
              <el-badge
                  v-if="pendingRequests[team.id] && pendingRequests[team.id].length > 0"
                  :value="pendingRequests[team.id].length"
                  class="badge">
              </el-badge>
            </el-button>
            <el-button
                v-if="isTeamLeader(team)"
                type="success"
                size="small"
                @click="showAddMember(team)">
              添加成员
            </el-button>
          </div>
        </div>
      </template>
      <div class="team-basic-info">
        <p><strong>领队：</strong> {{ team.leaderName }}</p>
        <p><strong>联系电话：</strong> {{ team.leaderPhone }}</p>
        <p><strong>所属部门：</strong> {{ getDepartmentName(team.departId) }}</p>
        <p><strong>创建时间：</strong> {{ team.createTime }}</p>
        <p><strong>身份：</strong>
          <el-tag type="success" v-if="isTeamLeader(team)">队长</el-tag>
          <el-tag type="info" v-else>成员</el-tag>
        </p>
      </div>

      <!-- 团队成员信息（如果存在） -->
      <div v-if="teamDetails[team.id]">
        <el-divider content-position="left">团队成员</el-divider>
        <el-table :data="teamDetails[team.id].members" border stripe style="width: 100%">
          <el-table-column prop="name" label="姓名" width="120"></el-table-column>
          <el-table-column prop="userNumber" label="学工号" width="150"></el-table-column>
          <el-table-column prop="sex" label="性别" width="80">
            <template #default="scope">
              {{ scope.row.sex === '0' ? '男' : scope.row.sex === '1' ? '女' : scope.row.sex }}
            </template>
          </el-table-column>
          <el-table-column prop="phone" label="联系电话"></el-table-column>
          <el-table-column label="角色" width="80">
            <template #default="scope">
              <el-tag type="success" v-if="scope.row.name === team.leaderName">队长</el-tag>
              <el-tag type="info" v-else>成员</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" v-if="isTeamLeader(team)">
            <template #default="scope">
              <el-button
                  type="danger"
                  size="small"
                  @click="removeMember(team, scope.row)"
                  :disabled="scope.row.name === team.leaderName">
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <el-empty v-if="userTeams.length === 0" description="您还没有加入任何团队">
      <el-button type="primary" @click="showCreateTeamDialog = true">创建团队</el-button>
      <el-button type="success" @click="showJoinTeamDialog = true">加入团队</el-button>
    </el-empty>

    <!-- 创建团队对话框 -->
    <el-dialog
        title="创建团队"
        v-model="showCreateTeamDialog"
        width="500px"
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

    <!-- 加入团队对话框 -->
    <el-dialog
        title="加入团队"
        v-model="showJoinTeamDialog"
        width="500px"
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
          <el-button @click="showJoinTeamDialog = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 团队申请状态对话框 -->
    <el-dialog
        title="我的申请状态"
        v-model="showJoinRequestsDialog"
        width="600px"
    >
      <el-table
          v-if="joinRequests.length > 0"
          :data="joinRequests"
          style="width: 100%"
          border
      >
        <el-table-column prop="teamName" label="团队名称" width="150"></el-table-column>
        <el-table-column prop="leaderName" label="队长" width="120"></el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="180"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag type="success" v-if="scope.row.status === 1">已通过</el-tag>
            <el-tag type="danger" v-else-if="scope.row.status === 2">已拒绝</el-tag>
            <el-tag type="info" v-else>待审核</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="scope">
            <el-button
                type="danger"
                size="small"
                @click="cancelJoinRequest(scope.row)"
                :disabled="scope.row.status !== 0">
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无申请记录"></el-empty>
    </el-dialog>

    <!-- 成员申请审批对话框 -->
    <el-dialog
        title="成员申请审批"
        v-model="showMemberRequestsDialog"
        width="600px"
    >
      <template #header>
        <div>
          <h3>{{ currentTeam?.teamName }} - 成员申请审批</h3>
        </div>
      </template>

      <el-table
          v-if="pendingRequests[currentTeam?.id]?.length > 0"
          :data="pendingRequests[currentTeam?.id]"
          style="width: 100%"
          border
      >
        <el-table-column prop="name" label="申请人" width="120"></el-table-column>
        <el-table-column prop="userNumber" label="学工号" width="150"></el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="success" size="small" @click="approveJoinRequest(scope.row)">通过</el-button>
            <el-button type="danger" size="small" @click="rejectJoinRequest(scope.row)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无待审批的申请"></el-empty>
    </el-dialog>

    <!-- 添加成员对话框 -->
    <el-dialog
        title="添加成员"
        v-model="showAddMemberDialog"
        width="500px"
    >
      <template #header>
        <div>
          <h3>{{ currentTeam?.teamName || '' }} - 添加成员</h3>
        </div>
      </template>

      <el-form label-width="100px">
        <el-form-item label="选择用户">
          <el-select v-model="selectedUserId" filterable placeholder="请选择用户" style="width: 100%">
            <el-option
                v-for="user in availableUsers"
                :key="user.value"
                :label="user.label"
                :value="user.value"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddMemberDialog = false">取消</el-button>
          <el-button type="primary" @click="addMember" :loading="submitting">确认添加</el-button>
        </span>
      </template>
    </el-dialog>
  </el-main>
</template>

<script setup lang="ts">
import {onMounted, reactive, ref} from 'vue';
import {ElMessage, ElMessageBox, FormInstance} from 'element-plus';
import {TeamControllerService} from '../../../../generated/services/TeamControllerService';
import {TeamMemberRelationControllerService} from '../../../../generated/services/TeamMemberRelationControllerService';
import {DepartmentControllerService} from '../../../../generated/services/DepartmentControllerService';
import {UserControllerService} from '../../../../generated/services/UserControllerService';
import {userStore} from '@/store/user';
import router from "@/router";
import type {Team} from "../../../../generated";

const store = userStore();

// 用户所在的团队列表
const userTeams = ref<Team[]>([]);
// 团队详情（包含成员信息）
const teamDetails = ref<Record<string, any>>({});
// 部门选项
const departmentOptions = ref<{ value: string | number, label: string }[]>([]);
// 加入申请记录
const joinRequests = ref<any[]>([]);
// 待审批的申请（按团队ID分组）
const pendingRequests = ref<Record<string, any[]>>({});
// 当前选中的团队（用于审批/添加成员）
const currentTeam = ref<any>(null);
// 可添加的用户列表
const availableUsers = ref<{ value: string | number, label: string }[]>([]);
// 选中的用户ID（用于添加成员）
const selectedUserId = ref<string | number>('');

// 对话框状态
const showJoinRequestsDialog = ref(false);
const showMemberRequestsDialog = ref(false);
const showAddMemberDialog = ref(false);

// 创建团队对话框
const showCreateTeamDialog = ref(false);
const createTeamFormRef = ref<FormInstance>();
const createTeamForm = reactive({
  teamName: '',
  leaderName: '',
  leaderPhone: '',
  departId: undefined as number | undefined
});

// 创建团队表单验证规则
const createTeamRules = {
  teamName: [{required: true, message: '请输入团队名称', trigger: 'blur'}],
  leaderPhone: [{required: true, message: '请输入联系电话', trigger: 'blur'}],
  departId: [{required: true, message: '请选择所属部门', trigger: 'change'}]
};

// 加入团队对话框
const showJoinTeamDialog = ref(false);
const joinTeamFormRef = ref<FormInstance>();
const joinTeamForm = reactive({
  searchText: ''
});
const availableTeams = ref<any[]>([]);
const searching = ref(false);
const submitting = ref(false);

// 获取部门名称
const getDepartmentName = (departId: number) => {
  const department = departmentOptions.value.find(item => item.value === departId);
  return department ? department.label : '-';
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

// 获取用户所在的团队
const loadUserTeams = async () => {
  try {
    const userId = store.getId;
    if (!userId) {
      ElMessage.warning('未获取到用户ID，请重新登录');
      router.push('/login');
      return;
    }

    // 获取用户创建的团队（作为队长）
    const leaderTeams = await TeamControllerService.listTeamUsingGet(
        undefined, // departId
        store.getName, // leaderName
        1, // page
        100, // pageSize
        undefined // teamName
    );

    let teamsList = [];
    if (leaderTeams && leaderTeams.code === 0 && leaderTeams.data && leaderTeams.data.records) {
      // 过滤出用户是队长的团队
      teamsList = leaderTeams.data.records.filter((team) => {
        return team.leaderName === store.getName;
      });
    }

    // 获取用户加入的团队（作为成员）
    try {
      const memberRelations = await TeamMemberRelationControllerService.listTeamUsingGet1(
          1,
          100,
          undefined,
          undefined,
          userId
      );
      if (memberRelations && memberRelations.code === 0 && memberRelations.data) {
        // 获取已通过的团队申请
        const approvedRelations = memberRelations.data.records.filter(relation => relation.status === 1);
        for (const relation of approvedRelations) {
          // 检查是否已经在队长的团队列表中
          const exists = teamsList.some(team => team.id === relation.teamId);
          if (!exists) {
            // 获取团队详情
            const teamDetail = await TeamControllerService.getTeamDetailUsingGet(Number(relation.teamId));
            if (teamDetail && teamDetail.code === 0 && teamDetail.data) {
              teamsList.push(teamDetail.data);
            }
          }
        }
      }
    } catch (error) {
      console.error('获取用户加入的团队失败:', error);
    }

    userTeams.value = teamsList;

    // 加载每个团队的详情
    userTeams.value.forEach(team => {
      viewTeamDetail(team);
    });

    // 加载审批申请
    userTeams.value.forEach(team => {
      if (isTeamLeader(team)) {
        loadPendingRequests(team.id);
      }
    });
  } catch (error) {
    console.error('获取团队列表失败:', error);
    ElMessage.error('获取团队列表失败，请稍后再试');
  }
};

// 加载待审批的申请
const loadPendingRequests = async (teamId: number) => {
  try {
    // 获取待审核的申请
    const res = await TeamMemberRelationControllerService.getTeamApplicationsUsingGet(
        teamId,
        0 // 待审核状态
    );
    if (res && res.code === 0 && res.data) {
      pendingRequests.value[teamId] = res.data;
    }
  } catch (error) {
    console.error('获取待审批申请失败:', error);
    ElMessage.error('获取待审批申请失败，请稍后再试');
  }
};

// 显示团队成员申请
const showTeamMemberRequests = (team: any) => {
  currentTeam.value = team;
  showMemberRequestsDialog.value = true;
};

// 显示添加成员对话框
const showAddMember = async (team: any) => {
  currentTeam.value = team;

  // 加载可添加的用户
  try {
    const res = await UserControllerService.getListUsingGet5(
        1,  // currentPage
        '',  // name
        1000,   // pageSize
        '',   // userNumber
        ''    // userType
    );

    if (res && res.code === 0 && res.data && res.data.records) {
      // 过滤掉已经是团队成员的用户
      const teamMembers = teamDetails.value[team.id]?.members || [];
      const memberIds = teamMembers.map((member: any) => member.id);

      availableUsers.value = res.data.records
          .filter((user: any) => !memberIds.includes(user.id))
          .map((user: any) => ({
            value: user.id,
            label: `${user.name}(${user.userNumber})`
          }));
    }

    showAddMemberDialog.value = true;
  } catch (error) {
    console.error('获取用户列表失败:', error);
    ElMessage.error('获取用户列表失败，请稍后再试');
  }
};

// 添加成员
const addMember = async () => {
  if (!selectedUserId.value) {
    ElMessage.warning('请选择要添加的用户');
    return;
  }

  submitting.value = true;
  try {
    const res = await TeamControllerService.addTeamMemberUsingPost(
        Number(currentTeam.value.id),
        Number(selectedUserId.value)
    );

    if (res && res.code === 0) {
      ElMessage.success('添加成员成功');
      showAddMemberDialog.value = false;

      // 重新加载团队详情
      await viewTeamDetail(currentTeam.value, true);

      // 清空选择
      selectedUserId.value = '';
    } else {
      ElMessage.error(res?.msg || '添加成员失败');
    }
  } catch (error) {
    console.error('添加成员失败:', error);
    ElMessage.error('添加成员失败，请稍后再试');
  } finally {
    submitting.value = false;
  }
};

// 移除成员
const removeMember = (team: any, member: any) => {
  if (member.id === team.leaderId) {
    ElMessage.warning('无法移除队长');
    return;
  }

  ElMessageBox.confirm(
      `确定要将 ${member.name} 从团队中移除吗？`,
      '移除成员',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(async () => {
    try {
      const res = await TeamControllerService.removeTeamMemberUsingPost(
          Number(team.id),
          Number(member.id)
      );

      if (res && res.code === 0) {
        ElMessage.success('移除成员成功');

        // 重新加载团队详情
        await viewTeamDetail(team, true);
      } else {
        ElMessage.error(res?.msg || '移除成员失败');
      }
    } catch (error) {
      console.error('移除成员失败:', error);
      ElMessage.error('移除成员失败，请稍后再试');
    }
  }).catch(() => {
    // 用户取消操作
  });
};

// 取消加入申请
const cancelJoinRequest = async (request: any) => {
  if (request.status !== 0) {
    ElMessage.warning('只能取消待审核的申请');
    return;
  }

  ElMessageBox.confirm(
      `确定要取消加入"${request.teamName}"的申请吗？`,
      '取消申请',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(async () => {
    try {
      // 调用删除申请的API
      const res = await TeamMemberRelationControllerService.deleteTeamMemberRelationUsingPost(request.id);

      if (res && res.code === 0) {
        ElMessage.success('取消申请成功');

        // 重新加载申请列表
        await loadJoinRequests();
      } else {
        ElMessage.error(res?.msg || '取消申请失败');
      }
    } catch (error) {
      console.error('取消申请失败:', error);
      ElMessage.error('取消申请失败，请稍后再试');
    }
  }).catch(() => {
    // 用户取消操作
  });
};

// 审批通过申请
const approveJoinRequest = async (request: any) => {
  try {
    // 调用批准申请API
    const res = await TeamMemberRelationControllerService.approveApplicationUsingPost(Number(request.id));

    if (res && res.code === 0) {
      ElMessage.success('已批准加入申请');

      // 更新待审批列表
      await loadPendingRequests(currentTeam.value.id);

      // 重新加载团队详情
      await viewTeamDetail(currentTeam.value, true);
    } else {
      ElMessage.error(res?.msg || '批准申请失败');
    }
  } catch (error) {
    console.error('批准申请失败:', error);
    ElMessage.error('批准申请失败，请稍后再试');
  }
};

// 拒绝申请
const rejectJoinRequest = async (request: any) => {
  try {
    // 调用拒绝申请API
    const res = await TeamMemberRelationControllerService.rejectApplicationUsingPost(Number(request.id));

    if (res && res.code === 0) {
      ElMessage.success('已拒绝加入申请');

      // 更新待审批列表
      await loadPendingRequests(currentTeam.value.id);
    } else {
      ElMessage.error(res?.msg || '拒绝申请失败');
    }
  } catch (error) {
    console.error('拒绝申请失败:', error);
    ElMessage.error('拒绝申请失败，请稍后再试');
  }
};

// 查看团队详情
const viewTeamDetail = async (team: any, forceRefresh = false) => {
  // 如果已经加载过且不是强制刷新，不重复加载
  if (teamDetails.value[team.id] && !forceRefresh) {
    return;
  }

  try {
    const res = await TeamControllerService.getTeamDetailUsingGet(Number(team.id));
    if (res && res.code === 0 && res.data) {
      teamDetails.value[team.id] = res.data;
    } else {
      ElMessage.error(res?.msg || '获取团队详情失败');
    }
  } catch (error) {
    console.error('获取团队详情失败:', error);
    ElMessage.error('获取团队详情失败，请稍后再试');
  }
};

// 判断是否是队长
const isTeamLeader = (team: any) => {
  return team.leaderName === store.getName;
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

    // 获取当前用户ID
    const userId = store.getId;

    // 设置领队信息
    const formData = {
      ...createTeamForm,
      leaderName: store.getName || createTeamForm.leaderName,
      memberIds: [userId] // 添加当前用户为团队成员
    };

    const res = await TeamControllerService.addTeamUsingPost(formData);

    if (res && res.code === 0) {
      ElMessage.success('团队创建成功');
      showCreateTeamDialog.value = false;

      // 重新加载团队列表
      await loadUserTeams();

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
      const joinedTeamIds = userTeams.value.map(team => team.id);
      availableTeams.value = res.data.records.filter(team => !joinedTeamIds.includes(team.id));
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
      showJoinTeamDialog.value = false;

      // 重新加载申请状态
      await loadJoinRequests();

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

// 加载用户申请状态
const loadJoinRequests = async () => {
  try {
    const userId = store.getId;
    if (!userId) {
      ElMessage.warning('未获取到用户ID，请重新登录');
      return;
    }

    // 获取用户申请记录
    const res = await TeamMemberRelationControllerService.getUserApplicationsUsingGet(userId);
    if (res && res.code === 0 && res.data) {
      // 处理数据
      const requests = await Promise.all(res.data.map(async (relation) => {
        // 获取团队信息
        const teamDetail = await TeamControllerService.getTeamDetailUsingGet(relation.teamId);

        return {
          id: relation.id,
          teamId: relation.teamId,
          teamName: teamDetail?.data?.teamName || `团队(${relation.teamId})`,
          leaderName: teamDetail?.data?.leaderName || '',
          applyTime: relation.createTime,
          status: relation.status // 0: 待审核, 1: 已通过, 2: 已拒绝
        };
      }));

      joinRequests.value = requests;
    }
  } catch (error) {
    console.error('获取申请状态失败:', error);
    ElMessage.error('获取申请状态失败，请稍后再试');
  }
};

// 初始化
onMounted(async () => {
  await loadDepartments();
  await loadUserTeams();
  await loadJoinRequests();
  loadCreateTeamFormUserInfo();
});
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-actions {
  display: flex;
  gap: 10px;
}

.team-info-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.team-basic-info {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 10px;
  margin-bottom: 20px;
}

.team-basic-info p {
  margin: 5px 0;
}

.search-container {
  display: flex;
  gap: 10px;
}

.dialog-footer {
  padding-top: 20px;
  text-align: right;
}

.team-actions {
  display: flex;
  gap: 10px;
}

.badge {
  margin-top: -8px;
  margin-right: -8px;
}
</style> 