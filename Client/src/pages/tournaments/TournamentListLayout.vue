<script setup lang="ts">
import HeaderBar from "@/components/HeaderBar.vue";
import CreateButton from "@/components/itemlist/CreateButton.vue";
import ItemList from "@/components/itemlist/ItemList.vue";
import MainContentContainer from "@/components/layout/MainContentContainer.vue";
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

const selectedItemId = computed(
  () => route.params.tournamentId as string | undefined,
);

const routerViewKey = useRouterViewKey(3);
</script>

<template>
  <MainContentContainer class="flex-col">
    <HeaderBar>
      <h2 class="px-2 text-2xl font-medium">{{ strings.tournament.items }}</h2>

      <div class="#header_end"></div>
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
            :label="strings.tournament.create"
            :link="{ name: RouteNames.TOURNAMENT_CREATE }"
          />
        </template>
      </ItemList>

      <RouterView :key="routerViewKey" />
    </MainContentContainer>
  </MainContentContainer>
</template>
