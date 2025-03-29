import {defineStore} from 'pinia'

export const testStore = defineStore('testStore', {
  state: () => {
    return {
      count: 0
    }
  },
 getters: {
   getCount(state){
     return state.count
   }
 },
 actions: {
   setCount(count:number){
     this.count = count
   }
 }
})