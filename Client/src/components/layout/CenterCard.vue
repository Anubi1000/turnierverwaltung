<script setup lang="ts">
defineProps<{
  title: string;
}>();
</script>

<template>
  <div
    class="m-auto flex w-96 flex-col justify-center rounded-xl border border-surface-200 bg-surface-0 p-6"
  >
    <h1 class="mb-6 text-center text-3xl font-bold select-none">
      {{ title }}
    </h1>

    <slot />
  </div>
</template>
