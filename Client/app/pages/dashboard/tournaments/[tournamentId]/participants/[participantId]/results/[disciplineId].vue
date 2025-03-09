<script setup lang="ts">
import { useQuery } from "@tanstack/vue-query";
import LoadedContent from "~/pageComponents/participant/results/LoadedContent.vue";

definePageMeta({
  middleware: "auth",
  layout: "participant-list",
});

const participantId = getIdFromRoute("participantId");
const disciplineId = getIdFromRoute("disciplineId");

const {
  data: participant,
  isPending: isParticipantPending,
  suspense: participantSuspense,
} = useQuery<ParticipantDetailDto>(QueryOptions.participant(participantId));

const {
  data: results,
  isPending: isResultsPending,
  suspense: resultsSuspense,
} = useQuery<ParticipantResultDetailDto>(
  QueryOptions.participantResults(participantId, disciplineId),
);

if (isParticipantPending || isResultsPending)
  await Promise.all([participantSuspense(), resultsSuspense()]);

watchEffect(() => {
  if (participant.value) {
    useSeoMeta({ title: `Ergebnisse - ${participant.value.name}` });
  }
});
</script>

<template>
  <!--Error Message-->
  <div v-if="!results" class="m-auto">
    <StaticMessage severity="error"
      >Die Ergebnisse des Teilnehmers konnten nicht geladen
      werden</StaticMessage
    >
  </div>

  <!--Main Wrapper-->
  <LoadedContent
    v-else
    :detail="results"
    :participant-id="participantId"
    :discipline-id="disciplineId"
  />
</template>
