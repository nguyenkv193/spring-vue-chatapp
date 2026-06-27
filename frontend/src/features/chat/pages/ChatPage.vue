<template>
  <AppLayout>
    <section class="chat-page">
      <header class="chat-page__hero">
        <div>
          <p class="chat-page__eyebrow">Chat Workspace</p>
          <h1>Realtime chat foundation</h1>
          <p>
            REST loads conversations and messages. STOMP is wired for `/app/chat.send` and
            `/topic/conversations/{conversationId}`.
          </p>
        </div>

        <span class="chat-page__status" :data-state="chatStore.connectionState">
          {{ connectionLabel }}
        </span>
      </header>

      <div class="chat-page__grid">
        <section class="chat-page__sidebar panel">
          <StatePanel
            v-if="chatStore.loadingConversations"
            eyebrow="Loading"
            title="Fetching conversations"
            body="The sidebar is waiting for the conversation list from the backend."
          />

          <StatePanel
            v-else-if="chatStore.error && chatStore.conversations.length === 0"
            eyebrow="Error"
            title="Could not load conversations"
            :body="chatStore.error"
            tone="error"
            action-label="Retry"
            @action="reload"
          />

          <StatePanel
            v-else-if="chatStore.conversations.length === 0"
            eyebrow="Empty"
            title="No conversations yet"
            body="Schema, repository, and page state are ready. Seed a conversation or build the create-conversation flow next."
          />

          <ConversationList
            v-else
            :conversations="chatStore.conversations"
            :selected-conversation-id="chatStore.selectedConversationId"
            @select="chatStore.selectConversation"
          />
        </section>

        <section class="chat-page__main panel">
          <template v-if="!chatStore.selectedConversation">
            <StatePanel
              eyebrow="Ready"
              title="Select a conversation"
              body="Once a conversation exists, messages and realtime subscriptions will appear here."
            />
          </template>

          <template v-else>
            <header class="chat-page__conversation-header">
              <div>
                <p>Current conversation</p>
                <h2>{{ chatStore.selectedConversation.title || fallbackTitle }}</h2>
              </div>
            </header>

            <StatePanel
              v-if="chatStore.loadingMessages"
              eyebrow="Loading"
              title="Fetching messages"
              body="The page is waiting for the latest conversation history."
            />

            <StatePanel
              v-else-if="chatStore.error && chatStore.messages.length === 0"
              eyebrow="Error"
              title="Messages are unavailable"
              :body="chatStore.error"
              tone="error"
              action-label="Retry"
              @action="retryMessages"
            />

            <StatePanel
              v-else-if="chatStore.messages.length === 0"
              eyebrow="Empty"
              title="No messages yet"
              body="You can publish a demo STOMP message below once the WebSocket client is connected."
            />

            <MessageList v-else :messages="chatStore.messages" />

            <MessageInput
              :disabled="chatStore.connectionState !== 'connected' || !chatStore.selectedConversationId"
              @send="sendMessage"
            />
          </template>
        </section>
      </div>
    </section>
  </AppLayout>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'

import AppLayout from '@/layouts/AppLayout.vue'
import StatePanel from '@/shared/components/StatePanel.vue'
import { useAuthStore } from '@/features/auth/stores/useAuthStore'
import ConversationList from '../components/ConversationList.vue'
import MessageInput from '../components/MessageInput.vue'
import MessageList from '../components/MessageList.vue'
import { useChatStore } from '../stores/useChatStore'

const authStore = useAuthStore()
const chatStore = useChatStore()

const connectionLabel = computed(() => {
  switch (chatStore.connectionState) {
    case 'connected':
      return 'Realtime connected'
    case 'connecting':
      return 'Connecting socket'
    case 'error':
      return 'Socket error'
    default:
      return 'Socket offline'
  }
})

const fallbackTitle = computed(() => {
  return chatStore.selectedConversationId
    ? `Conversation ${chatStore.selectedConversationId.slice(0, 8)}`
    : 'Conversation'
})

onMounted(async () => {
  if (authStore.token) {
    await chatStore.bootstrap(authStore.token)
  }
})

async function reload() {
  if (authStore.token) {
    await chatStore.loadConversations(authStore.token)
  }
}

async function retryMessages() {
  if (chatStore.selectedConversationId) {
    await chatStore.selectConversation(chatStore.selectedConversationId)
  }
}

function sendMessage(content: string) {
  if (!chatStore.selectedConversationId) {
    return
  }

  chatStore.sendMessage({
    conversationId: chatStore.selectedConversationId,
    content,
    messageType: 'TEXT'
  })
}
</script>

<style scoped>
.chat-page {
  display: grid;
  gap: 1.5rem;
}

.chat-page__hero {
  display: flex;
  justify-content: space-between;
  gap: 1.5rem;
  align-items: end;
}

.chat-page__hero h1,
.chat-page__hero p {
  margin: 0;
}

.chat-page__hero > div {
  display: grid;
  gap: 0.75rem;
}

.chat-page__eyebrow {
  font-size: 0.78rem;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: var(--color-accent);
}

.chat-page__hero p:last-child {
  color: var(--color-text-muted);
}

.chat-page__status {
  border-radius: 999px;
  padding: 0.75rem 1rem;
  font-size: 0.88rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.chat-page__status[data-state='connected'] {
  color: #8ff0cb;
  border-color: rgba(143, 240, 203, 0.35);
}

.chat-page__status[data-state='error'] {
  color: var(--color-danger);
  border-color: rgba(255, 106, 92, 0.3);
}

.chat-page__grid {
  display: grid;
  grid-template-columns: minmax(260px, 320px) minmax(0, 1fr);
  gap: 1.25rem;
}

.panel {
  display: grid;
  gap: 1rem;
  padding: 1.2rem;
  border-radius: 1.5rem;
  background: rgba(11, 18, 32, 0.74);
  border: 1px solid rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(22px);
}

.chat-page__conversation-header p,
.chat-page__conversation-header h2 {
  margin: 0;
}

.chat-page__conversation-header p {
  color: var(--color-text-muted);
  margin-bottom: 0.4rem;
}

@media (max-width: 960px) {
  .chat-page__hero {
    flex-direction: column;
    align-items: start;
  }

  .chat-page__grid {
    grid-template-columns: 1fr;
  }
}
</style>
