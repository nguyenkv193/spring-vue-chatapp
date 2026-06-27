<template>
  <section class="message-list">
    <article v-for="message in messages" :key="message.id" class="message-list__item">
      <header>
        <strong>{{ message.senderDisplayName }}</strong>
        <time>{{ formatTime(message.sentAt) }}</time>
      </header>
      <p>{{ message.content }}</p>
    </article>
  </section>
</template>

<script setup lang="ts">
import type { ChatMessage } from '../types/chat'

defineProps<{
  messages: ChatMessage[]
}>()

function formatTime(value: string) {
  return new Intl.DateTimeFormat('en', {
    hour: '2-digit',
    minute: '2-digit'
  }).format(new Date(value))
}
</script>

<style scoped>
.message-list {
  display: grid;
  gap: 0.9rem;
  align-content: start;
}

.message-list__item {
  padding: 1rem;
  border-radius: 1rem;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.message-list__item header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 0.55rem;
}

.message-list__item strong {
  font-size: 0.95rem;
}

.message-list__item time {
  color: var(--color-text-muted);
  font-size: 0.82rem;
}

.message-list__item p {
  margin: 0;
  color: var(--color-text);
  white-space: pre-wrap;
}
</style>
