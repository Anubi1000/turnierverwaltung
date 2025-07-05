<script setup lang="ts">
import TournamentEditor from "@/pages/tournaments/TournamentEditor.vue";
import {
  getGetTournamentQueryOptions,
  getGetTournamentsQueryKey,
  useCreateTournament,
} from "@/utils/api/api.ts";
import type { TournamentEditDto } from "@/utils/api/schemas";
import { RouteNames } from "@/utils/routes.ts";
import { strings } from "@/utils/strings.ts";
import { useQueryClient } from "@tanstack/vue-query";
import { useSeoMeta } from "@unhead/vue";
import { useRouter } from "vue-router";

const router = useRouter();
const queryClient = useQueryClient();

useSeoMeta({
  title: strings.tournament.create,
});

const initialValues: TournamentEditDto = {
  name: "",
  date: Date.now(),
  teamSize: 3,
  isTeamSizeFixed: true,
};

const createTournament = useCreateTournament();

async function onSubmit(data: TournamentEditDto) {
  const response = await createTournament.mutateAsync({
    data: data,
  });

  await Promise.all([
    router.push({
      name: RouteNames.TOURNAMENT_DETAIL,
      params: { tournamentId: response.data },
    }),
    queryClient.refetchQueries({
      queryKey: getGetTournamentsQueryKey(),
    }),
    queryClient.prefetchQuery(getGetTournamentQueryOptions(response.data)),
  ]);
}
</script>

<template>
  <TournamentEditor :initial-values="initialValues" @submit="onSubmit" />
</template>
