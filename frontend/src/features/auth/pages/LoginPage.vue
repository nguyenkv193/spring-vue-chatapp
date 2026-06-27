<template>
  <AuthLayout>
    <section class="login-page">
      <div class="login-page__intro">
        <p class="login-page__eyebrow">Realtime Base</p>
        <h1>Sign in to the chat skeleton</h1>
        <p>
          This login uses the backend placeholder JWT flow. It is only meant to unblock local
          development before real registration and credential checks are added.
        </p>
      </div>

      <form class="login-form" @submit.prevent="submit">
        <label class="login-form__field">
          <span>Email</span>
          <input v-model.trim="email" type="email" placeholder="dev@example.com" required />
        </label>

        <label class="login-form__field">
          <span>Password</span>
          <input v-model="password" type="password" placeholder="minimum 8 characters" required />
        </label>

        <p v-if="authStore.error" class="login-form__error">
          {{ authStore.error }}
        </p>

        <button class="login-form__submit" type="submit" :disabled="authStore.loading">
          {{ authStore.loading ? 'Signing in...' : 'Continue to chat' }}
        </button>
      </form>
    </section>
  </AuthLayout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import AuthLayout from '@/layouts/AuthLayout.vue'
import { useAuthStore } from '../stores/useAuthStore'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('dev@example.com')
const password = ref('password123')

async function submit() {
  await authStore.login(email.value, password.value)
  await router.push({ name: 'chat' })
}
</script>

<style scoped>
.login-page {
  display: grid;
  gap: 2rem;
}

.login-page__intro {
  display: grid;
  gap: 0.8rem;
}

.login-page__eyebrow {
  margin: 0;
  font-size: 0.78rem;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: var(--color-accent);
}

.login-page h1 {
  margin: 0;
  font-size: clamp(2rem, 5vw, 3rem);
}

.login-page p {
  margin: 0;
  color: var(--color-text-muted);
}

.login-form {
  display: grid;
  gap: 1rem;
}

.login-form__field {
  display: grid;
  gap: 0.55rem;
}

.login-form__field span {
  font-size: 0.9rem;
  color: var(--color-text-muted);
}

.login-form__field input {
  width: 100%;
  border-radius: 1rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.04);
  color: var(--color-text);
  padding: 0.95rem 1rem;
}

.login-form__submit {
  border: none;
  border-radius: 999px;
  padding: 0.95rem 1.2rem;
  font-weight: 800;
  background: linear-gradient(135deg, var(--color-accent), var(--color-accent-strong));
  color: var(--color-surface-strong);
  cursor: pointer;
}

.login-form__submit:disabled {
  opacity: 0.7;
  cursor: wait;
}

.login-form__error {
  margin: 0;
  color: var(--color-danger);
}
</style>
