
<template>
  <template v-for="menu in menuList" : key="menu.id">
    <!-- 有下级菜单 -->
    <el-sub-menu  v-if="menu.children && menu.children.length > 0" :index="menu.path">
        <template #title>
            <el-icon>
                <!-- 图标 -->
                <component :is="menu.meta.icon"></component>
            </el-icon>
            <!-- 标题 -->
            <span>{{ menu.meta.title }}</span>
        </template>
      <!-- 生成下级 递归调用 -->
       <MenuItem :menuList="menu.children"> </MenuItem>
      </el-sub-menu>
      <!-- 无下级菜单 -->
      <el-menu-item style="color: #f4f4f5" v-else :index="menu.path">
        <el-icon>
                <!-- 图标 -->
                <component :is="menu.meta.icon"></component>
            </el-icon>
            <!-- 标题 -->
      <template #title>{{ menu.meta.title }}</template>
    </el-menu-item>
  </template>
</template>


<script setup lang="ts">
//接收bar组件的menuList数据
defineProps(['menuList'])
</script>

<style scoped>

</style>