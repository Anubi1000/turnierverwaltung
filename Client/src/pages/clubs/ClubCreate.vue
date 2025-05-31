<script setup lang="ts">
import ClubEditor from "@/pages/clubs/ClubEditor.vue";
import {
  getGetClubQueryOptions,
  getGetClubsQueryKey,
  useCreateClub,
} from "@/utils/api/api.ts";
import type { ClubEditDto } from "@/utils/api/schemas";
import { RouteNames } from "@/utils/routes.ts";
import { strings } from "@/utils/strings.ts";
import { getIdFromRoute } from "@/utils/utils.ts";
import { useQueryClient } from "@tanstack/vue-query";
import { useSeoMeta } from "@unhead/vue";
import { useRouter } from "vue-router";

const router = useRouter();
const queryClient = useQueryClient();

useSeoMeta({
  title: strings.club.create,
});

const tournamentId = getIdFromRoute("tournamentId");

const initialValues: ClubEditDto = {
  name: "",
};

const createClub = useCreateClub();

async function onSubmit(data: ClubEditDto) {
  const response = await createClub.mutateAsync({
    tournamentId: tournamentId,
    data: data,
  });

  await Promise.all([
    router.push({
      name: RouteNames.CLUB_DETAIL,
      params: { tournamentId: tournamentId, clubId: response.data },
    }),
    queryClient.refetchQueries({
      queryKey: getGetClubsQueryKey(tournamentId),
    }),
    queryClient.prefetchQuery(getGetClubQueryOptions(response.data)),
  ]);
}
</script>

<template>
  <ClubEditor :initial-values="initialValues" @submit="onSubmit" />
</template>
