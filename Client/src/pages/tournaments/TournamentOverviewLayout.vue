<script setup lang="ts">
import ScreenContainer from "@/components/layout/ScreenContainer.vue";
import DashboardNavigationBar from "@/components/navigation/DashboardNavigationBar.vue";
import { LayoutNames, RouteNames } from "@/utils/routes.ts";
import { strings } from "@/utils/strings.ts";
import type { NavigationBarItem } from "@/utils/types.ts";
import { getIdFromRoute, useRouterViewKey } from "@/utils/utils.ts";
import { computed } from "vue";
import { useRoute } from "vue-router";
import AddBox from "~icons/material-symbols/add-box";
import Dashboard from "~icons/material-symbols/dashboard";
import Group from "~icons/material-symbols/group";
import Groups from "~icons/material-symbols/groups";
import LibraryAdd from "~icons/material-symbols/library-add";
import Person from "~icons/material-symbols/person";

const route = useRoute();
const tournamentId = getIdFromRoute("tournamentId");

const navigationItems: NavigationBarItem[] = [
  {
    label: strings.overview,
    icon: Dashboard,
    to: {
      name: RouteNames.TOURNAMENT_SCORES,
      params: { tournamentId: tournamentId },
    },
  },
  {
    label: strings.club.items,
    icon: Groups,
    to: {
      name: RouteNames.CLUB_LIST,
      params: { tournamentId: tournamentId },
    },
  },
  {
    label: strings.participant.items,
    icon: Person,
    to: {
      name: RouteNames.PARTICIPANT_LIST,
      params: { tournamentId: tournamentId },
    },
  },
  {
    label: strings.team.items,
    icon: Group,
    to: {
      name: RouteNames.TEAM_LIST,
      params: { tournamentId: tournamentId },
    },
  },
  {
    label: strings.discipline.items,
    icon: AddBox,
    to: {
      name: RouteNames.DISCIPLINE_LIST,
      params: { tournamentId: tournamentId },
    },
  },
  {
    label: strings.teamDiscipline.items,
    icon: LibraryAdd,
    to: {
      name: RouteNames.TEAM_DISCIPLINE_LIST,
      params: { tournamentId: tournamentId },
    },
  },
];

const selectedItemIndex = computed(() => {
  switch (route.matched[1].name) {
    case RouteNames.TOURNAMENT_SCORES:
      return 0;
    case LayoutNames.CLUB_LIST:
      return 1;
    case LayoutNames.PARTICIPANT_LIST:
      return 2;
    case LayoutNames.TEAM_LIST:
      return 3;
    case LayoutNames.DISCIPLINE_LIST:
      return 4;
    case LayoutNames.TEAM_DISCIPLINE_LIST:
      return 5;
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
        name: RouteNames.TOURNAMENT_DETAIL,
        params: { tournamentId: tournamentId },
      }"
    />

    <RouterView :key="routerViewKey" />
  </ScreenContainer>
</template>
