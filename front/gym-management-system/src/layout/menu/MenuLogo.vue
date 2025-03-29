<template>
    <div class="logo">
        <img :src="MenuLogo" style="width: 70px; height: auto;"/>
        <span class = "logo-title">{{ title }}</span>
    </div>
</template>

<script setup lang="ts">
import MenuLogo from "../../assets/MenuLogo.jpg"
import { ref } from 'vue'
const title = ref('海大体育馆管理系统')
</script>

<style scoped lang = "scss">
.logo{
    display: flex;
    width: 100%;
    height: 60px;
    background-color: #2b2f3a;
    text-align: center;
    cursor: pointer;
    align-items: center;
    
}
.logo-title{
    color: #FFF;
    font-weight: 800;
    line-height: 60px;
    font-size: 16px;
    font-family: FangSong;
}
</style>