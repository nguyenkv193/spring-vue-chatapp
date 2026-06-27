export interface ApiEnvelope<T> {
  success: boolean
  message: string
  data: T
  timestamp: string
}

export interface ApiErrorPayload {
  message: string
  status?: number
  details?: Record<string, unknown>
}
