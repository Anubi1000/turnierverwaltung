<script setup lang="ts">
import HeaderBar from "@/components/HeaderBar.vue";
import LinkButton from "@/components/LinkButton.vue";
import NavigationItem from "@/components/navigation/NavigationItem.vue";
import { strings } from "@/utils/strings.ts";
import type { NavigationBarItem } from "@/utils/types.ts";
import type { RouteLocationRaw } from "vue-router";
import ArrowBack from "~icons/material-symbols/arrow-back";

defineProps<{
  title: string;
  items: NavigationBarItem[];
  selectedItemIndex: number;
  backLocation: RouteLocationRaw;
}>();
</script>

<template>
  <div
    class="flex max-w-64 min-w-64 flex-col gap-2 overflow-y-auto border-r-1 border-r-surface-300 bg-surface-0"
  >
    <HeaderBar>
      <h1 class="px-2 text-[1.55rem] font-bold">{{ title }}</h1>
    </HeaderBar>

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
      variant="text"
      full-width
    />
  </div>
</template>
