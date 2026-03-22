import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { useAuthStore } from './stores/auth';
import './assets/app.css';

const app = createApp(App);
app.use(router);

const auth = useAuthStore();
auth.init().finally(() => {
  app.mount('#app');
});
