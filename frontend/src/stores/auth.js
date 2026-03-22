import { reactive } from 'vue';

/**
 * 轻量会话状态（与后端 Session + Cookie 配合）
 */
const state = reactive({
  user: null,
  role: null, // 'STUDENT' | 'ADMIN' | null
  ready: false,
});

export function useAuthStore() {
  async function init() {
    state.ready = false;
    try {
      const res = await fetch('/api/auth/me', { credentials: 'include' });
      if (res.ok) {
        const me = await res.json();
        state.user = me;
        state.role = me.role === 1 ? 'ADMIN' : 'STUDENT';
      } else {
        state.user = null;
        state.role = null;
      }
    } catch {
      state.user = null;
      state.role = null;
    } finally {
      state.ready = true;
    }
  }

  function setSession(user, role) {
    state.user = user;
    state.role = role;
  }

  async function logout() {
    try {
      await fetch('/api/auth/logout', {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: '{}',
      });
    } catch {
      /* ignore */
    }
    state.user = null;
    state.role = null;
  }

  return { state, init, setSession, logout };
}
