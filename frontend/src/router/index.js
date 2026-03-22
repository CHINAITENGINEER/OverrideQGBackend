import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'home', component: () => import('../views/HomeView.vue') },
    { path: '/login', name: 'login', component: () => import('../views/LoginView.vue') },
    { path: '/register', name: 'register', component: () => import('../views/RegisterView.vue') },
    {
      path: '/student',
      name: 'student',
      component: () => import('../views/StudentView.vue'),
      meta: { auth: true, role: 'STUDENT' },
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/AdminView.vue'),
      meta: { auth: true, role: 'ADMIN' },
    },
    {
      path: '/admin/orders/:id',
      name: 'admin-order',
      component: () => import('../views/AdminOrderDetail.vue'),
      meta: { auth: true, role: 'ADMIN' },
    },
  ],
});

router.beforeEach((to) => {
  const { state } = useAuthStore();
  if (!state.ready) return true;

  if (to.path === '/' && state.user) {
    return state.role === 'ADMIN' ? '/admin' : '/student';
  }

  if (to.meta.auth) {
    if (!state.user) return { name: 'login', query: { redirect: to.fullPath } };
    if (to.meta.role && state.role !== to.meta.role) {
      return state.role === 'ADMIN' ? '/admin' : '/student';
    }
  }

  if ((to.name === 'login' || to.name === 'register') && state.user) {
    return state.role === 'ADMIN' ? '/admin' : '/student';
  }

  return true;
});

export default router;
