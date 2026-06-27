import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

import { fetchConversations, fetchMessages } from '../services/chatApi'
import { stompChatClient } from '../services/stompClient'
import type {
  ChatConnectionState,
  ChatInboundMessage,
  ChatMessage,
  ConversationSummary
} from '../types/chat'

export const useChatStore = defineStore('chat', () => {
  const conversations = ref<ConversationSummary[]>([])
  const selectedConversationId = ref<string | null>(null)
  const messages = ref<ChatMessage[]>([])
  const loadingConversations = ref(false)
  const loadingMessages = ref(false)
  const error = ref<string | null>(null)
  const connectionState = ref<ChatConnectionState>('disconnected')
  const activeToken = ref<string | null>(null)

  const selectedConversation = computed(() =>
    conversations.value.find((conversation) => conversation.id === selectedConversationId.value) ?? null
  )

  async function bootstrap(token: string) {
    activeToken.value = token
    await loadConversations(token)
    connectRealtime(token)
  }

  async function loadConversations(token: string) {
    loadingConversations.value = true
    error.value = null

    try {
      const response = await fetchConversations(token)
      conversations.value = response.data

      if (response.data.length > 0) {
        await selectConversation(response.data[0].id)
      } else {
        selectedConversationId.value = null
        messages.value = []
      }
    } catch (caughtError) {
      error.value = caughtError instanceof Error ? caughtError.message : 'Failed to load conversations'
    } finally {
      loadingConversations.value = false
    }
  }

  async function selectConversation(conversationId: string) {
    if (!activeToken.value) {
      return
    }

    selectedConversationId.value = conversationId
    loadingMessages.value = true
    error.value = null

    try {
      const response = await fetchMessages(conversationId, activeToken.value)
      messages.value = response.data

      if (connectionState.value === 'connected') {
        stompChatClient.subscribeToConversation(conversationId, appendIncomingMessage)
      }
    } catch (caughtError) {
      error.value = caughtError instanceof Error ? caughtError.message : 'Failed to load messages'
    } finally {
      loadingMessages.value = false
    }
  }

  function connectRealtime(token: string) {
    if (connectionState.value === 'connecting' || connectionState.value === 'connected') {
      return
    }

    connectionState.value = 'connecting'
    stompChatClient.connect(token, {
      onConnect: () => {
        connectionState.value = 'connected'
        if (selectedConversationId.value) {
          stompChatClient.subscribeToConversation(selectedConversationId.value, appendIncomingMessage)
        }
      },
      onError: (message) => {
        connectionState.value = 'error'
        error.value = message
      }
    })
  }

  function disconnectRealtime() {
    stompChatClient.disconnect()
    connectionState.value = 'disconnected'
  }

  function appendIncomingMessage(message: ChatMessage) {
    if (message.conversationId !== selectedConversationId.value) {
      return
    }

    messages.value = [...messages.value, message]
  }

  function sendMessage(payload: ChatInboundMessage) {
    stompChatClient.publishMessage(payload)
  }

  function reset() {
    disconnectRealtime()
    conversations.value = []
    selectedConversationId.value = null
    messages.value = []
    error.value = null
    activeToken.value = null
  }

  return {
    conversations,
    selectedConversationId,
    selectedConversation,
    messages,
    loadingConversations,
    loadingMessages,
    error,
    connectionState,
    bootstrap,
    loadConversations,
    selectConversation,
    connectRealtime,
    disconnectRealtime,
    sendMessage,
    appendIncomingMessage,
    reset
  }
})
