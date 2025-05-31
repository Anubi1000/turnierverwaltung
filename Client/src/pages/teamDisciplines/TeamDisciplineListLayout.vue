<script setup lang="ts">
import CreateButton from "@/components/itemlist/CreateButton.vue";
import ItemList from "@/components/itemlist/ItemList.vue";
import { useGetTeamDisciplines } from "@/utils/api/api.ts";
import { RouteNames } from "@/utils/routes.ts";
import { strings } from "@/utils/strings.ts";
import type { ItemListItem } from "@/utils/types.ts";
import { getIdFromRoute, useRouterViewKey } from "@/utils/utils.ts";
import { computed } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const tournamentId = getIdFromRoute("tournamentId");

const { data, isPending, isError } = useGetTeamDisciplines(tournamentId);

const mappedItems = computed<ItemListItem[] | undefined>(() => {
  const rawItems = data.value?.data;
  if (!rawItems) return undefined;

  return rawItems.map<ItemListItem>((item) => ({
    id: item.id.toString(),
    title: item.name,
    link: {
      name: RouteNames.TEAM_DISCIPLINE_DETAIL,
      params: { tournamentId: tournamentId, teamDisciplineId: item.id },
    },
  }));
});

const selectedTeamDiscipline = computed(() => {
  const id = route.params.teamDisciplineId;
  if (id) {
    return mappedItems.value?.find((item) => item.id === id);
  }
  return undefined;
});

const routerViewKey = useRouterViewKey(3);
</script>

<template>
  <ItemList
    :is-loading="isPending"
    :is-error="isError"
    :items="mappedItems"
    :selected-item="selectedTeamDiscipline"
  >
    <template #actionButton>
      <CreateButton
        :label="strings.teamDiscipline.create"
        :link="{
          name: RouteNames.TEAM_DISCIPLINE_CREATE,
          params: { tournamentId: tournamentId },
        }"
      />
    </template>
  </ItemList>

  <div class="flex flex-grow pl-4">
    <RouterView :key="routerViewKey" />
  </div>
</template>
