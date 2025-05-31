<script setup lang="ts">
import LoadingMessage from "@/components/messages/LoadingMessage.vue";
import StatusMessage from "@/components/messages/StatusMessage.vue";
import TeamDisciplineEditor from "@/pages/teamDisciplines/TeamDisciplineEditor.vue";
import {
  getGetTeamDisciplineQueryKey,
  getGetTeamDisciplinesQueryKey,
  useGetTeamDiscipline,
  useUpdateTeamDiscipline,
} from "@/utils/api/api.ts";
import type { TeamDisciplineEditDto } from "@/utils/api/schemas";
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
const teamDisciplineId = getIdFromRoute("teamDisciplineId");

const { data, isPending, isError } = useGetTeamDiscipline(teamDisciplineId);

useSeoMeta({
  title: computed(
    () =>
      `${strings.teamDiscipline.edit} - ${data.value?.data.name ?? strings.loading}`,
  ),
});

const initialValues = computed<TeamDisciplineEditDto | undefined>(() => {
  const raw = data.value?.data;
  if (!raw) return undefined;
  return {
    name: raw.name,
    displayType: raw.displayType,
    basedOn: raw.basedOn.map((entry) => entry.id),
  };
});

const editTeamDiscipline = useUpdateTeamDiscipline();

async function onSubmit(data: TeamDisciplineEditDto) {
  await editTeamDiscipline.mutateAsync({
    teamDisciplineId: teamDisciplineId,
    data: data,
  });

  await Promise.all([
    router.push({
      name: RouteNames.TEAM_DISCIPLINE_DETAIL,
      params: {
        tournamentId: tournamentId,
        teamDisciplineId: teamDisciplineId,
      },
    }),
    queryClient.refetchQueries({
      queryKey: getGetTeamDisciplinesQueryKey(tournamentId),
    }),
    queryClient.refetchQueries({
      queryKey: getGetTeamDisciplineQueryKey(teamDisciplineId),
    }),
  ]);
}
</script>

<template>
  <LoadingMessage v-if="isPending" />

  <StatusMessage
    v-else-if="isError || !initialValues"
    severity="error"
    :message="strings.teamDiscipline.loadingError"
  />

  <TeamDisciplineEditor
    v-else
    :initial-values="initialValues"
    edit
    @submit="onSubmit"
  />
</template>
