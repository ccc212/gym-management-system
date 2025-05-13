<template>
    <el-container class="mycontainer">
        <el-aside width = "auto" class="leftmenu">
            <MenuBar>
            </MenuBar>
        </el-aside>
        <el-container>
            <el-header class="header">
                <Header></Header>
            </el-header>
            <el-main class="main">
                <Tabs></Tabs>
                <router-view></router-view>
            </el-main>
        </el-container>
    </el-container>
</template>

<script setup lang="ts">
import Header from './header/Header.vue';
import MenuBar from './menu/MenuBar.vue';
import Tabs from './tabs/Tabs.vue';
/* import MenuItem from './menu/MenuItem.vue'; */
</script>

<style scoped>
.mycontainer{
    height: 100%;
.leftmenu{
    background-color: #304156;
}
.header {
    background-color: #fff;
    display: flex;
    align-items: center;
    border: 1px solid black; 
}
.main{
    /* background-color: blue; */
}

}
</style>