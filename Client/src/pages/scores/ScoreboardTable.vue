<script setup lang="ts">
import type { ScoreboardDataTable } from "@/utils/api/schemas";
import { useDynamicStyles } from "@/utils/composables/useDynamicStyles.ts";
import { getTableStyles } from "@/utils/scoreboardTable.ts";
import { computed, useId } from "vue";

const { table } = defineProps<{
  table: ScoreboardDataTable;
}>();

const instanceId = `score-table-${useId()}`;

const tableStyles = computed(() => getTableStyles(table.columns, instanceId));
useDynamicStyles(tableStyles);
</script>

<template>
  <table
    :id="instanceId"
    class="flex w-full table-fixed flex-col overflow-y-auto pr-2 pb-2"
  >
    <thead class="sticky top-0 flex w-full bg-surface-50">
      <tr class="font-bold">
        <td v-for="(column, index) in table.columns" :key="index">
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
