<template>
    <div class="venues-container">
        <el-card>
            <div slot="header" class="card-header">
                <h2>场地管理</h2>
                <el-button type="primary" @click="handleAdd">添加场地</el-button>
            </div>
            
            <!-- 搜索筛选区域 -->
            <el-form :inline="true" class="search-form" size="small">
                <el-form-item label="场地类型">
                    <el-select v-model="searchForm.venueType" placeholder="选择场地类型" clearable @change="searchVenues">
                        <el-option v-for="item in venueTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="searchVenues">查询</el-button>
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
                <el-table-column prop="id" label="ID" width="80"></el-table-column>
                <el-table-column prop="name" label="场地名称" width="150"></el-table-column>
                <el-table-column prop="type" label="场地类型" width="120"></el-table-column>
                <el-table-column prop="location" label="场地位置"></el-table-column>
                <el-table-column prop="capacity" label="容纳人数" width="100"></el-table-column>
                <el-table-column prop="pricePerHour" label="价格" width="120">
                    <template #default="scope">
                        {{ scope.row.pricePerHour }} 元/小时
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="120">
                    <template #default="scope">
                        <el-tag v-if="scope.row.status === 'NORMAL'" type="success">正常</el-tag>
                        <el-tag v-else-if="scope.row.status === 'MAINTENANCE'" type="warning">维护中</el-tag>
                        <el-tag v-else-if="scope.row.status === 'SPECIAL'" type="danger">特殊场地</el-tag>
                        <el-tag v-else type="info">{{ scope.row.status }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="320">
                    <template #default="scope">
                        <div class="action-btns">
                            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
                            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
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
                    :page-sizes="[5, 10, 20, 50]"
                    :page-size="pagination.pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="pagination.total">
                </el-pagination>
            </div>
        </el-card>
        
        <!-- 添加/编辑场地弹窗 -->
        <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
            <el-form :model="venueForm" :rules="venueRules" ref="venueForm" label-width="100px">
                <el-form-item label="场地名称" prop="name">
                    <el-input v-model="venueForm.name" placeholder="请输入场地名称"></el-input>
                </el-form-item>
                <el-form-item label="场地类型" prop="type">
                    <el-select v-model="venueForm.type" placeholder="请选择场地类型">
                        <el-option v-for="item in venueTypes" :key="item.value" :label="item.label" :value="item.value"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="场地描述" prop="description">
                    <el-input type="textarea" v-model="venueForm.description" placeholder="请输入场地描述"></el-input>
                </el-form-item>
                <el-form-item label="价格" prop="pricePerHour">
                    <el-input-number v-model="venueForm.pricePerHour" :min="0" :precision="2" :step="10"></el-input-number>
                    <span class="unit">元/小时</span>
                </el-form-item>
                <el-form-item label="状态" prop="isAvailable">
                    <el-switch v-model="venueForm.isAvailable"></el-switch>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitForm">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

// 场地类型选项
const venueTypes = [
    { value: 'basketball', label: '篮球场' },
    { value: 'football', label: '足球场' },
    { value: 'badminton', label: '羽毛球场' },
    { value: 'tennis', label: '网球场' },
    { value: 'swimming', label: '游泳池' },
    { value: 'table_tennis', label: '乒乓球室' }
]

// 搜索表单
const searchForm = reactive({
    venueType: ''
})

// 分页数据
const pagination = reactive({
    currentPage: 1,
    pageSize: 10,
    total: 0
})

// 场地列表数据
const venueList = ref([])
const loading = ref(false)

// 弹窗相关数据
const dialogVisible = ref(false)
const dialogTitle = ref('')
const venueForm = reactive({
    id: '',
    name: '',
    type: '',
    description: '',
    pricePerHour: 0,
    isAvailable: true
})

// 表单验证规则
const venueRules = {
    name: [
        { required: true, message: '请输入场地名称', trigger: 'blur' },
        { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
    ],
    type: [
        { required: true, message: '请选择场地类型', trigger: 'change' }
    ],
    pricePerHour: [
        { required: true, message: '请输入价格', trigger: 'blur' }
    ]
}

// 加载场地数据
const loadVenues = async () => {
    loading.value = true
    try {
        const params = {
            page: pagination.currentPage,
            size: pagination.pageSize,
            type: searchForm.venueType
        }
        
        const response = await axios.get('/api/venues', { params })
        if (response.data.code === 200) {
            const pageData = response.data.data
            venueList.value = pageData.records || []
            // 转换场地类型为中文显示
            venueList.value.forEach(venue => {
                if (venue.type) {
                    const typeOption = venueTypes.find(t => t.value === venue.type)
                    if (typeOption) {
                        venue.type = typeOption.label
                    }
                }
            })
            pagination.total = pageData.total || 0
        } else {
            ElMessage.error(response.data.message || '加载场地数据失败')
        }
    } catch (error) {
        console.error('加载场地数据失败:', error)
        ElMessage.error('加载场地数据失败')
    } finally {
        loading.value = false
    }
}

// 搜索场地
const searchVenues = () => {
    pagination.currentPage = 1
    loadVenues()
}

// 重置搜索条件
const resetSearch = () => {
    searchForm.venueType = ''
    searchVenues()
}

// 分页大小变化处理
const handleSizeChange = (val: number) => {
    pagination.pageSize = val
    loadVenues()
}

// 页码变化处理
const handleCurrentChange = (val: number) => {
    pagination.currentPage = val
    loadVenues()
}

// 添加场地
const handleAdd = () => {
    dialogTitle.value = '添加场地'
    Object.assign(venueForm, {
        id: '',
        name: '',
        type: '',
        description: '',
        pricePerHour: 0,
        isAvailable: true
    })
    dialogVisible.value = true
}

// 编辑场地
const handleEdit = (row: any) => {
    dialogTitle.value = '编辑场地'
    Object.assign(venueForm, row)
    dialogVisible.value = true
}

// 删除场地
const handleDelete = (row: any) => {
    ElMessageBox.confirm('确定要删除该场地吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            const response = await axios.delete(`/api/venues/${row.id}`)
            if (response.data.code === 200) {
                ElMessage.success('删除成功')
                loadVenues()
            } else {
                ElMessage.error(response.data.message || '删除失败')
            }
        } catch (error) {
            console.error('删除场地失败:', error)
            ElMessage.error('删除失败')
        }
    })
}

// 提交表单
const submitForm = async () => {
    try {
        const response = venueForm.id
            ? await axios.put(`/api/venues/${venueForm.id}`, venueForm)
            : await axios.post('/api/venues', venueForm)

        if (response.data.code === 200) {
            ElMessage.success(venueForm.id ? '更新成功' : '添加成功')
            dialogVisible.value = false
            loadVenues()
        } else {
            ElMessage.error(response.data.message || (venueForm.id ? '更新失败' : '添加失败'))
        }
    } catch (error) {
        console.error(venueForm.id ? '更新场地失败:' : '添加场地失败:', error)
        ElMessage.error(venueForm.id ? '更新失败' : '添加失败')
    }
}

// 设置场地状态
const setVenueStatus = async (row, status) => {
    try {
        const response = await axios.put(`/api/venues/${row.id}/status`, { status });
        if (response.data.code === 200) {
            ElMessage.success(`更新场地状态为${status === 'NORMAL' ? '正常' : '维护中'}成功`);
            loadVenues();
        } else {
            ElMessage.error(response.data.message || '更新场地状态失败');
        }
    } catch (error) {
        ElMessage.error('更新场地状态失败');
    }
};

// 组件挂载时加载数据
onMounted(() => {
    loadVenues()
})
</script>

<style scoped>
.venues-container {
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

.unit {
    margin-left: 10px;
    color: #606266;
}

.action-btns {
    display: flex;
    gap: 8px;
}

.action-btns .el-button {
    font-size: 12px;
    padding: 4px 10px;
}
</style> 