<template>
    <div class="logo">
      <img :src="MenuLogo" style="width: 70px; height: 60px;" />
      <span v-if="show" class="logo-title">{{ title }}</span>
    </div>
  </template>
  
  <script setup lang="ts">
  import MenuLogo from "../../assets/MenuLogo.jpg";
  import { ref, watch } from "vue";
  import { menuStore } from "../../store/menu/index";
  
  // 获取store
  const store = menuStore();
  const title = ref("海大体育馆管理系统");
  const show = ref(true);
  
  // 监听 store.getCollapse 状态变化
  watch(
    () => store.getCollapse,
    (collapsed: boolean) => {
      if(!collapsed){
        setTimeout(() => {
          show.value = !collapsed;
        }, 300);
      }else{
        show.value = !collapsed;
      }
    }
  );
  </script>
  
  <style scoped lang="scss">
  .logo {
    display: flex;
    width: 100%;
    height: 60px;
    background-color: #2b2f3a;
    text-align: center;
    cursor: pointer;
    align-items: center;
  
    img {
      width: 50px;
      height: 50px;
    }
  
    .logo-title {
      color: #fff;
      font-weight: 800;
      line-height: 60px;
      font-size: 16px;
      font-family: FangSong;
    }
  }
  
  </style>