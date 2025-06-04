<script setup lang="ts">
import LoadingMessage from "@/components/messages/LoadingMessage.vue";
import StatusMessage from "@/components/messages/StatusMessage.vue";
import ParticipantEditor from "@/pages/participants/ParticipantEditor.vue";
import {
  getGetParticipantQueryKey,
  getGetParticipantsQueryKey,
  useGetParticipant,
  useUpdateParticipant,
} from "@/utils/api/api.ts";
import type { ParticipantEditDto } from "@/utils/api/schemas";
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
const participantId = getIdFromRoute("participantId");

const { data, isPending, isError } = useGetParticipant(participantId);

useSeoMeta({
  title: computed(
    () =>
      `${strings.participant.edit} - ${data.value?.data.name ?? strings.status.loading}`,
  ),
});

const initialValues = computed<ParticipantEditDto | undefined>(() => {
  const raw = data.value?.data;
  if (!raw) return undefined;

  return {
    name: raw.name,
    startNumber: raw.startNumber,
    gender: raw.gender,
    clubId: raw.clubId,
  };
});

const editParticipant = useUpdateParticipant();

async function onSubmit(data: ParticipantEditDto) {
  await editParticipant.mutateAsync({
    participantId: participantId,
    data: data,
  });

  await Promise.all([
    router.push({
      name: RouteNames.PARTICIPANT_DETAIL,
      params: { tournamentId: tournamentId, participantId: participantId },
    }),
    queryClient.refetchQueries({
      queryKey: getGetParticipantsQueryKey(tournamentId),
    }),
    queryClient.refetchQueries({
      queryKey: getGetParticipantQueryKey(participantId),
    }),
  ]);
}
</script>

<template>
  <LoadingMessage v-if="isPending" />

  <StatusMessage
    v-else-if="isError || !initialValues"
    severity="error"
    :message="strings.participant.loadingError"
  />

  <ParticipantEditor
    v-else
    :initial-values="initialValues"
    edit
    @submit="onSubmit"
  />
</template>
