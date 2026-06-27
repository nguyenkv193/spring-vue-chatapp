<template>
  <aside class="conversation-list">
    <div class="conversation-list__items">
      <button
        v-for="conversation in conversations"
        :key="conversation.id"
        class="conversation-list__item"
        :class="{ 'conversation-list__item--active': conversation.id === selectedConversationId }"
        type="button"
        @click="$emit('select', conversation.id)"
      >
        <span class="conversation-list__avatar">
          {{ initials(conversation.title || fallbackTitle(conversation.id)) }}
        </span>

        <div class="conversation-list__body">
          <div class="conversation-list__row">
            <strong>{{ conversation.title || fallbackTitle(conversation.id) }}</strong>
            <time :datetime="conversation.updatedAt">{{ formatUpdatedAt(conversation.updatedAt) }}</time>
          </div>

          <small>{{ conversation.lastMessagePreview || 'No messages yet' }}</small>
          <span class="conversation-list__type">{{ formatConversationType(conversation.type) }}</span>
        </div>
      </button>
    </div>
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

function formatUpdatedAt(value: string) {
  return new Intl.DateTimeFormat('en', {
    month: 'short',
    day: 'numeric'
  }).format(new Date(value))
}

function formatConversationType(type: ConversationSummary['type']) {
  return type === 'DIRECT' ? 'Direct' : 'Group'
}

function initials(value: string) {
  return value
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase() ?? '')
    .join('')
}
</script>

<style scoped>
.conversation-list {
  min-height: 0;
}

.conversation-list__items {
  display: grid;
  gap: 0.6rem;
  min-height: 0;
  overflow-y: auto;
  padding-right: 0.1rem;
}

.conversation-list__item {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  gap: 0.8rem;
  align-items: start;
  text-align: left;
  padding: 0.9rem;
  border-radius: 1rem;
  border: 1px solid transparent;
  background: transparent;
}

.conversation-list__item:hover {
  background: rgba(255, 255, 255, 0.78);
  border-color: var(--color-border);
}

.conversation-list__item--active {
  background: var(--color-surface);
  border-color: rgba(37, 99, 235, 0.18);
  box-shadow: var(--shadow-edge);
}

.conversation-list__avatar {
  display: grid;
  place-items: center;
  width: 2.45rem;
  aspect-ratio: 1;
  border-radius: 0.85rem;
  background: var(--color-accent-soft);
  color: var(--color-accent-ink);
  font-size: 0.78rem;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.conversation-list__body {
  display: grid;
  gap: 0.35rem;
  min-width: 0;
}

.conversation-list__row {
  display: flex;
  align-items: start;
  justify-content: space-between;
  gap: 0.75rem;
}

.conversation-list__row strong,
.conversation-list__row time,
.conversation-list__body small,
.conversation-list__type {
  margin: 0;
}

.conversation-list__row strong {
  font-size: 0.95rem;
  line-height: 1.3;
}

.conversation-list__row time,
.conversation-list__body small,
.conversation-list__type {
  color: var(--color-text-muted);
}

.conversation-list__row time {
  font-size: 0.76rem;
  white-space: nowrap;
}

.conversation-list__body small {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.conversation-list__type {
  font-size: 0.72rem;
  font-weight: 600;
}
</style>
