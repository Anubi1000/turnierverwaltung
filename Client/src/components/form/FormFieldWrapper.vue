<script setup lang="ts">
import Message from "@/components/primevue/Message.vue";
import FloatLabel from "primevue/floatlabel";

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
