<template>
  <el-main>
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
        <el-button icon="Search" @click="searchBtn">搜索</el-button>
        <el-button icon="Refresh" type="danger" @click="resetBtn">重置</el-button>
        <el-button icon="Plus" type="primary" @click="addBtn">新增</el-button>
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
          <el-button type="primary" icon="View" size="small" @click="viewBtn(scope.row)">查看详情</el-button>
          <el-button type="warning" icon="Edit" size="small" @click="editBtn(scope.row)">编辑</el-button>
          <el-button type="danger" icon="Delete" size="small" @click="deleteBtn(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
        :current-page.sync="searchParams.pageSize"
        :page-sizes="[10, 20, 40]"
        :page-size="searchParams.pageSize"
        layout="total, sizes, prev, pager, next, jumper"

        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
    />

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="700px" append-to-body>
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
        <el-form-item label="器材编号" prop="number">
          <el-input v-model="formData.id" placeholder="请输入器材编号" />
        </el-form-item>
        <el-form-item label="器材名称" prop="name">
          <el-input v-model="formData.equipmentName" placeholder="请输入器材名称" />
        </el-form-item>
        <el-form-item label="器材类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择器材类型">
            <el-option
                v-for="item in typeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="仓库剩余数量" prop="quantity">
          <el-input-number v-model="formData.remainingQuantity" :min="0" :max="10000" placeholder="请输入仓库剩余数量" />
        </el-form-item>
        <el-form-item label="器材管理员" prop="admin">
          <el-input v-model="formData.administrator" placeholder="请输入器材管理员名称" />
        </el-form-item>
        <el-form-item label="创建时间" prop="createTime">
          <el-date-picker
              v-model="formData.createTime"
              type="datetime"
              placeholder="请选择创建时间"
          ></el-date-picker>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="dialogVisible = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

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
import { ref, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {EquipBasicsControllerService} from '../../../../generated/services/EquipBasicsControllerService';
import type { Equipment } from '../../../../generated/models/Equipment';

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

const dialogTitle = ref('');
const formRef = ref();

const defaultFormData = (): Equipment => ({
  id: undefined,
  equipmentName: '',
  status: 0,
  administrator: '',
  type: '',
  remainingQuantity: 0,
  createTime: undefined
});

const formData = ref<Equipment>(defaultFormData());

const rules = {
  equipmentName: [{ required: true, message: '请输入器材名称', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
  type: [{ required: true, message: '请选择器材类型', trigger: 'change' }],
  administrator: [{ required: true, message: '请输入管理员', trigger: 'blur' }],
  remainingQuantity: [{ required: true, message: '请输入数量', trigger: 'change' }]
};

const statusLabel = (val: number) => statusOptions.find(s => s.value === val)?.label || '';
const statusTagType = (val: number) => val === 0 ? 'info' : val === 1 ? 'success' : 'danger';

const loadData = async () => {
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
};

onMounted(loadData);

const searchBtn = () => {
  searchParams.value.currentPage = 1;
  loadData();
};

const resetBtn = () => {
  Object.assign(searchParams.value, { equipmentName: '', status: null, type: '', currentPage: 1 });
  loadData();
};

const resetForm = async () => {
  formData.value = defaultFormData();
  await nextTick();
  formRef.value?.clearValidate();
};

const addBtn = async () => {
  dialogTitle.value = '新增器材';
  await resetForm();
  dialogVisible.value = true;
};

const editBtn = (row: Equipment) => {
  dialogTitle.value = '编辑器材';
  formData.value = { ...row };
  dialogVisible.value = true;
};

const deleteBtn = (id?: number) => {
  if (!id) return;
  ElMessageBox.confirm('确定要删除该器材？', '提示', {
    type: 'warning'
  }).then(async () => {
    const res = await EquipBasicsControllerService.deleteUsingDelete1(id);
    if (res?.code === 0) {
      ElMessage.success('删除成功');
      loadData();
    }
  });
};

const submitForm = async () => {
  if (!formRef.value) return;
  formRef.value.validate(async (valid: boolean) => {
    if (!valid) return;

    console.debug('🧪 Debug 提交状态：', dialogTitle.value, formData.value);
    if (dialogTitle.value === '新增器材' && formData.value.id !== undefined) {
      console.warn('⚠️ 警告：新增表单中检测到 ID，可能未正确重置！');
    }

    const res = formData.value.id
        ? await EquipBasicsControllerService.updateUsingPut1(formData.value)
        : await EquipBasicsControllerService.addUsingPost1(formData.value);

    if (res?.code === 0) {
      ElMessage.success('操作成功');
      dialogVisible.value = false;
      loadData();
    }
  });
};

const handleSizeChange = (val: number) => {
  searchParams.value.pageSize = val;
  loadData();
};

const handleCurrentChange = (val: number) => {
  searchParams.value.currentPage = val;
  loadData();
};

const dialogVisible = ref(false);
const detailDialogVisible = ref(false);
const detailData = ref<Equipment | null>(null);

const viewBtn = (row: Equipment) => {
  detailData.value = row;
  detailDialogVisible.value = true;
};
</script>