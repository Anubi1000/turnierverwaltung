<script setup lang="ts">
import { getTableStyles } from "@/pages/scoreboard/scoreboardTable.ts";
import type { ScoreboardDataTable } from "@/utils/api/schemas";
import { useAutoScroll } from "@/utils/composables/useAutoScroll.ts";
import { useDynamicStyles } from "@/utils/composables/useDynamicStyles.ts";
import { computed, type DeepReadonly, ref, useId } from "vue";

const { table } = defineProps<{
  table: DeepReadonly<ScoreboardDataTable>;
}>();

const emit = defineEmits<{
  (e: "onScrollComplete"): void;
}>();

const instanceId = `score-table-${useId()}`;
const tableRef = ref<HTMLElement | null>(null);

const tableStyles = computed(() => getTableStyles(table.columns, instanceId));
useDynamicStyles(tableStyles);

useAutoScroll(tableRef, {
  pxPerSecond: 30,
  iterations: 2,
  onComplete() {
    emit("onScrollComplete");
  },
});
</script>

<template>
  <table
    ref="tableRef"
    :id="instanceId"
    class="flex flex-1 flex-col overflow-y-hidden"
  >
    <thead class="sticky top-0">
      <tr class="bg-primary font-bold text-surface-0">
        <td
          v-for="(column, index) in table.columns"
          :key="index"
          class="px-2 py-4"
        >
          {{ column.name }}
        </td>
      </tr>
    </thead>

    <tbody>
      <tr v-for="(row, rowIndex) in table.rows" :key="rowIndex">
        <td v-for="(value, valueIndex) in row.values" :key="valueIndex">
          {{ value }}
        </td>
      </tr>
    </tbody>
  </table>
</template>
