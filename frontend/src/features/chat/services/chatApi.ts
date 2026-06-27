import { request } from '@/shared/api/httpClient'

import type { ChatMessage, ConversationSummary, CreateConversationPayload } from '../types/chat'

export async function fetchConversations(token: string) {
  return request<ConversationSummary[]>('/conversations', {
    method: 'GET',
    token
  })
}

export async function createConversation(payload: CreateConversationPayload, token: string) {
  return request<ConversationSummary>('/conversations', {
    method: 'POST',
    token,
    body: JSON.stringify(payload)
  })
}

export async function fetchMessages(conversationId: string, token: string) {
  return request<ChatMessage[]>(`/messages/conversations/${conversationId}`, {
    method: 'GET',
    token
  })
}
