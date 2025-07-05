<script setup lang="ts">
import ScreenContainer from "@/components/layout/ScreenContainer.vue";
import DashboardNavigationBar from "@/components/navigation/DashboardNavigationBar.vue";
import { LayoutNames, RouteNames } from "@/utils/routes.ts";
import { strings } from "@/utils/strings.ts";
import type { NavigationBarItem } from "@/utils/types.ts";
import { useRouterViewKey } from "@/utils/utils.ts";
import { computed } from "vue";
import { useRoute } from "vue-router";
import Groups from "~icons/material-symbols/groups";

const route = useRoute();

const navigationItems: NavigationBarItem[] = [
  {
    label: strings.tournament.items,
    icon: Groups,
    to: {
      name: RouteNames.TOURNAMENT_LIST,
    },
  },
];

const selectedItemIndex = computed(() => {
  switch (route.matched[1].name) {
    case LayoutNames.TOURNAMENT_LIST:
      return 0;
  }

  return -1;
});

const routerViewKey = useRouterViewKey(2);
</script>

<template>
  <ScreenContainer>
    <DashboardNavigationBar
      :title="strings.appName"
      :items="navigationItems"
      :selected-item-index="selectedItemIndex"
      :back-location="{
        name: RouteNames.ROOT,
      }"
    />

    <RouterView :key="routerViewKey" />
  </ScreenContainer>
</template>
