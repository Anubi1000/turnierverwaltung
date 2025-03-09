<script setup lang="ts">
defineProps<{
  id: string;
  label: string;
  error?: string;
}>();
</script>

<template>
  <div>
    <FloatLabel variant="in">
      <slot />
      <label :for="id">{{ label }}</label>
    </FloatLabel>
    <Message v-if="error" severity="error" variant="simple">
      {{ error }}
    </Message>
  </div>
</template>
