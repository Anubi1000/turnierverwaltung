<script setup lang="ts">
defineProps<{
  title: string;
}>();
</script>

<template>
  <div
    class="flex min-h-16 items-center p-2 text-3xl font-semibold select-none"
  >
    <h1>{{ title }}</h1>
  </div>
</template>
