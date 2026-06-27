<template>
  <div class="auth-layout">
    <div class="auth-layout__orb auth-layout__orb--left"></div>
    <div class="auth-layout__orb auth-layout__orb--right"></div>
    <main class="auth-layout__card">
      <slot />
    </main>
  </div>
</template>

<style scoped>
.auth-layout {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 1.5rem;
  position: relative;
  overflow: hidden;
}

.auth-layout__card {
  width: min(100%, 620px);
  padding: clamp(1.5rem, 4vw, 2.5rem);
  border-radius: 1.75rem;
  background: rgba(11, 18, 32, 0.76);
  border: 1px solid rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(24px);
  position: relative;
  z-index: 1;
}

.auth-layout__orb {
  position: absolute;
  width: 20rem;
  aspect-ratio: 1;
  border-radius: 50%;
  filter: blur(40px);
  opacity: 0.28;
}

.auth-layout__orb--left {
  background: #4adec4;
  top: -4rem;
  left: -4rem;
}

.auth-layout__orb--right {
  background: #f97316;
  bottom: -6rem;
  right: -3rem;
}
</style>
