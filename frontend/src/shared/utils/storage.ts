const AUTH_SESSION_KEY = 'chat-app-dev-auth-session'

export interface StoredAuthSession {
  token: string
  expiresAt: string
  email: string
  displayName: string
  userId: string
}

// Dev-only localStorage persistence. Prefer httpOnly cookies or stronger session controls in production.
export function readAuthSession(): StoredAuthSession | null {
  const rawValue = localStorage.getItem(AUTH_SESSION_KEY)
  if (!rawValue) {
    return null
  }

  try {
    return JSON.parse(rawValue) as StoredAuthSession
  } catch {
    localStorage.removeItem(AUTH_SESSION_KEY)
    return null
  }
}

export function writeAuthSession(session: StoredAuthSession): void {
  localStorage.setItem(AUTH_SESSION_KEY, JSON.stringify(session))
}

export function clearAuthSession(): void {
  localStorage.removeItem(AUTH_SESSION_KEY)
}
