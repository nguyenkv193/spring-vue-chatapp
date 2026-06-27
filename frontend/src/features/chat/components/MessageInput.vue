<template>
  <form class="message-input" @submit.prevent="submit">
    <textarea
      v-model="content"
      class="message-input__field"
      rows="3"
      :disabled="disabled"
      placeholder="Type a message to publish over STOMP..."
    />
    <button class="message-input__submit" type="submit" :disabled="disabled || !content.trim()">
      Send demo message
    </button>
  </form>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const props = defineProps<{
  disabled?: boolean
}>()

const emit = defineEmits<{
  send: [content: string]
}>()

const content = ref('')

function submit() {
  if (props.disabled || !content.value.trim()) {
    return
  }

  emit('send', content.value.trim())
  content.value = ''
}
</script>

<style scoped>
.message-input {
  display: grid;
  gap: 0.9rem;
}

.message-input__field {
  width: 100%;
  resize: vertical;
  min-height: 5rem;
  border-radius: 1rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.04);
  color: var(--color-text);
  padding: 1rem;
}

.message-input__submit {
  justify-self: end;
  border: none;
  border-radius: 999px;
  padding: 0.8rem 1.2rem;
  font-weight: 700;
  background: linear-gradient(135deg, var(--color-accent), var(--color-accent-strong));
  color: var(--color-surface-strong);
  cursor: pointer;
}

.message-input__submit:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}
</style>
