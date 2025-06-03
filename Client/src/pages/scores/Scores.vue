<script setup lang="ts">
import HeadlineRow from "@/components/HeadlineRow.vue";
import ItemListRaw from "@/components/itemlist/ItemListRaw.vue";
import LoadingMessage from "@/components/messages/LoadingMessage.vue";
import StatusMessage from "@/components/messages/StatusMessage.vue";
import ScoreboardTable from "@/pages/scores/ScoreboardTable.vue";
import { useGetTournamentOverview } from "@/utils/api/api.ts";
import { strings } from "@/utils/strings.ts";
import type { ItemListItemRaw } from "@/utils/types";
import { getIdFromRoute } from "@/utils/utils";
import { computed, ref } from "vue";
import {useSeoMeta} from "@unhead/vue";

const tournamentId = getIdFromRoute("tournamentId");

const { data, isPending, isError } = useGetTournamentOverview(tournamentId);

const mappedItems = computed<ItemListItemRaw[] | undefined>(() => {
  const tables = data.value?.data.tables;
  if (!tables) return undefined;

  return tables.map<ItemListItemRaw>((item) => ({
    id: item.id.toString(),
    title: item.name,
  }));
});

const selectedTableId = ref<string | undefined>();

const selectedTableItem = computed(() => {
  const id = selectedTableId.value;
  if (id) {
    return mappedItems.value?.find((item) => item.id === id);
  }
  return undefined;
});

const selectedTable = computed(() => {
  const id = selectedTableId.value;
  if (id) {
    return data.value?.data.tables.find((item) => item.id === id);
  }
  return undefined;
});

useSeoMeta({
  title: computed(() => {
    let title = strings.overview
    if (selectedTable.value) title += ` - ${selectedTable.value.name}`
    return title;
  })
})

function onItemClick(item: ItemListItemRaw) {
  selectedTableId.value = item.id;
}
</script>

<template>
  <ItemListRaw
    :is-loading="isPending"
    :is-error="isError"
    :items="mappedItems"
    :selected-item="selectedTableItem"
    @itemClick="onItemClick"
  />

  <LoadingMessage v-if="isPending" />

  <StatusMessage
    v-else-if="isError || !data"
    severity="error"
    :message="strings.overviewLoadingFailed"
  />

  <StatusMessage
    v-else-if="!selectedTable"
    severity="secondary"
    :message="strings.discipline.selectOne"
  />

  <div v-else class="flex flex-grow flex-col pl-4">
    <HeadlineRow :title="selectedTable.name ?? strings.loading">
      <template #actions> </template>
    </HeadlineRow>

    <ScoreboardTable v-if="selectedTable" :table="selectedTable" />
  </div>
</template>
