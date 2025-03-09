<script setup lang="ts">
definePageMeta({
  middleware: "auth",
  layout: "tournament-list",
});

useSeoMeta({
  title: "Turniere",
});
</script>

<template>
  <div class="m-auto">
    <StaticMessage severity="secondary" class="text-center">
      Kein Turnier ausgewählt
    </StaticMessage>
  </div>
</template>
