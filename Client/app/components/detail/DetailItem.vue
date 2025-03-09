<script setup lang="ts">
defineProps<{
  title: string;
  content: string;
}>();
</script>

<template>
  <ListItem>
    <template #title>
      {{ title }}
    </template>
    <template #content>
      {{ content }}
    </template>
  </ListItem>
</template>
