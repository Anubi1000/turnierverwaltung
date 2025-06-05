<script setup lang="ts">
import ScoreDownloadDialog from "./ScoreDownloadDialog.vue";
import HeadlineRow from "@/components/HeadlineRow.vue";
import ItemList from "@/components/itemlist/ItemList.vue";
import LoadingMessage from "@/components/messages/LoadingMessage.vue";
import StatusMessage from "@/components/messages/StatusMessage.vue";
import ScoreboardTable from "@/pages/scores/ScoreboardTable.vue";
import {
  setScoreboardTournament,
  useGetTournamentOverview,
} from "@/utils/api/api.ts";
import { strings } from "@/utils/strings.ts";
import type { ItemListItem } from "@/utils/types";
import { getIdFromRoute } from "@/utils/utils";
import { useSeoMeta } from "@unhead/vue";
import Button from "primevue/button";
import { computed, ref, watch } from "vue";
import Save from "~icons/material-symbols/save";
import Scoreboard from "~icons/material-symbols/scoreboard";

// Fetch overview data
const tournamentId = getIdFromRoute("tournamentId");
const { data, isPending, isError } = useGetTournamentOverview(tournamentId);

// Map tables to list items
const mappedItems = computed<ItemListItem[] | undefined>(() => {
  const tables = data.value?.data.tables;
  if (!tables) return undefined;

  return tables.map<ItemListItem>((item) => ({
    id: item.id.toString(),
    title: item.name,
  }));
});

// Set first table as selected if none selected and tables available
const selectedTableId = ref<string | undefined>();
watch(data, (newData) => {
  if (
    selectedTableId.value ||
    !newData?.data ||
    newData.data.tables.length === 0
  )
    return;
  selectedTableId.value = newData.data.tables[0].id;
});

// Compute selected table
const selectedTable = computed(() =>
  data.value?.data.tables.find((item) => item.id === selectedTableId.value),
);

// Update title depending on selected table
useSeoMeta({
  title: computed(() => {
    let title = strings.overview;
    if (selectedTable.value) title += ` - ${selectedTable.value.name}`;
    return title;
  }),
});

const showDownloadDialog = ref(false);

async function showTournamentOnScoreboard() {
  await setScoreboardTournament(tournamentId, {
    headers: {
      "Content-Type": "application/json",
    },
  });
}
</script>

<template>
  <ItemList
    :is-loading="isPending"
    :is-error="isError"
    :items="mappedItems"
    :selected-item-id="selectedTableId"
    :use-router-link="false"
    @itemClick="(item) => (selectedTableId = item.id)"
  >
    <template #actionButton>
      <Button :label="strings.actions.save" @click="showDownloadDialog = true">
        <template #icon>
          <Save />
        </template>
      </Button>

      <Button
        severity="secondary"
        :label="strings.scores.showOnScoreboard"
        @click="showTournamentOnScoreboard"
      >
        <template #icon>
          <Scoreboard />
        </template>
      </Button>
    </template>
  </ItemList>

  <LoadingMessage v-if="isPending" />

  <StatusMessage
    v-else-if="isError || !data"
    severity="error"
    :message="strings.status.overviewLoadingFailed"
  />

  <StatusMessage
    v-else-if="!selectedTable"
    severity="secondary"
    :message="strings.discipline.selectOne"
  />

  <div v-else class="flex flex-grow flex-col pl-4">
    <ScoreDownloadDialog
      v-model:visible="showDownloadDialog"
      :data="data.data"
      :tournament-id="tournamentId"
    />

    <HeadlineRow :title="selectedTable.name" />

    <StatusMessage
      v-if="selectedTable.rows.length === 0"
      severity="info"
      :message="strings.status.noEntriesAvailable"
    />

    <ScoreboardTable v-else :table="selectedTable" />
  </div>
</template>
