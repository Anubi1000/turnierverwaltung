<script setup lang="ts">
import Message from "@/components/primevue/Message.vue";

defineProps<{
  severity: "info" | "secondary" | "error";
  message: string;
}>();
</script>

<template>
  <Message class="m-auto text-center" :severity="severity">{{
    message
  }}</Message>
</template>
