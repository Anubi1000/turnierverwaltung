<script setup lang="ts">
definePageMeta({
  middleware: "auth",
  layout: "club-list",
});

useSeoMeta({
  title: "Vereine",
});
</script>

<template>
  <div class="m-auto">
    <StaticMessage severity="secondary" class="text-center">
      Kein Verein ausgewählt
    </StaticMessage>
  </div>
</template>
