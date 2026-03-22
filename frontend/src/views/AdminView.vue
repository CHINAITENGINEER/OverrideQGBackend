<script setup>
import { ref, computed, onMounted, inject, watch } from 'vue';
import { RouterLink } from 'vue-router';
import { fetchJson } from '@/api/http';
import { useAuthStore } from '@/stores/auth';
import StatusBadge from '@/components/StatusBadge.vue';

const setFlash = inject('setFlash');
const auth = useAuthStore();

const statusFilter = ref(null);
const orders = ref([]);
const counts = ref([0, 0, 0, 0]);
const loading = ref(true);

const pwdOld = ref('');
const pwdNew = ref('');
const pwdConfirm = ref('');

const query = computed(() =>
  statusFilter.value != null ? `?status=${statusFilter.value}` : '',
);

const labels = ['待处理', '处理中', '已完成', '已取消'];

async function load() {
  loading.value = true;
  try {
    const [list, stats] = await Promise.all([
      fetchJson('/api/repairs' + query.value),
      fetchJson('/api/repairs/stats/by-status'),
    ]);
    orders.value = list;
    counts.value = stats.counts || [0, 0, 0, 0];
  } catch (e) {
    setFlash(e.body?.error || e.message, false);
  } finally {
    loading.value = false;
  }
}

onMounted(load);
watch(statusFilter, () => load());

function setFilter(s) {
  statusFilter.value = s;
}

async function savePwd() {
  if (pwdNew.value !== pwdConfirm.value) {
    setFlash('两次新密码不一致', false);
    return;
  }
  try {
    await fetchJson(`/api/users/${auth.state.user.id}/password`, {
      method: 'PUT',
      body: { oldPassword: pwdOld.value, newPassword: pwdNew.value },
    });
    setFlash('密码已更新', true);
    pwdOld.value = '';
    pwdNew.value = '';
    pwdConfirm.value = '';
  } catch (e) {
    setFlash(e.body?.error || e.message, false);
  }
}
</script>

<template>
  <p v-if="loading" class="muted">加载中…</p>
  <template v-else>
    <h2 style="margin-top:0">报修单</h2>
    <div class="stats">
      <span v-for="(c, i) in counts" :key="i" class="stat-chip">
        {{ labels[i] }} <strong>{{ c }}</strong>
      </span>
    </div>
    <div class="filter-bar">
      <button
        type="button"
        class="filter-pill"
        :class="{ 'is-active': statusFilter === null }"
        @click="setFilter(null)"
      >全部</button>
      <button
        v-for="i in 4"
        :key="i - 1"
        type="button"
        class="filter-pill"
        :class="{ 'is-active': statusFilter === i - 1 }"
        @click="setFilter(i - 1)"
      >{{ labels[i - 1] }}</button>
    </div>
    <div class="table-wrap">
      <table class="data">
        <thead>
          <tr>
            <th>#</th>
            <th>学生</th>
            <th>宿舍</th>
            <th>设备</th>
            <th>状态</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="o in orders" :key="o.id">
            <td>{{ o.id }}</td>
            <td>{{ o.studentId }}</td>
            <td>{{ (o.building || '') + ' ' + (o.roomNo || '') }}</td>
            <td>{{ o.deviceType }}</td>
            <td><StatusBadge :code="o.status"/></td>
            <td>
              <RouterLink :to="`/admin/orders/${o.id}`">详情</RouterLink>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div class="card">
      <h3>修改密码</h3>
      <form @submit.prevent="savePwd">
        <div class="form-row">
          <label>当前密码</label>
          <input v-model="pwdOld" type="password" required/>
        </div>
        <div class="form-row">
          <label>新密码</label>
          <input v-model="pwdNew" type="password" required/>
        </div>
        <div class="form-row">
          <label>确认</label>
          <input v-model="pwdConfirm" type="password" required/>
        </div>
        <button class="btn btn--ghost" type="submit">更新</button>
      </form>
    </div>
  </template>
</template>
