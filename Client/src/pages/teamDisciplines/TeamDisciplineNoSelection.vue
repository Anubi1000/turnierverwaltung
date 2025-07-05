<script setup lang="ts">
import StatusMessage from "@/components/messages/StatusMessage.vue";
import { strings } from "@/utils/strings.ts";
import { useSeoMeta } from "@unhead/vue";

useSeoMeta({
  title: strings.teamDiscipline.items,
});
</script>

<template>
  <StatusMessage
    severity="secondary"
    :message="strings.teamDiscipline.selectOne"
  />
</template>
