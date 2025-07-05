<script setup lang="ts">
import Button from "primevue/button";
import type { RouteLocationRaw } from "vue-router";
import ChevronRight from "~icons/material-symbols/chevron-right";

defineProps<{
  to: RouteLocationRaw;
}>();
</script>

<template>
  <RouterLink :to="to">
    <Button text rounded>
      <template #icon>
        <ChevronRight />
      </template>
    </Button>
  </RouterLink>
</template>
