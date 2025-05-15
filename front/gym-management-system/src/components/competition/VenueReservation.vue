<template>
  <el-dialog
      title="场地预约"
      v-model="dialogVisible"
      width="600px"
      append-to-body
      :before-close="handleClose"
      v-loading="loading"
  >
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
      <el-form-item label="赛事名称">
        <el-input :value="competitionName" disabled></el-input>
      </el-form-item>

      <el-form-item label="选择场地" prop="venueId">
        <el-select
            v-model="formData.venueId"
            placeholder="请选择场地"
            filterable
            @change="handleVenueChange"
        >
          <el-option
              v-for="item in venueOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          >
            <span>{{ item.label }}</span>
            <span style="float: right; color: #8492a6; font-size: 13px">
              剩余: {{ item.remainingQuantity }}
            </span>
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="预约数量" prop="num">
        <el-input-number
            v-model="formData.num"
            :min="1"
            :max="selectedVenue ? selectedVenue.remainingQuantity : 999"
            placeholder="请输入数量"
        ></el-input-number>
      </el-form-item>

      <el-form-item label="预约时间" prop="timeRange">
        <el-date-picker
            v-model="formData.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD HH:mm:ss"
        ></el-date-picker>
      </el-form-item>
    </el-form>

    <!-- 已预约的场地 -->
    <div v-if="reservedVenues.length > 0" class="reserved-venue-section">
      <el-divider content-position="left">已预约场地</el-divider>
      <el-table :data="reservedVenues" border stripe size="small">
        <el-table-column prop="venueName" label="场地名称" width="150"></el-table-column>
        <el-table-column prop="num" label="数量" width="80"></el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="150"></el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="150"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="info">预约中</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success">预约成功</el-tag>
            <el-tag v-else-if="scope.row.status === 2" type="danger">预约失败</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="scope">
            <el-button
                type="danger"
                size="small"
                @click="cancelReservation(scope.row)"
                :disabled="scope.row.status !== 0"
            >取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button type="primary" @click="submitForm">提交预约</el-button>
        <el-button @click="handleClose">关闭</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import {onMounted, reactive, ref, watch} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {
  CompetitionVenueRelationControllerService
} from '../../../generated/services/CompetitionVenueRelationControllerService';
import type {Venue} from '../../../generated/models/VenueEntity';
import {VenueControllerService} from "../../../generated";

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  competitionId: {
    type: Number,
    required: true
  },
  competitionName: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['update:modelValue', 'success']);

const dialogVisible = ref(props.modelValue);
const loading = ref(false);
const formRef = ref();

const venueOptions = ref<{ value: number, label: string, remainingQuantity: number }[]>([]);
const selectedVenue = ref<Venue | null>(null);
const reservedVenues = ref<any[]>([]);

// 表单数据
const formData = reactive({
  venueId: undefined as number | undefined,
  num: 1,
  timeRange: [] as string[],
});

// 表单验证规则
const rules = {
  venueId: [{required: true, message: '请选择场地', trigger: 'change'}],
  num: [{required: true, message: '请输入数量', trigger: 'blur'}],
  timeRange: [{required: true, message: '请选择预约时间', trigger: 'change'}]
};

// 监听对话框显示状态
watch(() => props.modelValue, (val) => {
  dialogVisible.value = val;
  if (val) {
    // 加载场地列表和已预约场地
    loadVenues();
    loadReservedVenues();
  }
});

// 监听对话框内部状态变化
watch(dialogVisible, (val) => {
  emit('update:modelValue', val);
});

// 加载场地列表
const loadVenues = async () => {
  try {
    loading.value = true;
    const res = await VenueControllerService.getAllVenuesUsingGet(
        1,
        100,
        undefined
    );

    if (res.data?.records) {
      venueOptions.value = res.data.records.map((item: Venue) => ({
        value: item.id!,
        label: item.name || '',
        remainingQuantity: item.capacity || 0
      }));
    }
  } catch (error) {
    console.error('获取场地列表失败:', error);
    ElMessage.error('获取场地列表失败');
  } finally {
    loading.value = false;
  }
};

// 加载已预约场地
const loadReservedVenues = async () => {
  try {
    loading.value = true;
    const res = await CompetitionVenueRelationControllerService.getCompetitionVenueRelationUsingGet(
        props.competitionId
    );

    if (res && res.data) {
      reservedVenues.value = res.data;
    }
  } catch (error) {
    console.error('获取已预约场地失败:', error);
    ElMessage.error('获取已预约场地失败');
  } finally {
    loading.value = false;
  }
};

// 处理场地选择变更
const handleVenueChange = (value: number) => {
  if (!value) {
    selectedVenue.value = null;
    return;
  }

  const option = venueOptions.value.find(item => item.value === value);
  if (option) {
    // 查找场地详情
    const venue = {
      id: option.value,
      venueName: option.label,
      remainingQuantity: option.remainingQuantity
    } as Venue;
    selectedVenue.value = venue;

    // 限制预约数量不超过库存
    if (formData.num > option.remainingQuantity) {
      formData.num = option.remainingQuantity;
    }
  }
};

// 取消预约
const cancelReservation = (row: any) => {
  ElMessageBox.confirm('确定要取消此预约吗？', '取消预约', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      loading.value = true;
      const res = await CompetitionVenueRelationControllerService.deleteCompetitionVenueRelationUsingDelete(row.id);
      if (res.code === 0) {
        ElMessage.success('取消预约成功');
        loadReservedVenues(); // 重新加载列表
      } else {
        ElMessage.error(res.msg || '取消预约失败');
      }
    } catch (error) {
      console.error('取消预约失败:', error);
      ElMessage.error('取消预约失败');
    } finally {
      loading.value = false;
    }
  }).catch(() => {
  });
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;

  formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      if (!formData.timeRange || formData.timeRange.length !== 2) {
        ElMessage.error('请选择预约时间范围');
        return;
      }

      try {
        loading.value = true;

        const params = {
          competitionId: props.competitionId,
          venueId: formData.venueId,
          num: formData.num,
          startTime: formData.timeRange[0],
          endTime: formData.timeRange[1],
          status: 0 // 预约中
        };

        const res = await CompetitionVenueRelationControllerService.addCompetitionVenueRelationUsingPost(params);

        if (res.code === 0) {
          ElMessage.success('预约提交成功，等待管理员审核');
          // 重置表单
          formData.venueId = undefined;
          formData.num = 1;
          formData.timeRange = [];
          selectedVenue.value = null;

          // 重新加载列表
          loadReservedVenues();

          // 通知父组件更新
          emit('success');
        } else {
          ElMessage.error(res.msg || '预约提交失败');
        }
      } catch (error) {
        console.error('预约提交失败:', error);
        ElMessage.error('预约提交失败');
      } finally {
        loading.value = false;
      }
    } else {
      ElMessage.error('请完善表单信息');
    }
  });
};

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false;
};

// 组件挂载时
onMounted(() => {
  if (dialogVisible.value) {
    loadVenues();
    loadReservedVenues();
  }
});
</script>

<style scoped>
.reserved-venue-section {
  margin-top: 20px;
}

.el-divider {
  margin: 15px 0;
}
</style>