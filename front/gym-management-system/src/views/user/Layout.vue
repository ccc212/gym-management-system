<template>
  <div class="user-layout">
    <el-container>
      <el-aside width="200px">
        <div class="logo">
          <h2>体育场馆管理系统</h2>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical"
          :router="true"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF">
          <el-menu-item index="/user/venues">
            <el-icon><Location /></el-icon>
            <span>场地查看</span>
          </el-menu-item>
          <el-menu-item index="/user/reservations">
            <el-icon><Calendar /></el-icon>
            <span>我的预约</span>
          </el-menu-item>
          <el-menu-item index="/user/schedule">
            <el-icon><Timer /></el-icon>
            <span>场地排期</span>
          </el-menu-item>
          <el-menu-item index="/user/usage">
            <el-icon><Document /></el-icon>
            <span>使用记录</span>
          </el-menu-item>
          <el-menu-item index="/user/prices">
            <el-icon><Money /></el-icon>
            <span>价格查询</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container>
        <el-header>
          <div class="header-left">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/user/venues' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentRoute }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                {{ currentUser.username }}
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  Location,
  Calendar,
  Timer,
  Document,
  Money,
  ArrowDown
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 当前用户信息
const currentUser = ref(JSON.parse(localStorage.getItem('currentUser') || '{}'))

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 当前路由名称
const currentRoute = computed(() => {
  const routeMap: Record<string, string> = {
    '/user/venues': '场地查看',
    '/user/reservations': '我的预约',
    '/user/schedule': '场地排期',
    '/user/usage': '使用记录',
    '/user/prices': '价格查询'
  }
  return routeMap[route.path] || ''
})

// 处理下拉菜单命令
const handleCommand = (command: string) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      localStorage.removeItem('token')
      localStorage.removeItem('currentUser')
      router.push('/login')
    })
  } else if (command === 'profile') {
    // TODO: 实现个人信息页面
    console.log('查看个人信息')
  }
}
</script>

<style scoped>
.user-layout {
  height: 100vh;
}

.el-container {
  height: 100%;
}

.el-aside {
  background-color: #304156;
  color: #fff;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3649;
}

.logo h2 {
  color: #fff;
  margin: 0;
  font-size: 18px;
}

.el-menu {
  border-right: none;
}

.el-header {
  background-color: #fff;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e6e6e6;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #333;
}

.user-info .el-icon {
  margin-left: 5px;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style> 