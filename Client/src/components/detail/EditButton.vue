<script setup lang="ts">
import { strings } from "@/utils/strings.ts";
import Button from "primevue/button";
import type { RouteLocationRaw } from "vue-router";
import Edit from "~icons/material-symbols/edit";

defineProps<{
  link: RouteLocationRaw;
}>();
</script>

<template>
  <RouterLink :to="link">
    <Button :label="strings.edit">
      <template #icon>
        <Edit />
      </template>
    </Button>
  </RouterLink>
</template>
