<script setup lang="ts">
import LoadingMessage from "@/components/messages/LoadingMessage.vue";
import StatusMessage from "@/components/messages/StatusMessage.vue";
import TeamEditor from "@/pages/teams/TeamEditor.vue";
import {
  getGetTeamQueryOptions,
  getGetTeamsQueryKey,
  getNextTeamStartNumber,
  useCreateTeam,
} from "@/utils/api/api.ts";
import type { TeamEditDto } from "@/utils/api/schemas";
import { RouteNames } from "@/utils/routes.ts";
import { strings } from "@/utils/strings.ts";
import { getIdFromRoute, useFetch } from "@/utils/utils.ts";
import { useQueryClient } from "@tanstack/vue-query";
import { useSeoMeta } from "@unhead/vue";
import { reactive, watch } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const queryClient = useQueryClient();

useSeoMeta({
  title: strings.team.create,
});

const tournamentId = getIdFromRoute("tournamentId");

const nextStartNumberQuery = useFetch(() =>
  getNextTeamStartNumber(tournamentId),
);

const initialValues = reactive<TeamEditDto>({
  name: "",
  startNumber: 1,
  members: [],
  participatingDisciplines: [],
});

watch(nextStartNumberQuery.data, (newValue) => {
  if (newValue) {
    initialValues.startNumber = newValue;
  }
});

const createTeam = useCreateTeam();

async function onSubmit(data: TeamEditDto) {
  const response = await createTeam.mutateAsync({
    tournamentId: tournamentId,
    data: data,
  });

  await Promise.all([
    router.push({
      name: RouteNames.TEAM_DETAIL,
      params: { tournamentId: tournamentId, teamId: response.data },
    }),
    queryClient.refetchQueries({
      queryKey: getGetTeamsQueryKey(tournamentId),
    }),
    queryClient.prefetchQuery(getGetTeamQueryOptions(response.data)),
  ]);
}
</script>

<template>
  <LoadingMessage v-if="nextStartNumberQuery.isLoading.value" />

  <StatusMessage
    v-else-if="nextStartNumberQuery.hasError.value"
    severity="error"
    :message="strings.team.loadingError"
  />

  <TeamEditor :initial-values="initialValues" @submit="onSubmit" />
</template>
