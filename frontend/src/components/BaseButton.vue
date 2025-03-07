<script setup>
defineProps({
  variant: {
    type: String,
    default: 'primary', // primary | outline
  }
})
</script>

<template>
  <button
      :class="['base-button', variant]"
      @click="$emit('click')"
  >
    <slot />
  </button>
</template>

<style scoped>
.base-button {
  padding: 0.8rem 1.5rem;
  border-radius: 0.5rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.primary {
  background: var(--color-primary);
  color: white;
  border: none;
}

.primary:hover {
  background: #76cba8;
  transform: translateY(-1px);
}

.outline {
  background: transparent;
  border: 2px solid var(--color-primary);
  color: var(--color-primary);
}

.outline:hover {
  background: rgba(136, 216, 176, 0.1);
}
</style>