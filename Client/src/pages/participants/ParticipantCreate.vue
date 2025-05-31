<script setup lang="ts">
import LoadingMessage from "@/components/messages/LoadingMessage.vue";
import StatusMessage from "@/components/messages/StatusMessage.vue";
import ParticipantEditor from "@/pages/participants/ParticipantEditor.vue";
import {
  getGetParticipantQueryOptions,
  getGetParticipantsQueryKey,
  getNextParticipantStartNumber,
  useCreateParticipant,
} from "@/utils/api/api.ts";
import type { ParticipantEditDto } from "@/utils/api/schemas";
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
  title: strings.participant.create,
});

const tournamentId = getIdFromRoute("tournamentId");

const nextStartNumberQuery = useFetch(() =>
  getNextParticipantStartNumber(tournamentId),
);

const initialValues = reactive<ParticipantEditDto>({
  name: "",
  startNumber: 1,
  gender: "Male",
  clubId: -1,
});

watch(nextStartNumberQuery.data, (newValue) => {
  if (newValue) {
    initialValues.startNumber = newValue;
  }
});

const createParticipant = useCreateParticipant();

async function onSubmit(data: ParticipantEditDto) {
  const response = await createParticipant.mutateAsync({
    tournamentId: tournamentId,
    data: data,
  });

  await Promise.all([
    router.push({
      name: RouteNames.PARTICIPANT_DETAIL,
      params: { tournamentId: tournamentId, participantId: response.data },
    }),
    queryClient.refetchQueries({
      queryKey: getGetParticipantsQueryKey(tournamentId),
    }),
    queryClient.prefetchQuery(getGetParticipantQueryOptions(response.data)),
  ]);
}
</script>

<template>
  <LoadingMessage v-if="nextStartNumberQuery.isLoading.value" />

  <StatusMessage
    v-else-if="nextStartNumberQuery.hasError.value"
    severity="error"
    :message="strings.participant.loadingError"
  />

  <ParticipantEditor
    v-else
    :initial-values="initialValues"
    @submit="onSubmit"
  />
</template>
