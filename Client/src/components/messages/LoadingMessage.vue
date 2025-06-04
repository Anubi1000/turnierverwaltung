<script setup lang="ts">
import StatusMessage from "@/components/messages/StatusMessage.vue";
import { strings } from "@/utils/strings.ts";
</script>

<template>
  <StatusMessage severity="secondary" :message="strings.status.loading" />
</template>
