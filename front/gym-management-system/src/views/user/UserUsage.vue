<template>
  <div class="venue-usage-container">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <h2>今日可用预约</h2>
            </div>
          </template>
          <div v-if="todayReservations.length === 0" class="empty-data">
            <el-empty description="没有今日可用的预约"></el-empty>
          </div>
          <div v-else>
            <el-table :data="todayReservations" style="width: 100%" :header-cell-style="{background:'#f5f7fa', color:'#606266'}" border>
              <el-table-column prop="venueInfo.name" label="场地名称" width="120"></el-table-column>
              <el-table-column label="预约时间" width="180">
                <template #default="scope">
                  {{ scope.row.startTime }} - {{ scope.row.endTime }}
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="getStatusType(scope.row.usageStatus)">{{ getStatusText(scope.row.usageStatus) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="160">
                <template #default="scope">
                  <el-button v-if="scope.row.usageStatus === 'NOT_STARTED'" type="primary" size="mini" @click="startUsage(scope.row)">开始使用</el-button>
                  <el-button v-if="scope.row.usageStatus === 'IN_PROGRESS'" type="danger" size="mini" @click="endUsage(scope.row)">结束使用</el-button>
                  <el-button v-if="scope.row.usageStatus === 'COMPLETED'" type="info" size="mini" @click="viewUsageDetail(scope.row)">查看详情</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card v-if="currentUsage" class="usage-timer-card">
          <template #header>
            <div class="card-header">
              <h2>正在使用</h2>
            </div>
          </template>
          <div class="usage-info">
            <h3>{{ currentUsage.venueInfo.name }}</h3>
            <p>预约时间: {{ currentUsage.startTime }} - {{ currentUsage.endTime }}</p>
            <div class="timer-container">
              <div class="timer-label">已使用时间</div>
              <div class="timer-display">{{ formatDuration(elapsedTime) }}</div>
              <div class="cost-container">
                <div class="cost-label">当前费用</div>
                <div class="cost-display">¥ {{ calculateCurrentCost().toFixed(2) }}</div>
              </div>
              <el-alert v-if="isOvertime" title="您已超出预约时间" type="warning" :closable="false" show-icon>
                <template #description>
                  超时使用将按正常价格收费，请尽快结束使用。
                </template>
              </el-alert>
              <div class="timer-actions">
                <el-button type="danger" @click="endUsage(currentUsage)">结束使用</el-button>
              </div>
            </div>
          </div>
        </el-card>
        <el-card v-else class="no-usage-card">
          <template #header>
            <div class="card-header">
              <h2>场地使用</h2>
            </div>
          </template>
          <div class="empty-usage">
            <el-empty description="您当前没有正在使用的场地">
              <el-button type="primary" @click="$router.push('/user/venues')">去预约场地</el-button>
            </el-empty>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <!-- 使用结算弹窗 -->
    <el-dialog title="场地使用结算" v-model="settlementDialogVisible" width="500px">
      <div v-if="currentUsage" class="settlement-info">
        <h3>{{ currentUsage.venueInfo.name }}</h3>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="场地类型">{{ currentUsage.venueInfo.type }}</el-descriptions-item>
          <el-descriptions-item label="预约时间">{{ currentUsage.startTime }} - {{ currentUsage.endTime }}</el-descriptions-item>
          <el-descriptions-item label="实际使用时间">{{ usageStartTime }} - {{ getCurrentTime() }}</el-descriptions-item>
          <el-descriptions-item label="使用时长">{{ formatDuration(elapsedTime) }}</el-descriptions-item>
          <el-descriptions-item label="标准费用">{{ currentUsage.cost }} 元</el-descriptions-item>
          <el-descriptions-item label="实际费用" class="highlight-cost">
            {{ calculateCurrentCost().toFixed(2) }} 元
            <span v-if="isOvertime" class="overtime-tag">
              (含超时费用 {{ calculateOvertimeCost().toFixed(2) }} 元)
            </span>
          </el-descriptions-item>
        </el-descriptions>
        <div class="payment-options">
          <h4>支付方式</h4>
          <el-radio-group v-model="paymentMethod">
            <el-radio label="balance">余额支付</el-radio>
            <el-radio label="wechat">微信支付</el-radio>
            <el-radio label="alipay">支付宝</el-radio>
          </el-radio-group>
        </div>
      </div>
      <template #footer>
        <el-button @click="settlementDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSettlement">确认支付</el-button>
      </template>
    </el-dialog>
    <!-- 使用详情弹窗 -->
    <el-dialog title="使用详情" v-model="usageDetailDialogVisible" width="500px">
      <div v-if="selectedUsage" class="usage-detail">
        <h3>{{ selectedUsage.venueInfo.name }}</h3>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="场地类型">{{ selectedUsage.venueInfo.type }}</el-descriptions-item>
          <el-descriptions-item label="预约时间">{{ selectedUsage.startTime }} - {{ selectedUsage.endTime }}</el-descriptions-item>
          <el-descriptions-item label="实际使用时间">{{ selectedUsage.actualStartTime }} - {{ selectedUsage.actualEndTime }}</el-descriptions-item>
          <el-descriptions-item label="使用时长">{{ selectedUsage.duration }}</el-descriptions-item>
          <el-descriptions-item label="应付费用">{{ selectedUsage.cost }} 元</el-descriptions-item>
          <el-descriptions-item label="实付费用">{{ selectedUsage.actualCost }} 元</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ getPaymentMethodText(selectedUsage.paymentMethod) }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ selectedUsage.paymentTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="usageDetailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const todayReservations = ref([])
const currentUsage = ref(null)
const usageStartTime = ref('')
const timer = ref(null)
const elapsedTime = ref(0)
const settlementDialogVisible = ref(false)
const paymentMethod = ref('balance')
const usageDetailDialogVisible = ref(false)
const selectedUsage = ref(null)

const isOvertime = computed(() => {
  if (!currentUsage.value) return false
  const [hours, minutes] = currentUsage.value.endTime.split(':').map(Number)
  const endTimeInMinutes = hours * 60 + minutes
  const now = new Date()
  const currentTimeInMinutes = now.getHours() * 60 + now.getMinutes()
  return currentTimeInMinutes > endTimeInMinutes
})

onMounted(() => {
  loadTodayReservations()
  checkOngoingUsage()
})
onBeforeUnmount(() => {
  clearTimer()
})

function loadTodayReservations() {
  let currentUser = JSON.parse(localStorage.getItem('currentUser'))
  if (!currentUser) {
    currentUser = JSON.parse(localStorage.getItem('userStore'))
  }
  const userId = currentUser && (currentUser.id || currentUser.userId || currentUser.user_id) ? (currentUser.id || currentUser.userId || currentUser.user_id) : null
  if (!userId) {
    ElMessage.error('请先登录')
    return
  }
  axios.get(`/api/reservations/user/${userId}`, {
    params: {
      page: 1,
      size: 100,
      status: 'PENDING,CONFIRMED,IN_USE'
    }
  }).then(response => {
    const data = response.data
    if (data && data.records) {
      todayReservations.value = data.records
        .filter(reservation => {
          if (typeof reservation.date === 'string') {
            reservation.date = new Date(reservation.date)
          }
          const today = new Date()
          const reservationDate = new Date(reservation.date)
          return reservationDate.toDateString() === today.toDateString()
        })
        .map(reservation => {
          const usageStatus = getUsageStatus(reservation)
          return { ...reservation, usageStatus }
        })
      // 检查本地存储是否有使用中的记录
      const ongoingUsageStr = localStorage.getItem('ongoingUsage')
      if (ongoingUsageStr) {
        try {
          const ongoingUsage = JSON.parse(ongoingUsageStr)
          const index = todayReservations.value.findIndex(item => item.id === ongoingUsage.id)
          if (index !== -1) {
            todayReservations.value[index].usageStatus = 'IN_PROGRESS'
          }
        } catch (e) {
          localStorage.removeItem('ongoingUsage')
        }
      }
    } else {
      ElMessage.error('获取预约列表失败：数据格式不正确')
    }
  }).catch(error => {
    ElMessage.error('加载今日预约失败: ' + (error.response?.data?.msg || error.message))
  })
}
function getUsageStatus(reservation) {
  const now = new Date()
  const [hours, minutes] = reservation.startTime.split(':').map(Number)
  const [endHours, endMinutes] = reservation.endTime.split(':').map(Number)
  const reservationDate = new Date(reservation.date)
  const startTime = new Date(reservationDate)
  startTime.setHours(hours, minutes, 0, 0)
  const endTime = new Date(reservationDate)
  endTime.setHours(endHours, endMinutes, 0, 0)
  const ongoingUsageStr = localStorage.getItem('ongoingUsage')
  if (ongoingUsageStr) {
    try {
      const ongoingUsage = JSON.parse(ongoingUsageStr)
      if (ongoingUsage.id === reservation.id) {
        return 'IN_PROGRESS'
      }
    } catch (e) {
      localStorage.removeItem('ongoingUsage')
    }
  }
  if (reservation.status === 'COMPLETED') {
    return 'COMPLETED'
  }
  if (now < startTime) {
    return 'NOT_STARTED'
  } else if (now >= endTime) {
    return 'COMPLETED'
  } else {
    return 'NOT_STARTED'
  }
}
function checkOngoingUsage() {
  const ongoingUsageStr = localStorage.getItem('ongoingUsage')
  const usageStartTimeStr = localStorage.getItem('usageStartTime')
  if (ongoingUsageStr && usageStartTimeStr) {
    try {
      const ongoingUsage = JSON.parse(ongoingUsageStr)
      const startTime = new Date(usageStartTimeStr)
      const now = new Date()
      elapsedTime.value = Math.floor((now - startTime) / 1000)
      currentUsage.value = ongoingUsage
      usageStartTime.value = usageStartTimeStr
      startTimer()
    } catch (e) {
      localStorage.removeItem('ongoingUsage')
      localStorage.removeItem('usageStartTime')
    }
  }
}
function startUsage(reservation) {
  if (currentUsage.value) {
    ElMessage.error('您已有正在使用的场地，请先结束当前使用')
    return
  }
  const index = todayReservations.value.findIndex(item => item.id === reservation.id)
  if (index !== -1) {
    todayReservations.value[index].usageStatus = 'IN_PROGRESS'
    currentUsage.value = todayReservations.value[index]
    const now = new Date()
    usageStartTime.value = now.toLocaleString()
    elapsedTime.value = 0
    localStorage.setItem('ongoingUsage', JSON.stringify({ ...currentUsage.value, startTime: usageStartTime.value }))
    localStorage.setItem('usageStartTime', usageStartTime.value)
    startTimer()
    ElMessage.success('已开始使用场地')
  }
}
function startTimer() {
  if (timer.value) clearInterval(timer.value)
  timer.value = setInterval(() => {
    elapsedTime.value++
  }, 1000)
}
function clearTimer() {
  if (timer.value) {
    clearInterval(timer.value)
    timer.value = null
  }
}
function endUsage(reservation) {
  if (!currentUsage.value || currentUsage.value.id !== reservation.id) {
    ElMessage.error('只能结束当前正在使用的场地')
    return
  }
  settlementDialogVisible.value = true
}
function viewUsageDetail(usage) {
  axios.get(`/api/reservations/${usage.id}/usage`).then(response => {
    if (response.data && response.data.code === 200) {
      selectedUsage.value = { ...usage, ...response.data.data }
      usageDetailDialogVisible.value = true
    } else {
      ElMessage.error(response.data.msg || '获取使用详情失败')
    }
  }).catch(error => {
    ElMessage.error('获取使用详情失败: ' + error.message)
  })
}
function confirmSettlement() {
  if (!currentUsage.value) return
  clearTimer()
  const formatTime = (date) => {
    const d = new Date(date)
    return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false })
  }
  const now = new Date()
  const settlementData = {
    reservationId: currentUsage.value.id,
    actualStartTime: formatTime(usageStartTime.value),
    actualEndTime: formatTime(now),
    duration: formatDuration(elapsedTime.value),
    actualCost: calculateCurrentCost().toFixed(2),
    paymentMethod: paymentMethod.value
  }
  axios.post('/api/reservations/settle', settlementData).then(response => {
    if (response.data && response.data.code === 200) {
      const index = todayReservations.value.findIndex(item => item.id === currentUsage.value.id)
      if (index !== -1) {
        todayReservations.value[index].usageStatus = 'COMPLETED'
        todayReservations.value[index].actualStartTime = settlementData.actualStartTime
        todayReservations.value[index].actualEndTime = settlementData.actualEndTime
        todayReservations.value[index].duration = settlementData.duration
        todayReservations.value[index].actualCost = settlementData.actualCost
        todayReservations.value[index].paymentMethod = settlementData.paymentMethod
      }
      localStorage.removeItem('ongoingUsage')
      localStorage.removeItem('usageStartTime')
      currentUsage.value = null
      elapsedTime.value = 0
      usageStartTime.value = ''
      settlementDialogVisible.value = false
      ElMessage.success('结算成功！')
    } else {
      ElMessage.error(response.data.msg || '结算失败')
    }
  }).catch(error => {
    ElMessage.error('结算失败: ' + (error.response?.data?.msg || error.message))
  })
}
function formatDuration(seconds) {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const remainingSeconds = seconds % 60
  const parts = []
  if (hours > 0) parts.push(`${hours}小时`)
  if (minutes > 0) parts.push(`${minutes}分钟`)
  if (remainingSeconds > 0) parts.push(`${remainingSeconds}秒`)
  return parts.join(' ') || '0秒'
}
function calculateCurrentCost() {
  if (!currentUsage.value) return 0
  const pricePerHour = currentUsage.value.venueInfo.pricePerHour
  const hours = elapsedTime.value / 3600
  if (isOvertime.value) {
    const normalHours = getNormalHours()
    const overtimeHours = hours - normalHours
    return pricePerHour * normalHours + calculateOvertimeCost()
  }
  return pricePerHour * hours
}
function calculateOvertimeCost() {
  if (!currentUsage.value || !isOvertime.value) return 0
  const pricePerHour = currentUsage.value.venueInfo.pricePerHour
  const normalHours = getNormalHours()
  const totalHours = elapsedTime.value / 3600
  const overtimeHours = totalHours - normalHours
  return pricePerHour * overtimeHours * 1.5
}
function getNormalHours() {
  if (!currentUsage.value) return 0
  const [startHours, startMinutes] = currentUsage.value.startTime.split(':').map(Number)
  const [endHours, endMinutes] = currentUsage.value.endTime.split(':').map(Number)
  return (endHours - startHours) + (endMinutes - startMinutes) / 60
}
function getCurrentTime() {
  const now = new Date()
  return now.toLocaleString()
}
function getStatusText(status) {
  const statusMap = {
    'NOT_STARTED': '未开始',
    'IN_PROGRESS': '使用中',
    'COMPLETED': '已完成'
  }
  return statusMap[status] || '未知'
}
function getStatusType(status) {
  const typeMap = {
    'NOT_STARTED': 'info',
    'IN_PROGRESS': 'success',
    'COMPLETED': 'warning'
  }
  return typeMap[status] || 'info'
}
function getPaymentMethodText(method) {
  const methodMap = {
    'balance': '余额支付',
    'wechat': '微信支付',
    'alipay': '支付宝'
  }
  return methodMap[method] || '未知'
}
</script>

<style scoped>
.venue-usage-container {
  padding: 20px;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.usage-info {
  margin-top: 20px;
}
.timer-container {
  margin-top: 20px;
}
.timer-label {
  font-weight: bold;
}
.timer-display {
  font-size: 1.5em;
  margin: 10px 0;
}
.cost-container {
  margin: 10px 0;
}
.cost-label {
  font-weight: bold;
}
.cost-display {
  color: #f56c6c;
  font-size: 1.2em;
}
.overtime-tag {
  color: #e6a23c;
  margin-left: 10px;
}
.timer-actions {
  margin-top: 20px;
}
.settlement-info {
  margin-top: 10px;
}
.payment-options {
  margin-top: 20px;
}
.highlight-cost {
  color: #f56c6c;
  font-weight: bold;
}
.usage-detail {
  margin-top: 10px;
}
</style> 