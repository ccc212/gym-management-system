<template>
    <div class="my-reservations-container">
        <el-card>
            <template #header>
                <div class="card-header">
                    <h2>我的预约</h2>
                </div>
            </template>

            <!-- 筛选区域 -->
            <el-form :inline="true" class="search-form" size="small">
                <el-form-item label="预约状态">
                    <el-select v-model="searchForm.status" placeholder="选择状态" clearable @change="searchReservations">
                        <el-option label="全部" value=""></el-option>
                        <el-option label="待确认" value="PENDING"></el-option>
                        <el-option label="已确认" value="CONFIRMED"></el-option>
                        <el-option label="使用中" value="IN_USE"></el-option>
                        <el-option label="已完成" value="COMPLETED"></el-option>
                        <el-option label="已取消" value="CANCELED"></el-option>
                        <el-option label="已拒绝" value="REJECTED"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="日期范围">
                    <el-date-picker
                        v-model="searchForm.dateRange"
                        type="daterange"
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        value-format="YYYY-MM-DD"
                        @change="searchReservations">
                    </el-date-picker>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="searchReservations">查询</el-button>
                    <el-button @click="resetSearch">重置</el-button>
                </el-form-item>
            </el-form>

            <!-- 预约列表 -->
            <el-table 
                v-loading="loading" 
                :data="reservationList" 
                style="width: 100%"
                :header-cell-style="{background:'#f5f7fa', color:'#606266'}"
                border>
                <el-table-column prop="venueInfo.name" label="场地名称" width="150"></el-table-column>
                <el-table-column prop="venueInfo.type" label="场地类型" width="110"></el-table-column>
                <el-table-column label="预约时间" width="180">
                    <template #default="scope">
                        {{ formatDate(scope.row.date) }}<br>
                        {{ scope.row.startTime }} - {{ scope.row.endTime }}
                    </template>
                </el-table-column>
                <el-table-column prop="numberOfPeople" label="人数" width="80"></el-table-column>
                <el-table-column prop="cost" label="费用" width="100">
                    <template #default="scope">
                        {{ scope.row.cost }} 元
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="scope">
                        <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="160">
                    <template #default="scope">
                        <el-button type="primary" size="small" @click="showReservationDetail(scope.row)">详情</el-button>
                        <el-button 
                            v-if="scope.row.status === 'PENDING'" 
                            type="danger" 
                            size="small" 
                            @click="cancelReservation(scope.row)">取消</el-button>
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

        <!-- 预约详情弹窗 -->
        <el-dialog title="预约详情" v-model="detailDialogVisible" width="500px">
            <div v-if="selectedReservation" class="reservation-detail">
                <h3>预约信息</h3>
                <el-descriptions :column="1" border>
                    <el-descriptions-item label="场地名称">{{ selectedReservation.venueInfo.name }}</el-descriptions-item>
                    <el-descriptions-item label="场地类型">{{ selectedReservation.venueInfo.type }}</el-descriptions-item>
                    <el-descriptions-item label="预约日期">{{ formatDate(selectedReservation.date) }}</el-descriptions-item>
                    <el-descriptions-item label="预约时间">{{ selectedReservation.startTime }} - {{ selectedReservation.endTime }}</el-descriptions-item>
                    <el-descriptions-item label="预约人数">{{ selectedReservation.numberOfPeople }} 人</el-descriptions-item>
                    <el-descriptions-item label="费用">{{ selectedReservation.cost }} 元</el-descriptions-item>
                    <el-descriptions-item label="预约状态">
                        <el-tag :type="getStatusType(selectedReservation.status)">
                            {{ getStatusText(selectedReservation.status) }}
                        </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="预约时间">{{ formatDateTime(selectedReservation.createdTime) }}</el-descriptions-item>
                    <el-descriptions-item label="备注" :span="1">
                        {{ selectedReservation.remarks || '无' }}
                    </el-descriptions-item>
                </el-descriptions>
            </div>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="detailDialogVisible = false">关闭</el-button>
                    <el-button 
                        v-if="selectedReservation && selectedReservation.status === 'PENDING'" 
                        type="danger" 
                        @click="cancelReservation(selectedReservation, true)">取消预约</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 取消预约弹窗 -->
        <el-dialog title="取消预约" v-model="cancelDialogVisible" width="400px">
            <div>
                <p>确定要取消这个预约吗？</p>
                <el-form :model="cancelForm" ref="cancelFormRef" label-width="100px">
                    <el-form-item label="取消原因" prop="reason">
                        <el-input 
                            type="textarea" 
                            v-model="cancelForm.reason"
                            placeholder="请输入取消原因（可选）">
                        </el-input>
                    </el-form-item>
                </el-form>
            </div>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="cancelDialogVisible = false">取消</el-button>
                    <el-button type="danger" @click="confirmCancelReservation">确定取消</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import axios from 'axios'

// 搜索表单
const searchForm = reactive({
    status: '',
    dateRange: []
})

// 预约列表
const reservationList = ref([])
// 加载状态
const loading = ref(false)
// 分页信息
const pagination = reactive({
    currentPage: 1,
    pageSize: 10,
    total: 0
})
// 预约详情弹窗
const detailDialogVisible = ref(false)
// 选中的预约
const selectedReservation = ref(null)
// 取消预约弹窗
const cancelDialogVisible = ref(false)
// 取消表单
const cancelForm = reactive({
    reason: ''
})

const cancelFormRef = ref<FormInstance>()

// 加载预约数据
const loadReservationData = async () => {
    loading.value = true
    
    try {
        // 获取当前用户ID
        const userId = localStorage.getItem('userId')
        if (!userId) {
            ElMessage.error('请先登录')
            return
        }

        console.log('当前用户ID:', userId)

        // 构建查询参数
        const params = new URLSearchParams({
            page: pagination.currentPage.toString(),
            size: pagination.pageSize.toString()
        })
        
        if (searchForm.status) {
            params.append('status', searchForm.status)
        }
        
        if (searchForm.dateRange && searchForm.dateRange.length === 2) {
            params.append('startDate', searchForm.dateRange[0])
            params.append('endDate', searchForm.dateRange[1])
        }
        
        console.log('查询参数:', params.toString())
        
        const response = await axios.get(`/api/reservations/user/${userId}?${params.toString()}`)
        
        console.log('获取预约列表响应:', response.data)
        if (response.data && response.data.records) {
            const { records, total, current, size } = response.data
            console.log('预约记录数:', records.length)
            reservationList.value = records.map(reservation => {
                // 处理日期
                if (typeof reservation.date === 'string') {
                    // 如果是字符串，直接使用
                    reservation.date = reservation.date
                } else if (reservation.date instanceof Date) {
                    // 如果是Date对象，转换为字符串
                    reservation.date = reservation.date.toISOString().split('T')[0]
                }
                console.log('处理预约记录:', reservation)
                return reservation
            })
            pagination.total = total
            pagination.currentPage = current
            pagination.pageSize = size
        } else {
            console.error('预约数据格式不正确:', response.data)
            ElMessage.error('获取预约列表失败：数据格式不正确')
        }
    } catch (error) {
        console.error('获取预约列表失败:', error)
        if (error.response) {
            console.error('错误响应:', error.response.data)
        }
        ElMessage.error('获取预约列表失败：' + (error.response?.data?.message || error.message))
    } finally {
        loading.value = false
    }
}

// 搜索预约
const searchReservations = () => {
    pagination.currentPage = 1
    loadReservationData()
}

// 重置搜索条件
const resetSearch = () => {
    searchForm.status = ''
    searchForm.dateRange = []
    searchReservations()
}

// 处理每页显示数量变化
const handleSizeChange = (val: number) => {
    pagination.pageSize = val
    loadReservationData()
}

// 处理页码变化
const handleCurrentChange = (val: number) => {
    pagination.currentPage = val
    loadReservationData()
}

// 显示预约详情
const showReservationDetail = (reservation: any) => {
    selectedReservation.value = reservation
    detailDialogVisible.value = true
}

// 取消预约
const cancelReservation = (reservation: any, fromDetail = false) => {
    selectedReservation.value = reservation
    cancelForm.reason = ''
    cancelDialogVisible.value = true
    if (fromDetail) {
        detailDialogVisible.value = false
    }
}

// 确认取消预约
const confirmCancelReservation = async () => {
    if (!selectedReservation.value) return
    
    try {
        const response = await axios.put(`/api/reservations/${selectedReservation.value.id}/cancel`, {
            reason: cancelForm.reason
        })
        
        if (response.data.code === 200) {
            ElMessage.success('取消预约成功')
            cancelDialogVisible.value = false
            loadReservationData()
        } else {
            ElMessage.error(response.data.message || '取消预约失败')
        }
    } catch (error) {
        console.error('取消预约失败:', error)
        ElMessage.error('取消预约失败')
    }
}

// 获取状态文本
const getStatusText = (status: string) => {
    const statusMap: Record<string, string> = {
        'PENDING': '待确认',
        'CONFIRMED': '已确认',
        'IN_USE': '使用中',
        'COMPLETED': '已完成',
        'CANCELED': '已取消',
        'REJECTED': '已拒绝'
    }
    return statusMap[status] || status
}

// 获取状态类型
const getStatusType = (status: string) => {
    const typeMap: Record<string, string> = {
        'PENDING': 'warning',
        'CONFIRMED': 'success',
        'IN_USE': 'primary',
        'COMPLETED': 'info',
        'CANCELED': 'danger',
        'REJECTED': 'danger'
    }
    return typeMap[status] || ''
}

// 格式化日期
const formatDate = (dateStr: string) => {
    if (!dateStr) return ''
    const date = new Date(dateStr)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
}

// 格式化日期时间
const formatDateTime = (dateTimeStr: string) => {
    if (!dateTimeStr) return ''
    const date = new Date(dateTimeStr)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 初始加载
onMounted(() => {
    loadReservationData()
})
</script>

<style scoped>
.my-reservations-container {
    padding: 20px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.search-form {
    margin-bottom: 20px;
}

.pagination-container {
    margin-top: 20px;
    text-align: right;
}

.reservation-detail {
    padding: 20px;
}

.reservation-detail h3 {
    margin-top: 0;
    margin-bottom: 20px;
    color: #303133;
}
</style> 