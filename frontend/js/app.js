/**
 * 宿舍报修 — 前端（纯静态 JS，位于 frontend/）
 *
 * API_ROOT：
 *   - 经 Nginx 与本项目 nginx/nginx.conf 部署时，与页面同域，保持 '' 即可（请求 /api/...）
 *   - 本地直连后端调试时改为：'http://127.0.0.1:8080'
 */
const API_ROOT = '';

function api(path) {
  return API_ROOT + path;
}

async function fetchJson(path, options = {}) {
  const opts = {
    credentials: 'include',
    headers: { 'Content-Type': 'application/json', ...options.headers },
    ...options,
  };
  if (options.body && typeof options.body === 'object' && !(options.body instanceof FormData)) {
    opts.body = JSON.stringify(options.body);
  }
  const res = await fetch(api(path), opts);
  const text = await res.text();
  let data = null;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    data = text;
  }
  if (!res.ok) {
    const err = new Error((data && data.error) || res.statusText || '请求失败');
    err.status = res.status;
    err.body = data;
    throw err;
  }
  return data;
}

function showFlash(msg, ok) {
  const el = document.getElementById('flash');
  el.className = 'wrap';
  el.innerHTML = msg ? `<div class="alert ${ok ? 'alert--ok' : 'alert--err'}">${escapeHtml(msg)}</div>` : '';
}

function escapeHtml(s) {
  const d = document.createElement('div');
  d.textContent = s;
  return d.innerHTML;
}

function statusBadge(code) {
  const m = { 0: ['待处理', 'badge--0'], 1: ['处理中', 'badge--1'], 2: ['已完成', 'badge--2'], 3: ['已取消', 'badge--3'] };
  const x = m[code] || ['未知', ''];
  return `<span class="badge ${x[1]}">${x[0]}</span>`;
}

function priLabel(p) {
  return { 1: '低', 2: '中', 3: '高' }[p] || '—';
}

const DEVICE_TYPES = [
  '水龙头/水管', '电路/插座', '门窗/门锁', '桌椅/床铺', '网络/网线', '空调', '灯具', '其他',
];

let currentUser = null;
let currentRole = null;

function hideAll() {
  ['view-landing', 'view-login', 'view-register', 'view-student', 'view-admin', 'view-admin-detail'].forEach((id) => {
    document.getElementById(id).classList.add('hidden');
  });
}

function setNav(title, showLogout) {
  document.getElementById('nav-title').textContent = title;
  const na = document.getElementById('nav-actions');
  na.classList.toggle('hidden', !showLogout);
  na.innerHTML = showLogout
    ? `<span class="muted">${escapeHtml(currentUser?.account || '')}</span>
       <button type="button" class="btn btn--ghost" id="btn-logout">退出</button>`
    : '';
  if (showLogout) {
    document.getElementById('btn-logout').onclick = () => logout();
  }
}

async function logout() {
  try {
    await fetchJson('/api/auth/logout', { method: 'POST', body: '{}' });
  } catch (_) { /* ignore */ }
  currentUser = null;
  currentRole = null;
  location.hash = '#/login';
  showFlash('已退出', true);
  route();
}

async function bootstrap() {
  try {
    const me = await fetchJson('/api/auth/me');
    currentUser = me;
    currentRole = me.role === 1 ? 'ADMIN' : 'STUDENT';
    if (location.hash === '#/login' || location.hash === '#/register' || location.hash === '#/' || !location.hash) {
      location.hash = currentRole === 'ADMIN' ? '#/admin' : '#/student';
    }
  } catch {
    currentUser = null;
    currentRole = null;
  }
  route();
}

function route() {
  hideAll();
  const h = location.hash || '#/';
  showFlash('');

  if (!currentUser) {
    setNav('宿舍报修', false);
    if (h === '#/login') {
      document.getElementById('view-login').classList.remove('hidden');
      return;
    }
    if (h === '#/register') {
      document.getElementById('view-register').classList.remove('hidden');
      return;
    }
    document.getElementById('view-landing').classList.remove('hidden');
    return;
  }

  if (h.startsWith('#/admin/order/')) {
    const id = h.split('/').pop();
    renderAdminDetail(id);
    return;
  }

  if (h === '#/admin' || (h.startsWith('#/admin') && currentRole === 'ADMIN')) {
    renderAdmin();
    return;
  }
  if (h === '#/student' || currentRole === 'STUDENT') {
    renderStudent();
    return;
  }
  location.hash = currentRole === 'ADMIN' ? '#/admin' : '#/student';
}

async function renderStudent() {
  setNav('学生工作台', true);
  document.getElementById('view-student').classList.remove('hidden');
  const uid = currentUser.id;
  const [user, repairs] = await Promise.all([
    fetchJson(`/api/users/${uid}`),
    fetchJson('/api/repairs/my'),
  ]);

  const devOpts = DEVICE_TYPES.map((d) => `<option value="${escapeHtml(d)}">${escapeHtml(d)}</option>`).join('');

  document.getElementById('view-student').innerHTML = `
    <div class="grid-2 grid-2--split">
      <div>
        <div class="card">
          <h2>个人信息</h2>
          <p class="muted">账号：<strong>${escapeHtml(user.account)}</strong></p>
          <p class="muted">宿舍：${user.building && user.roomNo ? `<strong>${escapeHtml(user.building)} · ${escapeHtml(user.roomNo)}</strong>` : '<strong>未绑定</strong>'}</p>
        </div>
        <div class="card">
          <h2>绑定宿舍</h2>
          <form id="form-dorm">
            <div class="form-inline">
              <div class="form-row"><label>楼栋</label><input name="building" value="${escapeHtml(user.building || '')}"/></div>
              <div class="form-row"><label>房间</label><input name="roomNo" value="${escapeHtml(user.roomNo || '')}"/></div>
            </div>
            <button class="btn btn--primary" type="submit">保存</button>
          </form>
        </div>
        <div class="card">
          <h2>修改密码</h2>
          <form id="form-pwd">
            <div class="form-row"><label>当前密码</label><input name="oldPassword" type="password" required/></div>
            <div class="form-row"><label>新密码</label><input name="newPassword" type="password" required/></div>
            <div class="form-row"><label>确认</label><input name="confirmPassword" type="password" required/></div>
            <button class="btn btn--ghost" type="submit">更新</button>
          </form>
        </div>
      </div>
      <div>
        <div class="card">
          <h2>新建报修</h2>
          <form id="form-repair">
            <div class="form-row"><label>设备类型</label><select name="deviceType">${devOpts}</select></div>
            <div class="form-row"><label>优先级</label>
              <select name="priority"><option value="1">低</option><option value="2" selected>中</option><option value="3">高</option></select>
            </div>
            <div class="form-row"><label>描述</label><textarea name="description" required></textarea></div>
            <button class="btn btn--primary" type="submit">提交</button>
          </form>
        </div>
        <div class="card">
          <h2>我的报修</h2>
          ${repairs.length === 0 ? '<p class="muted">暂无记录</p>' : `
          <div class="table-wrap"><table class="data"><thead><tr><th>#</th><th>设备</th><th>状态</th><th>优先级</th><th>操作</th></tr></thead><tbody>
            ${repairs.map((o) => `
              <tr>
                <td>${o.id}</td>
                <td>${escapeHtml(o.deviceType || '')}</td>
                <td>${statusBadge(o.status)}</td>
                <td>${priLabel(o.priority)}</td>
                <td>
                  ${o.status === 0 ? `<button type="button" class="btn btn--danger btn--sm" data-cancel="${o.id}">取消</button>` : ''}
                  ${o.status === 2 && o.rating == null ? `<span data-rate="${o.id}"><select class="rate-sel"><option value="5">5</option><option value="4">4</option><option value="3">3</option><option value="2">2</option><option value="1">1</option></select>
                  <button type="button" class="btn btn--primary btn--sm" data-rate-btn="${o.id}">评</button></span>` : ''}
                  ${o.status === 2 && o.rating != null ? `<span class="muted">已评 ${o.rating} 星</span>` : ''}
                </td>
              </tr>`).join('')}
          </tbody></table></div>`}
        </div>
      </div>
    </div>`;

  document.getElementById('form-dorm').onsubmit = async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    await fetchJson(`/api/users/${uid}/dorm`, {
      method: 'PUT',
      body: { building: fd.get('building'), roomNo: fd.get('roomNo') },
    });
    showFlash('宿舍已保存', true);
    renderStudent();
  };
  document.getElementById('form-pwd').onsubmit = async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    if (fd.get('newPassword') !== fd.get('confirmPassword')) {
      showFlash('两次新密码不一致', false);
      return;
    }
    await fetchJson(`/api/users/${uid}/password`, {
      method: 'PUT',
      body: { oldPassword: fd.get('oldPassword'), newPassword: fd.get('newPassword') },
    });
    showFlash('密码已更新', true);
    e.target.reset();
  };
  document.getElementById('form-repair').onsubmit = async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    await fetchJson('/api/repairs', {
      method: 'POST',
      body: {
        deviceType: fd.get('deviceType'),
        description: fd.get('description'),
        priority: parseInt(fd.get('priority'), 10),
      },
    });
    showFlash('报修已提交', true);
    renderStudent();
  };
  document.querySelectorAll('[data-cancel]').forEach((btn) => {
    btn.onclick = async () => {
      if (!confirm('确定取消？')) return;
      await fetchJson(`/api/repairs/${btn.dataset.cancel}/cancel`, { method: 'POST' });
      showFlash('已取消', true);
      renderStudent();
    };
  });
  document.querySelectorAll('[data-rate-btn]').forEach((btn) => {
    btn.onclick = async () => {
      const id = btn.dataset.rateBtn;
      const sel = document.querySelector(`[data-rate="${id}"] .rate-sel`);
      const rating = parseInt(sel.value, 10);
      await fetchJson(`/api/repairs/${id}/rate?rating=${rating}`, { method: 'POST' });
      showFlash('感谢评价', true);
      renderStudent();
    };
  });
}

let adminStatusFilter = null;

async function renderAdmin() {
  setNav('管理台', true);
  document.getElementById('view-admin').classList.remove('hidden');
  const q = adminStatusFilter != null ? `?status=${adminStatusFilter}` : '';
  const [orders, stats] = await Promise.all([
    fetchJson('/api/repairs' + q),
    fetchJson('/api/repairs/stats/by-status'),
  ]);
  const counts = stats.counts || [0, 0, 0, 0];

  const filt = (s, label) =>
    `<a href="#/admin" data-status="${s === '' ? '' : s}" class="${adminStatusFilter === s || (s === '' && adminStatusFilter == null) ? 'is-active' : ''}">${label}</a>`;

  document.getElementById('view-admin').innerHTML = `
    <h2 style="margin-top:0">报修单</h2>
    <div class="stats">
      ${counts.map((c, i) => `<span class="stat-chip">${['待处理', '处理中', '已完成', '已取消'][i]} <strong>${c}</strong></span>`).join('')}
    </div>
    <div class="filter-bar">
      ${filt('', '全部')}${filt(0, '待处理')}${filt(1, '处理中')}${filt(2, '已完成')}${filt(3, '已取消')}
    </div>
    <div class="table-wrap"><table class="data"><thead><tr><th>#</th><th>学生</th><th>宿舍</th><th>设备</th><th>状态</th><th></th></tr></thead><tbody>
      ${orders.map((o) => `
        <tr>
          <td>${o.id}</td>
          <td>${o.studentId}</td>
          <td>${escapeHtml((o.building || '') + ' ' + (o.roomNo || ''))}</td>
          <td>${escapeHtml(o.deviceType || '')}</td>
          <td>${statusBadge(o.status)}</td>
          <td><a href="#/admin/order/${o.id}">详情</a></td>
        </tr>`).join('')}
    </tbody></table></div>
    <div class="card">
      <h3>修改密码</h3>
      <form id="form-admin-pwd">
        <div class="form-row"><label>当前密码</label><input name="oldPassword" type="password" required/></div>
        <div class="form-row"><label>新密码</label><input name="newPassword" type="password" required/></div>
        <div class="form-row"><label>确认</label><input name="confirmPassword" type="password" required/></div>
        <button class="btn btn--ghost" type="submit">更新</button>
      </form>
    </div>`;

  document.querySelectorAll('.filter-bar a[data-status]').forEach((a) => {
    a.onclick = (ev) => {
      ev.preventDefault();
      const v = a.dataset.status;
      adminStatusFilter = v === '' ? null : parseInt(v, 10);
      renderAdmin();
    };
  });

  document.getElementById('form-admin-pwd').onsubmit = async (e) => {
    e.preventDefault();
    const fd = new FormData(e.target);
    if (fd.get('newPassword') !== fd.get('confirmPassword')) {
      showFlash('两次新密码不一致', false);
      return;
    }
    await fetchJson(`/api/users/${currentUser.id}/password`, {
      method: 'PUT',
      body: { oldPassword: fd.get('oldPassword'), newPassword: fd.get('newPassword') },
    });
    showFlash('密码已更新', true);
    e.target.reset();
  };
}

async function renderAdminDetail(id) {
  setNav('报修详情', true);
  document.getElementById('view-admin-detail').classList.remove('hidden');
  const o = await fetchJson(`/api/repairs/${id}`);
  const statuses = [
    { code: 0, desc: '待处理' },
    { code: 1, desc: '处理中' },
    { code: 2, desc: '已完成' },
    { code: 3, desc: '已取消' },
  ];
  const opts = statuses.map((s) => `<option value="${s.code}" ${s.code === o.status ? 'selected' : ''}>${s.desc}</option>`).join('');

  document.getElementById('view-admin-detail').innerHTML = `
    <p><a href="#/admin">← 返回列表</a></p>
    <div class="card">
      <h2>报修 #${o.id} ${statusBadge(o.status)}</h2>
      <p class="muted">学生ID ${o.studentId} · ${escapeHtml(o.building || '')} ${escapeHtml(o.roomNo || '')}</p>
      <p><strong>设备</strong> ${escapeHtml(o.deviceType || '')}</p>
      <p><strong>描述</strong> ${escapeHtml(o.description || '')}</p>
      <p><strong>备注</strong> ${escapeHtml(o.remark || '—')}</p>
      <p><strong>评分</strong> ${o.rating != null ? o.rating + ' 星' : '未评'}</p>
    </div>
    ${o.status === 3 ? '' : `
    <div class="card">
      <h3>更新状态</h3>
      <form id="form-status">
        <div class="form-inline">
          <div class="form-row"><label>状态</label><select name="status">${opts}</select></div>
          <div class="form-row" style="flex:2"><label>备注</label><input name="remark" value="${escapeHtml(o.remark || '')}"/></div>
        </div>
        <button class="btn btn--primary" type="submit">保存</button>
      </form>
    </div>`}
    <div class="card">
      <h3 style="color:var(--danger)">删除</h3>
      <button type="button" class="btn btn--danger" id="btn-del-order">删除报修单</button>
    </div>`;

  const fs = document.getElementById('form-status');
  if (fs) {
    fs.onsubmit = async (e) => {
      e.preventDefault();
      const fd = new FormData(e.target);
      await fetchJson(`/api/repairs/${id}/status`, {
        method: 'PATCH',
        body: {
          adminId: currentUser.id,
          status: parseInt(fd.get('status'), 10),
          remark: fd.get('remark') || null,
        },
      });
      showFlash('已保存', true);
      renderAdminDetail(id);
    };
  }
  document.getElementById('btn-del-order').onclick = async () => {
    if (!confirm('确定删除？')) return;
    await fetchJson(`/api/repairs/${id}`, { method: 'DELETE' });
    showFlash('已删除', true);
    location.hash = '#/admin';
  };
}

document.getElementById('btn-go-login').onclick = () => {
  location.hash = '#/login';
};
document.getElementById('btn-go-register').onclick = () => {
  location.hash = '#/register';
};

document.getElementById('form-login').onsubmit = async (e) => {
  e.preventDefault();
  const fd = new FormData(e.target);
  try {
    const data = await fetchJson('/api/auth/login', {
      method: 'POST',
      body: { account: fd.get('username'), password: fd.get('password') },
    });
    currentUser = data.user;
    currentRole = data.role;
    showFlash('登录成功', true);
    location.hash = data.role === 'ADMIN' ? '#/admin' : '#/student';
  } catch (err) {
    showFlash(err.body?.error || err.message, false);
  }
};

document.getElementById('form-register').onsubmit = async (e) => {
  e.preventDefault();
  const fd = new FormData(e.target);
  if (fd.get('password') !== fd.get('confirmPassword')) {
    showFlash('两次密码不一致', false);
    return;
  }
  try {
    await fetchJson('/api/users/register', {
      method: 'POST',
      body: {
        account: fd.get('account'),
        password: fd.get('password'),
        role: parseInt(fd.get('role'), 10),
      },
    });
    showFlash('注册成功，请登录', true);
    location.hash = '#/login';
  } catch (err) {
    showFlash(err.body?.error || err.message, false);
  }
};

window.addEventListener('hashchange', route);

window.addEventListener('DOMContentLoaded', () => {
  bootstrap();
});
