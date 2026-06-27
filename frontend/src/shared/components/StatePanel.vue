<template>
  <section class="state-panel" :data-tone="tone">
    <p class="state-panel__eyebrow">{{ eyebrow }}</p>
    <h2 class="state-panel__title">{{ title }}</h2>
    <p class="state-panel__body">{{ body }}</p>
    <button
      v-if="actionLabel"
      class="state-panel__action"
      type="button"
      @click="$emit('action')"
    >
      {{ actionLabel }}
    </button>
  </section>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    eyebrow: string
    title: string
    body: string
    actionLabel?: string
    tone?: 'neutral' | 'error'
  }>(),
  {
    actionLabel: undefined,
    tone: 'neutral'
  }
)

defineEmits<{
  action: []
}>()
</script>

<style scoped>
.state-panel {
  display: grid;
  gap: 0.75rem;
  padding: 1.5rem;
  border-radius: 1.25rem;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(11, 18, 32, 0.7);
  backdrop-filter: blur(20px);
}

.state-panel[data-tone='error'] {
  border-color: rgba(255, 106, 92, 0.35);
}

.state-panel__eyebrow {
  margin: 0;
  font-size: 0.78rem;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: var(--color-accent);
}

.state-panel__title {
  margin: 0;
  font-size: 1.4rem;
}

.state-panel__body {
  margin: 0;
  color: var(--color-text-muted);
}

.state-panel__action {
  justify-self: start;
  border: none;
  border-radius: 999px;
  background: linear-gradient(135deg, var(--color-accent), var(--color-accent-strong));
  color: var(--color-surface-strong);
  padding: 0.8rem 1.1rem;
  font-weight: 700;
  cursor: pointer;
}
</style>
