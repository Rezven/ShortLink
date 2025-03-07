<script setup>
import { RouterView } from 'vue-router'
</script>

<template>
  <div class="app-container">
    <RouterView />
  </div>
</template>

<style>
@import './assets/base.css';

.app-container {
  min-height: 100vh;
  padding: 2rem 1rem;
}
</style>