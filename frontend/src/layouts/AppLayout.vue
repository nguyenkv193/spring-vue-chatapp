<template>
  <div class="app-layout">
    <div class="app-layout__frame">
      <header class="app-layout__header">
        <div class="app-layout__brand">
          <div class="app-layout__brand-mark">CA</div>
          <div class="app-layout__brand-copy">
            <strong>Chat app</strong>
            <small>Realtime workspace</small>
          </div>
        </div>

        <div class="app-layout__meta">
          <div v-if="authStore.user" class="app-layout__user">
            <strong>{{ authStore.user.displayName }}</strong>
            <small>{{ authStore.user.email }}</small>
          </div>

          <button class="app-layout__logout" type="button" @click="logout">Log out</button>
        </div>
      </header>

      <main class="app-layout__content">
        <slot />
      </main>
    </div>
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
  min-height: 100dvh;
  padding: clamp(0.75rem, 1.8vw, 1.5rem);
}

.app-layout__frame {
  width: min(1440px, 100%);
  margin: 0 auto;
  display: grid;
  gap: 1rem;
}

.app-layout__header {
  position: sticky;
  top: 0.75rem;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 0.85rem 1rem;
  border: 1px solid var(--color-border);
  border-radius: 1.25rem;
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: blur(16px);
  box-shadow: var(--shadow-soft), var(--shadow-edge);
}

.app-layout__brand {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  min-width: 0;
}

.app-layout__brand-mark {
  display: grid;
  place-items: center;
  width: 2.5rem;
  aspect-ratio: 1;
  border-radius: 0.9rem;
  background: var(--color-accent-soft);
  color: var(--color-accent-ink);
  font-size: 0.8rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  flex-shrink: 0;
}

.app-layout__brand-copy {
  display: grid;
  gap: 0.12rem;
}

.app-layout__brand-copy strong,
.app-layout__brand-copy small {
  margin: 0;
}

.app-layout__brand-copy small {
  color: var(--color-text-muted);
}

.app-layout__meta {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  min-width: 0;
}

.app-layout__user {
  display: grid;
  gap: 0.08rem;
  min-width: 0;
  padding: 0.55rem 0.8rem;
  border-radius: 1rem;
  background: var(--color-surface-muted);
  border: 1px solid var(--color-border);
}

.app-layout__user strong,
.app-layout__user small {
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.app-layout__user small {
  color: var(--color-text-muted);
}

.app-layout__logout {
  padding: 0.8rem 0.95rem;
  border-radius: 0.9rem;
  background: var(--color-text);
  color: #ffffff;
  font-weight: 600;
}

.app-layout__logout:hover {
  background: #1e293b;
  transform: translateY(-1px);
}

.app-layout__logout:active {
  transform: translateY(1px);
}

.app-layout__content {
  min-width: 0;
}

@media (max-width: 760px) {
  .app-layout__header {
    flex-direction: column;
    align-items: stretch;
  }

  .app-layout__meta {
    flex-direction: column;
    align-items: stretch;
  }

  .app-layout__logout {
    width: 100%;
  }
}
</style>
