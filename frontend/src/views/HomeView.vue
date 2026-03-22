<script setup>
import { RouterLink } from 'vue-router';
</script>

<template>
  <div class="card" style="max-width:520px">
    <h2>宿舍报修管理系统</h2>
    <p class="card__sub">Vue 3 + Vite 独立前端；开发时 <code>npm run dev</code>（5173），API 经代理转发到后端 8080。</p>
    <p>
      <RouterLink class="btn btn--primary" to="/login" style="text-decoration:none;margin-right:0.5rem">登录</RouterLink>
      <RouterLink class="btn btn--ghost" to="/register" style="text-decoration:none">注册</RouterLink>
    </p>
  </div>
</template>
