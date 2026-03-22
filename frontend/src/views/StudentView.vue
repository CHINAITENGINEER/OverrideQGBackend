<script setup>
import { ref, reactive, onMounted, inject } from 'vue';
import { fetchJson } from '@/api/http';
import { useAuthStore } from '@/stores/auth';
import StatusBadge from '@/components/StatusBadge.vue';

const setFlash = inject('setFlash');
const auth = useAuthStore();

const DEVICE_TYPES = [
  '水龙头/水管', '电路/插座', '门窗/门锁', '桌椅/床铺', '网络/网线', '空调', '灯具', '其他',
];

const user = ref(null);
const repairs = ref([]);
const loading = ref(true);

const dormBuilding = ref('');
const dormRoom = ref('');
const pwdOld = ref('');
const pwdNew = ref('');
const pwdConfirm = ref('');

const repairDevice = ref(DEVICE_TYPES[0]);
const repairPriority = ref(2);
const repairDesc = ref('');

const ratePick = reactive({});

function priLabel(p) {
  return { 1: '低', 2: '中', 3: '高' }[p] || '—';
}

async function load() {
  loading.value = true;
  const uid = auth.state.user.id;
  try {
    const [u, list] = await Promise.all([
      fetchJson(`/api/users/${uid}`),
      fetchJson('/api/repairs/my'),
    ]);
    user.value = u;
    repairs.value = list;
    dormBuilding.value = u.building || '';
    dormRoom.value = u.roomNo || '';
    list.forEach((o) => {
      if (ratePick[o.id] == null) ratePick[o.id] = 5;
    });
  } catch (e) {
    setFlash(e.body?.error || e.message, false);
  } finally {
    loading.value = false;
  }
}

onMounted(load);

async function saveDorm() {
  const uid = auth.state.user.id;
  try {
    await fetchJson(`/api/users/${uid}/dorm`, {
      method: 'PUT',
      body: { building: dormBuilding.value, roomNo: dormRoom.value },
    });
    setFlash('宿舍已保存', true);
    load();
  } catch (e) {
    setFlash(e.body?.error || e.message, false);
  }
}

async function savePwd() {
  if (pwdNew.value !== pwdConfirm.value) {
    setFlash('两次新密码不一致', false);
    return;
  }
  const uid = auth.state.user.id;
  try {
    await fetchJson(`/api/users/${uid}/password`, {
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

async function submitRepair() {
  try {
    await fetchJson('/api/repairs', {
      method: 'POST',
      body: {
        deviceType: repairDevice.value,
        description: repairDesc.value,
        priority: repairPriority.value,
      },
    });
    setFlash('报修已提交', true);
    repairDesc.value = '';
    load();
  } catch (e) {
    setFlash(e.body?.error || e.message, false);
  }
}

async function cancelRepair(id) {
  if (!confirm('确定取消？')) return;
  try {
    await fetchJson(`/api/repairs/${id}/cancel`, { method: 'POST' });
    setFlash('已取消', true);
    load();
  } catch (e) {
    setFlash(e.body?.error || e.message, false);
  }
}

async function submitRate(id) {
  const r = ratePick[id] ?? 5;
  try {
    await fetchJson(`/api/repairs/${id}/rate?rating=${r}`, { method: 'POST' });
    setFlash('感谢评价', true);
    load();
  } catch (e) {
    setFlash(e.body?.error || e.message, false);
  }
}
</script>

<template>
  <p v-if="loading" class="muted">加载中…</p>
  <div v-else-if="user" class="grid-2 grid-2--split">
    <div>
      <div class="card">
        <h2>个人信息</h2>
        <p class="muted">账号：<strong>{{ user.account }}</strong></p>
        <p class="muted">
          宿舍：
          <strong v-if="user.building && user.roomNo">{{ user.building }} · {{ user.roomNo }}</strong>
          <strong v-else>未绑定</strong>
        </p>
      </div>
      <div class="card">
        <h2>绑定宿舍</h2>
        <form @submit.prevent="saveDorm">
          <div class="form-inline">
            <div class="form-row">
              <label>楼栋</label>
              <input v-model="dormBuilding"/>
            </div>
            <div class="form-row">
              <label>房间</label>
              <input v-model="dormRoom"/>
            </div>
          </div>
          <button class="btn btn--primary" type="submit">保存</button>
        </form>
      </div>
      <div class="card">
        <h2>修改密码</h2>
        <form @submit.prevent="savePwd">
          <div class="form-row">
            <label>当前密码</label>
            <input v-model="pwdOld" type="password" required autocomplete="current-password"/>
          </div>
          <div class="form-row">
            <label>新密码</label>
            <input v-model="pwdNew" type="password" required autocomplete="new-password"/>
          </div>
          <div class="form-row">
            <label>确认</label>
            <input v-model="pwdConfirm" type="password" required autocomplete="new-password"/>
          </div>
          <button class="btn btn--ghost" type="submit">更新</button>
        </form>
      </div>
    </div>
    <div>
      <div class="card">
        <h2>新建报修</h2>
        <form @submit.prevent="submitRepair">
          <div class="form-row">
            <label>设备类型</label>
            <select v-model="repairDevice">
              <option v-for="d in DEVICE_TYPES" :key="d" :value="d">{{ d }}</option>
            </select>
          </div>
          <div class="form-row">
            <label>优先级</label>
            <select v-model.number="repairPriority">
              <option :value="1">低</option>
              <option :value="2">中</option>
              <option :value="3">高</option>
            </select>
          </div>
          <div class="form-row">
            <label>描述</label>
            <textarea v-model="repairDesc" required></textarea>
          </div>
          <button class="btn btn--primary" type="submit">提交</button>
        </form>
      </div>
      <div class="card">
        <h2>我的报修</h2>
        <p v-if="repairs.length === 0" class="muted">暂无记录</p>
        <div v-else class="table-wrap">
          <table class="data">
            <thead>
              <tr>
                <th>#</th>
                <th>设备</th>
                <th>状态</th>
                <th>优先级</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="o in repairs" :key="o.id">
                <td>{{ o.id }}</td>
                <td>{{ o.deviceType }}</td>
                <td><StatusBadge :code="o.status"/></td>
                <td>{{ priLabel(o.priority) }}</td>
                <td>
                  <button
                    v-if="o.status === 0"
                    type="button"
                    class="btn btn--danger btn--sm"
                    @click="cancelRepair(o.id)"
                  >取消</button>
                  <template v-if="o.status === 2 && o.rating == null">
                    <select v-model.number="ratePick[o.id]" class="btn--sm" style="width: auto; padding: 0.25rem; margin-right: 0.25rem">
                      <option :value="5">5</option>
                      <option :value="4">4</option>
                      <option :value="3">3</option>
                      <option :value="2">2</option>
                      <option :value="1">1</option>
                    </select>
                    <button type="button" class="btn btn--primary btn--sm" @click="submitRate(o.id)">评</button>
                  </template>
                  <span v-if="o.status === 2 && o.rating != null" class="muted">已评 {{ o.rating }} 星</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
