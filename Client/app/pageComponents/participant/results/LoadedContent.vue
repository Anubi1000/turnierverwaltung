<script setup lang="ts">
import { cloneDeep } from "lodash-es";
import { useMutation, useQueryClient } from "@tanstack/vue-query";
import type { FetchError } from "ofetch";

const { detail, participantId, disciplineId } = defineProps<{
  detail: ParticipantResultDetailDto;
  participantId: number;
  disciplineId: number;
}>();

const queryClient = useQueryClient();
const toast = useToast();

const { handleSubmit } = useForm({
  validationSchema: toTypedSchema(participantRoundEditSchema),
  initialValues: {
    rounds: cloneDeep(detail.rounds),
  },
  keepValuesOnUnmount: true,
});

const {
  remove: removeRound,
  push: pushRound,
  fields: rounds,
} = useFieldArray<ParticipantResultEditDtoRoundResult>("rounds");

function addRound() {
  pushRound({
    values: new Array(detail.disciplineValues.length).fill(0),
  });
}

function beforeCardLeave(el: Element) {
  if (!(el instanceof HTMLElement)) return;
  el.style.left = `${el.offsetLeft}px`;
  el.style.top = `${el.offsetTop}px`;
}

const { mutate } = useMutation<undefined, FetchError, ParticipantResultEditDto>(
  {
    mutationFn: (item) =>
      $fetch(`/api/participants/${participantId}/results/${disciplineId}`, {
        method: "PUT",
        body: item,
      }),
    onSuccess: async () => {
      toast.add(Toasts.itemUpdateSuccess("Teilnehmer", "Der"));

      await queryClient.invalidateQueries({
        queryKey: QueryKeys.participantResults(participantId, disciplineId),
      });

      navigateTo(`../../${participantId}`);
    },
    onError: (error) => {
      toast.add(Toasts.itemUpdateFail(error, "Der Teilnehmer"));
    },
  },
);

const onSubmit = handleSubmit((values) => {
  mutate(values);
});
</script>

<template>
  <form class="flex flex-1 flex-col" @submit="onSubmit">
    <!--Actions-->
    <div class="flex w-full justify-end gap-2 px-3 pt-2">
      <ButtonGroup>
        <PButton
          icon="material-symbols:add-2"
          label="Neue Runde"
          @click="addRound"
        />
        <PButton type="submit" label="Speichern" icon="mdi:content-save" />
      </ButtonGroup>
    </div>

    <TransitionGroup
      name="cards"
      tag="div"
      class="flex flex-wrap gap-3 overflow-y-auto px-3 py-2"
      @before-leave="beforeCardLeave"
    >
      <Card
        v-for="(item, roundIndex) in rounds"
        :key="item.key"
        class="h-fit min-w-80"
      >
        <template #title>
          <div class="flex items-center justify-between select-none">
            Runde {{ roundIndex + 1 }}

            <PButton
              icon="material-symbols:delete"
              text
              rounded
              severity="danger"
              @click="removeRound(roundIndex)"
            />
          </div>
        </template>

        <template #content>
          <div class="space-y-2">
            <FormInputNumber
              v-for="(value, index) in item.value.values"
              :key="index"
              :name="`rounds[${roundIndex}].values[${index}]`"
              :label="detail.disciplineValues[index].name"
              :props="{
                locale: 'de-DE',
                minFractionDigits: 1,
                maxFractionDigits: 10,
              }"
            />
          </div>
        </template>
      </Card>
    </TransitionGroup>
  </form>
</template>

<style scoped>
.cards-enter-active,
.cards-leave-active,
.cards-move {
  transition: all 0.25s ease;
}

.cards-enter-from,
.cards-leave-to {
  opacity: 0;
  transform: translateY(32px);
}
</style>
