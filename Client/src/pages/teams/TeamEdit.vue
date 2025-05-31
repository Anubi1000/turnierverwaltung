<script setup lang="ts">
import LoadingMessage from "@/components/messages/LoadingMessage.vue";
import StatusMessage from "@/components/messages/StatusMessage.vue";
import TeamEditor from "@/pages/teams/TeamEditor.vue";
import {
  getGetTeamQueryKey,
  getGetTeamsQueryKey,
  useGetTeam,
  useUpdateTeam,
} from "@/utils/api/api.ts";
import type { TeamEditDto } from "@/utils/api/schemas";
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
const teamId = getIdFromRoute("teamId");

const { data, isPending, isError } = useGetTeam(teamId);

useSeoMeta({
  title: computed(
    () => `${strings.team.edit} - ${data.value?.data.name ?? strings.loading}`,
  ),
});

const initialValues = computed<TeamEditDto | undefined>(() => {
  const raw = data.value?.data;
  if (!raw) return undefined;
  return {
    name: raw.name,
    startNumber: raw.startNumber,
    members: raw.members.map((item) => item.id),
    participatingDisciplines: raw.participatingDisciplines.map(
      (item) => item.id,
    ),
  };
});

const editTeam = useUpdateTeam();

async function onSubmit(data: TeamEditDto) {
  await editTeam.mutateAsync({
    teamId: teamId,
    data: data,
  });

  await Promise.all([
    router.push({
      name: RouteNames.TEAM_DETAIL,
      params: { tournamentId: tournamentId, teamId: teamId },
    }),
    queryClient.refetchQueries({
      queryKey: getGetTeamsQueryKey(tournamentId),
    }),
    queryClient.refetchQueries({
      queryKey: getGetTeamQueryKey(teamId),
    }),
  ]);
}
</script>

<template>
  <LoadingMessage v-if="isPending" />

  <StatusMessage
    v-else-if="isError || !initialValues"
    severity="error"
    :message="strings.team.loadingError"
  />

  <TeamEditor v-else :initial-values="initialValues" edit @submit="onSubmit" />
</template>
