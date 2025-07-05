<script setup lang="ts">
import TeamDisciplineEditor from "@/pages/teamDisciplines/TeamDisciplineEditor.vue";
import {
  getGetTeamDisciplineQueryOptions,
  getGetTeamDisciplinesQueryKey,
  useCreateTeamDiscipline,
} from "@/utils/api/api.ts";
import type { TeamDisciplineEditDto } from "@/utils/api/schemas";
import { RouteNames } from "@/utils/routes.ts";
import { strings } from "@/utils/strings.ts";
import { getIdFromRoute } from "@/utils/utils.ts";
import { useQueryClient } from "@tanstack/vue-query";
import { useSeoMeta } from "@unhead/vue";
import { useRouter } from "vue-router";

const router = useRouter();
const queryClient = useQueryClient();

useSeoMeta({
  title: strings.teamDiscipline.create,
});

const tournamentId = getIdFromRoute("tournamentId");

const initialValues: TeamDisciplineEditDto = {
  name: "",
  displayType: "Normal",
  basedOn: [],
};

const createTeamDiscipline = useCreateTeamDiscipline();

async function onSubmit(data: TeamDisciplineEditDto) {
  const response = await createTeamDiscipline.mutateAsync({
    tournamentId: tournamentId,
    data: data,
  });

  await Promise.all([
    router.push({
      name: RouteNames.TEAM_DISCIPLINE_DETAIL,
      params: { tournamentId: tournamentId, teamDisciplineId: response.data },
    }),
    queryClient.refetchQueries({
      queryKey: getGetTeamDisciplinesQueryKey(tournamentId),
    }),
    queryClient.prefetchQuery(getGetTeamDisciplineQueryOptions(response.data)),
  ]);
}
</script>

<template>
  <TeamDisciplineEditor :initial-values="initialValues" @submit="onSubmit" />
</template>
