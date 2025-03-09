<script setup lang="ts">
defineProps<{
  icon?: string;
}>();
</script>

<template>
  <Button>
    <template v-if="icon" #icon>
      <Icon :name="icon" />
    </template>
  </Button>
</template>
