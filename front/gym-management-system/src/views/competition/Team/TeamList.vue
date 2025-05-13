<template>
  <el-main>
    <!-- 搜索区域 -->
    <el-form :model="searchParm" :inline="true" size="default">
      <el-form-item>
        <el-input v-model="searchParm.teamName" placeholder="请输入团队名称"></el-input>
      </el-form-item>
      <el-form-item>
        <el-input v-model="searchParm.leaderName" placeholder="请输入领队姓名"></el-input>
      </el-form-item>
      <el-form-item style="width: 200px;">
        <el-select v-model="searchParm.departId" placeholder="请选择部门" clearable>
          <el-option
              v-for="item in departmentOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button icon="Search" @click="searchBtn">搜索</el-button>
        <el-button icon="Refresh" type="danger" @click="resetBtn">重置</el-button>
        <el-button icon="Plus" type="primary" @click="addBtn">新增</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格 -->
    <el-table :height="tableHeight" :data="tableList" border stripe>
      <el-table-column prop="id" label="ID" width="80px"></el-table-column>
      <el-table-column prop="teamName" label="团队名称" width="150px"></el-table-column>
      <el-table-column prop="leaderName" label="领队姓名" width="120px"></el-table-column>
      <el-table-column prop="leaderPhone" label="联系电话" width="120px"></el-table-column>
      <el-table-column prop="departName" label="所属部门" width="120px">
        <template #default="scope">
          {{ getDepartmentName(scope.row.departId) }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160px"></el-table-column>
      <el-table-column label="操作" width="420px">
        <template #default="scope">
          <el-button type="primary" icon="View" size="small" @click="viewBtn(scope.row)">查看成员</el-button>
          <el-button type="warning" icon="Edit" size="small" @click="editBtn(scope.row)">编辑</el-button>
          <el-button type="danger" icon="Delete" size="small" @click="deleteBtn(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
        :current-page.sync="searchParm.page"
        :page-sizes="[10, 20, 40]"
        :page-size="searchParm.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="searchParm.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
    />

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="openDialog" width="500px" append-to-body>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="团队名称" prop="teamName">
          <el-input v-model="formData.teamName" placeholder="请输入团队名称" />
        </el-form-item>
        <el-form-item label="领队姓名" prop="leaderName">
          <el-input v-model="formData.leaderName" placeholder="请输入领队姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="leaderPhone">
          <el-input v-model="formData.leaderPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="所属部门" prop="departId">
          <el-select v-model="formData.departId" placeholder="请选择部门" clearable>
            <el-option
                v-for="item in departmentOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="成员" v-if="formData.id">
          <el-select v-model="formData.memberIds" multiple placeholder="请选择成员" clearable>
            <el-option
                v-for="item in userOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancelDialog">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 成员详情对话框 -->
    <el-dialog title="团队成员" v-model="openMemberDialog" width="800px" append-to-body>
      <el-table :data="memberList" border>
        <el-table-column prop="id" label="ID" width="80px"></el-table-column>
        <el-table-column prop="name" label="姓名" width="120px"></el-table-column>
        <el-table-column prop="userNumber" label="学号/工号" width="150px"></el-table-column>
        <el-table-column prop="sex" label="性别" width="80px"></el-table-column>
        <el-table-column prop="phone" label="电话" width="120px"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="150px"></el-table-column>
        <el-table-column label="操作" width="120px">
          <template #default="scope">
            <el-button type="danger" icon="Delete" size="small" @click="removeMemberBtn(scope.row.id)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </el-main>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watchEffect } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { TeamControllerService } from '../../../../generated/services/TeamControllerService';
import { DepartmentControllerService } from '../../../../generated/services/DepartmentControllerService';
import { UserControllerService } from '../../../../generated/services/UserControllerService';

// 表格高度
const tableHeight = ref(0);

// 部门选项
const departmentOptions = ref([]);

// 用户选项
const userOptions = ref([]);

// 获取部门选项
const getDepartmentOptions = async () => {
  try {
    const res = await DepartmentControllerService.selectListUsingGet();
    if (res && res.data) {
      departmentOptions.value = res.data.map((item: any) => ({
        value: item.value,
        label: item.label
      }));
    }
  } catch (error) {
    console.error('获取部门列表失败:', error);
    ElMessage.error('获取部门列表失败');
  }
};

// 获取用户选项
const getUserOptions = async () => {
  try {
    const res = await UserControllerService.getListUsingGet5(
        1,  // currentPage
        '',  // name
        1000,   // pageSize
        '',   // userNumber
        ''    // userType
    );
    if (res && res.data && res.data.records) {
      userOptions.value = res.data.records.map((item: any) => ({
        value: item.id,
        label: `${item.name}(${item.userNumber})` // 显示姓名和工号，方便识别
      }));
    }
  } catch (error) {
    console.error('获取用户列表失败:', error);
    ElMessage.error('获取用户列表失败');
  }
};

// 列表数据
const tableList = ref([]);
const memberList = ref([]);

// 搜索参数
const searchParm = reactive({
  page: 1,
  pageSize: 10,
  total: 0,
  teamName: '',
  leaderName: '',
  departId: null as number | null
});

// 对话框相关
const openDialog = ref(false);
const dialogTitle = ref('');
const formRef = ref(null);
const formData = reactive({
  id: '',
  teamName: '',
  leaderName: '',
  leaderPhone: '',
  departId: null as number | null,
  memberIds: [] as number[]
});

// 成员对话框
const openMemberDialog = ref(false);
const currentTeamId = ref('');

// 表单校验规则
const rules = {
  teamName: [
    { required: true, message: '请输入团队名称', trigger: 'blur' }
  ],
  leaderName: [
    { required: true, message: '请输入领队姓名', trigger: 'blur' }
  ],
  leaderPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' }
  ],
  departId: [
    { required: true, message: '请选择所属部门', trigger: 'change' }
  ]
};

// 获取部门名称
const getDepartmentName = (departId: number) => {
  const department = departmentOptions.value.find(item => item.value === departId);
  return department ? department.label : '-';
};

// 获取列表数据
const loadData = async () => {
  try {
    // 确保部门选项已加载
    if (departmentOptions.value.length === 0) {
      await getDepartmentOptions();
    }

    const res = await TeamControllerService.listTeamUsingGet(
        searchParm.departId ? Number(searchParm.departId) : undefined,
        searchParm.leaderName,
        searchParm.page,
        searchParm.pageSize,
        searchParm.teamName
    );
    if (res && res.data) {
      tableList.value = res.data.records;
      searchParm.total = res.data.total;
    }
  } catch (error) {
    console.error('加载数据失败:', error);
    ElMessage.error('加载数据失败，请检查网络或联系管理员');
  }
};

/**
 * 监听 searchParm 变量，改变时触发页面的重新加载
 */
watchEffect(() => {
  const { page, pageSize, teamName, leaderName, departId } = searchParm;
  loadData();
});

/**
 * 页面加载时，计算表格高度
 */
onMounted(() => {
  // 计算表格高度
  tableHeight.value = window.innerHeight - 320;
  // 获取部门和用户选项
  getDepartmentOptions();
  getUserOptions();
});

// 搜索按钮
const searchBtn = () => {
  searchParm.page = 1;
};

// 重置按钮
const resetBtn = () => {
  searchParm.teamName = '';
  searchParm.leaderName = '';
  searchParm.departId = null;
  searchParm.page = 1;
};

// 新增按钮
const addBtn = () => {
  dialogTitle.value = '新增团队';
  formData.id = '';
  formData.teamName = '';
  formData.leaderName = '';
  formData.leaderPhone = '';
  formData.departId = null;
  formData.memberIds = [];
  openDialog.value = true;
};

// 编辑按钮
const editBtn = (row: any) => {
  dialogTitle.value = '编辑团队';
  formData.id = row.id;
  formData.teamName = row.teamName;
  formData.leaderName = row.leaderName;
  formData.leaderPhone = row.leaderPhone;
  formData.departId = row.departId;

  // 获取成员列表
  TeamControllerService.getTeamDetailUsingGet(Number(row.id)).then(res => {
    if (res && res.data && res.data.members) {
      formData.memberIds = res.data.members.map((member: any) => member.id);
    }
  });

  openDialog.value = true;
};

// 查看成员按钮
const viewBtn = async (row: any) => {
  currentTeamId.value = row.id;
  // 获取团队详情及成员列表
  const res = await TeamControllerService.getTeamDetailUsingGet(Number(row.id));
  if (res && res.data && res.data.members) {
    memberList.value = res.data.members;
    openMemberDialog.value = true;
  }
  return res;
};

// 移除成员按钮
const removeMemberBtn = (userId: string) => {
  ElMessageBox.confirm('确定要移除此成员吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await TeamControllerService.removeTeamMemberUsingPost(
          Number(currentTeamId.value),
          Number(userId)
      );
      if (res && res.code === 0) {
        ElMessage.success('移除成员成功');
        // 刷新成员列表
        await viewBtn({ id: currentTeamId.value });
      } else {
        ElMessage.error(res?.msg || '移除成员失败');
      }
    } catch (error) {
      console.error('移除成员错误:', error);
      ElMessage.error('移除成员失败，请检查网络或联系管理员');
    }
  }).catch(() => {});
};

// 删除按钮
const deleteBtn = (id: string) => {
  ElMessageBox.confirm('确定要删除此团队吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await TeamControllerService.deleteTeamUsingDelete(Number(id));
      if (res && res.code === 0) {
        ElMessage.success('删除成功');
        // 刷新数据
        loadData();
      } else {
        ElMessage.error(res?.msg || '删除失败');
      }
    } catch (error) {
      console.error('删除错误:', error);
      ElMessage.error('删除失败，请检查网络或联系管理员');
    }
  }).catch(() => {});
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;

  formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        const res = formData.id ?
            await TeamControllerService.updateTeamUsingPut(formData) :
            await TeamControllerService.addTeamUsingPost(formData);

        if (res && res.code === 0) {
          // 先关闭对话框
          openDialog.value = false;
          ElMessage.success(formData.id ? '更新成功' : '添加成功');
          // 刷新数据
          loadData();
        } else {
          ElMessage.error(res?.msg || '操作失败');
        }
      } catch (error) {
        console.error('提交表单错误:', error);
        ElMessage.error('操作失败，请检查网络或联系管理员');
      }
    }
  });
};

// 取消对话框
const cancelDialog = () => {
  openDialog.value = false;
};

// 分页大小改变
const handleSizeChange = (val: number) => {
  searchParm.pageSize = val;
};

// 当前页改变
const handleCurrentChange = (val: number) => {
  searchParm.page = val;
};
</script>

<style scoped>
</style> 