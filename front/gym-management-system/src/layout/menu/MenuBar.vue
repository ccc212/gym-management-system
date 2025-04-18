<template>
    <MenuLogo></MenuLogo>
    <el-menu
    :default-active="defaultActive"
    class="el-menu-vertical-demo"
    :collapse="isCollapse"
    @open="handleOpen"
    @close="handleClose"
    background-color="#304156"
    router
    unique-opened
  ><MenuItem :menuList ="menuList" ></MenuItem>
  </el-menu>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import {computed} from 'vue'
import MenuItem from './MenuItem.vue';
import {useRoute} from 'vue-router'
import MenuLogo from './MenuLogo.vue';
import { menuStore }  from '../../store/menu/index';


//获取store
const store = menuStore()
//获取状态
const isCollapse = computed(() => {
    return store.getCollapse
})



const route = useRoute()
//当前激活的菜单
const defaultActive = computed(() => {
    const {path} = route
    return path
})

//菜单数据
let menuList = reactive([
    {
        path: '/dashboard',
        component: '/dashboard/Index',
        name:"dashboard",
        meta:{
            title:"首页",
            icon:"House",
            roles:["sys:dashboard"]
        },
    },
    {
        path: '/system',
        component: 'layout',
        name:"system",
        meta:{
            title:"系统管理",
            icon:"Setting",
            roles:["sys:manage"]
        },
        children: [
        {
            path: "/departmentList",
            component: '/system/Department/DepartmentList',
            name:"departmentList",
            meta:{
                title:"部门管理",
                icon:"UserFilled",
                roles:["sys:department"]
            },
        },
        {
            path: "/sectionList",
            component: '/system/Section/SectionList',
            name:"sectionList",
            meta:{
                title:"班级管理",
                icon:"UserFilled",
                roles:["sys:section"]
            },
        },
        {
            path: '/userList',
            component: '/system/User/UserList',
            name:"userList",
            meta:{
                title:"用户管理",
                icon:"UserFilled",
                roles:["sys:user"]
            },
        },
        {
            path: '/roleList',
            component: '/system/Role/RoleList',
            name:"roleList",
            meta:{
                title:"角色管理",
                icon:"wallet",
                roles:["sys:role"]
            },
        },
        {
            path: '/menuList',
            component: '/system/Menu/MenuList',
            name:"menuList",
            meta:{
                title:"菜单管理",
                icon:"Menu",
                roles:["sys:menu"]
            },
        },
        
    ],
    },
    {
        path: '/venues',
        component: 'layout',
        name:"venues",
        meta:{
            title:"场地管理",
            icon:"Setting",
            roles:["sys:venues"]
        },
    },
    {
        path: '/euqipment',
        component: 'layout',
        name:"euqipment",
        meta:{
            title:"器材管理",
            icon:"Setting",
            roles:["sys:euqipment"]
        },
    },
    {
        path: '/competition',
        component: 'layout',
        name:"competition",
        meta:{
            title:"赛事管理",
            icon:"Setting",
            roles:["sys:competition"]
        },
    },
])
const handleOpen = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
</script>

<style scoped lang ="scss">
.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 230px;
  min-height: 400px;
}
.el-menu {
    border-right: none;
}

:deep(.el-sub-menu .el-sub-menu__title){
    color: #f4f4f5 !important;
}

:deep(.el-menu .el-menu-item){
    color:#bfcbd9;
}
/* 菜单点中文字样式 */
:deep(.el-menu-item.is-active){
    color: #409eff !important
}
/* 打开菜单的所有子菜单颜色 */
:deep(.is-opened .el-menu-item){
    background-color: #1f2d3d !important;
}
/* 鼠标移动菜单的颜色 */
:deep(.el-menu-item:hover){
    background-color: #001528 !important;
}
</style>