<template>
  <el-main>
    <!-- 搜索区域 -->
    <el-form :model="searchParm" :inline="true" size="default">
      <el-form-item>
        <el-input v-model="searchParm.name" placeholder="请输入赛事名称"></el-input>
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
        <el-button icon="Refresh" type="danger" @click="resetBtn">重置</el-button>
        <el-button icon="Plus" type="primary" @click="addBtn">新增</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格 -->
    <el-table :height="tableHeight" :data="tableList" border stripe>
      <el-table-column prop="name" label="赛事名称" width="150px"></el-table-column>
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
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="primary" icon="View" size="small" @click="viewBtn(scope.row)">查看详情</el-button>
          <el-button type="success" icon="Goods" size="small" @click="reserveEquipment(scope.row)">器材预约</el-button>
          <el-button type="info" icon="Location" size="small" @click="reserveVenue(scope.row)">场地预约</el-button>
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
          <el-input v-model="formData.name" placeholder="请输入赛事名称"/>
        </el-form-item>
        <el-form-item label="赛事描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入赛事描述"/>
        </el-form-item>
        <el-form-item label="举办方" prop="hoster">
          <el-input v-model="formData.hoster" placeholder="请输入举办方"/>
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
          <el-input-number v-model="formData.maxSignUpNum" :min="1" :max="1000" placeholder="请输入最大报名人数"/>
        </el-form-item>
        <template v-if="formData.isTeamCompetition === 1">
          <el-form-item label="每队人数上限" prop="teamMaxNum">
            <el-input-number v-model="formData.teamMaxNum" :min="1" :max="100" placeholder="请输入每队人数上限"/>
          </el-form-item>
          <el-form-item label="每队人数下限" prop="teamMinNum">
            <el-input-number v-model="formData.teamMinNum" :min="1" :max="100" placeholder="请输入每队人数下限"/>
          </el-form-item>
        </template>
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
          <el-input v-model="formData.requirement" type="textarea" :rows="2" placeholder="请输入参赛要求"/>
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
    <competition-details v-model="openDetailDialog" :competition="detailData"/>

    <!-- 器材预约对话框 -->
    <equipment-reservation
        v-model="openEquipmentDialog"
        :competition-id="currentCompetition?.id"
        :competition-name="currentCompetition?.name"
        @success="handleEquipmentReservationSuccess"
    />

    <!-- 场地预约对话框 -->
    <venue-reservation
        v-model="openVenueDialog"
        :competition-id="currentCompetition?.id"
        :competition-name="currentCompetition?.name"
        @success="handleVenueReservationSuccess"
    />
  </el-main>
</template>

<script setup lang="ts">
import {onMounted, reactive, ref, watch} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {CompetitionControllerService} from '../../../../generated/services/CompetitionControllerService';
import {CompetitionItemControllerService} from '../../../../generated/services/CompetitionItemControllerService';
import type {CompetitionDetailVO} from "../../../../generated";
import {CompetitionVO} from "../../../../generated";
import CompetitionDetails from "../../../components/competition/CompetitionDetails.vue";
import EquipmentReservation from "../../../components/competition/EquipmentReservation.vue";
import VenueReservation from "../../../components/competition/VenueReservation.vue";
import moment from 'moment';

// 表格高度
const tableHeight = ref(0);

// 赛事类型选项
const typeOptions = [
  {value: 0, label: '乒乓球赛'},
  {value: 1, label: '篮球赛'}
];

// 赛事类别选项
const categoryOptions = [
  {value: 0, label: '淘汰赛'},
  {value: 1, label: '循环赛'}
];

// 团体赛事选项
const teamCompetitionOptions = [
  {value: 0, label: '个人赛'},
  {value: 1, label: '团体赛'}
];

// 赛事项目选项
const competitionItemOptions = ref([]);

// 列表数据
const tableList = ref<CompetitionVO[]>([]);

// 搜索参数
const searchParm = reactive({
  page: 1,
  pageSize: 10,
  total: 0,
  name: '',
  status: undefined,
  timeRange: [],
  startTime: undefined,
  endTime: undefined
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
const detailData = ref<CompetitionDetailVO>({});

// 器材预约对话框相关
const openEquipmentDialog = ref(false);

// 场地预约对话框相关
const openVenueDialog = ref(false);

const currentCompetition = ref<CompetitionVO | null>(null);

// 表单校验规则
const rules = {
  name: [
    {required: true, message: '请输入赛事名称', trigger: 'blur'}
  ],
  hoster: [
    {required: true, message: '请输入举办方', trigger: 'blur'}
  ],
  type: [
    {required: true, message: '请选择赛事类型', trigger: 'change'}
  ],
  category: [
    {required: true, message: '请选择赛事类别', trigger: 'change'}
  ],
  isTeamCompetition: [
    {required: true, message: '请选择是否为团体赛事', trigger: 'change'}
  ],
  signUpDeadline: [
    {required: true, message: '请选择报名截止时间', trigger: 'change'}
  ],
  competitionTimeRange: [
    {required: true, message: '请选择比赛时间', trigger: 'change'}
  ]
};

// 获取列表数据
const loadData = async () => {
  try {
    // 处理时间范围
    const params = {...searchParm};
    if (params.timeRange && params.timeRange.length === 2) {
      // 添加时分秒信息
      params.startTime = params.timeRange[0] + ' 00:00:00';  // 开始时间添加零点
      params.endTime = params.timeRange[1] + ' 23:59:59';    // 结束时间添加23:59:59
    }

    delete params.timeRange;

    const res = await CompetitionControllerService.listCompetitionUsingGet(
        undefined, // category
        params.endTime, // endTime
        undefined, // hoster
        undefined, // isTeamCompetition
        params.name, // name
        params.page, // page
        params.pageSize, // pageSize
        params.startTime, // startTime
        undefined // type
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

// 根据条件获取赛事项目选项
const fetchCompetitionItems = async () => {
  try {
    // 只有当三个条件都选择了才进行查询
    if (formData.type !== null && formData.category !== null && formData.isTeamCompetition !== null) {
      const res = await CompetitionItemControllerService.listCompetitionItemUsingGet(
          formData.category, // category
          formData.isTeamCompetition, // isTeamCompetition
          '', // name
          1, // page
          100, // pageSize
          formData.type // type
      );
      if (res && res.data && res.data.records) {
        competitionItemOptions.value = res.data.records.map((item: any) => ({
          value: item.id,
          label: item.name
        }));

        // 如果当前选择的项目不在筛选结果中，则清空选择
        if (formData.competitionItemIds.length > 0) {
          const validItemIds = competitionItemOptions.value.map((item: any) => item.value);
          formData.competitionItemIds = formData.competitionItemIds.filter((id: any) =>
              validItemIds.includes(id)
          );
        }
      }
    }
  } catch (error) {
    console.error('获取赛事项目选项失败:', error);
    ElMessage.error('获取赛事项目选项失败，请检查网络或联系管理员');
  }
};


// 监听 searchParm 变量，改变时触发页面的重新加载
watch(() => searchParm, () => {
  loadData();
}, {deep: true});

// 监听表单中三个条件的变化
watch(() => formData.type, () => {
  fetchCompetitionItems();
});

watch(() => formData.category, () => {
  fetchCompetitionItems();
});

watch(() => formData.isTeamCompetition, () => {
  fetchCompetitionItems();
});

onMounted(() => {
  loadData();
  // 计算表格高度
  tableHeight.value = window.innerHeight - 320;
});

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

      // 设置基本信息
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

      // 设置赛事项目ID
      if (detail.itemRelations && detail.itemRelations.length > 0) {
        formData.competitionItemIds = detail.itemRelations.map(item => item.id);
      } else {
        formData.competitionItemIds = [];
      }

      openDialog.value = true;
      // 加载编辑模式下的赛事项目选项
      fetchCompetitionItems();
    }
  } catch (error) {
    console.error('获取详情失败:', error);
    ElMessage.error('获取详情失败，请检查网络或联系管理员');
  }
};

// 查看详情按钮
const viewBtn = async (row: any) => {
  try {
    const res = await CompetitionControllerService.getDetailUsingGet(row.id);
    if (res && res.data) {
      detailData.value = res.data;
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
  }).catch(() => {
  });
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;

  formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      const data = {...formData};

      // 确保id是数字类型
      if (data.id) {
        data.id = Number(data.id);
      }

      // 处理时间范围
      if (data.competitionTimeRange && data.competitionTimeRange.length === 2) {
        // 使用moment统一处理时间格式
        data.startTime = moment(data.competitionTimeRange[0]).format('YYYY-MM-DD HH:mm:ss');
        data.endTime = moment(data.competitionTimeRange[1]).format('YYYY-MM-DD HH:mm:ss');
      }
      delete data.competitionTimeRange;

      // 使用moment处理报名截止时间
      if (data.signUpDeadline) {
        data.signUpDeadline = moment(data.signUpDeadline).format('YYYY-MM-DD HH:mm:ss');
      }

      // 移除status字段，因为状态由系统根据时间自动计算
      delete data.status;

      // 如果不是团体赛，设置默认的团队人数值
      if (data.isTeamCompetition === 0) {
        data.teamMaxNum = 0;
        data.teamMinNum = 0;
      }

      try {
        if (dialogTitle.value.includes('新增')) {
          await CompetitionControllerService.addCompetitionUsingPost(data);
          ElMessage.success('新增成功');
        } else {
          await CompetitionControllerService.updateCompetitionUsingPut(data);
          ElMessage.success('编辑成功');
        }

        openDialog.value = false;
        loadData();
      } catch (error) {
        console.error('提交失败:', error);
        ElMessage.error('提交失败，请检查表单数据或网络连接');
      }
    } else {
      ElMessage.warning('请填写完整表单');
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

// 器材预约按钮
const reserveEquipment = (row: any) => {
  currentCompetition.value = row;
  openEquipmentDialog.value = true;
};

// 器材预约成功回调
const handleEquipmentReservationSuccess = () => {
  loadData();
};

// 场地预约按钮
const reserveVenue = (row: any) => {
  currentCompetition.value = row;
  openVenueDialog.value = true;
};

// 场地预约成功回调
const handleVenueReservationSuccess = () => {
  loadData();
};

</script>

<style scoped>
</style> 