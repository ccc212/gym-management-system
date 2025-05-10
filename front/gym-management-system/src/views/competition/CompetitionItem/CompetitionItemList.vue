<template>
    <el-main>
        <!-- 搜索区域 -->
        <el-form :model="searchParm" :inline="true" size="default">
            <el-form-item>
                <el-input v-model="searchParm.name" placeholder="请输入项目名称"></el-input>
            </el-form-item>
            <el-form-item style="width: 200px;">
                <el-select v-model="searchParm.type" placeholder="请选择项目类型" clearable>
                    <el-option
                        v-for="item in itemTypeOptions"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    ></el-option>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button icon="Search" @click="searchBtn">搜索</el-button>
                <el-button icon="Refresh" type="danger" @click="resetBtn">重置</el-button>
                <el-button icon="Plus" type="primary" @click="addBtn">新增</el-button>
            </el-form-item>
        </el-form>

        <!-- 表格 -->
        <el-table :height="tableHeight" :data="tableList" border stripe>
            <el-table-column prop="id" label="ID" width="80px"></el-table-column>
            <el-table-column prop="name" label="项目名称" width="150px"></el-table-column>
            <el-table-column prop="type" label="项目类型" width="120px">
                <template #default="scope">
                    <el-tag v-if="scope.row.type === 0" type="success">乒乓球项目</el-tag>
                    <el-tag v-else-if="scope.row.type === 1" type="primary">篮球项目</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="160px"></el-table-column>
            <el-table-column prop="updateTime" label="更新时间" width="160px"></el-table-column>
            <el-table-column label="操作" width="220px">
                <template #default="scope">
                    <el-button type="warning" icon="Edit" size="small" @click="editBtn(scope.row)">编辑</el-button>
                    <el-button type="danger" icon="Delete" size="small" @click="deleteBtn(scope.row.id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 -->
        <el-pagination
            :current-page.sync="searchParm.page"
            :page-sizes="[10, 20, 40]"
            :page-size="searchParm.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="searchParm.total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />

        <!-- 新增/编辑对话框 -->
        <el-dialog :title="dialogTitle" v-model="openDialog" width="500px" append-to-body>
            <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
                <el-form-item label="名称" prop="name">
                    <el-input v-model="formData.name" placeholder="请输入项目名称" />
                </el-form-item>
                <el-form-item label="项目类型" prop="type">
                    <el-select v-model="formData.type" placeholder="请选择项目类型" clearable>
                        <el-option
                            v-for="item in itemTypeOptions"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        ></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="赛事类别" prop="category">
                    <el-select v-model="formData.category" placeholder="请选择赛事类别" clearable>
                        <el-option
                            v-for="item in categoryOptions"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        ></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="是否团体赛事" prop="isTeamCompetition">
                    <el-select v-model="formData.isTeamCompetition" placeholder="请选择是否为团体赛事" clearable>
                        <el-option
                            v-for="item in teamCompetitionOptions"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        ></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="submitForm">确 定</el-button>
                    <el-button @click="cancelDialog">取 消</el-button>
                </div>
            </template>
        </el-dialog>
    </el-main>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watchEffect } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { CompetitionItemControllerService } from '../../../../generated/services/CompetitionItemControllerService';

// 表格高度
const tableHeight = ref(0);

// 项目类型选项
const itemTypeOptions = [
    { value: 0, label: '乒乓球项目' },
    { value: 1, label: '篮球项目' }
];

// 赛事类别选项
const categoryOptions = [
    { value: 0, label: '淘汰赛' },
    { value: 1, label: '循环赛' }
];

// 团体赛事选项
const teamCompetitionOptions = [
    { value: 0, label: '个人赛' },
    { value: 1, label: '团体赛' }
];

// 列表数据
const tableList = ref([]);

// 搜索参数
const searchParm = reactive({
    page: 1,
    pageSize: 10,
    total: 0,
    name: '',
    type: null
});

// 对话框相关
const openDialog = ref(false);
const dialogTitle = ref('');
const formRef = ref(null);
const formData = reactive({
    id: '',
    name: '',
    type: null,
    category: null,
    isTeamCompetition: null
});

// 表单校验规则
const rules = {
    name: [
        { required: true, message: '请输入项目名称', trigger: 'blur' }
    ],
    type: [
        { required: true, message: '请选择项目类型', trigger: 'change' }
    ],
    category: [
        { required: true, message: '请选择赛事类别', trigger: 'change' }
    ],
    isTeamCompetition: [
        { required: true, message: '请选择是否为团体赛事', trigger: 'change' }
    ]
};

// 生命周期
onMounted(() => {
    // 计算表格高度
    tableHeight.value = window.innerHeight - 320;
    // 获取列表数据
    getList();
});

// 获取列表数据
const getList = async () => {
    try {
        const res = await CompetitionItemControllerService.listCompetitionItemUsingGet(
            searchParm.category,
            searchParm.isTeamCompetition,
            searchParm.name,
            searchParm.page,
            searchParm.pageSize,
            searchParm.type
        );
        if (res && res.data) {
            tableList.value = res.data.records;
            searchParm.total = res.data.total;
        }
    } catch (error) {
        console.error('加载数据失败:', error);
        ElMessage.error('加载数据失败，请检查网络或联系管理员');
    }
};

// 搜索按钮
const searchBtn = () => {
    searchParm.page = 1;
    getList();
};

// 重置按钮
const resetBtn = () => {
    searchParm.name = '';
    searchParm.type = null;
    searchParm.page = 1;
    getList();
};

// 新增按钮
const addBtn = () => {
    dialogTitle.value = '新增赛事项目';
    formData.id = '';
    formData.name = '';
    formData.type = null;
    formData.category = null;
    formData.isTeamCompetition = null;
    openDialog.value = true;
};

// 编辑按钮
const editBtn = async (row: any) => {
    dialogTitle.value = '编辑赛事项目';
    
    // 获取详情
    try {
        const res = await CompetitionItemControllerService.getCompetitionItemUsingGet(Number(row.id));
        if (res && res.data) {
            const detail = res.data;
            formData.id = detail.id;
            formData.name = detail.name;
            formData.description = detail.description;
            formData.type = detail.type;
            formData.category = detail.category;
            formData.isTeamCompetition = detail.isTeamCompetition;
            formData.maxSignUpNum = detail.maxSignUpNum;
            formData.teamMaxNum = detail.teamMaxNum;
            formData.teamMinNum = detail.teamMinNum;
            formData.requirement = detail.requirement;
            
            openDialog.value = true;
        }
    } catch (error) {
        console.error('获取详情失败:', error);
        ElMessage.error('获取详情失败，请检查网络或联系管理员');
    }
};

// 删除按钮
const deleteBtn = (id: string) => {
    ElMessageBox.confirm('确定要删除此赛事项目吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            const res = await CompetitionItemControllerService.deleteCompetitionItemUsingDelete(Number(id));
            if (res && res.code === 0) {
                ElMessage.success('删除成功');
                // 刷新数据
                getList();
            } else {
                ElMessage.error(res?.msg || '删除失败');
            }
        } catch (error) {
            console.error('删除失败:', error);
            ElMessage.error('删除失败，请检查网络或联系管理员');
        }
    }).catch(() => {});
};

// 提交表单
const submitForm = async () => {
    if (!formRef.value) return;

    formRef.value.validate(async (valid: boolean) => {
        if (valid) {
            try {
                const res = formData.id ? 
                    await CompetitionItemControllerService.updateCompetitionItemUsingPut(formData) : 
                    await CompetitionItemControllerService.addCompetitionItemUsingPost(formData);
                
                if (res && res.code === 0) {
                    // 先关闭对话框
                    openDialog.value = false;
                    ElMessage.success(formData.id ? '更新成功' : '添加成功');
                    // 刷新数据
                    getList();
                } else {
                    ElMessage.error(res?.msg || '操作失败');
                }
            } catch (error) {
                console.error('提交表单错误:', error);
                ElMessage.error('操作失败，请检查网络或联系管理员');
            }
        }
    });
};

// 取消对话框
const cancelDialog = () => {
    openDialog.value = false;
};

// 分页大小改变
const handleSizeChange = (val: number) => {
    searchParm.pageSize = val;
    getList();
};

// 当前页改变
const handleCurrentChange = (val: number) => {
    searchParm.page = val;
    getList();
};
</script>

<style scoped>
</style> 