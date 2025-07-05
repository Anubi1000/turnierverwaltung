<script setup lang="ts">
import Message from "@/components/primevue/Message.vue";

defineProps<{
  severity: "info" | "secondary" | "error";
  message: string;
}>();
</script>

<template>
  <Message class="m-auto text-center" :severity="severity">
    <span class="mx-auto">{{ message }}</span>
  </Message>
</template>
