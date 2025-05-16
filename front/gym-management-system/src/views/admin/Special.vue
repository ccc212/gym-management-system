<template>
    <div class="admin-special-container">
        <el-card>
            <template #header>
                <div class="card-header">
                    <h2>特殊场地管理</h2>
                    <el-button type="primary" size="small" @click="showAddSpecialDialog">添加特殊安排</el-button>
                </div>
            </template>
            
            <!-- 搜索筛选区域 -->
            <el-form :inline="true" class="search-form" size="small">
                <el-form-item label="场地类型">
                    <el-select v-model="searchForm.venueType" placeholder="选择场地类型" clearable @change="searchSpecialArrangements">
                        <el-option v-for="item in venueTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="场地名称">
                    <el-select v-model="searchForm.venueId" placeholder="选择场地" clearable @change="searchSpecialArrangements">
                        <el-option v-for="venue in filteredVenues" :key="venue.id" :label="venue.name" :value="venue.id"></el-option>
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
                        @change="searchSpecialArrangements">
                    </el-date-picker>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="searchSpecialArrangements">查询</el-button>
                    <el-button @click="resetSearch">重置</el-button>
                </el-form-item>
            </el-form>
            
            <!-- 特殊场地列表 -->
            <el-table 
                v-loading="loading" 
                :data="specialArrangementList" 
                style="width: 100%"
                :header-cell-style="{background:'#f5f7fa', color:'#606266'}"
                border>
                <el-table-column prop="venueName" label="场地名称" width="150"></el-table-column>
                <el-table-column prop="venueType" label="场地类型" width="120"></el-table-column>
                <el-table-column prop="date" label="日期" width="120"></el-table-column>
                <el-table-column prop="timeSlot" label="时间段" width="150"></el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="scope">
                        <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="remarks" label="备注" min-width="200"></el-table-column>
                <el-table-column label="操作" width="150">
                    <template #default="scope">
                        <el-button type="primary" size="small" @click="editSpecialArrangement(scope.row)">编辑</el-button>
                        <el-button type="danger" size="small" @click="deleteSpecialArrangement(scope.row)">删除</el-button>
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
        
        <!-- 添加/编辑特殊场地弹窗 -->
        <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
            <el-form :model="specialForm" ref="specialFormRef" label-width="100px">
                <el-form-item label="场地" prop="venueId" required>
                    <el-select v-model="specialForm.venueId" placeholder="选择场地" style="width: 100%">
                        <el-option
                            v-for="venue in filteredVenues"
                            :key="venue.id"
                            :label="venue.name"
                            :value="venue.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="日期" prop="date" required>
                    <el-date-picker
                        v-model="specialForm.date"
                        type="date"
                        placeholder="选择日期"
                        value-format="YYYY-MM-DD"
                        style="width: 100%">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="时间段" required>
                    <el-time-picker
                        v-model="specialForm.startTime"
                        placeholder="开始时间"
                        format="HH:mm"
                        value-format="HH:mm"
                        style="width: 45%">
                    </el-time-picker>
                    <span style="margin: 0 10px">至</span>
                    <el-time-picker
                        v-model="specialForm.endTime"
                        placeholder="结束时间"
                        format="HH:mm"
                        value-format="HH:mm"
                        style="width: 45%">
                    </el-time-picker>
                </el-form-item>
                <el-form-item label="状态" prop="status" required>
                    <el-select v-model="specialForm.status" placeholder="选择状态" style="width: 100%">
                        <el-option label="特殊场地" value="SPECIAL"></el-option>
                        <el-option label="维护中" value="MAINTENANCE"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="备注" prop="remarks">
                    <el-input type="textarea" v-model="specialForm.remarks" rows="3"></el-input>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="saveSpecialArrangement">确定</el-button>
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

// 场馆列表
const venues = ref([])
// 特殊场地列表
const specialArrangementList = ref([])
// 加载状态
const loading = ref(false)
// 分页信息
const pagination = reactive({
    currentPage: 1,
    pageSize: 10,
    total: 0
})
// 搜索表单
const searchForm = reactive({
    venueType: '',
    venueId: '',
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
// 特殊场地弹窗可见性
const dialogVisible = ref(false)
// 弹窗类型（add/edit）
const dialogTitle = ref('')
// 特殊场地表单
const specialForm = reactive({
    id: null as number | null,
    venueId: null as number | null,
    date: '',
    startTime: '',
    endTime: '',
    status: 'SPECIAL',
    remarks: ''
})

const specialFormRef = ref<FormInstance>()

// 按类型筛选的场馆列表
const filteredVenues = computed(() => {
    if (!searchForm.venueType) {
        return venues.value
    }
    return venues.value.filter(venue => venue.type === searchForm.venueType)
})

// 获取场地列表
const fetchVenues = async () => {
    try {
        const response = await axios.get('/api/venues')
        if (response.data.code === 200) {
            venues.value = response.data.data.records || []
            // 转换场地类型为中文显示
            venues.value.forEach(venue => {
                if (venue.type) {
                    const typeOption = venueTypes.find(t => t.value === venue.type)
                    if (typeOption) {
                        venue.type = typeOption.label
                    }
                }
            })
            console.log('获取到的场地列表:', venues.value)
        } else {
            console.error('获取场地列表失败:', response.data.msg)
            ElMessage.error('获取场地列表失败')
        }
    } catch (error) {
        console.error('获取场地列表失败:', error)
        ElMessage.error('获取场地列表失败')
        venues.value = []
    }
}

// 加载特殊场地数据
const loadSpecialArrangementData = async () => {
    loading.value = true
    
    try {
        // 构建查询参数
        const params = new URLSearchParams({
            page: pagination.currentPage.toString(),
            size: pagination.pageSize.toString()
        })
        
        if (searchForm.venueType) {
            params.append('venueType', searchForm.venueType)
        }
        if (searchForm.venueId) {
            params.append('venueId', searchForm.venueId)
        }
        if (searchForm.dateRange && searchForm.dateRange.length === 2) {
            params.append('startDate', searchForm.dateRange[0])
            params.append('endDate', searchForm.dateRange[1])
        }
        
        const response = await axios.get(`/api/special-arrangements?${params.toString()}`)
        
        if (response.data && response.data.data) {
            specialArrangementList.value = response.data.data.records || []
            pagination.total = response.data.data.total || 0
        }
    } catch (error) {
        console.error('加载特殊场地数据失败:', error)
        ElMessage.error('加载特殊场地数据失败')
    } finally {
        loading.value = false
    }
}

// 搜索特殊场地
const searchSpecialArrangements = () => {
    pagination.currentPage = 1
    loadSpecialArrangementData()
}

// 重置搜索条件
const resetSearch = () => {
    searchForm.venueType = ''
    searchForm.venueId = ''
    searchForm.dateRange = []
    searchSpecialArrangements()
}

// 获取状态文本
const getStatusText = (status: string) => {
    const statusMap: Record<string, string> = {
        'SPECIAL': '特殊场地',
        'MAINTENANCE': '维护中'
    }
    return statusMap[status] || status
}

// 获取状态类型
const getStatusType = (status: string) => {
    const typeMap: Record<string, string> = {
        'SPECIAL': 'success',
        'MAINTENANCE': 'warning'
    }
    return typeMap[status] || ''
}

// 编辑特殊场地
const editSpecialArrangement = (row: any) => {
    specialForm.id = row.id
    specialForm.venueId = row.venueId
    specialForm.date = row.date
    specialForm.startTime = row.startTime
    specialForm.endTime = row.endTime
    specialForm.status = row.status
    specialForm.remarks = row.remarks
    dialogTitle.value = '编辑特殊场地'
    dialogVisible.value = true
}

// 删除特殊场地
const deleteSpecialArrangement = (row: any) => {
    ElMessageBox.confirm('确定要删除这条特殊场地记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            const response = await axios.delete(`/api/special-arrangements/${row.id}`)
            if (response.data.code === 200) {
                ElMessage.success('删除成功')
                loadSpecialArrangementData()
            } else {
                ElMessage.error(response.data.message || '删除失败')
            }
        } catch (error) {
            console.error('删除特殊场地失败:', error)
            ElMessage.error('删除特殊场地失败')
        }
    }).catch(() => {})
}

// 保存特殊场地
const saveSpecialArrangement = async () => {
    if (!specialFormRef.value) return
    
    try {
        const response = await axios({
            method: specialForm.id ? 'put' : 'post',
            url: `/api/special-arrangements${specialForm.id ? `/${specialForm.id}` : ''}`,
            data: specialForm
        })
        
        if (response.data.code === 200) {
            ElMessage.success(specialForm.id ? '更新成功' : '添加成功')
            dialogVisible.value = false
            loadSpecialArrangementData()
        } else {
            ElMessage.error(response.data.message || (specialForm.id ? '更新失败' : '添加失败'))
        }
    } catch (error) {
        console.error(specialForm.id ? '更新特殊场地失败:' : '添加特殊场地失败:', error)
        ElMessage.error(specialForm.id ? '更新特殊场地失败' : '添加特殊场地失败')
    }
}

// 显示添加特殊场地弹窗
const showAddSpecialDialog = () => {
    specialForm.id = null
    specialForm.venueId = null
    specialForm.date = ''
    specialForm.startTime = ''
    specialForm.endTime = ''
    specialForm.status = 'SPECIAL'
    specialForm.remarks = ''
    dialogTitle.value = '添加特殊场地'
    dialogVisible.value = true
}

// 处理页码变化
const handleCurrentChange = (val: number) => {
    pagination.currentPage = val
    loadSpecialArrangementData()
}

// 处理每页显示数量变化
const handleSizeChange = (val: number) => {
    pagination.pageSize = val
    loadSpecialArrangementData()
}

// 初始加载
onMounted(() => {
    fetchVenues()
    loadSpecialArrangementData()
})
</script>

<style scoped>
.admin-special-container {
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
</style> 