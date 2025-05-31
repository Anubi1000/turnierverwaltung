<script setup lang="ts">
import DashboardHeader from "@/components/DashboardHeader.vue";
import DashboardNavigationItem from "@/components/navigation/DashboardNavigationItem.vue";
import { useGetTournament } from "@/utils/api/api.ts";
import { LayoutNames, RouteNames } from "@/utils/routes.ts";
import { strings } from "@/utils/strings.ts";
import { getIdFromRoute, useRouterViewKey } from "@/utils/utils.ts";
import Button from "primevue/button";
import { computed } from "vue";
import { useRoute } from "vue-router";
import AddBox from "~icons/material-symbols/add-box";
import ArrowBack from "~icons/material-symbols/arrow-back";
import Group from "~icons/material-symbols/group";
import Groups from "~icons/material-symbols/groups";
import LibraryAdd from "~icons/material-symbols/library-add";
import Person from "~icons/material-symbols/person";

const route = useRoute();
const currentRoute = computed(() => route.matched[1].name);

const tournamentId = getIdFromRoute("tournamentId");

const tournamentQuery = useGetTournament(tournamentId);
const tournamentName = computed(
  () => tournamentQuery.data.value?.data.name ?? strings.loading,
);

const routerViewKey = useRouterViewKey(2);
</script>

<template>
  <div class="flex h-dvh w-dvw flex-col bg-surface-50">
    <DashboardHeader :title="tournamentName" />

    <div class="flex flex-grow flex-row overflow-y-hidden">
      <div class="flex min-w-64 flex-col gap-2 overflow-y-auto">
        <DashboardNavigationItem
          :link="{
            name: RouteNames.CLUB_LIST,
            params: { tournamentId: tournamentId },
          }"
          :selected="currentRoute == LayoutNames.CLUB_LIST"
        >
          <Groups />
          {{ strings.club.items }}
        </DashboardNavigationItem>

        <DashboardNavigationItem
          :link="{
            name: RouteNames.PARTICIPANT_LIST,
            params: { tournamentId: tournamentId },
          }"
          :selected="currentRoute == LayoutNames.PARTICIPANT_LIST"
        >
          <Person />
          {{ strings.participant.items }}
        </DashboardNavigationItem>

        <DashboardNavigationItem
          :link="{
            name: RouteNames.TEAM_LIST,
            params: { tournamentId: tournamentId },
          }"
          :selected="currentRoute == LayoutNames.TEAM_LIST"
        >
          <Group />
          {{ strings.team.items }}
        </DashboardNavigationItem>

        <DashboardNavigationItem
          :link="{
            name: RouteNames.DISCIPLINE_LIST,
            params: { tournamentId: tournamentId },
          }"
          :selected="currentRoute == LayoutNames.DISCIPLINE_LIST"
        >
          <AddBox />
          {{ strings.discipline.items }}
        </DashboardNavigationItem>

        <DashboardNavigationItem
          :link="{
            name: RouteNames.TEAM_DISCIPLINE_LIST,
            params: { tournamentId: tournamentId },
          }"
          :selected="currentRoute == LayoutNames.TEAM_DISCIPLINE_LIST"
        >
          <LibraryAdd />
          {{ strings.teamDiscipline.items }}
        </DashboardNavigationItem>

        <RouterLink
          class="mx-2 mt-auto mb-1"
          :to="{
            name: RouteNames.TOURNAMENT_DETAIL,
            params: { tournamentId: tournamentId },
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
