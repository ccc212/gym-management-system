import { defineStore } from 'pinia'

export const userStore = defineStore('userStore', {
  state: () => ({
    id: '',
    userNumber: '',
  }),
  getters: {
    getId: (state) => state.id,
    getUserNumber: (state) => state.userNumber,
  },
  actions: {
    setId(id: string) {
      this.id = id
    },
    setUserNumber(userNumber: string) {
      this.userNumber = userNumber
    }
  },
  persist: true, // 使用简单布尔值
})


