<template>
    <div class="header-container">
        <div class="left">
            <Collapse></Collapse>
            <BreadCrumb></BreadCrumb>
        </div>
    </div>
    <div class="right">
        <span style="color: #606266;">
            当前账号为 {{ userNumber }}
        </span>
    </div>
    <div class="login-out">
        <LoginOut></LoginOut>
    </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import Collapse from './Collapse.vue';
import BreadCrumb from './BreadCrumb.vue';
import LoginOut from './LoginOut.vue';
import { userStore } from '../../store/user';
import { computed } from 'vue';

const store = userStore();
const userNumber = computed(() => {
    return store.getUserNumber;
});

onMounted(() => {
    // 确保在组件挂载时获取 userNumber
    if (!store.getUserNumber) {
        // 如果 userNumber 为空，可以尝试重新获取用户信息
        store.getInfo().then(() => {
            console.log('User info fetched:', store.getUserNumber);
        }).catch((error) => {
            console.error('Failed to fetch user info:', error);
        });
    }
});
</script>

<style scoped lang="scss">
.header-container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    .left {
        display: flex; // 修正为 flex
        justify-content: flex-start;
        align-items: center;
    }
    .right {
        display: flex;
        align-items: center; // 修正为 center
        gap: 10px;
    }
    .login-out {
        display: flex;
        align-items: center;
    }
}
</style>