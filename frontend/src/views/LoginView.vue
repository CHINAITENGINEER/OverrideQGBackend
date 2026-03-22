<script setup>
import { ref, inject } from 'vue';
import { useRouter, useRoute, RouterLink } from 'vue-router';
import { fetchJson } from '@/api/http';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const route = useRoute();
const setFlash = inject('setFlash');
const auth = useAuthStore();

const username = ref('');
const password = ref('');
const loading = ref(false);

async function onSubmit() {
  loading.value = true;
  try {
    const data = await fetchJson('/api/auth/login', {
      method: 'POST',
      body: { account: username.value, password: password.value },
    });
    auth.setSession(data.user, data.role);
    setFlash('登录成功', true);
    const redir = route.query.redirect;
    if (typeof redir === 'string' && redir.startsWith('/')) {
      router.replace(redir);
    } else {
      router.replace(data.role === 'ADMIN' ? '/admin' : '/student');
    }
  } catch (e) {
    setFlash(e.body?.error || e.message, false);
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="card" style="max-width:420px;margin:0 auto">
    <h2>登录</h2>
    <form @submit.prevent="onSubmit">
      <div class="form-row">
        <label>账号</label>
        <input v-model="username" type="text" required autocomplete="username"/>
      </div>
      <div class="form-row">
        <label>密码</label>
        <input v-model="password" type="password" required autocomplete="current-password"/>
      </div>
      <button class="btn btn--primary" type="submit" style="width:100%" :disabled="loading">
        {{ loading ? '登录中…' : '登录' }}
      </button>
    </form>
    <p class="muted" style="margin-top:1rem">
      <RouterLink to="/register">没有账号？注册</RouterLink>
    </p>
  </div>
</template>
