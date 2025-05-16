<template>
  <el-main>
    <h2 style="margin-bottom: 16px">器材报修</h2>

    <!-- 搜索区域 -->
    <el-form :model="searchParams" :inline="true" size="default">
      <el-form-item>
        <el-input v-model="searchParams.equipmentName" placeholder="请输入器材名称"></el-input>
      </el-form-item>
      <el-form-item style="width: 200px;">
        <el-select v-model="searchParams.status" placeholder="请选择器材状态" clearable>
          <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button icon="Refresh" type="danger" @click="resetBtn">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格 -->
    <el-table :data="tableList" border stripe>
      <el-table-column prop="id" label="ID" width="80px"></el-table-column>
      <el-table-column prop="equipmentName" label="器材名称" width="150px"></el-table-column>
      <el-table-column prop="type" label="器材类型" width="150px"></el-table-column>
      <el-table-column prop="remainingQuantity" label="仓库剩余数量" width="150px"></el-table-column>
      <el-table-column prop="administrator" label="器材管理员" width="120px"></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160px"></el-table-column>
      <el-table-column prop="status" label="状态" width="120px">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="success">在库中</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="warning">已借出</el-tag>
          <el-tag v-else-if="scope.row.status === 2" type="danger">待维修</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150px">
        <template #default="scope">
          <el-button type="info" icon="Warning" size="small" @click="repairBtn(scope.row)">报修</el-button>
          <el-button type="primary" icon="View" size="small" @click="viewBtn(scope.row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
        :current-page.sync="searchParams.currentPage"
        :page-sizes="[10, 20, 40]"
        :page-size="searchParams.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
    />

    <!-- 报修组件 -->
    <repair
        v-model="openRepair"
        :id="currentRepair?.id"
        :equipmentName="currentRepair?.equipmentName"
        :administrator="currentRepair?.administrator"
    />

    <!-- 详情对话框 -->
    <el-dialog title="器材详情" v-model="detailDialogVisible" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="器材编号">{{ detailData?.id }}</el-descriptions-item>
        <el-descriptions-item label="器材名称">{{ detailData?.equipmentName }}</el-descriptions-item>
        <el-descriptions-item label="器材类型">{{ detailData?.type }}</el-descriptions-item>
        <el-descriptions-item label="仓库剩余数量">{{ detailData?.remainingQuantity }}</el-descriptions-item>
        <el-descriptions-item label="器材管理员">{{ detailData?.administrator }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detailData?.status === 0" type="success">在库中</el-tag>
          <el-tag v-else-if="detailData?.status === 1" type="warning">已借出</el-tag>
          <el-tag v-else-if="detailData?.status === 2" type="danger">待维修</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData?.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </el-main>
</template>

<script setup lang="ts">
import { nextTick, onMounted, ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { EquipBasicsControllerService } from '../../../../generated/services/EquipBasicsControllerService';
import type { Equipment } from '../../../../generated/models/Equipment';
import Repair from '../../../components/equipment/Repair.vue';

const statusOptions = [
  { value: 0, label: '在库中' },
  { value: 1, label: '已借出' },
  { value: 2, label: '报修中' }
];
const typeOptions = [
  { value: 'BALL', label: '球类器材' },
  { value: 'FIELD', label: '场地所需器材' }
];

const tableList = ref<Equipment[]>([]);
const total = ref(0);
const searchParams = ref({
  equipmentName: '',
  status: null,
  type: '',
  currentPage: 1,
  pageSize: 10
});

// 报修相关
const openRepair = ref(false);
const currentRepair = ref<Equipment | null>(null);

const dialogVisible = ref(false);
const detailDialogVisible = ref(false);
const detailData = ref<Equipment | null>(null);

const statusLabel = (val: number) => statusOptions.find(s => s.value === val)?.label || '';
const statusTagType = (val: number) => (val === 0 ? 'info' : val === 1 ? 'success' : 'danger');

const loadData = async () => {
  try {
    const res = await EquipBasicsControllerService.getListUsingGet1(
        searchParams.value.currentPage,
        searchParams.value.equipmentName,
        searchParams.value.pageSize,
        searchParams.value.status ?? undefined
    );
    if (res?.data?.records) {
      tableList.value = res.data.records;
      total.value = res.data.total;
    }
  } catch (e) {
    ElMessage.error('加载器材数据失败');
  }
};

// 监听 searchParams 变化，自动加载数据
watch(
    () => searchParams.value,
    () => {
      loadData();
    },
    { deep: true }
);

onMounted(loadData);

const resetBtn = () => {
  Object.assign(searchParams.value, { equipmentName: '', status: null, type: '', currentPage: 1 });
  loadData();
};

const repairBtn = (row: Equipment) => {
  currentRepair.value = row;
  openRepair.value = true;
};

const viewBtn = (row: Equipment) => {
  detailData.value = row;
  detailDialogVisible.value = true;
};

const handleSizeChange = (val: number) => {
  searchParams.value.pageSize = val;
  loadData();
};

const handleCurrentChange = (val: number) => {
  searchParams.value.currentPage = val;
  loadData();
};
</script>
