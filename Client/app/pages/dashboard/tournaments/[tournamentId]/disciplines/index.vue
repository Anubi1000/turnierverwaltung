<script setup lang="ts">
definePageMeta({
  middleware: "auth",
  layout: "discipline-list",
});

useSeoMeta({
  title: "Disziplinen",
});
</script>

<template>
  <div class="m-auto">
    <StaticMessage severity="secondary" class="text-center">
      Keine Disziplin ausgewählt
    </StaticMessage>
  </div>
</template>
