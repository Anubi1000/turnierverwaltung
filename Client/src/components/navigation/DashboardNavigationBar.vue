<script setup lang="ts">
import LinkButton from "@/components/LinkButton.vue";
import NavigationItem from "@/components/navigation/NavigationItem.vue";
import { strings } from "@/utils/strings.ts";
import type { NavigationBarItem } from "@/utils/types.ts";
import type { RouteLocationRaw } from "vue-router";
import ArrowBack from "~icons/material-symbols/arrow-back";

defineProps<{
  items: NavigationBarItem[];
  selectedItemIndex: number;
  backLocation: RouteLocationRaw;
}>();
</script>

<template>
  <div class="flex min-w-64 flex-col gap-2 overflow-y-auto">
    <RouterLink v-for="(item, index) in items" :key="index" :to="item.to">
      <NavigationItem :selected="index == selectedItemIndex">
        <component :is="item.icon" />
        {{ item.label }}
      </NavigationItem>
    </RouterLink>

    <LinkButton
      class="mx-2 mt-auto mb-1"
      :label="strings.actions.back"
      :icon="ArrowBack"
      :to="backLocation"
      variant="outlined"
      full-width
    />
  </div>
</template>
