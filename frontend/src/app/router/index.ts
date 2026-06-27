import { createRouter, createWebHistory } from 'vue-router'

import LoginPage from '@/features/auth/pages/LoginPage.vue'
import ChatPage from '@/features/chat/pages/ChatPage.vue'
import { useAuthStore } from '@/features/auth/stores/useAuthStore'

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/chat'
    },
    {
      path: '/login',
      name: 'login',
      component: LoginPage,
      meta: { guestOnly: true }
    },
    {
      path: '/chat',
      name: 'chat',
      component: ChatPage,
      meta: { requiresAuth: true }
    }
  ]
})

router.beforeEach(async (to) => {
  const authStore = useAuthStore()
  authStore.hydrate()

  if (authStore.isAuthenticated && !authStore.user) {
    await authStore.refreshCurrentUser()
  }

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return { name: 'login' }
  }

  if (to.meta.guestOnly && authStore.isAuthenticated) {
    return { name: 'chat' }
  }

  return true
})
