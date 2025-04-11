import {defineStore} from 'pinia'

//定义选项卡数据
export type Tab = {
    title:String;
    path:String;
    
}

//state
export type TabState ={
    tabList:Tab[]
}

//定义store
export const tabStore = defineStore('tabStore', {
    state:():TabState=>{
        return {
            tabList:[]
        }
    },
    getters:{
        getTab(state){
            return state.tabList
        }
    },
    actions:{
        //选项卡添加数据
        addTab(tab:Tab){
            //判断重复
            if(this.tabList.some(item=>item.path===tab.path)) return;
            this.tabList.push(tab)
        }

    }
}) 