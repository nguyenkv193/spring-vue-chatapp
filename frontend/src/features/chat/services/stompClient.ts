import { Client, type IMessage, type StompSubscription } from '@stomp/stompjs'

import type { ChatInboundMessage, ChatMessage } from '../types/chat'

const brokerURL = import.meta.env.VITE_WS_URL

type ConnectionHooks = {
  onConnect: () => void
  onError: (message: string) => void
}

class StompChatClient {
  private client: Client | null = null
  private conversationSubscription: StompSubscription | null = null

  connect(token: string, hooks: ConnectionHooks) {
    if (this.client?.active) {
      hooks.onConnect()
      return
    }

    this.client = new Client({
      brokerURL,
      reconnectDelay: 5000,
      connectHeaders: {
        Authorization: `Bearer ${token}`
      },
      onConnect: () => hooks.onConnect(),
      onStompError: (frame) => hooks.onError(frame.headers.message ?? 'STOMP connection failed'),
      onWebSocketError: () => hooks.onError('WebSocket handshake failed')
    })

    this.client.activate()
  }

  disconnect() {
    this.conversationSubscription?.unsubscribe()
    this.conversationSubscription = null
    this.client?.deactivate()
    this.client = null
  }

  subscribeToConversation(conversationId: string, onMessage: (message: ChatMessage) => void) {
    if (!this.client?.connected) {
      return
    }

    this.conversationSubscription?.unsubscribe()
    this.conversationSubscription = this.client.subscribe(
      `/topic/conversations/${conversationId}`,
      (frame: IMessage) => {
        onMessage(JSON.parse(frame.body) as ChatMessage)
      }
    )
  }

  publishMessage(payload: ChatInboundMessage) {
    if (!this.client?.connected) {
      throw new Error('Realtime client is not connected yet.')
    }

    this.client.publish({
      destination: '/app/chat.send',
      body: JSON.stringify(payload)
    })
  }
}

export const stompChatClient = new StompChatClient()
