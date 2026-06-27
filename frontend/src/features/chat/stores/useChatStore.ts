import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

import { createConversation as createConversationRequest, fetchConversations, fetchMessages } from '../services/chatApi'
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
  const creatingConversation = ref(false)
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

  async function createConversation(title: string) {
    if (!activeToken.value) {
      return null
    }

    creatingConversation.value = true
    error.value = null

    try {
      const response = await createConversationRequest(
        {
          title,
          type: 'GROUP'
        },
        activeToken.value
      )

      conversations.value = [response.data, ...conversations.value.filter((item) => item.id !== response.data.id)]
      await selectConversation(response.data.id)
      return response.data
    } catch (caughtError) {
      error.value = caughtError instanceof Error ? caughtError.message : 'Failed to create conversation'
      throw caughtError
    } finally {
      creatingConversation.value = false
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

    if (messages.value.some((item) => item.id === message.id)) {
      return
    }

    messages.value = [...messages.value, message]

    const currentConversation = conversations.value.find(
      (conversation) => conversation.id === message.conversationId
    )

    if (!currentConversation) {
      return
    }

    const updatedConversation: ConversationSummary = {
      ...currentConversation,
      updatedAt: message.sentAt,
      lastMessagePreview: message.content
    }

    conversations.value = [
      updatedConversation,
      ...conversations.value.filter((conversation) => conversation.id !== updatedConversation.id)
    ]
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
    creatingConversation.value = false
  }

  return {
    conversations,
    selectedConversationId,
    selectedConversation,
    messages,
    loadingConversations,
    loadingMessages,
    creatingConversation,
    error,
    connectionState,
    bootstrap,
    loadConversations,
    createConversation,
    selectConversation,
    connectRealtime,
    disconnectRealtime,
    sendMessage,
    appendIncomingMessage,
    reset
  }
})
