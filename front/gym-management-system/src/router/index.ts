import { createRouter, createWebHistory } from "vue-router"
import type { RouteRecordRaw } from "vue-router"
import Layout from '../layout/index.vue'

const routes: Array<RouteRecordRaw> = [
    /* {
        path: '/home',
        name: 'Home',
        component: Layout
    } */
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
            path: "/studentList",
            component: () => import('../views/system/Student/StudentList.vue'),
            name:"studentList",
            meta:{
                title:"学生管理",
                icon:"UserFilled",
                roles:["sys:student"]
            },
        },
        {
            path: '/staffList',
            component: () => import('../views/system/Staff/StaffList.vue'),
            name:"staffList",
            meta:{
                title:"教职管理",
                icon:"UserFilled",
                roles:["sys:staff"]
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
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router