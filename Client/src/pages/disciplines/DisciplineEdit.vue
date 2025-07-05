<script setup lang="ts">
import LoadingMessage from "@/components/messages/LoadingMessage.vue";
import StatusMessage from "@/components/messages/StatusMessage.vue";
import DisciplineEditor from "@/pages/disciplines/DisciplineEditor.vue";
import {
  getGetDisciplineQueryKey,
  getGetDisciplinesQueryKey,
  useGetDiscipline,
  useUpdateDiscipline,
} from "@/utils/api/api.ts";
import type { DisciplineEditDto } from "@/utils/api/schemas";
import { RouteNames } from "@/utils/routes.ts";
import { strings } from "@/utils/strings.ts";
import { getIdFromRoute } from "@/utils/utils.ts";
import { useQueryClient } from "@tanstack/vue-query";
import { useSeoMeta } from "@unhead/vue";
import { computed } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const queryClient = useQueryClient();

const tournamentId = getIdFromRoute("tournamentId");
const disciplineId = getIdFromRoute("disciplineId");

const { data, isPending, isError } = useGetDiscipline(disciplineId);

useSeoMeta({
  title: computed(
    () =>
      `${strings.discipline.edit} - ${data.value?.data.name ?? strings.status.loading}`,
  ),
});

const initialValues = computed<DisciplineEditDto | undefined>(() => {
  const raw = data.value?.data;
  if (!raw) return undefined;
  return {
    name: raw.name,
    amountOfBestRoundsToShow: raw.amountOfBestRoundsToShow,
    areGendersSeparated: raw.areGendersSeparated,
    showInResults: raw.showInResults,
    values: raw.values.map((value) => ({
      name: value.name,
      isAdded: value.isAdded,
    })),
  };
});

const editDiscipline = useUpdateDiscipline();

async function onSubmit(data: DisciplineEditDto) {
  await editDiscipline.mutateAsync({
    disciplineId: disciplineId,
    data: data,
  });

  await Promise.all([
    router.push({
      name: RouteNames.DISCIPLINE_DETAIL,
      params: { tournamentId: tournamentId, disciplineId: disciplineId },
    }),
    queryClient.refetchQueries({
      queryKey: getGetDisciplinesQueryKey(tournamentId),
    }),
    queryClient.refetchQueries({
      queryKey: getGetDisciplineQueryKey(disciplineId),
    }),
  ]);
}
</script>

<template>
  <LoadingMessage v-if="isPending" />

  <StatusMessage
    v-else-if="isError || !initialValues"
    severity="error"
    :message="strings.discipline.loadingError"
  />

  <DisciplineEditor
    v-else
    :initial-values="initialValues"
    edit
    @submit="onSubmit"
  />
</template>
