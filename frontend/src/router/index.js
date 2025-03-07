import { createRouter, createWebHistory } from 'vue-router'
import ShortenView from '../views/ShortenView.vue'

const routes = [
    {
        path: '/',
        name: 'home',
        component: ShortenView
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router