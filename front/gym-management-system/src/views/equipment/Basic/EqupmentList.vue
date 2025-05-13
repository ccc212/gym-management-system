<script setup lang="ts">
import { ref } from 'vue';
import {
  getListApi as getEquipmentList,
  addApi as addEquipment,
  updateApi as updateEquipment,
  deleteApi as deleteEquipment,
  approveRepairApi as approveRepair,
  lendEquipmentApi as borrowEquipment
} from '../../../api/equip/equipment/index';
import type { Equipment } from '../../../api/equip/equipment/EquipmentModel'

const equipmentList = ref<Equipment[]>([]);
const selectedEquipment = ref<Equipment | null>(null);
const showDialog = ref(false);
const dialogType = ref<'add' | 'edit'>('add');

const fetchEquipment = async () => {
  const result = await getEquipmentList({});
  equipmentList.value = result.data;
};

const handleAdd = () => {
  dialogType.value = 'add';
  selectedEquipment.value = null;
  showDialog.value = true;
};

const handleEdit = (equipment: Equipment) => {
  dialogType.value = 'edit';
  selectedEquipment.value = equipment;
  showDialog.value = true;
};

const handleDelete = async (id: string) => {
  await deleteEquipment(id);
  await fetchEquipment();
};

const handleApproveRepair = async (id: string) => {
  await approveRepair(id, true);
  await fetchEquipment();
};

const handleBorrow = async (id: string) => {
  await borrowEquipment(Number(id));
  await fetchEquipment();
};

const handleSubmit = async (equipment: Equipment) => {
  if (dialogType.value === 'add') {
    const newEquipment = {
      ...equipment,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      approvalStatus: ''
    };
    await addEquipment(newEquipment);
  } else {
    const updatedEquipment = {
      ...equipment,
      createdAt: equipment.createdAt ?? new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      approvalStatus: equipment.approvalStatus ?? ''
    };
    await updateEquipment(updatedEquipment);
  }
  showDialog.value = false;
  await fetchEquipment();
};

fetchEquipment();
</script>

<template>
  <div class="container">
    <el-button type="primary" @click="handleAdd">添加器材</el-button>
    <el-table :data="equipmentList" style="width: 100%">
      <el-table-column prop="name" label="器材名称" />
      <el-table-column prop="status" label="状态" />
      <el-table-column label="操作" width="300">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          <el-button size="small" type="warning" @click="handleApproveRepair(scope.row.id)">审核报修</el-button>
          <el-button size="small" type="success" @click="handleBorrow(scope.row.id)">借出</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showDialog" :title="dialogType === 'add' ? '添加器材' : '编辑器材'">
      <el-form :model="selectedEquipment">
        <el-form-item label="器材名称">
          <el-input v-model="selectedEquipment.name" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="selectedEquipment.status">
            <el-option label="可用" value="available" />
            <el-option label="维修中" value="repairing" />
            <el-option label="已借出" value="borrowed" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit(selectedEquipment)">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.container {
  padding: 20px;
}
</style>