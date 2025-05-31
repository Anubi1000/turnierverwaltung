<script setup lang="ts">
import LoadingMessage from "@/components/messages/LoadingMessage.vue";
import StatusMessage from "@/components/messages/StatusMessage.vue";
import LoadedContent from "@/pages/participants/result/LoadedContent.vue";
import {
  useGetParticipant,
  useGetParticipantResults,
} from "@/utils/api/api.ts";
import { strings } from "@/utils/strings.ts";
import { getIdFromRoute } from "@/utils/utils.ts";
import { useSeoMeta } from "@unhead/vue";
import { computed } from "vue";

const participantId = getIdFromRoute("participantId");
const disciplineId = getIdFromRoute("disciplineId");

const participantQuery = useGetParticipant(participantId);
const participant = computed(() => participantQuery.data.value?.data);

const resultsQuery = useGetParticipantResults(participantId, disciplineId);
const results = computed(() => resultsQuery.data.value?.data);

useSeoMeta({
  title: computed(
    () =>
      `${strings.participant.editResults} - ${participant.value?.name ?? strings.loading}`,
  ),
});
</script>

<template>
  <LoadingMessage
    v-if="participantQuery.isPending.value || resultsQuery.isPending.value"
  />

  <StatusMessage
    v-else-if="
      participantQuery.isError.value ||
      !participant ||
      resultsQuery.isError.value ||
      !results
    "
    severity="error"
    :message="strings.participant.loadingError"
  />

  <LoadedContent
    v-else
    :discipline-id="disciplineId"
    :participant="participant"
    :results="results"
  />
</template>
