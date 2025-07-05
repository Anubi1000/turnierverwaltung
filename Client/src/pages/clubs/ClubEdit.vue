<script setup lang="ts">
import LoadingMessage from "@/components/messages/LoadingMessage.vue";
import StatusMessage from "@/components/messages/StatusMessage.vue";
import ClubEditor from "@/pages/clubs/ClubEditor.vue";
import {
  getGetClubQueryKey,
  getGetClubsQueryKey,
  useGetClub,
  useUpdateClub,
} from "@/utils/api/api.ts";
import type { ClubEditDto } from "@/utils/api/schemas";
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
const clubId = getIdFromRoute("clubId");

const { data, isPending, isError } = useGetClub(clubId);

useSeoMeta({
  title: computed(
    () =>
      `${strings.club.edit} - ${data.value?.data.name ?? strings.status.loading}`,
  ),
});

const initialValues = computed<ClubEditDto | undefined>(() => {
  const raw = data.value?.data;
  if (!raw) return undefined;
  return {
    name: raw.name,
  };
});

const editClub = useUpdateClub();

async function onSubmit(data: ClubEditDto) {
  await editClub.mutateAsync({
    clubId: clubId,
    data: data,
  });

  await Promise.all([
    router.push({
      name: RouteNames.CLUB_DETAIL,
      params: { tournamentId: tournamentId, clubId: clubId },
    }),
    queryClient.refetchQueries({
      queryKey: getGetClubsQueryKey(tournamentId),
    }),
    queryClient.refetchQueries({
      queryKey: getGetClubQueryKey(clubId),
    }),
  ]);
}
</script>

<template>
  <LoadingMessage v-if="isPending" />

  <StatusMessage
    v-else-if="isError || !initialValues"
    severity="error"
    :message="strings.club.loadingError"
  />

  <ClubEditor v-else :initial-values="initialValues" edit @submit="onSubmit" />
</template>
