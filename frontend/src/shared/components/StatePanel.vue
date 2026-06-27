<template>
  <section class="state-panel" :data-tone="tone">
    <span class="state-panel__label">{{ eyebrow }}</span>
    <h2 class="state-panel__title">{{ title }}</h2>
    <p class="state-panel__body">{{ body }}</p>

    <div v-if="tone === 'loading'" class="state-panel__skeleton" aria-hidden="true">
      <span></span>
      <span></span>
      <span></span>
    </div>

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
    tone?: 'neutral' | 'empty' | 'loading' | 'error'
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
  gap: 0.8rem;
  align-content: start;
  width: 100%;
  padding: 1.2rem;
  border-radius: 1.2rem;
  border: 1px solid var(--color-border);
  background: var(--color-surface-muted);
  min-height: 100%;
}

.state-panel[data-tone='empty'] {
  background: rgba(37, 99, 235, 0.05);
}

.state-panel[data-tone='error'] {
  background: var(--color-danger-soft);
  border-color: rgba(180, 35, 24, 0.12);
}

.state-panel__label,
.state-panel__title,
.state-panel__body {
  margin: 0;
}

.state-panel__label {
  justify-self: start;
  padding: 0.38rem 0.62rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid var(--color-border);
  color: var(--color-text-muted);
  font-size: 0.78rem;
  font-weight: 600;
}

.state-panel__title {
  font-size: 1.35rem;
}

.state-panel__body {
  color: var(--color-text-muted);
  max-width: 46ch;
}

.state-panel__skeleton {
  display: grid;
  gap: 0.55rem;
}

.state-panel__skeleton span {
  display: block;
  height: 0.72rem;
  border-radius: 999px;
  background:
    linear-gradient(
      90deg,
      rgba(37, 99, 235, 0.08),
      rgba(37, 99, 235, 0.18),
      rgba(37, 99, 235, 0.08)
    );
  background-size: 200% 100%;
  animation: shimmer 1.6s linear infinite;
}

.state-panel__skeleton span:nth-child(1) {
  width: 78%;
}

.state-panel__skeleton span:nth-child(2) {
  width: 92%;
}

.state-panel__skeleton span:nth-child(3) {
  width: 64%;
}

.state-panel__action {
  justify-self: start;
  padding: 0.8rem 1rem;
  border-radius: 0.9rem;
  background: var(--color-text);
  color: #ffffff;
  font-weight: 600;
}

.state-panel__action:hover {
  background: #1e293b;
  transform: translateY(-1px);
}

.state-panel__action:active {
  transform: translateY(1px);
}
</style>
