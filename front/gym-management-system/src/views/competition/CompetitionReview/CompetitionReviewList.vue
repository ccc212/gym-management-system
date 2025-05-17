<template>
  <el-main>
    <h2 style="margin-bottom: 16px">赛事报名审核</h2>

    <!-- 切换类型 -->
    <el-radio-group v-model="signUpType" style="margin-bottom: 16px">
      <el-radio-button label="user">个人赛</el-radio-button>
      <el-radio-button label="team">团体赛</el-radio-button>
    </el-radio-group>

    <!-- 搜索区域 -->
    <el-form :inline="true" :model="searchParams" class="mb-4">
      <el-form-item label="姓名/团队名">
        <el-input v-model="searchParams.nameOrTeamName" placeholder="请输入姓名或团队名"></el-input>
      </el-form-item>
      <el-form-item label="报名状态">
        <el-select v-model="searchParams.status" placeholder="请选择状态" clearable style="width: 150px">
          <el-option label="报名中" :value="0"/>
          <el-option label="报名成功" :value="1"/>
          <el-option label="报名失败" :value="2"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="loadSignUpList">搜索</el-button>
        <el-button icon="Refresh" @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格区域 -->
    <el-table :data="signUps" border stripe size="small">
      <template v-if="signUpType === 'user'">
        <el-table-column prop="name" label="姓名" width="150"></el-table-column>
        <el-table-column prop="userNumber" label="学号/职工号" width="180"></el-table-column>
        <el-table-column prop="phone" label="电话" width="150"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
      </template>
      <template v-else>
        <el-table-column prop="teamName" label="团队名字" width="150"></el-table-column>
        <el-table-column prop="leaderName" label="领队姓名" width="150"></el-table-column>
        <el-table-column prop="leaderPhone" label="联系电话" width="150"></el-table-column>
      </template>
      <el-table-column prop="status" label="状态" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="info">报名中</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="success">报名成功</el-tag>
          <el-tag v-else-if="scope.row.status === 2" type="danger">报名失败</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button
              type="success"
              size="small"
              @click="approveSignUp(scope.row)"
              :disabled="scope.row.status !== 0"
          >通过
          </el-button>
          <el-button
              type="danger"
              size="small"
              @click="rejectSignUp(scope.row)"
              :disabled="scope.row.status !== 0"
          >驳回
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
        :current-page="pagination.currentPage"
        :page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="mt-4"
    />
  </el-main>
</template>

<script setup lang="ts">
import {onMounted, reactive, ref, watch} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {CompetitionSignUpTeamControllerService, CompetitionSignUpUserControllerService} from '../../../../generated';

const competitionId = 1;
const signUpType = ref('user');
const signUps = ref<any[]>([]);
const searchParams = reactive({
  nameOrTeamName: '',
  status: undefined as number | undefined
});
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

const resetSearch = () => {
  searchParams.nameOrTeamName = '';
  searchParams.status = undefined;
  pagination.currentPage = 1;
  loadSignUpList();
};

const loadSignUpList = async () => {
  try {
    let res;
    if (signUpType.value === 'user') {
      res = await CompetitionSignUpUserControllerService.listCompetitionSignUpUserUsingGet(
          undefined,
          undefined,
          undefined,
          searchParams.nameOrTeamName,
          pagination.currentPage,
          pagination.pageSize,
          searchParams.status,
          undefined
      );
    } else {
      res = await CompetitionSignUpTeamControllerService.listCompetitionUsingGet1(
          undefined,
          pagination.currentPage,
          pagination.pageSize,
          searchParams.status,
          searchParams.nameOrTeamName
      );
    }
    if (res?.data?.records) {
      signUps.value = res.data.records;
      pagination.total = res.data.total;
    }
  } catch (error) {
    console.error('加载报名信息失败:', error);
    ElMessage.error('加载报名信息失败');
  }
};

watch(signUpType, () => {
  resetSearch();
});

const handleSizeChange = (val: number) => {
  pagination.pageSize = val;
  pagination.currentPage = 1;
  loadSignUpList();
};

const handleCurrentChange = (val: number) => {
  pagination.currentPage = val;
  loadSignUpList();
};

const approveSignUp = async (row: any) => {
  await updateStatus(row.id, 1, signUpType.value);
};

const rejectSignUp = async (row: any) => {
  ElMessageBox.prompt('请输入拒绝原因', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'textarea'
  }).then(async ({value}) => {
    await updateStatus(row.id, 2, signUpType.value, value);
  }).catch(() => {
    // 用户取消
  });
};

const updateStatus = async (id: number, status: number, type: string, rejectReason?: string) => {
  try {
    let res;
    if (type === 'user') {
      res = await CompetitionSignUpUserControllerService.updateCompetitionSignUpUserUsingPut({
        id,
        status,
        rejectReason
      });
    } else {
      res = await CompetitionSignUpTeamControllerService.updateCompetitionSignUpTeamUsingPut({
        id,
        status,
        rejectReason
      });
    }
    if (res?.code === 0) {
      ElMessage.success(status === 1 ? '审核通过' : '审核驳回');
      loadSignUpList();
    } else {
      ElMessage.error(res.msg || '状态更新失败');
    }
  } catch (error) {
    console.error('状态更新失败:', error);
    ElMessage.error('状态更新失败');
  }
};

onMounted(() => {
  loadSignUpList();
});
</script>

<style scoped>
.mt-4 {
  margin-top: 16px;
}
</style>