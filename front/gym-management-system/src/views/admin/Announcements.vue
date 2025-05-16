<template>
    <div class="announcements-container">
        <el-card class="box-card">
            <template #header>
                <div class="card-header">
                    <span>公告管理</span>
                    <el-button type="primary" @click="showAddDialog">发布公告</el-button>
                </div>
            </template>

            <el-table :data="announcements" style="width: 100%" v-loading="loading">
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="title" label="标题" />
                <el-table-column prop="content" label="内容" show-overflow-tooltip />
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="scope">
                        <el-tag :type="scope.row.status === 'PUBLISHED' ? 'success' : 'info'">
                            {{ scope.row.status === 'PUBLISHED' ? '已发布' : '草稿' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createdTime" label="创建时间" width="180" />
                <el-table-column label="操作" width="200" fixed="right">
                    <template #default="scope">
                        <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
                        <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-container">
                <el-pagination
                    v-model:current-page="currentPage"
                    v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 50, 100]"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="total"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                />
            </div>
        </el-card>

        <el-dialog
            v-model="dialogVisible"
            :title="dialogType === 'add' ? '发布公告' : '编辑公告'"
            width="50%"
        >
            <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
                <el-form-item label="标题" prop="title">
                    <el-input v-model="form.title" placeholder="请输入公告标题" />
                </el-form-item>
                <el-form-item label="内容" prop="content">
                    <el-input
                        v-model="form.content"
                        type="textarea"
                        :rows="6"
                        placeholder="请输入公告内容"
                    />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-select v-model="form.status" placeholder="请选择状态">
                        <el-option label="发布" value="PUBLISHED" />
                        <el-option label="草稿" value="DRAFT" />
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleSubmit">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getAnnouncements, addAnnouncement, updateAnnouncement, deleteAnnouncement } from '@/api/system/announcement'

// 表格数据
const announcements = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref<FormInstance>()
const form = reactive({
    id: '',
    title: '',
    content: '',
    status: 'DRAFT'
})

// 表单验证规则
const rules = {
    title: [
        { required: true, message: '请输入公告标题', trigger: 'blur' },
        { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
    ],
    content: [
        { required: true, message: '请输入公告内容', trigger: 'blur' },
        { min: 10, max: 1000, message: '长度在 10 到 1000 个字符', trigger: 'blur' }
    ],
    status: [
        { required: true, message: '请选择状态', trigger: 'change' }
    ]
}

// 加载公告列表
const loadAnnouncements = async () => {
    loading.value = true
    try {
        const res = await getAnnouncements({
            page: currentPage.value,
            size: pageSize.value
        })
        announcements.value = res.data.list
        total.value = res.data.total
    } catch (error) {
        console.error('加载公告列表失败:', error)
        ElMessage.error('加载公告列表失败')
    } finally {
        loading.value = false
    }
}

// 显示添加对话框
const showAddDialog = () => {
    dialogType.value = 'add'
    form.id = ''
    form.title = ''
    form.content = ''
    form.status = 'DRAFT'
    dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row: any) => {
    dialogType.value = 'edit'
    form.id = row.id
    form.title = row.title
    form.content = row.content
    form.status = row.status
    dialogVisible.value = true
}

// 处理删除
const handleDelete = (row: any) => {
    ElMessageBox.confirm(
        '确定要删除这条公告吗？',
        '警告',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        try {
            await deleteAnnouncement(row.id)
            ElMessage.success('删除成功')
            loadAnnouncements()
        } catch (error) {
            console.error('删除公告失败:', error)
            ElMessage.error('删除公告失败')
        }
    })
}

// 提交表单
const handleSubmit = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (valid) {
            try {
                if (dialogType.value === 'add') {
                    await addAnnouncement(form)
                    ElMessage.success('发布成功')
                } else {
                    await updateAnnouncement(form)
                    ElMessage.success('更新成功')
                }
                dialogVisible.value = false
                loadAnnouncements()
            } catch (error) {
                console.error('保存公告失败:', error)
                ElMessage.error('保存公告失败')
            }
        }
    })
}

// 分页相关
const handleSizeChange = (val: number) => {
    pageSize.value = val
    loadAnnouncements()
}

const handleCurrentChange = (val: number) => {
    currentPage.value = val
    loadAnnouncements()
}

onMounted(() => {
    loadAnnouncements()
})
</script>

<style scoped lang="scss">
.announcements-container {
    padding: 20px;

    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .pagination-container {
        margin-top: 20px;
        display: flex;
        justify-content: flex-end;
    }
}
</style>
 