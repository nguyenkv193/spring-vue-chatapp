<template>
  <aside class="conversation-list">
    <header class="conversation-list__header">
      <p>Inbox</p>
      <span>{{ conversations.length }}</span>
    </header>

    <button
      v-for="conversation in conversations"
      :key="conversation.id"
      class="conversation-list__item"
      :class="{ 'conversation-list__item--active': conversation.id === selectedConversationId }"
      type="button"
      @click="$emit('select', conversation.id)"
    >
      <strong>{{ conversation.title || fallbackTitle(conversation.id) }}</strong>
      <small>{{ conversation.lastMessagePreview || 'No messages yet' }}</small>
    </button>
  </aside>
</template>

<script setup lang="ts">
import type { ConversationSummary } from '../types/chat'

defineProps<{
  conversations: ConversationSummary[]
  selectedConversationId: string | null
}>()

defineEmits<{
  select: [conversationId: string]
}>()

function fallbackTitle(conversationId: string) {
  return `Conversation ${conversationId.slice(0, 8)}`
}
</script>

<style scoped>
.conversation-list {
  display: grid;
  gap: 0.75rem;
}

.conversation-list__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: var(--color-text-muted);
}

.conversation-list__header p,
.conversation-list__header span {
  margin: 0;
}

.conversation-list__item {
  display: grid;
  gap: 0.35rem;
  text-align: left;
  padding: 0.95rem 1rem;
  border-radius: 1rem;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.04);
  color: inherit;
  cursor: pointer;
}

.conversation-list__item strong {
  font-size: 0.95rem;
}

.conversation-list__item small {
  color: var(--color-text-muted);
}

.conversation-list__item--active {
  border-color: rgba(74, 222, 196, 0.55);
  background: rgba(74, 222, 196, 0.1);
}
</style>
