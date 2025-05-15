<template>
  <el-dialog
    title="赛事详情"
    v-model="dialogVisible"
    width="800px"
    append-to-body
    :before-close="handleClose"
  >
    <el-descriptions :column="2" border>
      <el-descriptions-item label="赛事名称">{{ competition.name }}</el-descriptions-item>
      <el-descriptions-item label="赛事类型">
        <el-tag v-if="competition.type === 0" type="success">乒乓球赛</el-tag>
        <el-tag v-else-if="competition.type === 1" type="primary">篮球赛</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="赛事形式">
        <el-tag v-if="competition.isTeamCompetition === 0">个人赛</el-tag>
        <el-tag v-else-if="competition.isTeamCompetition === 1" type="warning">团队赛</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="主办方">{{ competition.hoster }}</el-descriptions-item>
      <el-descriptions-item label="赛事状态">
        <el-tag v-if="competition.status === 0" type="info">未开始</el-tag>
        <el-tag v-else-if="competition.status === 1" type="success">报名中</el-tag>
        <el-tag v-else-if="competition.status === 2" type="warning">进行中</el-tag>
        <el-tag v-else-if="competition.status === 3" type="danger">已结束</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="报名截止时间">{{ competition.signUpDeadline }}</el-descriptions-item>
      <el-descriptions-item label="比赛时间">
        {{ competition.startTime }} 至 {{ competition.endTime }}
      </el-descriptions-item>
      <el-descriptions-item label="报名人数">
        {{ competition.signUpNum || 0 }}/{{ competition.maxSignUpNum || 0 }}
      </el-descriptions-item>
      <el-descriptions-item v-if="competition.isTeamCompetition === 1" label="每队人数">
        最少 {{ competition.teamMinNum }} 人 / 最多 {{ competition.teamMaxNum }} 人
      </el-descriptions-item>
      <el-descriptions-item label="参赛要求" :span="2">{{ competition.requirement }}</el-descriptions-item>
      <el-descriptions-item label="赛事描述" :span="2">{{ competition.description }}</el-descriptions-item>
    </el-descriptions>

    <!-- 赛事项目 - 使用itemRelations字段 -->
    <el-divider v-if="competition.itemRelations && competition.itemRelations.length > 0">赛事项目</el-divider>
    <el-table v-if="competition.itemRelations && competition.itemRelations.length > 0" :data="competition.itemRelations" border>
      <el-table-column prop="name" label="项目名称" width="150px"></el-table-column>
      <el-table-column prop="type" label="项目类型" width="120px">
        <template #default="scope">
          <el-tag v-if="scope.row.type === 0" type="success">乒乓球项目</el-tag>
          <el-tag v-else-if="scope.row.type === 1" type="primary">篮球项目</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="category" label="比赛类别" width="120px">
        <template #default="scope">
          <el-tag v-if="scope.row.category === 0" type="info">淘汰赛</el-tag>
          <el-tag v-else-if="scope.row.category === 1" type="warning">循环赛</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isTeamCompetition" label="比赛形式" width="120px">
        <template #default="scope">
          <el-tag v-if="scope.row.isTeamCompetition === 0">个人赛</el-tag>
          <el-tag v-else-if="scope.row.isTeamCompetition === 1" type="warning">团队赛</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <!-- 场地信息 - 使用venueRelations字段 -->
    <el-divider v-if="competition.venueRelations && competition.venueRelations.length > 0">场地信息</el-divider>
    <el-table v-if="competition.venueRelations && competition.venueRelations.length > 0" :data="competition.venueRelations" border>
      <el-table-column prop="venueName" label="场地名称" width="150px"></el-table-column>
      <el-table-column prop="num" label="数量" width="80px"></el-table-column>
<!--      <el-table-column prop="responsibleName" label="负责人" width="120px"></el-table-column>-->
<!--      <el-table-column prop="phone" label="联系电话" width="120px"></el-table-column>-->
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

    <!-- 器材信息 - 使用equipmentRelations字段 -->
    <el-divider v-if="competition.equipmentRelations && competition.equipmentRelations.length > 0">器材信息</el-divider>
    <el-table v-if="competition.equipmentRelations && competition.equipmentRelations.length > 0" :data="competition.equipmentRelations" border>
      <el-table-column prop="equipmentName" label="器材名称" width="150px"></el-table-column>
      <el-table-column prop="num" label="数量" width="80px"></el-table-column>
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

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import type { CompetitionDetailVO } from '../../../../generated';

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  competition: {
    type: Object as () => CompetitionDetailVO,
    required: true
  }
});

const emit = defineEmits(['update:modelValue']);

const dialogVisible = ref(props.modelValue);

watch(() => props.modelValue, (val) => {
  dialogVisible.value = val;
});

watch(dialogVisible, (val) => {
  emit('update:modelValue', val);
});

const handleClose = () => {
  dialogVisible.value = false;
};
</script>

<style scoped>
.el-divider {
  margin: 20px 0;
}
</style> 