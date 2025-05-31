<script setup lang="ts">
import CreateButton from "@/components/itemlist/CreateButton.vue";
import ItemList from "@/components/itemlist/ItemList.vue";
import { useGetTournaments } from "@/utils/api/api.ts";
import { RouteNames } from "@/utils/routes.ts";
import { strings } from "@/utils/strings.ts";
import type { ItemListItem } from "@/utils/types.ts";
import { useRouterViewKey } from "@/utils/utils.ts";
import { computed } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const { data, isPending, isError } = useGetTournaments();

const mappedItems = computed<ItemListItem[] | undefined>(() => {
  const rawItems = data.value?.data;
  if (!rawItems) return undefined;

  return rawItems.map<ItemListItem>((item) => ({
    id: item.id.toString(),
    title: item.name,
    content: strings.formatting.date(item.date),
    link: {
      name: RouteNames.TOURNAMENT_DETAIL,
      params: { tournamentId: item.id },
    },
  }));
});

const selectedTournament = computed(() => {
  const id = route.params.tournamentId;
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
    :selected-item="selectedTournament"
  >
    <template #actionButton>
      <CreateButton
        :label="strings.tournament.create"
        :link="{ name: RouteNames.TOURNAMENT_CREATE }"
      />
    </template>
  </ItemList>

  <div class="flex flex-grow pl-4">
    <RouterView :key="routerViewKey" />
  </div>
</template>
