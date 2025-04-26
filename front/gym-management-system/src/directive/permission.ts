import type { Directive } from "vue";
import { userStore } from "../store/user/index";
//自定义按钮权限指令
export const permission:Directive ={
    mounted(el,binding){
        const store = userStore()
        //按钮上的权限字段
        const {value} = binding;
        //当前用户的所有权限字段
        const permissions = store.getCodeList
        if(value && value instanceof Array && value.length > 0){
            const permissionRoles = value;
            //判断按钮权限是否存在用户的权限里面
            const hasPermission = permissions.some(role => {
                return permissionRoles.includes(role)
            })
            //如果没有按钮权限，隐藏按钮
            if(!hasPermission){
                el.style.display = 'none'
            }
        }else{
            throw new Error(`按钮的传递方式为v-permission="[sys:role:add"`)
        }
    }
    
}