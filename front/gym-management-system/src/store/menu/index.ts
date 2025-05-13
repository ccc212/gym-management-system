import {defineStore} from 'pinia'
import {getMenuListApi} from '../../api/system/menu/index'
import type {RouteRecordRaw} from 'vue-router'
import Layout from '../../layout/index.vue'
import Center from '../../layout/center/center.vue'
const modules = import.meta.glob('../../views/**/*.vue')
export const menuStore = defineStore('menuStore', {
  state: () => ({
    collapse:false,
    //菜单数据
    menuList:[
      {
          path: '/dashboard',
          component: Layout,
          name: 'dashboard',
          meta: {
            title: '首页',
            icon: 'HomeFilled',
            roles:['sys:dashboard']
          },
      },
    ],
    baseMenu: [
      {
          path: '/dashboard',
          component: Layout,
          name: 'dashboard',
          meta: {
            title: '首页',
            icon: 'HomeFilled',
            roles:['sys:dashboard']
          },
      },
    ]
  }),
  getters: {
    getCollapse(state){return state.collapse},
    getMenu(state){return state.menuList},
  }, 
  actions: {
    setCollapse(collapse:boolean){this.collapse = collapse;},
    //获取菜单数据
    getMenuList(router:any,id:string){
        return new Promise(async(resolve,reject)=>{
            getMenuListApi(id).then((res:any)=>{
              let accessRoute;
              this.menuList = this.baseMenu
              if(res && res.code == 0){
                accessRoute = generateRoute(res.data,router) as any
                this.menuList = this.menuList.concat(accessRoute)
              }
              localStorage.setItem('menuList',JSON.stringify(this.menuList))
              resolve(accessRoute)
            }).catch((error)=>{
              reject(error)
            })
        })
    },
  },
  persist: true, // 使用简单布尔值
})


// 动态生成路由
export function generateRoute(routes: RouteRecordRaw[], router: any) {
  // 路由数据
  const res:Array<RouteRecordRaw> = [];
  routes.forEach((route: any) => {
    const tmp = { ...route };
    const component = tmp.component;
    // 生成路由的component
    if (route.component) {
      if (component == 'Layout') {
        tmp.component = Layout;
      } else {
        tmp.component = modules[`../../${component}`];
      }
       
    }
    if (tmp.children && tmp.children.length > 0) {
      if(route.component != 'Layout'){
        tmp.component = Center;
      }
      tmp.children = generateRoute(tmp.children, router);
    }
    res.push(tmp);
    // 加入路由
    router.addRoute(tmp);
  });
  return res;
}



