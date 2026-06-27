import { request } from '@/shared/api/httpClient'

import type { AuthTokenResponse, CurrentUser, LoginRequest } from '../types/auth'

export async function login(payload: LoginRequest) {
  return request<AuthTokenResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload)
  })
}

export async function getCurrentUser(token: string) {
  return request<CurrentUser>('/users/me', {
    method: 'GET',
    token
  })
}
