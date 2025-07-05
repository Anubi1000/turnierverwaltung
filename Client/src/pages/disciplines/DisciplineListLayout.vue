<script setup lang="ts">
import HeaderBar from "@/components/HeaderBar.vue";
import CreateButton from "@/components/itemlist/CreateButton.vue";
import ItemList from "@/components/itemlist/ItemList.vue";
import MainContentContainer from "@/components/layout/MainContentContainer.vue";
import { useGetDisciplines } from "@/utils/api/api.ts";
import { RouteNames } from "@/utils/routes.ts";
import { strings } from "@/utils/strings.ts";
import type { ItemListItem } from "@/utils/types.ts";
import { getIdFromRoute, useRouterViewKey } from "@/utils/utils.ts";
import { computed } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const tournamentId = getIdFromRoute("tournamentId");

const { data, isPending, isError } = useGetDisciplines(tournamentId);

const mappedItems = computed<ItemListItem[] | undefined>(() => {
  const rawItems = data.value?.data;
  if (!rawItems) return undefined;

  return rawItems.map<ItemListItem>((item) => ({
    id: item.id.toString(),
    title: item.name,
    link: {
      name: RouteNames.DISCIPLINE_DETAIL,
      params: { tournamentId: tournamentId, disciplineId: item.id },
    },
  }));
});

const selectedItemId = computed(
  () => route.params.disciplineList as string | undefined,
);

const routerViewKey = useRouterViewKey(3);
</script>

<template>
  <MainContentContainer class="flex-col">
    <HeaderBar>
      <h2 class="px-2 text-2xl font-medium">{{ strings.discipline.items }}</h2>
    </HeaderBar>

    <MainContentContainer>
      <ItemList
        :is-loading="isPending"
        :is-error="isError"
        :items="mappedItems"
        :selected-item-id="selectedItemId"
      >
        <template #actionButton>
          <CreateButton
            :label="strings.discipline.create"
            :link="{
              name: RouteNames.DISCIPLINE_CREATE,
              params: { tournamentId: tournamentId },
            }"
          />
        </template>
      </ItemList>

      <RouterView :key="routerViewKey" />
    </MainContentContainer>
  </MainContentContainer>
</template>
