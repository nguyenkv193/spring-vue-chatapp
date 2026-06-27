export interface AuthTokenResponse {
  userId: string
  email: string
  displayName: string
  accessToken: string
  expiresAt: string
  placeholder: boolean
}

export interface LoginRequest {
  email: string
  password: string
}

export interface CurrentUser {
  id: string
  email: string
  displayName: string
  placeholder: boolean
}
