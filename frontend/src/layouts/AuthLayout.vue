<template>
  <div class="auth-layout">
    <div class="auth-layout__glow"></div>

    <div class="auth-layout__frame">
      <header class="auth-layout__masthead">
        <div class="auth-layout__brand">
          <div class="auth-layout__mark">CA</div>
          <div>
            <strong>Chat app</strong>
            <small>Local development workspace</small>
          </div>
        </div>
      </header>

      <main class="auth-layout__card">
        <slot />
      </main>
    </div>
  </div>
</template>

<style scoped>
.auth-layout {
  min-height: 100dvh;
  display: grid;
  place-items: center;
  padding: clamp(1rem, 2.5vw, 2rem);
  position: relative;
  overflow: hidden;
}

.auth-layout__glow {
  position: absolute;
  inset: auto auto -14rem -12rem;
  width: 32rem;
  aspect-ratio: 1;
  border-radius: 50%;
  background: rgba(37, 99, 235, 0.12);
  filter: blur(52px);
}

.auth-layout__frame {
  width: min(1040px, 100%);
  position: relative;
  z-index: 1;
  display: grid;
  gap: 0.9rem;
}

.auth-layout__masthead {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.auth-layout__brand {
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.auth-layout__mark {
  display: grid;
  place-items: center;
  width: 2.5rem;
  aspect-ratio: 1;
  border-radius: 0.9rem;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid var(--color-border);
  color: var(--color-accent-ink);
  font-size: 0.8rem;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.auth-layout__brand strong,
.auth-layout__brand small {
  display: block;
  margin: 0;
}

.auth-layout__brand small {
  color: var(--color-text-muted);
}

.auth-layout__card {
  width: 100%;
  padding: clamp(1.1rem, 3.5vw, 1.75rem);
  border-radius: 1.5rem;
  background: rgba(255, 255, 255, 0.86);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-soft), var(--shadow-edge);
}
</style>
