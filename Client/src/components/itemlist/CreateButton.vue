<script setup lang="ts">
import Button from "primevue/button";
import type { RouteLocationRaw } from "vue-router";
import Add from "~icons/material-symbols/add-2";

defineProps<{
  link: RouteLocationRaw;
  label: string;
}>();
</script>

<template>
  <RouterLink :to="link">
    <Button class="w-full" :label="label">
      <template #icon>
        <Add />
      </template>
    </Button>
  </RouterLink>
</template>
