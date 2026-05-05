import { createRouter, createWebHistory } from 'vue-router'
import HomeRechercheView from '../views/HomeRechercheView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: HomeRechercheView },
  ],
})

export default router