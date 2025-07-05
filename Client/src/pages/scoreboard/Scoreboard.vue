<script setup lang="ts">
import ScreenContainer from "@/components/layout/ScreenContainer.vue";
import Clock from "@/pages/scoreboard/Clock.vue";
import ScoreboardTable from "@/pages/scoreboard/ScoreboardTable.vue";
import { useScoreboardStore } from "@/utils/stores.ts";
import { useSeoMeta } from "@unhead/vue";
import { computed, onMounted, onUnmounted, ref } from "vue";

useSeoMeta({
  title: "Scoreboard",
});

const iconUrl = "/api/files/scoreboard_icon.png";
const showIcon = ref(true);

const scoreboardStore = useScoreboardStore();

onMounted(() => {
  scoreboardStore.connect();
});

onUnmounted(() => {
  scoreboardStore.disconnect();
});

const name = computed(
  () =>
    scoreboardStore.scoreboardData?.tournamentName ?? "Kein Turnier ausgewählt",
);

const tables = computed(() => scoreboardStore.scoreboardData?.tables ?? []);
const selectedTableIndex = ref(0);

const selectedTable = computed(() => {
  if (selectedTableIndex.value >= tables.value.length) return undefined;
  return tables.value[selectedTableIndex.value];
});

const tableNumber = ref(0);

function onScrollComplete() {
  if (!tables.value.length) return;

  selectedTableIndex.value =
    (selectedTableIndex.value + 1) % tables.value.length;
  tableNumber.value += 1;
}
</script>

<template>
  <ScreenContainer class="flex-col">
    <div
      class="flex items-center justify-between gap-2 bg-primary p-3 text-2xl font-bold text-surface-0"
    >
      <img
        v-if="showIcon"
        @error="showIcon = false"
        :src="iconUrl"
        alt="Icon"
        width="64"
      />
      <h1>
        {{ name }}
      </h1>
      <Clock class="ml-auto" />
    </div>

    <h2
      class="px-auto bg-primary text-center text-2xl font-bold text-surface-0"
      v-if="selectedTable"
    >
      {{ selectedTable.name }}
    </h2>

    <div
      class="m-auto text-2xl font-semibold"
      v-if="!tables || tables.length === 0"
    >
      Keine Disziplinen vorhanden
    </div>

    <ScoreboardTable
      v-else-if="selectedTable"
      :key="tableNumber"
      :table="selectedTable"
      @on-scroll-complete="onScrollComplete"
    />
  </ScreenContainer>
</template>
