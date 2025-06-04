<script setup lang="ts">
import { strings } from "@/utils/strings.ts";
import Button from "primevue/button";
import type { RouteLocationRaw } from "vue-router";
import ArrowBack from "~icons/material-symbols/arrow-back";

defineProps<{
  to: RouteLocationRaw;
}>();
</script>

<template>
  <RouterLink class="mx-2 mt-auto mb-1" :to="to">
    <Button class="w-full" :label="strings.actions.back" severity="secondary">
      <template #icon>
        <ArrowBack />
      </template>
    </Button>
  </RouterLink>
</template>
