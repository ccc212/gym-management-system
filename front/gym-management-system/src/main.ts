import { createApp } from 'vue'
import router from './router/index'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'
/* import './style.css' */
import App from './App.vue'
import {createPinia} from 'pinia'

/* createApp(App).mount('#app') */
const app = createApp(App);
const pinia = createPinia();
app.use(router).use(ElementPlus).use(pinia).mount('#app')
for (const [key, component] of Object.entries(ElementPlusIconsVue)){
    app.component(key, component)
}