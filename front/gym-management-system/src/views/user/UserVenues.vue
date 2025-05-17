<template>
  <div class="venue-booking-container">
    <el-card>
      <div slot="header" class="card-header">
        <h2>场地预约</h2>
      </div>
      <!-- 搜索筛选区域 -->
      <el-form :inline="true" class="search-form" size="small">
        <el-form-item label="场地类型">
          <el-select v-model="searchForm.venueType" placeholder="选择场地类型" clearable @change="searchVenues">
            <el-option v-for="item in venueTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker
            v-model="searchForm.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            :picker-options="datePickerOptions"
            @change="searchVenues">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchVenues">查询场地</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      <!-- 场地列表 -->
      <el-table
        v-loading="loading"
        :data="venueList"
        style="width: 100%"
        :header-cell-style="{background:'#f5f7fa', color:'#606266'}"
        border>
        <el-table-column prop="name" label="场地名称" width="150"></el-table-column>
        <el-table-column prop="type" label="场地类型" width="120"></el-table-column>
        <el-table-column prop="description" label="场地描述"></el-table-column>
        <el-table-column prop="pricePerHour" label="价格" width="120">
          <template #default="scope">
            {{ scope.row.pricePerHour }} 元/小时
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.isAvailable ? 'success' : 'danger'">
              {{ scope.row.isAvailable ? '可用' : '不可用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button type="primary" size="mini" @click="showTimeSlots(scope.row)" :disabled="!scope.row.isAvailable">预约</el-button>
            <el-button type="info" size="mini" @click="showVenueDetails(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.currentPage"
          :page-sizes="[5, 10, 20, 50]"
          :page-size="pagination.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total">
        </el-pagination>
      </div>
    </el-card>
    <!-- 时间段选择弹窗 -->
    <el-dialog
      v-model="timeSlotDialogVisible"
      title="选择预约时间段"
      width="650px"
      destroy-on-close>
      <div v-if="selectedVenue">
        <h4>场地: {{ selectedVenue.name }}</h4>
        <p>日期: {{ formatDate(searchForm.date) }}</p>
        <el-divider content-position="left">可用时间段</el-divider>
        <div class="time-slots-container">
          <el-row :gutter="10">
            <el-col :span="8" v-for="timeSlot in timeSlots" :key="timeSlot.startTime">
              <div
                :class="['time-slot', {
                  'available': timeSlot.status === 'AVAILABLE',
                  'booked': timeSlot.status === 'BOOKED',
                  'special': timeSlot.status === 'SPECIAL',
                  'selected': selectedTimeSlot && selectedTimeSlot.startTime === timeSlot.startTime
                }]"
                @click="timeSlot.status === 'AVAILABLE' && selectTimeSlot(timeSlot)">
                {{ timeSlot.startTime }} - {{ timeSlot.endTime }}
                <div class="time-slot-price" v-if="timeSlot.status === 'AVAILABLE'">
                  {{ timeSlot.price }}元/小时
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
        <el-divider></el-divider>
        <el-form :model="bookingForm" :rules="bookingRules" ref="bookingForm" label-width="100px" size="small">
          <el-form-item label="备注信息" prop="remarks">
            <el-input type="textarea" v-model="bookingForm.remarks" placeholder="请输入预约备注信息"></el-input>
          </el-form-item>
        </el-form>
        <div class="booking-summary" v-if="selectedTimeSlot">
          <p>预约时间: {{ selectedTimeSlot.startTime }} - {{ selectedTimeSlot.endTime }}</p>
          <p>预计费用: {{ calculateEstimatedCost() }} 元</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="timeSlotDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBooking" :disabled="!selectedTimeSlot">确认预约</el-button>
      </template>
    </el-dialog>
    <!-- 场地详情弹窗 -->
    <el-dialog
      v-model="venueDetailDialogVisible"
      title="场地详情"
      width="500px">
      <div v-if="selectedVenue" class="venue-detail">
        <div class="venue-image">
          <img :src="selectedVenue.imageUrl || '/img/default-venue.jpg'" alt="场地图片">
        </div>
        <div class="venue-info">
          <h3>{{ selectedVenue.name }}</h3>
          <p><strong>类型:</strong> {{ selectedVenue.type }}</p>
          <p><strong>描述:</strong> {{ selectedVenue.description || '暂无描述' }}</p>
          <p><strong>价格:</strong> {{ selectedVenue.pricePerHour }} 元/小时</p>
          <p><strong>状态:</strong>
            <el-tag :type="selectedVenue.isAvailable ? 'success' : 'danger'">
              {{ selectedVenue.isAvailable ? '可用' : '不可用' }}
            </el-tag>
          </p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const venues = ref([])
const loading = ref(false)
const searchForm = ref({
  venueType: '',
  date: getTodayDate()
})
const venueTypes = [
  { value: 'basketball', label: '篮球场' },
  { value: 'football', label: '足球场' },
  { value: 'badminton', label: '羽毛球场' },
  { value: 'tennis', label: '网球场' },
  { value: 'swimming', label: '游泳池' },
  { value: 'table_tennis', label: '乒乓球室' }
]
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})
const venueList = ref([])
const datePickerOptions = {
  disabledDate(time) {
    return time.getTime() < Date.now() - 8.64e7
  }
}
const timeSlotDialogVisible = ref(false)
const selectedVenue = ref(null)
const timeSlots = ref([])
const selectedTimeSlot = ref(null)
const bookingForm = ref({
  remarks: ''
})
const bookingRules = {
  remarks: [
    { required: false, message: '请输入预约备注信息', trigger: 'blur' }
  ]
}
const venueDetailDialogVisible = ref(false)
const apiBaseUrl = '/api/venues'

function getTodayDate() {
  const today = new Date()
  const year = today.getFullYear()
  const month = (today.getMonth() + 1).toString().padStart(2, '0')
  const day = today.getDate().toString().padStart(2, '0')
  return `${year}-${month}-${day}`
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = typeof dateStr === 'string' ? new Date(dateStr) : dateStr
  if (!(date instanceof Date) || isNaN(date)) {
    return ''
  }
  const year = date.getFullYear()
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  return `${year}-${month}-${day}`
}

async function loadVenues() {
  loading.value = true
  try {
    const params = {
      page: pagination.value.currentPage,
      size: pagination.value.pageSize
    }
    if (searchForm.value.venueType) {
      params.type = searchForm.value.venueType
    }
    const response = await axios.get(apiBaseUrl, { params })
    if (response.data && response.data.code === 200) {
      const pageData = response.data.data
      venueList.value = Array.isArray(pageData.records) ? pageData.records.slice() : []
      pagination.value.total = pageData.total || 0
    } else {
      ElMessage.error(response.data.msg || '加载场地信息失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '加载场地信息失败')
  } finally {
    loading.value = false
  }
}

function searchVenues() {
  pagination.value.currentPage = 1
  loadVenues()
}

function resetSearch() {
  searchForm.value = {
    venueType: '',
    date: getTodayDate()
  }
  searchVenues()
}

function handleSizeChange(val) {
  pagination.value.pageSize = val
  loadVenues()
}

function handleCurrentChange(val) {
  pagination.value.currentPage = val
  loadVenues()
}

async function showTimeSlots(venue) {
  console.log('点击预约按钮，venue:', venue)
  selectedVenue.value = venue
  await loadTimeSlots()
  timeSlotDialogVisible.value = true
  console.log('弹窗状态:', timeSlotDialogVisible.value)
}

async function loadTimeSlots() {
  try {
    const date = searchForm.value.date
    if (!date) {
      ElMessage.warning('请先选择日期')
      return
    }
    
    console.log('加载时间段，日期:', date)
    
    const formattedDate = date.split('T')[0]
    
    const response = await axios.get(`${apiBaseUrl}/${selectedVenue.value.id}/time-slots`, {
      params: { date: formattedDate }
    })
    
    console.log('获取到的时间段数据:', response.data)
    
    if (response.data.code === 200 && Array.isArray(response.data.data)) {
      timeSlots.value = response.data.data.map(slot => {
        const startTime = typeof slot.startTime === 'string' ? slot.startTime : 
                       (slot.startTime.format ? slot.startTime.format('HH:mm') : '00:00')
        const endTime = typeof slot.endTime === 'string' ? slot.endTime : 
                     (slot.endTime.format ? slot.endTime.format('HH:mm') : '00:00')
        
        return {
          ...slot,
          startTime: startTime,
          endTime: endTime,
          price: Number(slot.price || selectedVenue.value.pricePerHour).toFixed(2),
          status: slot.status || 'AVAILABLE'
        }
      })
      console.log('处理后的时间段数据:', timeSlots.value)
    } else {
      timeSlots.value = []
      ElMessage.warning(response.data.msg || '没有可用的时间段')
    }
  } catch (error) {
    console.error('加载时间段失败:', error)
    ElMessage.error('加载时间段失败: ' + (error.response?.data?.msg || error.message))
    timeSlots.value = []
  }
}

function selectTimeSlot(timeSlot) {
  selectedTimeSlot.value = timeSlot
}

function calculateEstimatedCost() {
  if (!selectedTimeSlot.value) return 0
  return selectedTimeSlot.value.price
}

async function confirmBooking() {
  if (!selectedTimeSlot.value) return
  try {
    // 获取当前用户，兼容 currentUser 和 userStore
    let currentUser = JSON.parse(localStorage.getItem('currentUser'))
    if (!currentUser) {
      currentUser = JSON.parse(localStorage.getItem('userStore'))
    }
    const userId = currentUser && (currentUser.id || currentUser.userId || currentUser.user_id) ? (currentUser.id || currentUser.userId || currentUser.user_id) : null
    const cardNumber = currentUser && (currentUser.cardNumber || currentUser.card_number || currentUser.user_number || currentUser.username || currentUser.userNumber)
      ? (currentUser.cardNumber || currentUser.card_number || currentUser.user_number || currentUser.username || currentUser.userNumber)
      : '12345678'
    if (!userId) {
      ElMessage.error('请先登录')
      return
    }

    // 计算时间差（小时）
    const startTime = selectedTimeSlot.value.startTime
    const endTime = selectedTimeSlot.value.endTime
    const [startHour, startMinute] = startTime.split(':').map(Number)
    const [endHour, endMinute] = endTime.split(':').map(Number)
    const duration = (endHour - startHour) + (endMinute - startMinute) / 60

    // 构建预约请求数据
    const bookingData = {
      venueId: selectedVenue.value.id,
      userId: userId,
      cardNumber: cardNumber,
      startTime: `${searchForm.value.date}T${startTime}:00`,
      endTime: `${searchForm.value.date}T${endTime}:00`,
      numberOfPeople: 1,
      remarks: bookingForm.value.remarks || '',
      status: 'PENDING',
      cost: Number((selectedVenue.value.pricePerHour * duration).toFixed(2)),
      duration: duration
    }

    console.log('预约数据:', bookingData)

    const res = await axios.post(`${apiBaseUrl}/${selectedVenue.value.id}/reserve`, bookingData)
    console.log('预约返回：', res.data)
    if (res.data && res.data.code === 200 && res.data.data) {
      ElMessage.success('预约成功')
      timeSlotDialogVisible.value = false
      loadVenues()
    } else {
      ElMessage.error(res.data.msg || '预约失败')
    }
  } catch (error) {
    ElMessage.error('预约失败')
  }
}

function showVenueDetails(venue) {
  selectedVenue.value = venue
  venueDetailDialogVisible.value = true
}

onMounted(() => {
  loadVenues()
})
</script>

<style scoped>
.venue-booking-container {
  padding: 20px;
}

.time-slots-container {
  margin: 20px 0;
}

.time-slot {
  padding: 10px;
  margin: 5px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  text-align: center;
}

.time-slot.available {
  background-color: #f0f9eb;
  border-color: #67c23a;
}

.time-slot.booked {
  background-color: #fef0f0;
  border-color: #f56c6c;
  cursor: not-allowed;
}

.time-slot.special {
  background-color: #fdf6ec;
  border-color: #e6a23c;
}

.time-slot.selected {
  background-color: #ecf5ff;
  border-color: #409eff;
}

.time-slot-price {
  margin-top: 5px;
  font-weight: bold;
}
.time-slot.available .time-slot-price {
  color: #67c23a;
}
.time-slot.booked .time-slot-price,
.time-slot.special .time-slot-price {
  color: #f56c6c;
}

.booking-summary {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.venue-detail {
  display: flex;
  gap: 20px;
}

.venue-image img {
  max-width: 200px;
  border-radius: 4px;
}

.venue-info {
  flex: 1;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style>