import router from './router/index';
import {userStore} from './store/user/index';
import {menuStore} from './store/menu/index';
const whiteList = ['/login'] // 不需要权限的白名单路由

let isDynamicRouteAdded = false

router.beforeEach(async (to:any, from:any, next:any) => {
  const uStore = userStore()
  const mStore = menuStore()
  const token = uStore.token
    
  if (token) {
    if (to.path === '/login') {
      next({ path: '/' })
    } else {
      if (!isDynamicRouteAdded) {
        try {
          await mStore.getMenuList(router, uStore.getId)
          isDynamicRouteAdded = true
          next({ ...to, replace: true }) // 确保 addRoute 生效
        } catch (err) {
          console.error('获取菜单失败', err)

          next('/login')
        }
      } else {
        next()
      }
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next('/login')
    }
  }
})

