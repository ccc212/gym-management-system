<template>
    <div class="admin-noshows-container">
        <el-card>
            <template #header>
                <div class="card-header">
                    <h2>失约处理</h2>
                    <el-button type="primary" size="small" @click="createTestData">创建测试数据</el-button>
                </div>
            </template>
            
            <!-- 搜索筛选区域 -->
            <el-form :inline="true" class="search-form" size="small">
                <el-form-item label="场地类型">
                    <el-select v-model="searchForm.venueType" placeholder="选择场地类型" clearable @change="searchNoshows">
                        <el-option v-for="item in venueTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="处理状态">
                    <el-select v-model="searchForm.status" placeholder="选择处理状态" clearable @change="searchNoshows">
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
                        @change="searchNoshows">
                    </el-date-picker>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="searchNoshows">查询</el-button>
                    <el-button @click="resetSearch">重置</el-button>
                </el-form-item>
            </el-form>
            
            <!-- 失约列表 -->
            <el-table 
                v-loading="loading" 
                :data="noshowsList" 
                style="width: 100%"
                :header-cell-style="{background:'#f5f7fa', color:'#606266'}"
                border>
                <el-table-column prop="id" label="ID" width="80"></el-table-column>
                <el-table-column prop="reservationId" label="预约ID" width="80"></el-table-column>
                <el-table-column prop="userName" label="用户" width="100"></el-table-column>
                <el-table-column prop="venueName" label="场地名称" width="120"></el-table-column>
                <el-table-column prop="venueType" label="场地类型" width="100"></el-table-column>
                <el-table-column prop="date" label="预约日期" width="110"></el-table-column>
                <el-table-column prop="timeRange" label="时间段" width="150"></el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="scope">
                        <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="penalty" label="违约金" width="100"></el-table-column>
                <el-table-column prop="createdTime" label="创建时间" width="180"></el-table-column>
                <el-table-column label="操作" width="220">
                    <template #default="scope">
                        <el-button v-if="scope.row.status === 'PENDING'" type="success" size="small" @click="handleNoshow(scope.row, 'EXCUSED')">免责</el-button>
                        <el-button v-if="scope.row.status === 'PENDING'" type="danger" size="small" @click="handleNoshow(scope.row, 'PENALIZED')">处罚</el-button>
                        <el-button type="primary" size="small" @click="viewNoshowDetail(scope.row)">详情</el-button>
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
        
        <!-- 失约处理弹窗 -->
        <el-dialog :title="'处理失约 - ' + (currentAction === 'EXCUSED' ? '免责' : '处罚')" v-model="handleDialogVisible" width="500px">
            <el-form v-if="currentNoshow" :model="handleForm" ref="handleFormRef" label-width="100px">
                <el-form-item label="用户" class="no-edit">
                    <el-input v-model="currentNoshow.userName" disabled></el-input>
                </el-form-item>
                <el-form-item label="预约信息" class="no-edit">
                    <el-input v-model="noshowInfo" disabled></el-input>
                </el-form-item>
                <el-form-item v-if="currentAction === 'PENALIZED'" label="处罚金额" prop="penalty">
                    <el-input-number v-model="handleForm.penalty" :min="0" :precision="2" :step="10" style="width: 100%"></el-input-number>
                </el-form-item>
                <el-form-item v-if="currentAction === 'PENALIZED'" label="限制预约" prop="restrictDays">
                    <el-select v-model="handleForm.restrictDays" placeholder="选择限制天数" style="width: 100%">
                        <el-option label="不限制" :value="0"></el-option>
                        <el-option label="限制3天" :value="3"></el-option>
                        <el-option label="限制7天" :value="7"></el-option>
                        <el-option label="限制15天" :value="15"></el-option>
                        <el-option label="限制30天" :value="30"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="处理原因" prop="reason">
                    <el-input type="textarea" v-model="handleForm.reason" placeholder="请输入处理原因"></el-input>
                </el-form-item>
                <el-form-item label="通知用户" prop="notifyUser">
                    <el-switch v-model="handleForm.notifyUser"></el-switch>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="handleDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitHandle">确定</el-button>
                </span>
            </template>
        </el-dialog>
        
        <!-- 详情弹窗 -->
        <el-dialog title="失约详情" v-model="detailDialogVisible" width="600px">
            <div v-if="currentNoshow" class="noshow-detail">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="失约ID">{{ currentNoshow.id }}</el-descriptions-item>
                    <el-descriptions-item label="预约ID">{{ currentNoshow.reservationId }}</el-descriptions-item>
                    <el-descriptions-item label="用户姓名">{{ currentNoshow.userName }}</el-descriptions-item>
                    <el-descriptions-item label="用户手机">{{ currentNoshow.userPhone }}</el-descriptions-item>
                    <el-descriptions-item label="场地名称">{{ currentNoshow.venueName }}</el-descriptions-item>
                    <el-descriptions-item label="场地类型">{{ currentNoshow.venueType }}</el-descriptions-item>
                    <el-descriptions-item label="预约日期">{{ currentNoshow.date }}</el-descriptions-item>
                    <el-descriptions-item label="时间段">{{ currentNoshow.timeRange }}</el-descriptions-item>
                    <el-descriptions-item label="预约人数">{{ currentNoshow.numberOfPeople }} 人</el-descriptions-item>
                    <el-descriptions-item label="预约费用">{{ currentNoshow.cost }} 元</el-descriptions-item>
                    <el-descriptions-item label="失约状态">
                        <el-tag :type="getStatusType(currentNoshow.status)">{{ getStatusText(currentNoshow.status) }}</el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="违约金">{{ currentNoshow.penalty || '无' }}</el-descriptions-item>
                    <el-descriptions-item label="限制预约">{{ currentNoshow.restrictDays ? currentNoshow.restrictDays + '天' : '无' }}</el-descriptions-item>
                    <el-descriptions-item label="处理时间">{{ currentNoshow.handleTime || '未处理' }}</el-descriptions-item>
                    <el-descriptions-item label="处理人">{{ currentNoshow.handler || '未处理' }}</el-descriptions-item>
                    <el-descriptions-item label="处理原因" :span="2">{{ currentNoshow.reason || '未处理' }}</el-descriptions-item>
                </el-descriptions>
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
import { ref, reactive, computed, onMounted } from 'vue'
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

// 失约状态选项
const statusOptions = [
    { value: 'PENDING', label: '待处理' },
    { value: 'EXCUSED', label: '已免责' },
    { value: 'PENALIZED', label: '已处罚' }
]

// 失约列表
const noshowsList = ref([])
// 加载状态
const loading = ref(false)
// 分页信息
const pagination = reactive({
    currentPage: 1,
    pageSize: 10,
    total: 0
})
// 处理弹窗可见性
const handleDialogVisible = ref(false)
// 详情弹窗可见性
const detailDialogVisible = ref(false)
// 当前选中的失约记录
const currentNoshow = ref(null)
// 当前处理动作
const currentAction = ref('')
// 处理表单
const handleForm = reactive({
    penalty: 0,
    restrictDays: 0,
    reason: '',
    notifyUser: true
})

const handleFormRef = ref<FormInstance>()

// 格式化的失约信息
const noshowInfo = computed(() => {
    if (!currentNoshow.value) return ''
    return `${currentNoshow.value.venueName} / ${currentNoshow.value.date} / ${currentNoshow.value.timeRange}`
})

// 加载失约数据
const loadNoshowsData = async () => {
    loading.value = true
    try {
        // 构建查询参数（用对象，日期格式为'yyyy-MM-dd'）
        const params = {
            page: pagination.currentPage,
            size: pagination.pageSize
        }
        if (searchForm.venueType) {
            params.venueType = searchForm.venueType
        }
        if (searchForm.status) {
            params.status = searchForm.status
        }
        if (searchForm.dateRange && searchForm.dateRange.length === 2) {
            params.startDate = searchForm.dateRange[0]
            params.endDate = searchForm.dateRange[1]
        }
        // 直接用对象传参
        const response = await axios.get('/api/noshows', { params })
        // 兼容后端返回结构
        if (response.data && (response.data.records || Array.isArray(response.data))) {
            noshowsList.value = response.data.records || response.data
            pagination.total = response.data.total || response.data.length || 0
        }
    } catch (error) {
        console.error('加载失约数据失败:', error)
        ElMessage.error('加载失约数据失败')
    } finally {
        loading.value = false
    }
}

// 搜索失约记录
const searchNoshows = () => {
    pagination.currentPage = 1
    loadNoshowsData()
}

// 重置搜索条件
const resetSearch = () => {
    searchForm.venueType = ''
    searchForm.status = ''
    searchForm.dateRange = []
    searchNoshows()
}

// 处理每页显示数量变化
const handleSizeChange = (val: number) => {
    pagination.pageSize = val
    loadNoshowsData()
}

// 处理页码变化
const handleCurrentChange = (val: number) => {
    pagination.currentPage = val
    loadNoshowsData()
}

// 获取状态文本
const getStatusText = (status: string) => {
    const statusMap: Record<string, string> = {
        'PENDING': '待处理',
        'EXCUSED': '已免责',
        'PENALIZED': '已处罚'
    }
    return statusMap[status] || status
}

// 获取状态类型
const getStatusType = (status: string) => {
    const typeMap: Record<string, string> = {
        'PENDING': 'warning',
        'EXCUSED': 'success',
        'PENALIZED': 'danger'
    }
    return typeMap[status] || ''
}

// 处理失约
const handleNoshow = (noshow: any, action: string) => {
    currentNoshow.value = noshow
    currentAction.value = action
    handleForm.penalty = 0
    handleForm.restrictDays = 0
    handleForm.reason = ''
    handleForm.notifyUser = true
    handleDialogVisible.value = true
}

// 提交处理
const submitHandle = async () => {
    if (!currentNoshow.value || !handleFormRef.value) return
    try {
        const handleData = {
            status: currentAction.value,
            penalty: handleForm.penalty,
            restrictDays: handleForm.restrictDays,
            reason: handleForm.reason,
            notifyUser: handleForm.notifyUser
        }
        const response = await axios.post(`/api/noshows/${currentNoshow.value.id}/handle`, handleData)
        // 只要返回对象就认为成功
        if (response.data && typeof response.data === 'object') {
            ElMessage.success('处理成功')
            handleDialogVisible.value = false
            await loadNoshowsData()
        } else {
            ElMessage.error(response.data.message || response.data.msg || '处理失败')
        }
    } catch (error) {
        console.error('处理失约失败:', error, error.response?.data)
        ElMessage.error('处理失约失败: ' + (error.response?.data?.message || error.message))
    }
}

// 查看失约详情
const viewNoshowDetail = (noshow: any) => {
    currentNoshow.value = noshow
    detailDialogVisible.value = true
}

// 创建测试数据
const createTestData = async () => {
    try {
        // 兼容JS组件的POST方式
        const response = await axios.post('/api/noshows/test/create')
        if (response.data && (response.data.code === 200 || response.data.success)) {
            ElMessage.success('创建测试数据成功')
            loadNoshowsData()
        } else {
            ElMessage.error(response.data.message || '创建测试数据失败')
        }
    } catch (error) {
        console.error('创建测试数据失败:', error)
        ElMessage.error('创建测试数据失败')
    }
}

// 初始加载
onMounted(() => {
    loadNoshowsData()
})
</script>

<style scoped>
.admin-noshows-container {
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

.noshow-detail {
    padding: 20px;
}

.no-edit :deep(.el-input__inner) {
    background-color: #f5f7fa;
    cursor: not-allowed;
}
</style> 