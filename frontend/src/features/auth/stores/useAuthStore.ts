import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

import { login as loginRequest, getCurrentUser } from '../services/authApi'
import type { AuthTokenResponse, CurrentUser } from '../types/auth'
import { clearAuthSession, readAuthSession, writeAuthSession } from '@/shared/utils/storage'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(null)
  const user = ref<CurrentUser | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)
  const initialized = ref(false)

  const isAuthenticated = computed(() => Boolean(token.value))

  function hydrate() {
    if (initialized.value) {
      return
    }

    const session = readAuthSession()
    if (session) {
      token.value = session.token
      user.value = {
        id: session.userId,
        email: session.email,
        displayName: session.displayName,
        placeholder: true
      }
    }

    initialized.value = true
  }

  function applySession(payload: AuthTokenResponse) {
    token.value = payload.accessToken
    user.value = {
      id: payload.userId,
      email: payload.email,
      displayName: payload.displayName,
      placeholder: payload.placeholder
    }

    writeAuthSession({
      token: payload.accessToken,
      expiresAt: payload.expiresAt,
      email: payload.email,
      displayName: payload.displayName,
      userId: payload.userId
    })
  }

  async function login(email: string, password: string) {
    loading.value = true
    error.value = null

    try {
      const response = await loginRequest({ email, password })
      applySession(response.data)
      return response.data
    } catch (caughtError) {
      error.value = caughtError instanceof Error ? caughtError.message : 'Login failed'
      throw caughtError
    } finally {
      loading.value = false
    }
  }

  async function refreshCurrentUser() {
    if (!token.value) {
      return null
    }

    try {
      const response = await getCurrentUser(token.value)
      user.value = response.data
      return response.data
    } catch (caughtError) {
      error.value = caughtError instanceof Error ? caughtError.message : 'Failed to load current user'
      logout()
      return null
    }
  }

  function logout() {
    token.value = null
    user.value = null
    error.value = null
    clearAuthSession()
  }

  return {
    token,
    user,
    loading,
    error,
    initialized,
    isAuthenticated,
    hydrate,
    login,
    logout,
    refreshCurrentUser
  }
})
