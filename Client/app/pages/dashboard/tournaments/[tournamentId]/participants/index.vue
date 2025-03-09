<script setup lang="ts">
definePageMeta({
  middleware: "auth",
  layout: "participant-list",
});

useSeoMeta({
  title: "Teilnehmer",
});
</script>

<template>
  <div class="m-auto">
    <StaticMessage severity="secondary" class="text-center">
      Kein Teilnehmer ausgewählt
    </StaticMessage>
  </div>
</template>
