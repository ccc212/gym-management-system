<template>
  <el-dialog
      title="器材预约审核"
      v-model="dialogVisible"
      width="900px"
      append-to-body
      :before-close="handleClose"
      v-loading="loading"
      :destroy-on-close="false"
  >
    <!-- 搜索区域 -->
    <el-form :inline="true" :model="searchParams" class="mb-4">
      <el-form-item label="器材名称">
        <el-input v-model="searchParams.equipmentName" placeholder="请输入器材名称"></el-input>
      </el-form-item>
      <el-form-item label="预约状态">
        <el-select v-model="searchParams.status" placeholder="请选择状态" clearable style="width: 150px">
          <el-option label="预约中" :value="0" />
          <el-option label="预约成功" :value="1" />
          <el-option label="预约失败" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="loadReservedEquipments">搜索</el-button>
        <el-button icon="Refresh" @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格区域 -->
    <el-table :data="reservedEquipments" border stripe size="small">
      <el-table-column prop="equipmentName" label="器材名称" width="150"></el-table-column>
      <el-table-column prop="num" label="预约数量" width="100"></el-table-column>
      <el-table-column prop="startTime" label="开始时间" width="180"></el-table-column>
      <el-table-column prop="endTime" label="结束时间" width="180"></el-table-column>
      <el-table-column prop="status" label="状态" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="info">预约中</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="success">预约成功</el-tag>
          <el-tag v-else-if="scope.row.status === 2" type="danger">预约失败</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button
              type="success"
              size="small"
              @click="approveReservation(scope.row)"
              :disabled="scope.row.status !== 0"
          >通过</el-button>
          <el-button
              type="danger"
              size="small"
              @click="updateStatus(scope.row.id, 2)"
              :disabled="scope.row.status !== 0"
          >驳回</el-button>
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

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { CompetitionEquipmentRelationControllerService } from '../../../../generated/services/CompetitionEquipmentRelationControllerService';
import { EquipBasicsControllerService } from '../../../../generated/services/EquipBasicsControllerService';

// 直接写死一个竞赛ID，实际可以从路由或者其他来源获取
const competitionId = 1;

const dialogVisible = ref(true);
const loading = ref(false);
const reservedEquipments = ref<any[]>([]);

const searchParams = reactive({
  equipmentName: '',
  status: undefined as number | undefined
});

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

const handleClose = () => {
  dialogVisible.value = false;
};

const resetSearch = () => {
  searchParams.equipmentName = '';
  searchParams.status = undefined;
  pagination.currentPage = 1;
  loadReservedEquipments();
};

const loadReservedEquipments = async () => {
  try {
    loading.value = true;
    let res;
    if (searchParams.status !== undefined) {
      res = await CompetitionEquipmentRelationControllerService.listCompetitionEquipmentRelationUsingGet(
          competitionId,
          undefined,
          pagination.currentPage,
          pagination.pageSize,
          searchParams.status
      );
    } else {
      res = await CompetitionEquipmentRelationControllerService.listCompetitionEquipmentRelationUsingGet(
          competitionId,
          undefined,
          pagination.currentPage,
          pagination.pageSize
      );
    }
    if (res?.data?.records) {
      reservedEquipments.value = res.data.records;
      pagination.total = res.data.total;
    }
  } catch (error) {
    console.error('加载预约信息失败:', error);
    ElMessage.error('加载预约信息失败');
  } finally {
    loading.value = false;
  }
};

const updateStatus = async (id: number, status: number) => {
  try {
    loading.value = true;
    const res = await CompetitionEquipmentRelationControllerService.updateCompetitionEquipmentRelationUsingPut({
      id,
      status
    });
    if (res?.code === 0) {
      ElMessage.success(status === 1 ? '审核通过' : '审核驳回');
      loadReservedEquipments();
    } else {
      ElMessage.error(res.msg || '状态更新失败');
    }
  } catch (error) {
    console.error('状态更新失败:', error);
    ElMessage.error('状态更新失败');
  } finally {
    loading.value = false;
  }
};

const approveReservation = async (row: any) => {
  ElMessageBox.confirm('确定要通过该预约申请吗？', '确认审核', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await updateStatus(row.id, 1);
    if (row.equipmentId) {
      try {
        await EquipBasicsControllerService.updateUsingPut1({
          id: row.equipmentId,
          status: 1
        });
      } catch (e) {
        console.warn('更新器材状态失败:', e);
      }
    }
  }).catch(() => {
    // 用户取消
  });
};

onMounted(() => {
  if (dialogVisible.value) loadReservedEquipments();
});

const handleSizeChange = (val: number) => {
  pagination.pageSize = val;
  pagination.currentPage = 1;
  loadReservedEquipments();
};

const handleCurrentChange = (val: number) => {
  pagination.currentPage = val;
  loadReservedEquipments();
};
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style>