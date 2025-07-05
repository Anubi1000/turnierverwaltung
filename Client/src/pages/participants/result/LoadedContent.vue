<script setup lang="ts">
import CardContainer from "@/components/CardContainer.vue";
import HeadlineRow from "@/components/HeadlineRow.vue";
import DetailCard from "@/components/detail/DetailCard.vue";
import FormSaveButton from "@/components/form/FormSaveButton.vue";
import FormInputNumber from "@/components/form/input/FormInputNumber.vue";
import {
  getGetParticipantQueryKey,
  getGetParticipantResultsQueryKey,
  useUpdateParticipantResults,
} from "@/utils/api/api.ts";
import type {
  ParticipantDetailDto,
  ParticipantResultDetailDto,
  ParticipantResultEditDtoRoundResult,
} from "@/utils/api/schemas";
import { RouteNames } from "@/utils/routes.ts";
import { strings } from "@/utils/strings.ts";
import { getIdFromRoute } from "@/utils/utils.ts";
import { participantResultEditDtoSchema } from "@/utils/validation.ts";
import { useQueryClient } from "@tanstack/vue-query";
import { toTypedSchema } from "@vee-validate/zod";
import Button from "primevue/button";
import { useFieldArray, useForm } from "vee-validate";
import { toRaw, watch } from "vue";
import { useRouter } from "vue-router";
import Add from "~icons/material-symbols/add-2";
import Delete from "~icons/material-symbols/delete";

const { disciplineId, participant, results } = defineProps<{
  disciplineId: number;
  participant: ParticipantDetailDto;
  results: ParticipantResultDetailDto;
}>();

const router = useRouter();
const queryClient = useQueryClient();

const tournamentId = getIdFromRoute("tournamentId");

const { handleSubmit, meta, setValues } = useForm({
  validationSchema: toTypedSchema(participantResultEditDtoSchema),
  initialValues: {
    rounds: structuredClone(toRaw(results.rounds)),
  },
});

watch(
  () => results,
  (newResults) => {
    if (!meta.value.dirty) {
      setValues({
        rounds: structuredClone(toRaw(newResults.rounds)),
      });
    }
  },
);

const {
  fields: rounds,
  push: pushRound,
  remove: removeRound,
} = useFieldArray<ParticipantResultEditDtoRoundResult>("rounds");

function addRound() {
  pushRound({
    values: new Array(results.disciplineValues.length).fill(0),
  });
}

const updateResults = useUpdateParticipantResults();

const onSubmit = handleSubmit(async (values) => {
  const data = structuredClone(values);
  await updateResults.mutateAsync({
    participantId: participant.id,
    disciplineId: disciplineId,
    data: data,
  });

  await Promise.all([
    router.push({
      name: RouteNames.PARTICIPANT_DETAIL,
      params: { tournamentId: tournamentId, participantId: participant.id },
    }),
    queryClient.refetchQueries({
      queryKey: getGetParticipantQueryKey(participant.id),
    }),
  ]);

  await queryClient.invalidateQueries({
    queryKey: getGetParticipantResultsQueryKey(participant.id, disciplineId),
  });
});
</script>

<template>
  <form class="flex flex-grow flex-col" @submit="onSubmit">
    <HeadlineRow :title="strings.participant.editResults">
      <template #actions>
        <Button :label="strings.newRound" @click="addRound">
          <template #icon>
            <Add />
          </template>
        </Button>

        <FormSaveButton />
      </template>
    </HeadlineRow>

    <CardContainer>
      <DetailCard
        v-for="(round, idx) in rounds"
        :key="round.key"
        :title="`${strings.round} ${idx + 1}`"
      >
        <template #trailing>
          <Button severity="danger" text rounded @click="removeRound(idx)">
            <template #icon>
              <Delete />
            </template>
          </Button>
        </template>

        <div class="flex flex-col gap-2">
          <FormInputNumber
            v-for="(_, index) in round.value.values"
            :key="index"
            :name="`rounds[${idx}].values[${index}]`"
            :label="results.disciplineValues[index].name"
            :props="{
              locale: 'de-DE',
              minFractionDigits: 1,
              maxFractionDigits: 10,
            }"
          />
        </div>
      </DetailCard>
    </CardContainer>
  </form>
</template>
