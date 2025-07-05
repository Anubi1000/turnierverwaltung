<script setup lang="ts">
import LoadingMessage from "@/components/messages/LoadingMessage.vue";
import StatusMessage from "@/components/messages/StatusMessage.vue";
import TournamentEditor from "@/pages/tournaments/TournamentEditor.vue";
import {
  getGetTournamentQueryKey,
  getGetTournamentsQueryKey,
  useGetTournament,
  useUpdateTournament,
} from "@/utils/api/api.ts";
import type { TournamentEditDto } from "@/utils/api/schemas";
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

const { data, isPending, isError } = useGetTournament(tournamentId);

useSeoMeta({
  title: computed(
    () =>
      `${strings.tournament.edit} - ${data.value?.data.name ?? strings.status.loading}`,
  ),
});

const initialValues = computed<TournamentEditDto | undefined>(() => {
  const raw = data.value?.data;
  if (!raw) return undefined;
  return {
    name: raw.name,
    date: raw.date,
    teamSize: raw.teamSize,
    isTeamSizeFixed: raw.isTeamSizeFixed,
  };
});

const editTournament = useUpdateTournament();

async function onSubmit(data: TournamentEditDto) {
  await editTournament.mutateAsync({
    tournamentId: tournamentId,
    data: data,
  });

  await Promise.all([
    router.push({
      name: RouteNames.TOURNAMENT_DETAIL,
      params: { tournamentId: tournamentId },
    }),
    queryClient.refetchQueries({
      queryKey: getGetTournamentsQueryKey(),
    }),
    queryClient.refetchQueries({
      queryKey: getGetTournamentQueryKey(tournamentId),
    }),
  ]);
}
</script>

<template>
  <LoadingMessage v-if="isPending" />

  <StatusMessage
    v-else-if="isError || !initialValues"
    severity="error"
    :message="strings.tournament.loadingError"
  />

  <TournamentEditor
    v-else
    :initial-values="initialValues"
    edit
    @submit="onSubmit"
  />
</template>
