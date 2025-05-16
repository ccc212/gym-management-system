<template>
  <el-main>
    <el-row :gutter="20" justify="center" class="equipment-cards">
      <el-col :span="24" v-for="(item, key) in dataMap" :key="key">
        <el-card class="equipment-detail-card" shadow="hover">
          <div class="content">
            <img :src="item.imgSrc" alt="equipment image" class="equipment-image" />
            <div class="description">
              <h2>{{ item.title }}</h2>
              <p v-for="(para, index) in item.description" :key="index">{{ para }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </el-main>
</template>

<script setup lang="ts">
const dataMap = {
  pingpong: {
    title: '乒乓球 (Table Tennis)',
    imgSrc: 'https://img.takungpao.com/2021/0216/20210216042447874.jpg',
    description: [
      '乒乓球起源于19世纪末的英国，是一项快速反应的室内球类运动。',
      '比赛通常为单打或双打，选手使用小巧轻便的球拍击打轻盈的乒乓球，目标是使球落在对方台面上并令其无法有效回击。',
      '乒乓球因其速度快、技巧丰富，被誉为“国球”，深受全球各年龄段爱好者喜爱。'
    ]
  },
  basketball: {
    title: '篮球 (Basketball)',
    imgSrc: 'https://upload.wikimedia.org/wikipedia/commons/7/7a/Basketball.png',
    description: [
      '篮球由詹姆斯·奈史密斯于1891年在美国发明，是一项集速度、力量和技巧于一体的球类运动。',
      '比赛在长方形场地进行，两个队伍争夺篮球并尝试投篮得分，比赛节奏紧凑、观赏性强。',
      '篮球运动不仅是世界性的竞技项目，也促进了团队协作与身体素质的全面发展。'
    ]
  },
  badminton: {
    title: '羽毛球 (Badminton)',
    imgSrc: 'https://pic35.photophoto.cn/20150628/0037037416760727_b.jpg',
    description: [
      '简称羽球 ，是一项隔着球网使用长柄球拍击打羽毛球的运动。',
      '羽毛球运动对爆发力、反应速度及手眼协调能力有较高要求。',
      '羽毛球于1992年成为奥运会正式比赛项目，深受大众喜爱。'
    ]
  }
}
</script>

<style scoped>
.equipment-cards {
  padding: 20px;
}
.equipment-detail-card {
  max-width: 1000px;
  margin: 20px auto;
  padding: 20px;
  font-family: 'Helvetica Neue', Arial, sans-serif;
}
.content {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}
.equipment-image {
  width: 300px;
  height: 200px;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}
.description {
  flex: 1;
  color: #333;
}
.description h2 {
  margin-bottom: 16px;
  font-weight: 700;
  font-size: 28px;
  color: #409EFF;
}
.description p {
  margin-bottom: 12px;
  font-size: 16px;
  line-height: 1.5;
}
</style>