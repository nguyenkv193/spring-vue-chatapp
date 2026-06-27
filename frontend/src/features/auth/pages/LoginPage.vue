<template>
  <AuthLayout>
    <section class="login-page">
      <div class="login-page__copy">
        <p class="login-page__eyebrow">Local sign in</p>
        <h1>Sign in and test the chat flow.</h1>
        <p class="login-page__lead">
          Use the seeded account below to load conversations and send demo messages.
        </p>

        <ul class="login-page__notes">
          <li>Placeholder JWT authentication is still active.</li>
          <li>Conversation history loads over REST.</li>
          <li>Live updates arrive through STOMP.</li>
        </ul>
      </div>

      <form class="login-form" @submit.prevent="submit">
        <div class="login-form__panel">
          <div class="login-form__header">
            <h2>Welcome back</h2>
            <p>Continue with the local development account.</p>
          </div>

          <label class="login-form__field">
            <span>Email</span>
            <input v-model.trim="email" type="email" placeholder="dev@example.com" required />
          </label>

          <label class="login-form__field">
            <span>Password</span>
            <input v-model="password" type="password" placeholder="password123" required />
          </label>

          <p v-if="authStore.error" class="login-form__error">
            {{ authStore.error }}
          </p>

          <button class="login-form__submit" type="submit" :disabled="authStore.loading">
            {{ authStore.loading ? 'Signing in...' : 'Enter chat' }}
          </button>

          <div class="login-form__credentials">
            <span>Dev account</span>
            <div>
              <kbd>dev@example.com</kbd>
              <kbd>password123</kbd>
            </div>
          </div>
        </div>
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
  grid-template-columns: minmax(0, 1fr) minmax(320px, 400px);
  gap: clamp(1.25rem, 4vw, 2.5rem);
  align-items: center;
}

.login-page__copy {
  display: grid;
  gap: 0.95rem;
  max-width: 36rem;
}

.login-page__eyebrow,
.login-page__lead,
.login-page__notes {
  margin: 0;
}

.login-page__eyebrow {
  color: var(--color-text-muted);
  font-size: 0.8rem;
  letter-spacing: 0.1em;
  text-transform: uppercase;
}

.login-page h1 {
  margin: 0;
  font-size: clamp(2.4rem, 5vw, 4.1rem);
  max-width: 10ch;
}

.login-page__lead {
  color: var(--color-text-muted);
  max-width: 46ch;
}

.login-page__notes {
  padding: 0;
  list-style: none;
  display: grid;
  gap: 0.7rem;
}

.login-page__notes li {
  padding: 0.8rem 0.9rem;
  border-radius: 1rem;
  background: rgba(255, 255, 255, 0.56);
  border: 1px solid var(--color-border);
}

.login-form {
  min-width: 0;
}

.login-form__panel {
  display: grid;
  gap: 1rem;
  padding: 1.1rem;
  border-radius: 1.25rem;
  background: var(--color-surface-muted);
  border: 1px solid var(--color-border);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.74);
}

.login-form__header {
  display: grid;
  gap: 0.3rem;
}

.login-form__header h2,
.login-form__header p,
.login-form__field span,
.login-form__error,
.login-form__credentials span {
  margin: 0;
}

.login-form__header p,
.login-form__field span,
.login-form__credentials span {
  color: var(--color-text-muted);
}

.login-form__field {
  display: grid;
  gap: 0.5rem;
}

.login-form__field span {
  font-size: 0.85rem;
  font-weight: 600;
}

.login-form__field input {
  width: 100%;
  padding: 0.92rem 1rem;
  border-radius: var(--radius-control);
  border: 1px solid var(--color-border);
  background: var(--color-surface-strong);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.66);
}

.login-form__error {
  padding: 0.8rem 0.9rem;
  border-radius: 0.95rem;
  background: var(--color-danger-soft);
  color: var(--color-danger);
}

.login-form__submit {
  width: 100%;
  padding: 0.95rem 1.1rem;
  border-radius: 0.95rem;
  background: var(--color-text);
  color: #ffffff;
  font-weight: 700;
}

.login-form__submit:hover:not(:disabled) {
  background: #1e293b;
  transform: translateY(-1px);
}

.login-form__submit:active:not(:disabled) {
  transform: translateY(1px);
}

.login-form__submit:disabled {
  opacity: 0.7;
  cursor: wait;
}

.login-form__credentials {
  display: grid;
  gap: 0.55rem;
  padding-top: 0.25rem;
  border-top: 1px solid var(--color-border);
}

.login-form__credentials div {
  display: flex;
  flex-wrap: wrap;
  gap: 0.6rem;
}

.login-form__credentials kbd {
  padding: 0.5rem 0.65rem;
  border-radius: 0.7rem;
  border: 1px solid var(--color-border);
  background: var(--color-surface);
  font-family: var(--font-mono);
  font-size: 0.84rem;
}

@media (max-width: 900px) {
  .login-page {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .login-form__panel {
    padding: 1rem;
  }
}
</style>
