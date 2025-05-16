<template>
  <div class="venue-usage-container">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <div slot="header" class="card-header">
            <h2>今日可用预约</h2>
          </div>
          
          <div v-if="todayReservations.length === 0" class="empty-data">
            <el-empty description="没有今日可用的预约"></el-empty>
          </div>
          
          <div v-else>
            <el-table 
              :data="todayReservations" 
              style="width: 100%"
              :header-cell-style="{background:'#f5f7fa', color:'#606266'}"
              border>
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
                  <el-button 
                    v-if="scope.row.usageStatus === 'NOT_STARTED'" 
                    type="primary" 
                    size="small" 
                    @click="startUsage(scope.row)">开始使用</el-button>
                  <el-button 
                    v-if="scope.row.usageStatus === 'IN_PROGRESS'" 
                    type="danger" 
                    size="small" 
                    @click="endUsage(scope.row)">结束使用</el-button>
                  <el-button 
                    v-if="scope.row.usageStatus === 'COMPLETED'" 
                    type="info" 
                    size="small" 
                    @click="viewUsageDetail(scope.row)">查看详情</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card v-if="currentUsage" class="usage-timer-card">
          <div slot="header" class="card-header">
            <h2>正在使用</h2>
          </div>
          
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
              
              <el-alert
                v-if="isOvertime"
                title="您已超出预约时间"
                type="warning"
                :closable="false"
                show-icon>
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
          <div slot="header" class="card-header">
            <h2>场地使用</h2>
          </div>
          
          <div class="empty-usage">
            <el-empty description="您当前没有正在使用的场地">
              <el-button type="primary" @click="$router.push('/user/venues')">去预约场地</el-button>
            </el-empty>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 使用结算弹窗 -->
    <el-dialog v-model="settlementDialogVisible" title="场地使用结算" width="500px">
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
        <span class="dialog-footer">
          <el-button @click="settlementDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmSettlement">确认支付</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 使用详情弹窗 -->
    <el-dialog v-model="usageDetailDialogVisible" title="使用详情" width="500px">
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
        <span class="dialog-footer">
          <el-button @click="usageDetailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { Reservation } from '@/types/venue'
import { getTodayReservations, startVenueUsage, endVenueUsage, settleVenueUsage, getUsageDetail } from '@/api/venue/usage'

const router = useRouter()

// 状态变量
const todayReservations = ref<Reservation[]>([])
const currentUsage = ref<Reservation | null>(null)
const selectedUsage = ref<Reservation | null>(null)
const settlementDialogVisible = ref(false)
const usageDetailDialogVisible = ref(false)
const paymentMethod = ref('balance')
const usageStartTime = ref('')
const elapsedTime = ref(0)
const timer = ref<number | null>(null)

// 计算属性
const isOvertime = computed(() => {
  if (!currentUsage.value) return false
  const endTime = new Date(currentUsage.value.endTime)
  return new Date() > endTime
})

// 方法
const getStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    'NOT_STARTED': 'info',
    'IN_PROGRESS': 'warning',
    'COMPLETED': 'success',
    'CANCELLED': 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    'NOT_STARTED': '未开始',
    'IN_PROGRESS': '使用中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

const getPaymentMethodText = (method: string) => {
  const methodMap: Record<string, string> = {
    'balance': '余额支付',
    'wechat': '微信支付',
    'alipay': '支付宝'
  }
  return methodMap[method] || method
}

const formatDuration = (seconds: number) => {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const remainingSeconds = seconds % 60
  return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
}

const getCurrentTime = () => {
  return new Date().toLocaleTimeString()
}

const calculateCurrentCost = () => {
  if (!currentUsage.value) return 0
  const baseCost = currentUsage.value.cost
  if (isOvertime.value) {
    return baseCost + calculateOvertimeCost()
  }
  return baseCost
}

const calculateOvertimeCost = () => {
  if (!currentUsage.value) return 0
  const endTime = new Date(currentUsage.value.endTime)
  const now = new Date()
  const overtimeMinutes = Math.max(0, (now.getTime() - endTime.getTime()) / (1000 * 60))
  return overtimeMinutes * (currentUsage.value.cost / 60) // 假设每分钟费用是总费用的1/60
}

const loadTodayReservations = async () => {
  try {
    const res = await getTodayReservations()
    if (res.code === 0) {
      todayReservations.value = res.data
    } else {
      ElMessage.error(res.msg || '加载预约列表失败')
    }
  } catch (error) {
    ElMessage.error('加载预约列表失败')
  }
}

const startUsage = async (reservation: Reservation) => {
  try {
    const res = await startVenueUsage(reservation.id)
    if (res.code === 0) {
      currentUsage.value = reservation
      usageStartTime.value = getCurrentTime()
      startTimer()
      ElMessage.success('已开始使用场地')
    } else {
      ElMessage.error(res.msg || '开始使用失败')
    }
  } catch (error) {
    ElMessage.error('开始使用失败')
  }
}

const endUsage = async (reservation: Reservation) => {
  try {
    const res = await endVenueUsage(reservation.id)
    if (res.code === 0) {
      currentUsage.value = reservation
      settlementDialogVisible.value = true
      stopTimer()
    } else {
      ElMessage.error(res.msg || '结束使用失败')
    }
  } catch (error) {
    ElMessage.error('结束使用失败')
  }
}

const confirmSettlement = async () => {
  if (!currentUsage.value) return
  try {
    const res = await settleVenueUsage(currentUsage.value.id, {
      paymentMethod: paymentMethod.value,
      actualCost: calculateCurrentCost()
    })
    if (res.code === 0) {
      settlementDialogVisible.value = false
      currentUsage.value = null
      ElMessage.success('结算成功')
      loadTodayReservations()
    } else {
      ElMessage.error(res.msg || '结算失败')
    }
  } catch (error) {
    ElMessage.error('结算失败')
  }
}

const viewUsageDetail = async (reservation: Reservation) => {
  try {
    const res = await getUsageDetail(reservation.id)
    if (res.code === 0) {
      selectedUsage.value = res.data
      usageDetailDialogVisible.value = true
    } else {
      ElMessage.error(res.msg || '获取使用详情失败')
    }
  } catch (error) {
    ElMessage.error('获取使用详情失败')
  }
}

const startTimer = () => {
  timer.value = window.setInterval(() => {
    elapsedTime.value++
  }, 1000)
}

const stopTimer = () => {
  if (timer.value) {
    clearInterval(timer.value)
    timer.value = null
  }
}

// 生命周期钩子
onMounted(() => {
  loadTodayReservations()
})

onUnmounted(() => {
  stopTimer()
})
</script>

<style scoped>
.venue-usage-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-data {
  padding: 40px 0;
}

.usage-info {
  text-align: center;
}

.timer-container {
  margin-top: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.timer-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.timer-display {
  font-size: 36px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 20px;
}

.cost-container {
  margin: 20px 0;
}

.cost-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.cost-display {
  font-size: 24px;
  font-weight: bold;
  color: #67C23A;
}

.timer-actions {
  margin-top: 20px;
}

.empty-usage {
  padding: 40px 0;
}

.settlement-info {
  padding: 20px;
}

.payment-options {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #EBEEF5;
}

.highlight-cost {
  color: #F56C6C;
  font-weight: bold;
}

.overtime-tag {
  font-size: 12px;
  color: #E6A23C;
  margin-left: 10px;
}

.usage-detail {
  padding: 20px;
}
</style> 