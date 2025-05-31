<script setup lang="ts">
import DashboardHeader from "@/components/DashboardHeader.vue";
import DashboardNavigationItem from "@/components/navigation/DashboardNavigationItem.vue";
import { LayoutNames, RouteNames } from "@/utils/routes.ts";
import { strings } from "@/utils/strings.ts";
import { useRouterViewKey } from "@/utils/utils.ts";
import Button from "primevue/button";
import { computed } from "vue";
import { useRoute } from "vue-router";
import ArrowBack from "~icons/material-symbols/arrow-back";
import Groups from "~icons/material-symbols/groups";

const route = useRoute();
const currentRoute = computed(() => route.matched[1].name);

const routerViewKey = useRouterViewKey(2);
</script>

<template>
  <div class="flex h-dvh w-dvw flex-col bg-surface-50">
    <DashboardHeader :title="strings.appName" />

    <div class="flex flex-grow flex-row overflow-y-hidden">
      <div class="flex min-w-64 flex-col gap-2 overflow-y-auto">
        <DashboardNavigationItem
          :link="{
            name: RouteNames.TOURNAMENT_LIST,
          }"
          :selected="currentRoute == LayoutNames.TOURNAMENT_LIST"
        >
          <Groups />
          {{ strings.tournament.items }}
        </DashboardNavigationItem>

        <RouterLink
          class="mx-2 mt-auto mb-1"
          :to="{
            name: RouteNames.ROOT,
          }"
        >
          <Button class="w-full" :label="strings.back" severity="secondary">
            <template #icon>
              <ArrowBack />
            </template>
          </Button>
        </RouterLink>
      </div>

      <RouterView :key="routerViewKey" />
    </div>
  </div>
</template>
