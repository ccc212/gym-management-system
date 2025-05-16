<template>
  <el-main>
    <h2 style="margin-bottom: 16px">器材报修管理</h2>

    <el-table :data="repairRecords" border stripe>
      <el-table-column prop="equipmentName" label="器材名称" width="150" />
      <el-table-column prop="repairReason" label="报修原因" />
      <el-table-column prop="repairTime" label="报修时间" width="180" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 2" type="danger">报修中</el-tag>
          <el-tag v-else type="info">正常</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <el-form inline class="mt-4" :model="repairForm">
      <el-form-item label="器材 ID">
        <el-input-number v-model="repairForm.id" :min="1" />
      </el-form-item>
      <el-form-item label="报修原因">
        <el-input v-model="repairForm.reason" placeholder="请输入报修原因" style="width: 300px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitRepair">提交报修</el-button>
      </el-form-item>
    </el-form>
  </el-main>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { EquipmentRepairControllerService } from '../../../../generated/services/EquipmentRepairControllerService';
import { EquipBasicsControllerService } from '../../../../generated/services/EquipBasicsControllerService';

const repairRecords = ref<any[]>([]);
const repairForm = ref({ id: undefined as number | undefined, reason: '' });

const loadRecords = async () => {
  try {
    const equipRes = await EquipBasicsControllerService.getListUsingGet1(1, '', 999);
    if (!equipRes?.data?.records) return;
    const list = equipRes.data.records.filter((item: any) => item.status === 2);

    repairRecords.value = list.map((item: any) => ({
      id: item.id,
      equipmentName: item.equipmentName,
      repairReason: item.repairReason,
      repairTime: item.repairTime,
      status: item.status
    }));
  } catch (error) {
    ElMessage.error('获取报修记录失败');
  }
};

const submitRepair = async () => {
  if (!repairForm.value.id || !repairForm.value.reason.trim()) {
    ElMessage.warning('请填写器材 ID 和报修原因');
    return;
  }
  try {
    const res = await EquipmentRepairControllerService.reportRepairUsingPost(
        repairForm.value.id,
        repairForm.value.reason
    );
    if (res?.code === 0) {
      ElMessage.success('报修成功');
      repairForm.value.id = undefined;
      repairForm.value.reason = '';
      loadRecords();
    } else {
      ElMessage.error(res.msg || '报修失败');
    }
  } catch (error) {
    ElMessage.error('报修提交失败');
  }
};

onMounted(loadRecords);
</script>

<style scoped>
.mt-4 {
  margin-top: 20px;
}
</style>
