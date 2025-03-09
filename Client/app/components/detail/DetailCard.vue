<script setup lang="ts">
const props = defineProps<{
  title: string;
}>();
</script>

<template>
  <Card class="h-fit min-w-80">
    <template #title>
      <p class="select-none">
        {{ props.title }}
      </p>
    </template>
    <template #content>
      <slot />
    </template>
  </Card>
</template>
