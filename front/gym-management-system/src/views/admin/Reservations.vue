<template>
    <div class="admin-reservations-container">
        <el-card>
            <template #header>
                <div class="card-header">
                    <h2>预约管理</h2>
                </div>
            </template>
            
            <!-- 搜索筛选区域 -->
            <el-form :inline="true" class="search-form" size="small">
                <el-form-item label="场地类型">
                    <el-select v-model="searchForm.venueType" placeholder="选择场地类型" clearable @change="searchReservations">
                        <el-option v-for="item in venueTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="预约状态">
                    <el-select v-model="searchForm.status" placeholder="选择预约状态" clearable @change="searchReservations">
                        <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
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
                <el-table-column prop="id" label="ID" width="80"></el-table-column>
                <el-table-column prop="venueName" label="场地名称" width="120"></el-table-column>
                <el-table-column prop="venueType" label="场地类型" width="100"></el-table-column>
                <el-table-column prop="userName" label="预约用户" width="100"></el-table-column>
                <el-table-column prop="date" label="预约日期" width="120"></el-table-column>
                <el-table-column prop="timeRange" label="时间段" width="160"></el-table-column>
                <el-table-column prop="numberOfPeople" label="人数" width="80" align="center"></el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="scope">
                        <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
                <el-table-column label="操作" width="240">
                    <template #default="scope">
                        <el-button type="primary" size="small" @click="showReservationDetail(scope.row)">详情</el-button>
                        <el-button v-if="scope.row.status === 'PENDING'" type="success" size="small" @click="approveReservation(scope.row)">批准</el-button>
                        <el-button v-if="scope.row.status === 'PENDING'" type="danger" size="small" @click="rejectReservation(scope.row)">拒绝</el-button>
                        <el-button v-if="scope.row.status === 'APPROVED' && !isReservationStarted(scope.row)" type="warning" size="small" @click="cancelReservation(scope.row)">取消</el-button>
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
        
        <!-- 预约详情弹窗 -->
        <el-dialog title="预约详情" v-model="detailDialogVisible" width="600px">
            <div v-if="selectedReservation" class="reservation-detail">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="预约ID">{{ selectedReservation.id }}</el-descriptions-item>
                    <el-descriptions-item label="预约状态">
                        <el-tag :type="getStatusType(selectedReservation.status)">{{ getStatusText(selectedReservation.status) }}</el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="场地名称">{{ selectedReservation.venueName }}</el-descriptions-item>
                    <el-descriptions-item label="场地类型">{{ selectedReservation.venueType }}</el-descriptions-item>
                    <el-descriptions-item label="预约用户">{{ selectedReservation.userName }}</el-descriptions-item>
                    <el-descriptions-item label="用户手机">{{ selectedReservation.userPhone || '无' }}</el-descriptions-item>
                    <el-descriptions-item label="预约日期">{{ selectedReservation.date }}</el-descriptions-item>
                    <el-descriptions-item label="时间段">{{ selectedReservation.timeRange }}</el-descriptions-item>
                    <el-descriptions-item label="预约人数">{{ selectedReservation.numberOfPeople }} 人</el-descriptions-item>
                    <el-descriptions-item label="费用">{{ selectedReservation.cost }} 元</el-descriptions-item>
                    <el-descriptions-item label="创建时间" :span="2">{{ selectedReservation.createTime }}</el-descriptions-item>
                    <el-descriptions-item label="修改时间" :span="2">{{ selectedReservation.updateTime || '无' }}</el-descriptions-item>
                    <el-descriptions-item label="备注" :span="2">{{ selectedReservation.remarks || '无' }}</el-descriptions-item>
                </el-descriptions>
                
                <!-- 操作记录 -->
                <div class="operation-history" v-if="selectedReservation.operationHistory && selectedReservation.operationHistory.length > 0">
                    <h3>操作记录</h3>
                    <el-timeline>
                        <el-timeline-item
                            v-for="(activity, index) in selectedReservation.operationHistory"
                            :key="index"
                            :timestamp="activity.time"
                            :type="getOperationHistoryType(activity.type)">
                            {{ activity.content }}
                            <div class="history-operator">操作人: {{ activity.operator }}</div>
                        </el-timeline-item>
                    </el-timeline>
                </div>
                
                <!-- 审核操作 -->
                <div class="approval-section" v-if="selectedReservation.status === 'PENDING'">
                    <h3>预约审核</h3>
                    <el-form :model="approvalForm" ref="approvalFormRef" label-width="80px">
                        <el-form-item label="审核意见" prop="comment">
                            <el-input type="textarea" v-model="approvalForm.comment" placeholder="请输入审核意见"></el-input>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="success" @click="doApprove">批准预约</el-button>
                            <el-button type="danger" @click="doReject">拒绝预约</el-button>
                        </el-form-item>
                    </el-form>
                </div>
            </div>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="detailDialogVisible = false">关闭</el-button>
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
    venueType: '',
    status: '',
    dateRange: []
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

// 预约状态选项
const statusOptions = [
    { value: 'PENDING', label: '待审核' },
    { value: 'CONFIRMED', label: '已确认' },
    { value: 'CANCELED', label: '已取消' },
    { value: 'COMPLETED', label: '已完成' },
    { value: 'REJECTED', label: '已拒绝' }
]

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
// 详情弹窗可见性
const detailDialogVisible = ref(false)
// 选中的预约
const selectedReservation = ref(null)
// 审核表单
const approvalForm = reactive({
    comment: ''
})

const approvalFormRef = ref<FormInstance>()

// 加载预约数据
const loadReservationData = async () => {
    loading.value = true
    
    try {
        // 构建查询参数
        const params = new URLSearchParams({
            page: pagination.currentPage.toString(),
            size: pagination.pageSize.toString()
        })
        
        // 添加场地类型过滤
        if (searchForm.venueType) {
            params.append('venueType', searchForm.venueType)
        }
        
        // 添加状态过滤
        if (searchForm.status) {
            params.append('status', searchForm.status)
        }
        
        // 添加日期范围过滤
        if (searchForm.dateRange && searchForm.dateRange.length === 2) {
            params.append('startDate', searchForm.dateRange[0])
            params.append('endDate', searchForm.dateRange[1])
        }
        
        const response = await axios.get(`/api/reservations?${params.toString()}`)
        
        if (response.data && response.data.records) {
            reservationList.value = response.data.records.map((reservation: any) => ({
                id: reservation.id,
                venueName: reservation.venueInfo ? reservation.venueInfo.name : '未知',
                venueType: reservation.venueInfo ? reservation.venueInfo.type : '未知',
                userName: reservation.userInfo ? reservation.userInfo.username : '未知',
                date: reservation.date || '未知',
                timeRange: `${reservation.startTime} - ${reservation.endTime}`,
                numberOfPeople: reservation.numberOfPeople,
                status: reservation.status,
                createTime: reservation.createdTime,
                cost: reservation.cost,
                remarks: reservation.remarks
            }))
            pagination.total = response.data.total
        }
    } catch (error) {
        console.error('Error loading reservations:', error)
        ElMessage.error('加载预约数据失败')
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
    searchForm.venueType = ''
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

// 获取状态文本
const getStatusText = (status: string) => {
    const statusMap: Record<string, string> = {
        'PENDING': '待审核',
        'CONFIRMED': '已确认',
        'CANCELED': '已取消',
        'COMPLETED': '已完成',
        'REJECTED': '已拒绝'
    }
    return statusMap[status] || status
}

// 获取状态类型
const getStatusType = (status: string) => {
    const typeMap: Record<string, string> = {
        'PENDING': 'warning',
        'CONFIRMED': 'success',
        'CANCELED': 'info',
        'COMPLETED': 'success',
        'REJECTED': 'danger'
    }
    return typeMap[status] || ''
}

// 获取操作记录类型
const getOperationHistoryType = (type: string) => {
    const typeMap: Record<string, string> = {
        'CREATE': 'primary',
        'APPROVE': 'success',
        'REJECT': 'danger',
        'CANCEL': 'info',
        'COMPLETE': 'success'
    }
    return typeMap[type] || 'info'
}

// 检查预约是否已开始
const isReservationStarted = (reservation: any) => {
    if (!reservation.date || !reservation.timeRange) return false
    
    const [startTime] = reservation.timeRange.split(' - ')
    const reservationDateTime = new Date(`${reservation.date} ${startTime}`)
    const now = new Date()
    
    return reservationDateTime <= now
}

// 显示预约详情
const showReservationDetail = (reservation: any) => {
    selectedReservation.value = reservation
    detailDialogVisible.value = true
}

// 批准预约
const approveReservation = (reservation: any) => {
    selectedReservation.value = reservation
    detailDialogVisible.value = true
}

// 执行批准
const doApprove = async () => {
    if (!selectedReservation.value) return
    
    try {
        const response = await axios.put(`/api/reservations/${selectedReservation.value.id}/approve`, {
            comment: approvalForm.comment
        })
        
        if (response.data && response.data.code === 200) {
            ElMessage.success('预约已批准')
            detailDialogVisible.value = false
            loadReservationData()
        } else {
            ElMessage.error(response.data.message || '批准失败')
        }
    } catch (error) {
        console.error('批准预约失败:', error)
        ElMessage.error('批准预约失败')
    }
}

// 拒绝预约
const rejectReservation = (reservation: any) => {
    selectedReservation.value = reservation
    detailDialogVisible.value = true
}

// 执行拒绝
const doReject = async () => {
    if (!selectedReservation.value) return
    
    try {
        const response = await axios.put(`/api/reservations/${selectedReservation.value.id}/reject`, {
            comment: approvalForm.comment
        })
        
        if (response.data && response.data.code === 200) {
            ElMessage.success('预约已拒绝')
            detailDialogVisible.value = false
            loadReservationData()
        } else {
            ElMessage.error(response.data.message || '拒绝失败')
        }
    } catch (error) {
        console.error('拒绝预约失败:', error)
        ElMessage.error('拒绝预约失败')
    }
}

// 取消预约
const cancelReservation = async (reservation: any) => {
    try {
        const response = await axios.put(`/api/reservations/${reservation.id}/cancel`)
        
        if (response.data && response.data.code === 200) {
            ElMessage.success('预约已取消')
            loadReservationData()
        } else {
            ElMessage.error(response.data.message || '取消失败')
        }
    } catch (error) {
        console.error('取消预约失败:', error)
        ElMessage.error('取消预约失败')
    }
}

// 初始加载
onMounted(() => {
    loadReservationData()
})
</script>

<style scoped>
.admin-reservations-container {
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

.operation-history {
    margin-top: 20px;
}

.history-operator {
    font-size: 12px;
    color: #909399;
    margin-top: 5px;
}

.approval-section {
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #EBEEF5;
}
</style> 