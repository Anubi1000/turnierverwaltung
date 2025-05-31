<script setup lang="ts">
import DisciplineEditor from "@/pages/disciplines/DisciplineEditor.vue";
import {
  getGetDisciplineQueryOptions,
  getGetDisciplinesQueryKey,
  useCreateDiscipline,
} from "@/utils/api/api.ts";
import type { DisciplineEditDto } from "@/utils/api/schemas";
import { RouteNames } from "@/utils/routes.ts";
import { strings } from "@/utils/strings.ts";
import { getIdFromRoute } from "@/utils/utils.ts";
import { useQueryClient } from "@tanstack/vue-query";
import { useSeoMeta } from "@unhead/vue";
import { useRouter } from "vue-router";

const router = useRouter();
const queryClient = useQueryClient();

useSeoMeta({
  title: strings.discipline.create,
});

const tournamentId = getIdFromRoute("tournamentId");

const initialValues: DisciplineEditDto = {
  name: "",
  amountOfBestRoundsToShow: 1,
  areGendersSeparated: false,
  values: [
    {
      name: "",
      isAdded: true,
    },
  ],
};

const createDiscipline = useCreateDiscipline();

async function onSubmit(data: DisciplineEditDto) {
  const response = await createDiscipline.mutateAsync({
    tournamentId: tournamentId,
    data: data,
  });

  await Promise.all([
    router.push({
      name: RouteNames.DISCIPLINE_DETAIL,
      params: { tournamentId: tournamentId, disciplineId: response.data },
    }),
    queryClient.refetchQueries({
      queryKey: getGetDisciplinesQueryKey(tournamentId),
    }),
    queryClient.prefetchQuery(getGetDisciplineQueryOptions(response.data)),
  ]);
}
</script>

<template>
  <DisciplineEditor :initial-values="initialValues" @submit="onSubmit" />
</template>
