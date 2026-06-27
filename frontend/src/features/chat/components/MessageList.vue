<template>
  <section class="message-list" aria-live="polite">
    <article
      v-for="message in messages"
      :key="message.id"
      class="message-list__item"
      :data-owned="isOwnMessage(message)"
      :data-system="message.messageType === 'SYSTEM'"
    >
      <template v-if="message.messageType === 'SYSTEM'">
        <p class="message-list__system">{{ message.content }}</p>
      </template>

      <template v-else>
        <div class="message-list__meta">
          <strong>{{ isOwnMessage(message) ? 'You' : message.senderDisplayName }}</strong>
          <time :datetime="message.sentAt">{{ formatTime(message.sentAt) }}</time>
        </div>

        <div class="message-list__bubble">
          <p>{{ message.content }}</p>
        </div>
      </template>
    </article>
  </section>
</template>

<script setup lang="ts">
import type { ChatMessage } from '../types/chat'

const props = defineProps<{
  messages: ChatMessage[]
  currentUserId?: string | null
}>()

function formatTime(value: string) {
  return new Intl.DateTimeFormat('en', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(new Date(value))
}

function isOwnMessage(message: ChatMessage) {
  return Boolean(props.currentUserId && message.senderId === props.currentUserId)
}
</script>

<style scoped>
.message-list {
  display: flex;
  flex-direction: column;
  gap: 0.9rem;
  min-height: 0;
  overflow-y: auto;
  padding-right: 0.25rem;
}

.message-list__item {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  max-width: min(78%, 42rem);
}

.message-list__item[data-owned='true'] {
  align-self: flex-end;
}

.message-list__item[data-system='true'] {
  align-self: center;
  max-width: 30rem;
}

.message-list__meta {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  color: var(--color-text-muted);
}

.message-list__item[data-owned='true'] .message-list__meta {
  justify-content: flex-end;
}

.message-list__meta strong,
.message-list__meta time,
.message-list__bubble p,
.message-list__system {
  margin: 0;
}

.message-list__meta strong {
  font-size: 0.84rem;
}

.message-list__meta time {
  font-size: 0.76rem;
}

.message-list__bubble {
  padding: 0.9rem 1rem;
  border-radius: 1.1rem;
  border: 1px solid var(--color-border);
  background: var(--color-surface);
  box-shadow: var(--shadow-edge);
}

.message-list__item[data-owned='true'] .message-list__bubble {
  background: var(--color-accent-soft);
  border-color: rgba(37, 99, 235, 0.16);
}

.message-list__bubble p {
  white-space: pre-wrap;
}

.message-list__system {
  padding: 0.5rem 0.75rem;
  border-radius: 999px;
  background: var(--color-surface-muted);
  border: 1px solid var(--color-border);
  color: var(--color-text-muted);
  text-align: center;
  font-size: 0.88rem;
}

@media (max-width: 640px) {
  .message-list__item {
    max-width: 100%;
  }
}
</style>
