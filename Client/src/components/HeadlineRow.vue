<script setup lang="ts">
defineProps<{
  title: string;
}>();
</script>

<template>
  <div class="mb-s mr-2 ml-2 flex min-h-12 items-center justify-between">
    <h2 class="text-2xl font-semibold select-none">{{ title }}</h2>
    <div v-if="$slots.actions" class="space-x-1">
      <slot name="actions" />
    </div>
  </div>
</template>
