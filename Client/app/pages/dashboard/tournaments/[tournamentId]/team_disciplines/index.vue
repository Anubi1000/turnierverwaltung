<script setup lang="ts">
definePageMeta({
  middleware: "auth",
  layout: "team-discipline-list",
});

useSeoMeta({
  title: "Team-Disziplinen",
});
</script>

<template>
  <div class="m-auto">
    <StaticMessage severity="secondary" class="text-center">
      Keine Team-Disziplin ausgewählt
    </StaticMessage>
  </div>
</template>
