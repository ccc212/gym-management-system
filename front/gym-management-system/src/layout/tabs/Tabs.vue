<template>
    <el-tabs
    v-model = "activeTab"
    type="card"
    class="demo-tabs"
    closable
    @tab-remove="removeTab"
    @tab-click="handleClick"
    >

    <el-tab-pane
    v-for="item in tabsList"
    :key="item.path"
    :label="item.title"
    :name="item.path"
    >
    </el-tab-pane>
    </el-tabs>
</template>

<script setup lang="ts">
import { ref,computed,watch,onMounted } from 'vue'
import { tabStore, type Tab } from '../../store/tabs/index.ts'
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router'
import type { TabsPaneContext } from 'element-plus'

//路由
const router = useRouter()

// 获取路由信息
const route = useRoute()

//获取store
const store = tabStore()

//选中的选项卡数据
const activeTab = ref('')

//选项卡数据
const tabsList = computed(() => {
  return store.getTab
})

//选项卡点击事件
const handleClick = (pane: TabsPaneContext) => {
  const {props} = pane
  //跳转到路由
  router.push({path:props.name as string})
}

//删除选项卡
const removeTab = (targetName: string) => {
  const tabs = tabsList.value
  let activeName = activeTab.value
  if (activeName === targetName) {
    tabs.forEach((tab, index) => {
      if (tab.path === targetName) {
        const nextTab = tabs[index + 1] || tabs[index - 1]
        if (nextTab) {
          activeName  = nextTab.path as string
        }
      }
    })
  }

  //重新设置激活的选项卡
  activeTab.value = activeName
  //重新设置选项卡数据
  store.tabList = tabs.filter((tab) => tab.path !== targetName)
  //删除跳转路由
  router.push({path:activeName})
}

const addTab = ()=>{
  const{path,meta} = route;
  const tab:Tab = {
    title:meta.title as string,
    path:path 
  }
  store.addTab(tab)
}

//添加选项卡:监听路由
watch(
  ()=>route.path,
  ()=>{
    setActiveTab()
    //添加选项卡数据
    addTab()
    // 存储到 localStorage
    /* localStorage.setItem('tabsList', JSON.stringify(store.tabList)) */
  }
)


//设置激活选项卡
const setActiveTab = () => {
  activeTab.value = route.path
}
//刷新
onMounted(() => {
  // 从 localStorage 恢复数据
  /* const storedTabs = localStorage.getItem('tabsList')
  if (storedTabs) {
    store.tabList = JSON.parse(storedTabs)
  } */
  setActiveTab()
  addTab()
})

</script>

<style scoped>

</style>