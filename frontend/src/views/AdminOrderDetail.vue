<script setup>
import { ref, watch, inject } from 'vue';
import { useRoute, useRouter, RouterLink } from 'vue-router';
import { fetchJson } from '@/api/http';
import { useAuthStore } from '@/stores/auth';
import StatusBadge from '@/components/StatusBadge.vue';

const route = useRoute();
const router = useRouter();
const setFlash = inject('setFlash');
const auth = useAuthStore();

const order = ref(null);
const loading = ref(true);
const statusVal = ref(0);
const remarkVal = ref('');

async function load(id) {
  loading.value = true;
  try {
    const o = await fetchJson(`/api/repairs/${id}`);
    order.value = o;
    statusVal.value = o.status;
    remarkVal.value = o.remark || '';
  } catch (e) {
    setFlash(e.body?.error || e.message, false);
    order.value = null;
  } finally {
    loading.value = false;
  }
}

watch(
  () => route.params.id,
  (id) => {
    if (id) load(id);
  },
  { immediate: true },
);

async function saveStatus() {
  const id = route.params.id;
  try {
    await fetchJson(`/api/repairs/${id}/status`, {
      method: 'PATCH',
      body: {
        adminId: auth.state.user.id,
        status: statusVal.value,
        remark: remarkVal.value || null,
      },
    });
    setFlash('已保存', true);
    load(id);
  } catch (e) {
    setFlash(e.body?.error || e.message, false);
  }
}

async function delOrder() {
  if (!confirm('确定删除？')) return;
  const id = route.params.id;
  try {
    await fetchJson(`/api/repairs/${id}`, { method: 'DELETE' });
    setFlash('已删除', true);
    router.push('/admin');
  } catch (e) {
    setFlash(e.body?.error || e.message, false);
  }
}
</script>

<template>
  <p v-if="loading" class="muted">加载中…</p>
  <template v-else-if="order">
    <p><RouterLink to="/admin">← 返回列表</RouterLink></p>
    <div class="card">
      <h2>报修 #{{ order.id }} <StatusBadge :code="order.status"/></h2>
      <p class="muted">学生ID {{ order.studentId }} · {{ order.building }} {{ order.roomNo }}</p>
      <p><strong>设备</strong> {{ order.deviceType }}</p>
      <p><strong>描述</strong> {{ order.description }}</p>
      <p><strong>备注</strong> {{ order.remark || '—' }}</p>
      <p><strong>评分</strong> {{ order.rating != null ? order.rating + ' 星' : '未评' }}</p>
    </div>
    <div v-if="order.status !== 3" class="card">
      <h3>更新状态</h3>
      <form @submit.prevent="saveStatus">
        <div class="form-inline">
          <div class="form-row">
            <label>状态</label>
            <select v-model.number="statusVal">
              <option :value="0">待处理</option>
              <option :value="1">处理中</option>
              <option :value="2">已完成</option>
              <option :value="3">已取消</option>
            </select>
          </div>
          <div class="form-row" style="flex:2;min-width:200px">
            <label>备注</label>
            <input v-model="remarkVal"/>
          </div>
        </div>
        <button class="btn btn--primary" type="submit">保存</button>
      </form>
    </div>
    <div class="card">
      <h3 style="color:var(--danger)">删除</h3>
      <button type="button" class="btn btn--danger" @click="delOrder">删除报修单</button>
    </div>
  </template>
</template>
