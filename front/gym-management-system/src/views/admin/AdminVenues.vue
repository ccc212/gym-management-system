<template>
  <div class="admin-venues-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>场地管理</h2>
          <el-button type="primary" size="small" @click="showAddVenueDialog">添加场地</el-button>
        </div>
      </template>
      <!-- 搜索筛选区域 -->
      <el-form :inline="true" class="search-form" size="small">
        <el-form-item label="场地类型">
          <el-select v-model="searchForm.venueType" placeholder="选择场地类型" clearable @change="searchVenues">
            <el-option v-for="item in venueTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="场地状态">
          <el-select v-model="searchForm.status" placeholder="选择场地状态" clearable @change="searchVenues">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchVenues">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      <!-- 场地列表 -->
      <el-table v-loading="loading" :data="venueList" style="width: 100%" :header-cell-style="{background:'#f5f7fa', color:'#606266'}" border>
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="name" label="场地名称" width="150"></el-table-column>
        <el-table-column prop="type" label="场地类型" width="120"></el-table-column>
        <el-table-column prop="location" label="场地位置"></el-table-column>
        <el-table-column prop="capacity" label="容纳人数" width="100"></el-table-column>
        <el-table-column prop="pricePerHour" label="每小时价格" width="120">
          <template #default="scope">
            {{ scope.row.pricePerHour }} 元
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260">
          <template #default="scope">
            <div style="display: flex; gap: 6px; flex-wrap: wrap;">
              <el-button type="primary" size="small" @click="editVenue(scope.row)">编辑</el-button>
              <el-button type="danger" size="small" @click="confirmDeleteVenue(scope.row)">删除</el-button>
              <el-button v-if="scope.row.status === 'NORMAL'" type="warning" size="small" @click="setVenueStatus(scope.row, 'MAINTENANCE')">维护</el-button>
              <el-button v-if="scope.row.status === 'MAINTENANCE'" type="success" size="small" @click="setVenueStatus(scope.row, 'NORMAL')">恢复</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pagination.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total">
        </el-pagination>
      </div>
    </el-card>
    <!-- 添加/编辑场地弹窗 -->
    <el-dialog :title="dialogType === 'add' ? '添加场地' : '编辑场地'" v-model="venueDialogVisible" width="600px" :modal-append-to-body="true">
      <el-form :model="venueForm" :rules="venueRules" ref="venueFormRef" label-width="100px">
        <el-form-item label="场地名称" prop="name">
          <el-input v-model="venueForm.name" placeholder="请输入场地名称"></el-input>
        </el-form-item>
        <el-form-item label="场地类型" prop="type">
          <el-select v-model="venueForm.type" placeholder="请选择场地类型" style="width: 100%">
            <el-option v-for="item in venueTypeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="场地位置" prop="location">
          <el-input v-model="venueForm.location" placeholder="请输入场地位置"></el-input>
        </el-form-item>
        <el-form-item label="容纳人数" prop="capacity">
          <el-input-number v-model="venueForm.capacity" :min="1" :max="1000" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="每小时价格" prop="pricePerHour">
          <el-input-number v-model="venueForm.pricePerHour" :min="0" :precision="2" :step="10" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="高峰时段价格" prop="peakHourPrice">
          <el-input-number v-model="venueForm.peakHourPrice" :min="0" :precision="2" :step="10" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="设施" prop="facilities">
          <el-input v-model="venueForm.facilities" type="textarea" placeholder="场地配套设施描述"></el-input>
        </el-form-item>
        <el-form-item label="场地描述" prop="description">
          <el-input v-model="venueForm.description" type="textarea" placeholder="场地详细描述"></el-input>
        </el-form-item>
        <el-form-item label="场地图片" prop="imageUrl">
          <el-input v-model="venueForm.imageUrl" placeholder="场地图片URL"></el-input>
        </el-form-item>
        <el-form-item label="场地状态" prop="status">
          <el-select v-model="venueForm.status" placeholder="请选择场地状态" style="width: 100%">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="venueDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveVenue">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchForm = reactive({
  venueType: '',
  status: ''
})
const venueTypes = [
  { value: 'basketball', label: '篮球场' },
  { value: 'football', label: '足球场' },
  { value: 'badminton', label: '羽毛球场' },
  { value: 'tennis', label: '网球场' },
  { value: 'swimming', label: '游泳池' },
  { value: 'table_tennis', label: '乒乓球室' }
]
const statusOptions = [
  { value: 'NORMAL', label: '正常' },
  { value: 'MAINTENANCE', label: '维护中' },
  { value: 'SPECIAL', label: '特殊场地' }
]
const venueTypeOptions = [...venueTypes]
const venueList = ref([])
const loading = ref(false)
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})
const venueDialogVisible = ref(false)
const dialogType = ref('add')
const venueForm = reactive({
  id: null,
  name: '',
  type: '',
  location: '',
  capacity: 10,
  pricePerHour: 50,
  peakHourPrice: 80,
  facilities: '',
  description: '',
  imageUrl: '',
  status: 'NORMAL'
})
const venueRules = {
  name: [
    { required: true, message: '请输入场地名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择场地类型', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入场地位置', trigger: 'blur' }
  ],
  capacity: [
    { required: true, message: '请输入容纳人数', trigger: 'blur' }
  ],
  pricePerHour: [
    { required: true, message: '请输入每小时价格', trigger: 'blur' }
  ]
}
const venueFormRef = ref(null)

onMounted(() => {
  loadVenueData()
})

async function loadVenueData() {
  loading.value = true
  try {
    const response = await axios.get('/api/venues', {
      params: {
        page: pagination.currentPage,
        size: pagination.pageSize,
        type: searchForm.venueType,
        status: searchForm.status
      }
    })
    if (response.data && response.data.code === 200) {
      venueList.value = response.data.data.records || []
      pagination.total = response.data.data.total || 0
    } else {
      venueList.value = []
      pagination.total = 0
    }
  } catch (error) {
    ElMessage.error('加载场馆数据失败')
    venueList.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}
function searchVenues() {
  pagination.currentPage = 1
  loadVenueData()
}
function resetSearch() {
  searchForm.venueType = ''
  searchForm.status = ''
  searchVenues()
}
function handleSizeChange(val) {
  pagination.pageSize = val
  loadVenueData()
}
function handleCurrentChange(val) {
  pagination.currentPage = val
  loadVenueData()
}
function getStatusText(status) {
  const statusMap = {
    'NORMAL': '正常',
    'MAINTENANCE': '维护中',
    'SPECIAL': '特殊场地'
  }
  return statusMap[status] || '未知'
}
function getStatusType(status) {
  const typeMap = {
    'NORMAL': 'success',
    'MAINTENANCE': 'warning',
    'SPECIAL': 'danger'
  }
  return typeMap[status] || 'info'
}
function showAddVenueDialog() {
  dialogType.value = 'add'
  Object.assign(venueForm, {
    id: null,
    name: '',
    type: '',
    location: '',
    capacity: 10,
    pricePerHour: 50,
    peakHourPrice: 80,
    facilities: '',
    description: '',
    imageUrl: '',
    status: 'NORMAL'
  })
  venueDialogVisible.value = true
  nextTick(() => {
    venueFormRef.value && venueFormRef.value.clearValidate()
  })
}
function editVenue(venue) {
  console.log('editVenue', venue)
  dialogType.value = 'edit'
  Object.assign(venueForm, {
    id: venue.id,
    name: venue.name,
    type: venue.type,
    location: venue.location,
    capacity: venue.capacity,
    pricePerHour: venue.pricePerHour,
    peakHourPrice: venue.peakHourPrice || 0,
    facilities: venue.facilities || '',
    description: venue.description || '',
    imageUrl: venue.imageUrl || '',
    status: venue.status
  })
  venueDialogVisible.value = true
  nextTick(() => {
    venueFormRef.value && venueFormRef.value.clearValidate()
  })
}
function saveVenue() {
  venueFormRef.value.validate(async valid => {
    if (valid) {
      loading.value = true
      try {
        const venueData = {
          ...venueForm,
          pricePerHour: Number(venueForm.pricePerHour),
          peakHourPrice: Number(venueForm.peakHourPrice),
          capacity: Number(venueForm.capacity)
        }
        if (dialogType.value === 'add') {
          const response = await axios.post('/api/venues', venueData)
          if (response.data && response.data.code === 200) {
            ElMessage.success('添加场地成功')
            loadVenueData()
          } else {
            ElMessage.error(response.data.message || '添加场地失败')
          }
        } else {
          const response = await axios.put(`/api/venues/${venueForm.id}`, venueData)
          if (response.data && response.data.code === 200) {
            ElMessage.success('更新场地成功')
            loadVenueData()
          } else {
            ElMessage.error(response.data.message || '更新场地失败')
          }
        }
        venueDialogVisible.value = false
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '保存场地失败')
      } finally {
        loading.value = false
      }
    }
  })
}
function confirmDeleteVenue(venue) {
  ElMessageBox.confirm(`确定要删除场地 "${venue.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteVenue(venue)
  }).catch(() => {})
}
async function deleteVenue(venue) {
  loading.value = true
  try {
    const response = await axios.delete(`/api/venues/${venue.id}`)
    if (response.status === 200) {
      ElMessage.success('删除场地成功')
      loadVenueData()
    }
  } catch (error) {
    ElMessage.error('删除场地失败')
  } finally {
    loading.value = false
  }
}
async function setVenueStatus(venue, status) {
  loading.value = true
  try {
    // 兼容后端接口，传递 status 字段
    const response = await axios.put(`/api/venues/${venue.id}/status`, { status })
    if (response.data && response.data.code === 200) {
      ElMessage.success(`更新场地状态为${getStatusText(status)}成功`)
      loadVenueData()
    } else {
      ElMessage.error(response.data.message || '更新场地状态失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '更新场地状态失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.admin-venues-container {
  padding: 20px;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.search-form {
  margin-bottom: 20px;
}
.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style>