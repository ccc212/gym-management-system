import { defineStore } from 'pinia'
import { getInfoApi } from '../../api/system/user'
export const userStore = defineStore('userStore', {
  state: () => ({
    id: '',
    userNumber: '',
    token: '',
    codeList:[]
  }),
  getters: {
    getId: (state) => state.id,
    getUserNumber: (state) => state.userNumber,
    getToken: (state) => state.token,
    getCodeList: (state) => state.codeList,
  },
  actions: {
    setId(id: string) {
      this.id = id
    },
    setUserNumber(userNumber: string) {
      this.userNumber = userNumber
    },
    setToken(token: string) {
      this.token = token
    },
    getInfo(){
      return new Promise((resolve,reject)=>{
          getInfoApi(this.id).then((res)=>{
            if(res && res.code == 0){
                this.codeList = res.data.permissions
            }
            resolve(this.codeList)
          }).catch((error)=>{
            reject(error)
          })
      })
    },
  },
  persist: true, // 使用简单布尔值
})


