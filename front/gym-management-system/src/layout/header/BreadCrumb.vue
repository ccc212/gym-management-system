<template>
    <el-breadcrumb class = "bred" separator="/">
    <el-breadcrumb-item v-for="item in tabs">{{ item.meta.title }}</el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup lang="ts">
import {useRoute } from 'vue-router';
import type {RouteLocationMatched} from 'vue-router';
import {ref,watch } from 'vue';
import type {Ref} from 'vue';

const route = useRoute()
//导航
const tabs : Ref<RouteLocationMatched[]> = ref([])
//获取数据
const getBredcrumb = () => {
  let mached = route.matched.filter((item) => item.meta && item.meta.title)
  //获取第一个数据
  const first = mached[0]
  //判断首页，不是则要构造
  if(first.path != '/dashboard'){
    mached = [{path:'/dashboard',meta:{title:'首页'}} as any].concat(mached)
  }
  tabs.value = mached;
}
getBredcrumb()
watch(
  ()=>route.path,
  ()=>getBredcrumb()
)
</script>

<style scoped lang = "scss">
//字体颜色以及大小
.bred{
    margin-left: 20px;

}
:deep(.el-breadcrumb__inner){
    color: #000000 !important;
}
:deep(.el-breadcrumb__inner a){
    color: #000000 !important;
}
:deep(.el-breadcrumb__inner){
  font-size: 16px !important;
}
</style>