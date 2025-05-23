import { createRouter, createWebHistory } from "vue-router"
import type { RouteRecordRaw } from "vue-router"
import Layout from '../layout/index.vue'

//动态生成
export const constantRoutes:Array<RouteRecordRaw>=[
    {
        path:"/login",
        component: () => import('../views/login/login.vue'),
        name: 'login',
    },
    {
        path: '/',
        component: Layout,
        redirect: '/dashboard',
        children: [
           {
              path: '/dashboard',
              component: () => import('../views/dashboard/Index.vue'),
              name: 'dashboard',
              meta: {
                 title: '首页',
                 icon: 'HomeFilled',
              }
           },
           {
              path: '/venues',
              component: () => import('../views/admin/Venues.vue'),
              name: 'venues',
              meta: {
                 title: '场地管理',
                 icon: 'Location',
              }
           }
        ]
     },
    
]


//静态路由
/* const routes: Array<RouteRecordRaw> = [
   {
        path: '/login',
        component: () => import('../views/login/login.vue'),
        name: 'login',
   },
   {
      path: '/',
      component: Layout,
      redirect: '/dashboard',
      children: [
         {
            path: '/dashboard',
            component: () => import('../views/dashboard/Index.vue'),
            name: 'dashboard',
            meta: {
               title: '首页',
               icon: '#icondashboard'
            }
         }
      ]
   },
   {
    path: '/system',
    component: Layout,
    name: "system",
    meta: {
        title: '系统管理',
        icon: "Setting",
        roles: ["sys:manage"]
    },
    children: [
        {
            path: "/departmentList",
            component: () => import('../views/system/Department/DepartmentList.vue'),
            name:"departmentList",
            meta:{
                title:"部门管理",
                icon:"UserFilled",
                roles:["sys:department"]
            },
        },
        {
            path: "/sectionList",
            component: () => import('../views/system/Section/SectionList.vue'),
            name:"sectionList",
            meta:{
                title:"班级管理",
                icon:"UserFilled",
                roles:["sys:section"]
            },
        },
        {
            path: "/userList",
            component: () => import('../views/system/User/UserList.vue'),
            name:"userList",
            meta:{
                title:"用户管理",
                icon:"UserFilled",
                roles:["sys:user"]
            },
        },
        {
            path: '/roleList',
            component: () => import('../views/system/Role/RoleList.vue'),
            name:"roleList",
            meta:{
                title:"角色管理",
                icon:"wallet",
                roles:["sys:role"]
            },
        },
        {
            path: '/menuList',
            component: () => import('../views/system/Menu/MenuList.vue'),
            name:"menuList",
            meta:{
                title:"菜单管理",
                icon:"Menu",
                roles:["sys:menu"]
            },
        },
    ]
   },
   {
    path: '/venues',
    component: Layout,
    name:"venues",
    meta:{
        title:"场地管理",
        icon:"Setting",
        roles:["sys:venues"]
    },
},
{
    path: '/euqipment',
    component: Layout,
    name:"euqipment",
    meta:{
        title:"器材管理",
        icon:"Setting",
        roles:["sys:euqipment"]
    },
},
{
    path: '/competition',
    component: Layout,
    name:"competition",
    meta:{
        title:"赛事管理",
        icon:"Setting",
        roles:["sys:competition"]
    },
},
] */

const router = createRouter({
    history: createWebHistory(),
    routes: constantRoutes
    /* routes */
})

export default router