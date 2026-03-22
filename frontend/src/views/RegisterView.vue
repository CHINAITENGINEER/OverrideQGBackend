<script setup>
import { ref, inject } from 'vue';
import { useRouter, RouterLink } from 'vue-router';
import { fetchJson } from '@/api/http';

const router = useRouter();
const setFlash = inject('setFlash');

const role = ref(0);
const account = ref('');
const password = ref('');
const confirmPassword = ref('');
const loading = ref(false);

async function onSubmit() {
  if (password.value !== confirmPassword.value) {
    setFlash('两次密码不一致', false);
    return;
  }
  loading.value = true;
  try {
    await fetchJson('/api/users/register', {
      method: 'POST',
      body: {
        account: account.value,
        password: password.value,
        role: role.value,
      },
    });
    setFlash('注册成功，请登录', true);
    router.push('/login');
  } catch (e) {
    setFlash(e.body?.error || e.message, false);
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="card" style="max-width:420px;margin:0 auto">
    <h2>注册</h2>
    <form @submit.prevent="onSubmit">
      <div class="form-row">
        <label>角色</label>
        <select v-model.number="role">
          <option :value="0">学生</option>
          <option :value="1">管理员</option>
        </select>
      </div>
      <div class="form-row">
        <label>账号</label>
        <input v-model="account" required/>
      </div>
      <div class="form-row">
        <label>密码</label>
        <input v-model="password" type="password" required/>
      </div>
      <div class="form-row">
        <label>确认密码</label>
        <input v-model="confirmPassword" type="password" required/>
      </div>
      <button class="btn btn--primary" type="submit" style="width:100%" :disabled="loading">
        {{ loading ? '提交中…' : '注册' }}
      </button>
    </form>
    <p class="muted" style="margin-top:1rem">
      <RouterLink to="/login">返回登录</RouterLink>
    </p>
  </div>
</template>
