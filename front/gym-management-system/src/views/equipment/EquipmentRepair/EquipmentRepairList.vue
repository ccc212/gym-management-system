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
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button size="small" type="danger" @click="openRepairDialog(scope.row.id)">审核</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="器材报修" v-model="dialogVisible" width="500px" append-to-body>
      <el-form :model="repairForm" label-width="100px">
        <el-form-item label="器材 ID">
          <el-input-number v-model="repairForm.id" :min="1" disabled />
        </el-form-item>
        <el-form-item label="原因">
          <el-input type="textarea" v-model="repairForm.reason" placeholder="请输入审核原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRepair">提交</el-button>
        </div>
      </template>
    </el-dialog>
  </el-main>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { EquipmentRepairControllerService } from '../../../../generated/services/EquipmentRepairControllerService';
import { EquipBasicsControllerService } from '../../../../generated/services/EquipBasicsControllerService';

const repairRecords = ref<any[]>([]);
const dialogVisible = ref(false);
const repairForm = ref({ id: undefined as number | undefined, reason: '' });

const loadRecords = async () => {
  try {
    const equipRes = await EquipBasicsControllerService.getListUsingGet1(1, '', 999);
    if (!equipRes?.data?.records) return;
    // 只筛选状态为“报修中”的器材
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

const openRepairDialog = (id: number) => {
  repairForm.value.id = id;
  repairForm.value.reason = '';
  dialogVisible.value = true;
};

const submitRepair = async () => {
  if (!repairForm.value.id || !repairForm.value.reason.trim()) {
    ElMessage.warning('请填写审核原因');
    return;
  }
  try {
    const repairPayload = {
      equipmentId: repairForm.value.id,
      reason: repairForm.value.reason.trim(),
      applyTime: new Date().toISOString(),
    };

    // 调用接口新增报修记录
    const res = await EquipmentRepairControllerService.addRepairUsingPost(repairPayload);

    if (res?.code === 0) {
      // 报修添加成功后，更新器材状态为报修中（2）
      await EquipBasicsControllerService.updateUsingPut1({
        id: repairForm.value.id,
        status: 2
      });

      ElMessage.success('审核成功，报修记录已添加');
      dialogVisible.value = false;
      loadRecords();
    } else {
      ElMessage.error(res.msg || '审核失败');
    }
  } catch (error) {
    ElMessage.error('审核提交失败');
  }
};

onMounted(loadRecords);
</script>



<style scoped>
.mt-4 {
  margin-top: 20px;
}
.dialog-footer {
  text-align: right;
}
</style>

