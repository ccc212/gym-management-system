<template>
    <div class="venue-schedule-container">
        <el-card>
            <template #header>
                <div class="card-header">
                    <h2>场地安排</h2>
                </div>
            </template>
            
            <!-- 搜索筛选区域 -->
            <el-form :inline="true" class="search-form" size="small">
                <el-form-item label="场地类型">
                    <el-select v-model="searchForm.venueType" placeholder="选择场地类型" clearable @change="searchSchedule">
                        <el-option v-for="item in venueTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="场地名称">
                    <el-select v-model="searchForm.venueId" placeholder="选择场地" clearable @change="searchSchedule">
                        <el-option v-for="venue in filteredVenues" :key="venue.id" :label="venue.name" :value="venue.id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="searchSchedule">查询安排</el-button>
                    <el-button @click="resetSearch">重置</el-button>
                </el-form-item>
            </el-form>
            
            <!-- 日期导航 -->
            <div class="date-navigation">
                <el-button @click="changeWeek(-1)">上一周</el-button>
                <span class="date-range">{{ formatDateRange(currentWeekStart, 6) }}</span>
                <el-button @click="changeWeek(1)">下一周</el-button>
                <el-button type="text" @click="resetToCurrentWeek">回到本周</el-button>
            </div>
            
            <!-- 日历表格 -->
            <div class="schedule-calendar" v-loading="loading">
                <table class="schedule-table">
                    <thead>
                        <tr>
                            <th class="time-column">时间段</th>
                            <th v-for="(day, index) in weekDays" :key="index">
                                {{ day.dayOfWeek }}<br>
                                <small>{{ day.date }}</small>
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="timeSlot in timeSlots" :key="timeSlot.id">
                            <td class="time-column">{{ timeSlot.timeRange }}</td>
                            <td v-for="(day, dayIndex) in weekDays" :key="dayIndex" 
                                :class="getCellClass(getScheduleForTimeAndDay(timeSlot, day))">
                                <div v-if="getScheduleForTimeAndDay(timeSlot, day)" 
                                     class="schedule-cell-content"
                                     @click="showScheduleDetail(getScheduleForTimeAndDay(timeSlot, day))">
                                    {{ getScheduleForTimeAndDay(timeSlot, day).venueName }}
                                    <div class="schedule-status">{{ getStatusText(getScheduleForTimeAndDay(timeSlot, day).status) }}</div>
                                </div>
                                <div v-else class="schedule-cell-empty"></div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            
            <!-- 图例说明 -->
            <div class="schedule-legend">
                <div class="legend-item">
                    <div class="legend-color available"></div>
                    <div class="legend-text">可预约</div>
                </div>
                <div class="legend-item">
                    <div class="legend-color booked"></div>
                    <div class="legend-text">已预约</div>
                </div>
                <div class="legend-item">
                    <div class="legend-color in-use"></div>
                    <div class="legend-text">使用中</div>
                </div>
                <div class="legend-item">
                    <div class="legend-color special"></div>
                    <div class="legend-text">特殊场地</div>
                </div>
                <div class="legend-item">
                    <div class="legend-color maintenance"></div>
                    <div class="legend-text">维护中</div>
                </div>
            </div>
        </el-card>
        
        <!-- 场地详情弹窗 -->
        <el-dialog v-model="scheduleDetailVisible" title="场地安排详情" width="500px">
            <div v-if="selectedSchedule" class="schedule-detail">
                <h3>{{ selectedSchedule.venueName }}</h3>
                <el-descriptions :column="1" border>
                    <el-descriptions-item label="场地类型">
                        {{ getVenueTypeLabel(selectedSchedule.type) }}
                    </el-descriptions-item>
                    <el-descriptions-item label="日期">{{ selectedSchedule.date }}</el-descriptions-item>
                    <el-descriptions-item label="时间段">{{ selectedSchedule.timeSlot }}</el-descriptions-item>
                    <el-descriptions-item label="状态">
                        <el-tag :type="getStatusType(selectedSchedule.status)">
                            {{ getStatusText(selectedSchedule.status) }}
                        </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item v-if="selectedSchedule.bookedBy" label="预约人">
                        {{ selectedSchedule.bookedBy }}
                    </el-descriptions-item>
                    <el-descriptions-item v-if="selectedSchedule.numberOfPeople" label="使用人数">
                        {{ selectedSchedule.numberOfPeople }} 人
                    </el-descriptions-item>
                    <el-descriptions-item v-if="selectedSchedule.remarks" label="备注">
                        {{ selectedSchedule.remarks }}
                    </el-descriptions-item>
                </el-descriptions>
                
                <div class="schedule-actions" v-if="selectedSchedule.status === 'AVAILABLE'">
                    <el-button type="primary" @click="goToBooking">去预约</el-button>
                </div>
            </div>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="scheduleDetailVisible = false">关闭</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getVenues } from '@/api/venue'
import { getVenueReservations } from '@/api/reservation'

const router = useRouter()

// 搜索表单
const searchForm = ref({
    venueType: '',
    venueId: ''
})

// 场馆类型选项
const venueTypes = [
    { value: 'basketball', label: '篮球场' },
    { value: 'football', label: '足球场' },
    { value: 'badminton', label: '羽毛球场' },
    { value: 'tennis', label: '网球场' },
    { value: 'swimming', label: '游泳池' },
    { value: 'table_tennis', label: '乒乓球室' }
]

// 场馆列表
const venues = ref([])

// 当前周的开始日期
const currentWeekStart = ref(new Date())

// 时间段列表
const timeSlots = ref([])

// 场地安排数据
const scheduleData = ref([])

// 加载状态
const loading = ref(false)

// 选中的安排详情
const selectedSchedule = ref(null)

// 详情弹窗可见性
const scheduleDetailVisible = ref(false)

// 根据类型筛选的场馆列表
const filteredVenues = computed(() => {
    if (!searchForm.value.venueType) {
        return venues.value
    }
    return venues.value.filter(venue => venue.type === searchForm.value.venueType)
})

// 本周的日期列表
const weekDays = computed(() => {
    const days = []
    const weekStart = new Date(currentWeekStart.value)
    const dayNames = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']

    for (let i = 0; i < 7; i++) {
        const date = new Date(weekStart)
        date.setDate(weekStart.getDate() + i)

        days.push({
            date: formatDate(date),
            dayOfWeek: dayNames[date.getDay()],
            fullDate: date
        })
    }

    return days
})

// 设置周起始日为周日
const setWeekStartToSunday = () => {
    const today = new Date()
    const day = today.getDay()

    if (day !== 0) {
        today.setDate(today.getDate() - day)
    }

    today.setHours(0, 0, 0, 0)
    currentWeekStart.value = today
}

// 加载场馆数据
const loadVenueData = async () => {
    loading.value = true
    try {
        const res = await getVenues({ page: 1, size: 100 })
        if (res.code === 200 && res.data) {
            venues.value = res.data.records || []
            venues.value.forEach(venue => {
                if (venue.type) {
                    const typeOption = venueTypes.find(t => t.value === venue.type)
                    if (typeOption) {
                        venue.type = typeOption.label
                    }
                }
            })
        } else {
            venues.value = []
            ElMessage.error(res.msg || '加载场馆数据失败')
        }
    } catch (error) {
        console.error('加载场馆数据失败:', error)
        ElMessage.error('加载场馆数据失败')
    } finally {
        loading.value = false
    }
}

// 初始化时间段
const initTimeSlots = () => {
    const slots = []
    let startTime = new Date()
    startTime.setHours(8, 0, 0, 0)
    
    const endTime = new Date()
    endTime.setHours(22, 0, 0, 0)
    
    while (startTime < endTime) {
        const endTimeSlot = new Date(startTime.getTime() + 60 * 60000)
        slots.push({
            id: slots.length + 1,
            timeRange: `${formatTime(startTime)} - ${formatTime(endTimeSlot)}`
        })
        startTime = endTimeSlot
    }
    
    timeSlots.value = slots
}

// 格式化时间
const formatTime = (date: Date) => {
    const hours = date.getHours().toString().padStart(2, '0')
    const minutes = date.getMinutes().toString().padStart(2, '0')
    return `${hours}:${minutes}`
}

// 加载场地安排数据
const loadScheduleData = async () => {
    loading.value = true
    
    const params = {
        startDate: formatDate(currentWeekStart.value),
        endDate: formatDate(new Date(currentWeekStart.value.getTime() + 6 * 24 * 60 * 60 * 1000))
    }
    
    if (searchForm.value.venueType) {
        params.venueType = searchForm.value.venueType
    }
    
    if (searchForm.value.venueId) {
        params.venueId = searchForm.value.venueId
    }
    
    try {
        const res = await getVenueReservations(params)
        if (res.code === 200) {
            scheduleData.value = res.data || []
        } else {
            ElMessage.error(res.msg || '获取场地安排失败')
        }
    } catch (error) {
        console.error('获取场地安排失败:', error)
        ElMessage.error('获取场地安排失败')
    } finally {
        loading.value = false
    }
}

// 搜索场地安排
const searchSchedule = () => {
    loadScheduleData()
}

// 重置搜索条件
const resetSearch = () => {
    searchForm.value = {
        venueType: '',
        venueId: ''
    }
    searchSchedule()
}

// 改变周次
const changeWeek = (offset: number) => {
    const newDate = new Date(currentWeekStart.value)
    newDate.setDate(newDate.getDate() + (offset * 7))
    currentWeekStart.value = newDate
    loadScheduleData()
}

// 重置到当前周
const resetToCurrentWeek = () => {
    setWeekStartToSunday()
    loadScheduleData()
}

// 获取指定时间和日期的场地安排
const getScheduleForTimeAndDay = (timeSlot: any, day: any) => {
    return scheduleData.value.find(schedule => 
        schedule.date === day.date && 
        schedule.timeSlot === timeSlot.timeRange
    )
}

// 获取单元格的CSS类
const getCellClass = (schedule: any) => {
    if (!schedule) return 'schedule-cell-empty'

    const statusClassMap: { [key: string]: string } = {
        'AVAILABLE': 'schedule-cell-available',
        'BOOKED': 'schedule-cell-booked',
        'IN_USE': 'schedule-cell-in-use',
        'SPECIAL': 'schedule-cell-special',
        'MAINTENANCE': 'schedule-cell-maintenance'
    }

    return `schedule-cell ${statusClassMap[schedule.status] || ''}`
}

// 显示安排详情
const showScheduleDetail = (schedule: any) => {
    selectedSchedule.value = schedule
    scheduleDetailVisible.value = true
}

// 转到预约页面
const goToBooking = () => {
    if (selectedSchedule.value) {
        router.push({
            path: '/user/venues',
            query: {
                venueId: selectedSchedule.value.venueId,
                date: selectedSchedule.value.date
            }
        })
        scheduleDetailVisible.value = false
    }
}

// 格式化日期
const formatDate = (date: Date) => {
    const year = date.getFullYear()
    const month = (date.getMonth() + 1).toString().padStart(2, '0')
    const day = date.getDate().toString().padStart(2, '0')
    return `${year}-${month}-${day}`
}

// 格式化日期范围
const formatDateRange = (startDate: Date, days: number) => {
    const start = new Date(startDate)
    const end = new Date(startDate)
    end.setDate(start.getDate() + days)

    return `${formatDate(start)} 至 ${formatDate(end)}`
}

// 获取场地类型标签
const getVenueTypeLabel = (type: string) => {
    const typeOption = venueTypes.find(t => t.value === type)
    return typeOption ? typeOption.label : type
}

// 获取状态文本
const getStatusText = (status: string) => {
    const statusMap: { [key: string]: string } = {
        'AVAILABLE': '可预约',
        'BOOKED': '已预约',
        'IN_USE': '使用中',
        'SPECIAL': '特殊场地',
        'MAINTENANCE': '维护中',
        'PAST': '已过期',
        'PENDING': '待确认'
    }
    return statusMap[status] || status
}

// 获取状态类型（用于显示不同颜色）
const getStatusType = (status: string) => {
    const typeMap: { [key: string]: string } = {
        'AVAILABLE': 'success',
        'BOOKED': 'warning',
        'IN_USE': 'danger',
        'SPECIAL': 'info',
        'MAINTENANCE': 'info',
        'PAST': '',
        'PENDING': 'warning'
    }
    return typeMap[status] || ''
}

onMounted(() => {
    setWeekStartToSunday()
    loadVenueData()
    initTimeSlots()
    loadScheduleData()
})
</script>

<style scoped lang="scss">
.venue-schedule-container {
    padding: 20px;

    .search-form {
        margin-bottom: 20px;
    }

    .date-navigation {
        display: flex;
        align-items: center;
        gap: 16px;
        margin-bottom: 20px;

        .date-range {
            font-size: 16px;
            font-weight: 500;
        }
    }

    .schedule-table {
        width: 100%;
        border-collapse: collapse;
        margin-bottom: 20px;

        th, td {
            border: 1px solid #dcdfe6;
            padding: 8px;
            text-align: center;
        }

        .time-column {
            width: 120px;
            background-color: #f5f7fa;
        }

        .schedule-cell {
            height: 60px;
            cursor: pointer;
            transition: all 0.3s;

            &:hover {
                opacity: 0.8;
            }

            &-content {
                height: 100%;
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                padding: 4px;
            }

            &-empty {
                height: 100%;
                background-color: #f5f7fa;
            }

            &-available {
                background-color: #67c23a;
                color: white;
            }

            &-booked {
                background-color: #e6a23c;
                color: white;
            }

            &-in-use {
                background-color: #f56c6c;
                color: white;
            }

            &-special {
                background-color: #909399;
                color: white;
            }

            &-maintenance {
                background-color: #909399;
                color: white;
            }
        }
    }

    .schedule-legend {
        display: flex;
        gap: 20px;
        margin-top: 20px;

        .legend-item {
            display: flex;
            align-items: center;
            gap: 8px;

            .legend-color {
                width: 20px;
                height: 20px;
                border-radius: 4px;

                &.available {
                    background-color: #67c23a;
                }

                &.booked {
                    background-color: #e6a23c;
                }

                &.in-use {
                    background-color: #f56c6c;
                }

                &.special {
                    background-color: #909399;
                }

                &.maintenance {
                    background-color: #909399;
                }
            }
        }
    }

    .schedule-detail {
        h3 {
            margin-bottom: 20px;
            text-align: center;
        }

        .schedule-actions {
            margin-top: 20px;
            text-align: center;
        }
    }
}
</style> 