<template>
  <AppLayout>
    <section class="chat-page">
      <div class="chat-page__workspace">
        <aside class="chat-page__sidebar panel">
          <header class="chat-page__panel-header">
            <div>
              <p>Inbox</p>
              <small>Choose a conversation to load history and live updates.</small>
            </div>

            <span class="chat-page__status" :data-state="chatStore.connectionState">
              {{ connectionLabel }}
            </span>
          </header>

          <form class="chat-page__create-form" @submit.prevent="createNewConversation">
            <label class="chat-page__create-label" for="conversation-title">New conversation</label>

            <div class="chat-page__create-row">
              <input
                id="conversation-title"
                v-model.trim="newConversationTitle"
                class="chat-page__create-input"
                type="text"
                maxlength="160"
                placeholder="Product sync"
              />

              <button class="chat-page__create-button" type="submit" :disabled="chatStore.creatingConversation">
                {{ chatStore.creatingConversation ? 'Creating...' : 'Create' }}
              </button>
            </div>

            <p v-if="createConversationError" class="chat-page__create-error">
              {{ createConversationError }}
            </p>
          </form>

          <StatePanel
            v-if="chatStore.loadingConversations"
            eyebrow="Loading"
            title="Fetching conversations"
            body="The sidebar is waiting for the conversation list from the backend."
            tone="loading"
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
            body="Create one above, then the inbox and thread view will populate here."
            tone="empty"
          />

          <ConversationList
            v-else
            :conversations="chatStore.conversations"
            :selected-conversation-id="chatStore.selectedConversationId"
            @select="chatStore.selectConversation"
          />
        </aside>

        <section class="chat-page__main panel">
          <template v-if="!chatStore.selectedConversation">
            <StatePanel
              class="chat-page__placeholder"
              eyebrow="Start here"
              title="Create or select a conversation"
              body="Use the inbox panel to start a thread, then send messages from here."
              tone="empty"
            />
          </template>

          <template v-else>
            <header class="chat-page__thread-header">
              <div class="chat-page__thread-title">
                <p>{{ formatConversationType(chatStore.selectedConversation.type) }} conversation</p>
                <h1>{{ chatStore.selectedConversation.title || fallbackTitle }}</h1>
              </div>

              <div class="chat-page__thread-meta">
                <span class="chat-page__thread-badge">{{ messageCountLabel }}</span>
                <small>Updated {{ formatConversationUpdated(chatStore.selectedConversation.updatedAt) }}</small>
              </div>
            </header>

            <div class="chat-page__thread-body">
              <StatePanel
                v-if="chatStore.loadingMessages"
                eyebrow="Loading"
                title="Fetching messages"
                body="The message history is loading for this conversation."
                tone="loading"
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
                body="Send the first message once the realtime connection is live."
                tone="empty"
              />

              <MessageList
                v-else
                :messages="chatStore.messages"
                :current-user-id="authStore.user?.id ?? null"
              />
            </div>

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
import { computed, onMounted, ref } from 'vue'

import AppLayout from '@/layouts/AppLayout.vue'
import StatePanel from '@/shared/components/StatePanel.vue'
import { useAuthStore } from '@/features/auth/stores/useAuthStore'
import ConversationList from '../components/ConversationList.vue'
import MessageInput from '../components/MessageInput.vue'
import MessageList from '../components/MessageList.vue'
import { useChatStore } from '../stores/useChatStore'

const authStore = useAuthStore()
const chatStore = useChatStore()

const newConversationTitle = ref('')
const createConversationError = ref<string | null>(null)

const connectionLabel = computed(() => {
  switch (chatStore.connectionState) {
    case 'connected':
      return 'Live'
    case 'connecting':
      return 'Connecting'
    case 'error':
      return 'Error'
    default:
      return 'Offline'
  }
})

const messageCountLabel = computed(() => {
  const count = chatStore.messages.length
  return `${count} ${count === 1 ? 'message' : 'messages'}`
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

async function createNewConversation() {
  const title = newConversationTitle.value.trim() || 'New conversation'

  try {
    createConversationError.value = null
    await chatStore.createConversation(title)
    newConversationTitle.value = ''
  } catch (caughtError) {
    createConversationError.value =
      caughtError instanceof Error ? caughtError.message : 'Could not create conversation'
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

function formatConversationType(type: 'DIRECT' | 'GROUP') {
  return type === 'DIRECT' ? 'Direct' : 'Group'
}

function formatConversationUpdated(value: string) {
  return new Intl.DateTimeFormat('en', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(new Date(value))
}
</script>

<style scoped>
.chat-page {
  min-height: 0;
}

.chat-page__workspace {
  display: grid;
  grid-template-columns: minmax(280px, 340px) minmax(0, 1fr);
  gap: 1rem;
  min-height: clamp(36rem, calc(100dvh - 7.75rem), 50rem);
}

.panel {
  display: flex;
  flex-direction: column;
  gap: 0.9rem;
  min-height: 0;
  padding: 0.95rem;
  border-radius: 1.5rem;
  border: 1px solid var(--color-border);
  background: rgba(255, 255, 255, 0.84);
  box-shadow: var(--shadow-soft), var(--shadow-edge);
}

.chat-page__panel-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  padding-bottom: 0.8rem;
  border-bottom: 1px solid var(--color-border);
}

.chat-page__panel-header p,
.chat-page__panel-header small,
.chat-page__thread-title p,
.chat-page__thread-title h1,
.chat-page__thread-meta small,
.chat-page__create-label,
.chat-page__create-error {
  margin: 0;
}

.chat-page__panel-header p {
  font-size: 1rem;
  font-weight: 600;
}

.chat-page__panel-header small,
.chat-page__thread-title p,
.chat-page__thread-meta small,
.chat-page__create-label {
  color: var(--color-text-muted);
}

.chat-page__status {
  flex-shrink: 0;
  padding: 0.45rem 0.7rem;
  border-radius: 999px;
  border: 1px solid var(--color-border);
  background: var(--color-surface);
  font-size: 0.82rem;
  font-weight: 600;
}

.chat-page__status[data-state='connected'] {
  color: var(--color-success);
  background: var(--color-success-soft);
  border-color: rgba(2, 122, 72, 0.14);
}

.chat-page__status[data-state='error'] {
  color: var(--color-danger);
  background: var(--color-danger-soft);
  border-color: rgba(180, 35, 24, 0.14);
}

.chat-page__create-form {
  display: grid;
  gap: 0.6rem;
  padding: 0.85rem;
  border-radius: 1.1rem;
  background: var(--color-surface-muted);
  border: 1px solid var(--color-border);
}

.chat-page__create-label {
  font-size: 0.84rem;
  font-weight: 600;
}

.chat-page__create-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 0.6rem;
}

.chat-page__create-input {
  width: 100%;
  padding: 0.82rem 0.95rem;
  border-radius: 0.9rem;
  border: 1px solid var(--color-border);
  background: var(--color-surface);
}

.chat-page__create-button {
  padding: 0.82rem 1rem;
  border-radius: 0.9rem;
  background: var(--color-text);
  color: #ffffff;
  font-weight: 700;
}

.chat-page__create-button:hover:not(:disabled) {
  background: #1e293b;
  transform: translateY(-1px);
}

.chat-page__create-button:active:not(:disabled) {
  transform: translateY(1px);
}

.chat-page__create-button:disabled {
  opacity: 0.7;
  cursor: wait;
}

.chat-page__create-error {
  color: var(--color-danger);
  font-size: 0.84rem;
}

.chat-page__main {
  overflow: hidden;
}

.chat-page__placeholder {
  flex: 1;
}

.chat-page__thread-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  padding-bottom: 0.85rem;
  border-bottom: 1px solid var(--color-border);
}

.chat-page__thread-title {
  display: grid;
  gap: 0.3rem;
}

.chat-page__thread-title h1 {
  font-size: clamp(1.35rem, 2.2vw, 2rem);
}

.chat-page__thread-meta {
  display: grid;
  justify-items: end;
  gap: 0.35rem;
}

.chat-page__thread-badge {
  padding: 0.45rem 0.7rem;
  border-radius: 999px;
  background: var(--color-surface-muted);
  border: 1px solid var(--color-border);
  font-size: 0.8rem;
  font-weight: 600;
}

.chat-page__thread-body {
  flex: 1;
  min-height: 0;
  display: flex;
}

.chat-page__thread-body > * {
  flex: 1;
}

@media (max-width: 1024px) {
  .chat-page__workspace {
    grid-template-columns: 1fr;
    min-height: auto;
  }
}

@media (max-width: 640px) {
  .chat-page__thread-header,
  .chat-page__panel-header,
  .chat-page__create-row {
    grid-template-columns: 1fr;
    flex-direction: column;
    align-items: stretch;
  }

  .chat-page__thread-meta {
    justify-items: start;
  }
}
</style>
