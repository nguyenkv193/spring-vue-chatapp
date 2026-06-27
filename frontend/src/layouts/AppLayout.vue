<template>
  <div class="app-layout">
    <header class="app-layout__header">
      <div>
        <p class="app-layout__eyebrow">Monorepo Skeleton</p>
        <strong>Chat App</strong>
      </div>

      <div class="app-layout__meta">
        <span v-if="authStore.user">{{ authStore.user.displayName }}</span>
        <button type="button" @click="logout">Logout</button>
      </div>
    </header>

    <main class="app-layout__content">
      <slot />
    </main>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

import { useAuthStore } from '@/features/auth/stores/useAuthStore'
import { useChatStore } from '@/features/chat/stores/useChatStore'

const router = useRouter()
const authStore = useAuthStore()
const chatStore = useChatStore()

async function logout() {
  chatStore.reset()
  authStore.logout()
  await router.push({ name: 'login' })
}
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  padding: 1.25rem;
}

.app-layout__header {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  align-items: center;
  margin-bottom: 1.25rem;
}

.app-layout__header strong,
.app-layout__header p {
  margin: 0;
}

.app-layout__eyebrow {
  color: var(--color-text-muted);
  font-size: 0.8rem;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  margin-bottom: 0.3rem;
}

.app-layout__meta {
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.app-layout__meta span {
  color: var(--color-text-muted);
}

.app-layout__meta button {
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 999px;
  background: transparent;
  color: var(--color-text);
  padding: 0.65rem 1rem;
  cursor: pointer;
}

.app-layout__content {
  width: min(1200px, 100%);
  margin: 0 auto;
}
</style>
