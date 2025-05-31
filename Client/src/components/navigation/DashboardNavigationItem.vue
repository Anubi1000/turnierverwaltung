<script setup lang="ts">
import NavigationItem from "@/components/navigation/NavigationItem.vue";
import type { RouteLocationRaw } from "vue-router";

defineProps<{
  link: RouteLocationRaw;
  selected: boolean;
}>();
</script>

<template>
  <RouterLink :to="link">
    <NavigationItem :selected="selected">
      <slot />
    </NavigationItem>
  </RouterLink>
</template>
