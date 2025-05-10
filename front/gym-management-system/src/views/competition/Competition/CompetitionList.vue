<template>
    <el-main>
        <!-- 搜索区域 -->
        <el-form :model="searchParm" :inline="true" size="default">
            <el-form-item>
                <el-input v-model="searchParm.name" placeholder="请输入赛事名称"></el-input>
            </el-form-item>
            <el-form-item style="width: 200px;">
                <el-select v-model="searchParm.status" placeholder="请选择赛事状态" clearable>
                    <el-option
                        v-for="item in statusOptions"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    ></el-option>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-date-picker
                    v-model="searchParm.timeRange"
                    type="daterange"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    value-format="YYYY-MM-DD"
                ></el-date-picker>
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
            <el-table-column prop="name" label="赛事名称" width="150px"></el-table-column>
            <el-table-column prop="description" label="描述" width="180px" show-overflow-tooltip></el-table-column>
            <el-table-column prop="status" label="状态" width="100px">
                <template #default="scope">
                    <el-tag v-if="scope.row.status === 0" type="info">未开始</el-tag>
                    <el-tag v-else-if="scope.row.status === 1" type="success">报名中</el-tag>
                    <el-tag v-else-if="scope.row.status === 2" type="warning">进行中</el-tag>
                    <el-tag v-else-if="scope.row.status === 3" type="danger">已结束</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="signUpDeadline" label="报名截止时间" width="160px"></el-table-column>
            <el-table-column prop="startTime" label="比赛开始时间" width="160px"></el-table-column>
            <el-table-column prop="endTime" label="比赛结束时间" width="160px"></el-table-column>
            <el-table-column prop="hoster" label="举办方" width="120px"></el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="160px"></el-table-column>
            <el-table-column label="操作" width="280px">
                <template #default="scope">
                    <el-button type="primary" icon="View" size="small" @click="viewBtn(scope.row)">查看详情</el-button>
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
        <el-dialog :title="dialogTitle" v-model="openDialog" width="700px" append-to-body>
            <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
                <el-form-item label="赛事名称" prop="name">
                    <el-input v-model="formData.name" placeholder="请输入赛事名称" />
                </el-form-item>
                <el-form-item label="赛事描述" prop="description">
                    <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入赛事描述" />
                </el-form-item>
                <el-form-item label="举办方" prop="hoster">
                    <el-input v-model="formData.hoster" placeholder="请输入举办方" />
                </el-form-item>
                <el-form-item label="赛事类型" prop="type">
                    <el-select v-model="formData.type" placeholder="请选择赛事类型">
                        <el-option
                            v-for="item in typeOptions"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        ></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="赛事类别" prop="category">
                    <el-select v-model="formData.category" placeholder="请选择赛事类别">
                        <el-option
                            v-for="item in categoryOptions"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        ></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="是否团体赛事" prop="isTeamCompetition">
                    <el-select v-model="formData.isTeamCompetition" placeholder="请选择是否为团体赛事">
                        <el-option
                            v-for="item in teamCompetitionOptions"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        ></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="最大报名人数" prop="maxSignUpNum">
                    <el-input-number v-model="formData.maxSignUpNum" :min="1" :max="1000" placeholder="请输入最大报名人数" />
                </el-form-item>
                <template v-if="formData.isTeamCompetition === 1">
                    <el-form-item label="每队人数上限" prop="teamMaxNum">
                        <el-input-number v-model="formData.teamMaxNum" :min="1" :max="100" placeholder="请输入每队人数上限" />
                    </el-form-item>
                    <el-form-item label="每队人数下限" prop="teamMinNum">
                        <el-input-number v-model="formData.teamMinNum" :min="1" :max="100" placeholder="请输入每队人数下限" />
                    </el-form-item>
                </template>
                <el-form-item label="赛事状态" prop="status">
                    <el-select v-model="formData.status" placeholder="请选择赛事状态">
                        <el-option
                            v-for="item in statusOptions"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        ></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="报名截止时间" prop="signUpDeadline">
                    <el-date-picker
                        v-model="formData.signUpDeadline"
                        type="datetime"
                        placeholder="请选择报名截止时间"
                    ></el-date-picker>
                </el-form-item>
                <el-form-item label="比赛时间范围" prop="competitionTimeRange">
                    <el-date-picker
                        v-model="formData.competitionTimeRange"
                        type="datetimerange"
                        range-separator="至"
                        start-placeholder="比赛开始时间"
                        end-placeholder="比赛结束时间"
                    ></el-date-picker>
                </el-form-item>
                <el-form-item label="参赛要求" prop="requirement">
                    <el-input v-model="formData.requirement" type="textarea" :rows="2" placeholder="请输入参赛要求" />
                </el-form-item>
                <el-form-item label="赛事项目" prop="competitionItemIds">
                    <el-select v-model="formData.competitionItemIds" multiple placeholder="请选择赛事项目">
                        <el-option
                            v-for="item in competitionItemOptions"
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

        <!-- 详情对话框 -->
        <el-dialog title="赛事详情" v-model="openDetailDialog" width="800px" append-to-body>
            <el-descriptions :column="2" border>
                <el-descriptions-item label="赛事名称">{{ detailData.name }}</el-descriptions-item>
                <el-descriptions-item label="举办方">{{ detailData.hoster }}</el-descriptions-item>
                <el-descriptions-item label="赛事状态">
                    <el-tag v-if="detailData.status === 0" type="info">未开始</el-tag>
                    <el-tag v-else-if="detailData.status === 1" type="success">报名中</el-tag>
                    <el-tag v-else-if="detailData.status === 2" type="warning">进行中</el-tag>
                    <el-tag v-else-if="detailData.status === 3" type="danger">已结束</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="报名截止时间">{{ detailData.signUpDeadline }}</el-descriptions-item>
                <el-descriptions-item label="比赛时间">{{ detailData.startTime }} 至 {{ detailData.endTime }}</el-descriptions-item>
                <el-descriptions-item label="赛事描述" :span="2">{{ detailData.description }}</el-descriptions-item>
            </el-descriptions>

            <el-divider>赛事项目</el-divider>
            <el-table :data="detailItems" border>
                <el-table-column prop="id" label="ID" width="80px"></el-table-column>
                <el-table-column prop="name" label="项目名称" width="150px"></el-table-column>
                <el-table-column prop="type" label="项目类型" width="120px">
                    <template #default="scope">
                        <el-tag v-if="scope.row.type === 0" type="success">乒乓球项目</el-tag>
                        <el-tag v-else-if="scope.row.type === 1" type="primary">篮球项目</el-tag>
                    </template>
                </el-table-column>
            </el-table>

            <el-divider>场地信息</el-divider>
            <el-table :data="detailVenues" border>
                <el-table-column prop="id" label="ID" width="80px"></el-table-column>
                <el-table-column prop="venueName" label="场地名称" width="150px"></el-table-column>
                <el-table-column prop="responsibleName" label="负责人" width="120px"></el-table-column>
                <el-table-column prop="phone" label="联系电话" width="120px"></el-table-column>
                <el-table-column prop="startTime" label="开始时间" width="160px"></el-table-column>
                <el-table-column prop="endTime" label="结束时间" width="160px"></el-table-column>
                <el-table-column prop="status" label="状态" width="100px">
                    <template #default="scope">
                        <el-tag v-if="scope.row.status === 0" type="info">预约中</el-tag>
                        <el-tag v-else-if="scope.row.status === 1" type="success">预约成功</el-tag>
                        <el-tag v-else-if="scope.row.status === 2" type="danger">预约失败</el-tag>
                    </template>
                </el-table-column>
            </el-table>
        </el-dialog>
    </el-main>
</template>

<script setup lang="ts">
import {onMounted, reactive, ref, watchEffect} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {CompetitionControllerService} from '../../../../generated/services/CompetitionControllerService';
import {CompetitionItemControllerService} from '../../../../generated/services/CompetitionItemControllerService';
import { formatDateTime } from '@/utils/moment';

// 表格高度
const tableHeight = ref(0);

// 状态选项
const statusOptions = [
    { value: 0, label: '未开始' },
    { value: 1, label: '报名中' },
    { value: 2, label: '进行中' },
    { value: 3, label: '已结束' }
];

// 赛事类型选项
const typeOptions = [
    { value: 0, label: '乒乓球赛' },
    { value: 1, label: '篮球赛' }
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

// 赛事项目选项
const competitionItemOptions = ref([]);

// 列表数据
const tableList = ref([]);

// 搜索参数
const searchParm = reactive({
    page: 1,
    pageSize: 10,
    total: 0,
    name: '',
    status: null,
    timeRange: []
});

// 对话框相关
const openDialog = ref(false);
const dialogTitle = ref('');
const formRef = ref(null);
const formData = reactive({
    id: '',
    name: '',
    description: '',
    hoster: '',
    type: null,
    category: null,
    isTeamCompetition: null,
    maxSignUpNum: 100,
    teamMaxNum: 10,
    teamMinNum: 5,
    status: 0,
    signUpDeadline: '',
    competitionTimeRange: [],
    requirement: '',
    competitionItemIds: []
});

// 详情对话框相关
const openDetailDialog = ref(false);
const detailData = ref({});
const detailItems = ref([]);
const detailVenues = ref([]);

// 表单校验规则
const rules = {
    name: [
        { required: true, message: '请输入赛事名称', trigger: 'blur' }
    ],
    hoster: [
        { required: true, message: '请输入举办方', trigger: 'blur' }
    ],
    type: [
        { required: true, message: '请选择赛事类型', trigger: 'change' }
    ],
    category: [
        { required: true, message: '请选择赛事类别', trigger: 'change' }
    ],
    isTeamCompetition: [
        { required: true, message: '请选择是否为团体赛事', trigger: 'change' }
    ],
    status: [
        { required: true, message: '请选择赛事状态', trigger: 'change' }
    ],
    signUpDeadline: [
        { required: true, message: '请选择报名截止时间', trigger: 'change' }
    ],
    competitionTimeRange: [
        { required: true, message: '请选择比赛时间', trigger: 'change' }
    ]
};

// 获取列表数据
const loadData = async () => {
    try {
        // 处理时间范围
        const params = { ...searchParm };
        if (params.timeRange && params.timeRange.length === 2) {
            params.startTime = params.timeRange[0];
            params.endTime = params.timeRange[1];
        }
        delete params.timeRange;

        const res = await CompetitionControllerService.listCompetitionUsingGet(
            params.category,
            params.hoster,
            params.isTeamCompetition,
            params.name,
            params.page,
            params.pageSize,
            params.status,
            params.type
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

/**
 * 监听 searchParm 变量，改变时触发页面的重新加载
 */
watchEffect(() => {
    loadData();
});

// 获取赛事项目选项
const getCompetitionItemOptions = async () => {
    try {
        const res = await CompetitionItemControllerService.listCompetitionItemUsingGet(
            null, // category
            null, // isTeamCompetition
            '', // name
            1, // page
            100, // pageSize
            null // type
        );
        if (res && res.data && res.data.records) {
            competitionItemOptions.value = res.data.records.map((item: any) => ({
                value: item.id,
                label: item.name
            }));
        }
    } catch (error) {
        console.error('获取赛事项目选项失败:', error);
        ElMessage.error('获取赛事项目选项失败，请检查网络或联系管理员');
    }
};

/**
 * 页面加载时，初始化数据
 */
onMounted(() => {
    // 计算表格高度
    tableHeight.value = window.innerHeight - 320;
    // 获取赛事项目选项
    getCompetitionItemOptions();
});

// 搜索按钮
const searchBtn = () => {
    searchParm.page = 1;
};

// 重置按钮
const resetBtn = () => {
    searchParm.name = '';
    searchParm.status = null;
    searchParm.timeRange = [];
    searchParm.page = 1;
};

// 新增按钮
const addBtn = () => {
    dialogTitle.value = '新增赛事';
    formData.id = '';
    formData.name = '';
    formData.description = '';
    formData.hoster = '';
    formData.type = null;
    formData.category = null;
    formData.isTeamCompetition = null;
    formData.maxSignUpNum = 100;
    formData.teamMaxNum = 10;
    formData.teamMinNum = 5;
    formData.status = 0;
    formData.signUpDeadline = '';
    formData.competitionTimeRange = [];
    formData.requirement = '';
    formData.competitionItemIds = [];
    openDialog.value = true;
};

// 编辑按钮
const editBtn = async (row: any) => {
    dialogTitle.value = '编辑赛事';
    
    // 获取详情
    try {
        const res = await CompetitionControllerService.getDetailUsingGet(Number(row.id));
        if (res && res.data) {
            const detail = res.data;
            formData.id = detail.id;
            formData.name = detail.name;
            formData.description = detail.description;
            formData.hoster = detail.hoster;
            formData.type = detail.type;
            formData.category = detail.category;
            formData.isTeamCompetition = detail.isTeamCompetition;
            formData.maxSignUpNum = detail.maxSignUpNum;
            formData.teamMaxNum = detail.teamMaxNum;
            formData.teamMinNum = detail.teamMinNum;
            formData.status = detail.status;
            formData.signUpDeadline = detail.signUpDeadline;
            formData.competitionTimeRange = [detail.startTime, detail.endTime];
            formData.requirement = detail.requirement;
            formData.competitionItemIds = detail.competitionItemIds || [];
            
            openDialog.value = true;
        }
    } catch (error) {
        console.error('获取详情失败:', error);
        ElMessage.error('获取详情失败，请检查网络或联系管理员');
    }
};

// 查看详情按钮
const viewBtn = async (row: any) => {
    try {
        const res = await CompetitionControllerService.getDetailUsingGet(Number(row.id));
        if (res && res.data) {
            detailData.value = res.data;
            detailItems.value = res.data.items || [];
            detailVenues.value = res.data.venues || [];
            openDetailDialog.value = true;
        }
    } catch (error) {
        console.error('获取详情失败:', error);
        ElMessage.error('获取详情失败，请检查网络或联系管理员');
    }
};

// 删除按钮
const deleteBtn = (id: string) => {
    ElMessageBox.confirm('确定要删除此赛事吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            const res = await CompetitionControllerService.deleteCompetitionUsingDelete(Number(id));
            if (res && res.code === 0) {
                ElMessage.success('删除成功');
                // 刷新数据
                loadData();
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
                const params = { ...formData };

                // 统一格式化日期
                if (params.signUpDeadline) {
                    params.signUpDeadline = formatDateTime(params.signUpDeadline);
                }
                if (params.competitionTimeRange && params.competitionTimeRange.length === 2) {
                    params.startTime = formatDateTime(params.competitionTimeRange[0]);
                    params.endTime = formatDateTime(params.competitionTimeRange[1]);
                }

                delete params.competitionTimeRange;

                const res = params.id
                    ? await CompetitionControllerService.updateCompetitionUsingPut(params)
                    : await CompetitionControllerService.addCompetitionUsingPost(params);
                
                if (res && res.code === 0) {
                    // 先关闭对话框
                    openDialog.value = false;
                    ElMessage.success(params.id ? '更新成功' : '添加成功');
                    // 刷新数据
                    loadData();
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
};

// 当前页改变
const handleCurrentChange = (val: number) => {
    searchParm.page = val;
};
</script>

<style scoped>
</style> 