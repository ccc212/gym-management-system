<template>
  <el-main>
    <h2 style="margin-bottom: 16px">器材管理</h2>

    <!-- 搜索区域 -->
    <el-form :model="searchParams" :inline="true" size="default" class="mb-4">
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
      <el-table-column label="操作" width="280px">
        <template #default="scope">
          <el-button
              type="primary"
              icon="View"
              size="small"
              @click="viewDetails(scope.row)"
          >查看详情</el-button>
          <el-button
              type="success"
              icon="Edit"
              size="small"
              @click="reserveEquipment(scope.row)"
          >预约器材</el-button>
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

    <!-- 器材详情组件 -->
    <EquipmentDetails
        v-if="showDetails"
        :equipment="selectedEquipment"
        @close="showDetails = false"
    />

    <!-- 预约器材组件 -->
    <equipment-reservation
        v-model="openEquipmentDialog"
        :competition-id="currentCompetition?.id"
        :competition-name="currentCompetition?.name"
        @success="handleEquipmentReservationSuccess"
    />

  </el-main>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { EquipBasicsControllerService } from '../../../../generated/services/EquipBasicsControllerService';
import type { Equipment } from '../../../../generated/models/Equipment';
import EquipmentDetails from '../../../components/equipment/Details.vue';
import EquipmentReservation from '../../../components/competition/EquipmentReservation.vue';
import {CompetitionVO} from "../../../../generated";

const statusOptions = [
  { value: 0, label: '在库中' },
  { value: 1, label: '已借出' },
  { value: 2, label: '待维修' }
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

const selectedEquipment = ref<Equipment | null>(null);
const showDetails = ref(false);
const showReservation = ref(false);

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

watch(
    () => searchParams.value,
    () => {
      loadData();
    },
    { deep: true }
);

onMounted(loadData);

const resetBtn = () => {
  Object.assign(searchParams.value, {
    equipmentName: '',
    status: null,
    type: '',
    currentPage: 1
  });
  loadData();
};
// 器材预约对话框相关
const openEquipmentDialog = ref(false);


const currentCompetition = ref<CompetitionVO | null>(null);

const viewDetails = (row: Equipment) => {
  selectedEquipment.value = row;
  showDetails.value = true;
};

// 器材预约按钮
const reserveEquipment = (row: any) => {
  currentCompetition.value = row;
  openEquipmentDialog.value = true;
};

// 器材预约成功回调
const handleEquipmentReservationSuccess = () => {
  loadData();
};


const handleSizeChange = (val: number) => {
  searchParams.value.pageSize = val;
  loadData();
};

const handleCurrentChange = (val: number) => {
  searchParams.value.currentPage = val;
  loadData();
};

const onReservationSuccess = () => {
  ElMessage.success('预约成功！');
  showReservation.value = false;
  loadData();
};
</script>
