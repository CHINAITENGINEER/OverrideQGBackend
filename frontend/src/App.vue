<script setup>
import { reactive, provide } from 'vue';
import { RouterLink, RouterView, useRouter } from 'vue-router';
import { useAuthStore } from './stores/auth';

const router = useRouter();
const auth = useAuthStore();

const flash = reactive({ text: '', ok: true });
function setFlash(text, ok = true) {
  flash.text = text || '';
  flash.ok = ok;
  if (text) {
    setTimeout(() => {
      flash.text = '';
    }, 4500);
  }
}
provide('setFlash', setFlash);

async function onLogout() {
  await auth.logout();
  setFlash('已退出', true);
  router.push('/login');
}

function navTitle(routeName) {
  if (routeName === 'student') return '学生工作台';
  if (routeName === 'admin' || routeName === 'admin-order') return '管理台';
  return '宿舍报修';
}
</script>

<template>
  <nav class="app-nav">
    <RouterLink class="app-nav__brand" to="/">
      <span class="app-nav__logo">🏠</span>
      <span>{{ navTitle($route.name) }}</span>
    </RouterLink>
    <div v-if="auth.state.user" style="display:flex;align-items:center;gap:0.75rem">
      <span class="muted">{{ auth.state.user.account }}</span>
      <button type="button" class="btn btn--ghost" @click="onLogout">退出</button>
    </div>
  </nav>

  <div class="wrap">
    <div
      v-if="flash.text"
      class="alert"
      :class="flash.ok ? 'alert--ok' : 'alert--err'"
    >
      {{ flash.text }}
    </div>
    <RouterView />
  </div>
</template>
