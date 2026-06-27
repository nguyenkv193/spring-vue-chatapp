<template>
  <form class="message-input" @submit.prevent="submit">
    <label class="message-input__field-wrap">
      <span class="message-input__label">Write a message</span>
      <textarea
        v-model="content"
        class="message-input__field"
        rows="3"
        :disabled="disabled"
        placeholder="Write a message to publish over STOMP..."
      />
    </label>

    <div class="message-input__footer">
      <p>
        {{
          disabled
            ? 'Select a conversation and reconnect before sending.'
            : 'The message is sent through the current STOMP session.'
        }}
      </p>

      <button class="message-input__submit" type="submit" :disabled="disabled || !content.trim()">
        Send
      </button>
    </div>
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
  gap: 0.8rem;
  padding-top: 0.1rem;
}

.message-input__field-wrap {
  display: grid;
  gap: 0.55rem;
}

.message-input__label,
.message-input__footer p {
  margin: 0;
  color: var(--color-text-muted);
}

.message-input__label {
  font-size: 0.84rem;
  font-weight: 600;
}

.message-input__field {
  width: 100%;
  min-height: 5rem;
  resize: vertical;
  padding: 0.95rem 1rem;
  border-radius: var(--radius-control);
  border: 1px solid var(--color-border);
  background: var(--color-surface);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.66);
}

.message-input__field:disabled {
  background: rgba(255, 255, 255, 0.6);
  cursor: not-allowed;
}

.message-input__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.message-input__submit {
  flex-shrink: 0;
  padding: 0.85rem 1.15rem;
  border-radius: 0.9rem;
  background: var(--color-text);
  color: #ffffff;
  font-weight: 700;
}

.message-input__submit:hover:not(:disabled) {
  background: #1e293b;
  transform: translateY(-1px);
}

.message-input__submit:active:not(:disabled) {
  transform: translateY(1px);
}

.message-input__submit:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

@media (max-width: 640px) {
  .message-input__footer {
    flex-direction: column;
    align-items: stretch;
  }

  .message-input__submit {
    width: 100%;
  }
}
</style>
