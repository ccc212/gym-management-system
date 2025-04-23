import { createApp } from 'vue'
import router from './router/index'
import zhCn from 'element-plus/es/locale/lang/zh-cn.mjs'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import myconfirm from './utils/myconfirm'
import App from './App.vue'
import { createPinia } from 'pinia'
import './permission'
//按钮权限
import {permission} from './directive/permission'

const app = createApp(App)
const pinia = createPinia()

pinia.use(piniaPluginPersistedstate)

app.directive('permission',permission)

app.use(router)
  .use(ElementPlus, { locale: zhCn })
  .use(pinia)
  .mount('#app')


for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.config.globalProperties.$myconfirm = myconfirm