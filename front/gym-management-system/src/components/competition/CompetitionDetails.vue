<template>
  <el-dialog
    title="赛事详情"
    v-model="dialogVisible"
    width="800px"
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
      <el-descriptions-item label="报名截止时间">{{ competition.signUpDeadline }}</el-descriptions-item>
      <el-descriptions-item label="比赛时间">
        {{ competition.startTime }} 至 {{ competition.endTime }}
      </el-descriptions-item>
      <el-descriptions-item label="比赛地点" :span="2">{{ competition.venue }}</el-descriptions-item>
      <el-descriptions-item label="参赛要求" :span="2">
        <div v-html="competition.requirements"></div>
      </el-descriptions-item>
      <el-descriptions-item label="赛事描述" :span="2">
        <div v-html="competition.description"></div>
      </el-descriptions-item>
    </el-descriptions>

    <!-- 比赛项目列表 -->
    <div class="competition-items" v-if="competition.items && competition.items.length > 0">
      <h3>比赛项目</h3>
      <el-table :data="competition.items" border stripe>
        <el-table-column prop="name" label="项目名称"></el-table-column>
        <el-table-column prop="category" label="项目类别"></el-table-column>
        <el-table-column prop="maxParticipants" label="最大参与人数"></el-table-column>
        <el-table-column prop="currentParticipants" label="当前报名人数"></el-table-column>
      </el-table>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  competition: {
    type: Object,
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
.competition-items {
  margin-top: 20px;
}
.competition-items h3 {
  margin-bottom: 15px;
}
</style> 