export type ChatConnectionState = 'disconnected' | 'connecting' | 'connected' | 'error'

export type MessageType = 'TEXT' | 'SYSTEM'

export interface ConversationSummary {
  id: string
  title: string | null
  type: 'DIRECT' | 'GROUP'
  updatedAt: string
  lastMessagePreview: string | null
}

export interface CreateConversationPayload {
  title: string
  type?: 'DIRECT' | 'GROUP'
}

export interface ChatMessage {
  id: string
  conversationId: string
  senderId: string | null
  senderDisplayName: string
  content: string
  messageType: MessageType
  sentAt: string
}

export interface ChatInboundMessage {
  conversationId: string
  content: string
  messageType: MessageType
}
