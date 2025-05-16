<template>
  <el-dialog title="器材报修" v-model="dialogVisible" :before-close="handleClose" width="500px" append-to-body>
    <el-form :model="repairForm" ref="formRef" label-width="100px">
      <el-form-item label="器材ID">
        <el-input v-model="id" disabled />
      </el-form-item>
      <el-form-item label="器材名称">
        <el-input v-model="equipmentName" disabled />
      </el-form-item>
      <el-form-item label="器材管理员">
        <el-input v-model="administrator" disabled />
      </el-form-item>
      <el-form-item label="报修原因" prop="repairReason" :rules="[{ required: true, message: '请输入报修原因', trigger: 'blur' }]">
        <el-input type="textarea" v-model="repairForm.repairReason" placeholder="请输入报修原因" rows="4" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRepair">提交报修</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import {ref, onMounted, watch} from 'vue';
import { ElMessage } from 'element-plus';
import { EquipBasicsControllerService } from '../../../generated/services/EquipBasicsControllerService';


const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  id: {
    type: Number
  },
  equipmentName: {
    type: String
  },
  administrator: {
    type: String
  }
})

const emit = defineEmits(['update:modelValue',
  'update:id','update:equipmentName','update:administrator'
]);

const repairRecords = ref<any[]>([]);
const dialogVisible = ref(props.modelValue);
const id = ref(props.id);
const equipmentName = ref(props.equipmentName);
const administrator = ref(props.administrator);
const formRef = ref();

// 监听对话框显示状态
watch(() => props.modelValue, (val) => {
  dialogVisible.value = val;
});
watch(() => props.id, (val) => {
  id.value = val;
})
watch(() => props.equipmentName, (val) => {
  equipmentName.value = val;
})
watch(() => props.administrator, (val) => {
  administrator.value = val;
})

// 监听对话框内部状态变化
watch(dialogVisible, (val) => {
  emit('update:modelValue', val);
});

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false;
};

const repairForm = ref({
  repairReason: ''
});

const loadRecords = async () => {
  try {
    const res = await EquipBasicsControllerService.getListUsingGet1(1, '', 999);
    if (!res?.data?.records) return;
    repairRecords.value = res.data.records;
  } catch (error) {
    ElMessage.error('获取器材列表失败');
  }
};

const submitRepair = async () => {
  if (!repairForm.value.repairReason.trim()) {
    ElMessage.warning('请输入报修原因');
    return;
  }
  try {
    const res = await EquipBasicsControllerService.updateUsingPut1({
      id: id.value,
      status: 2, // 报修状态
      repairReason: repairForm.value.repairReason
    });
    if (res?.code === 0) {
      ElMessage.success('报修申请提交成功');
      dialogVisible.value = false;
      loadRecords();
    } else {
      ElMessage.error(res.msg || '报修提交失败');
    }
  } catch {
    ElMessage.error('报修提交失败');
  }
};

onMounted(loadRecords);
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style>
