<script setup lang="ts">
defineProps<{
  label: string;
  content: string | number;
}>();
</script>

<template>
  <div class="flex flex-row items-center justify-between">
    <div>
      <div class="text-sm font-bold select-none">{{ label }}</div>
      <div>{{ content }}</div>
    </div>
    <div v-if="$slots.trailing">
      <slot name="trailing" />
    </div>
  </div>
</template>
