<script setup lang="ts">
import CardContainer from "@/components/CardContainer.vue";
import HeadlineRow from "@/components/HeadlineRow.vue";
import DetailCard from "@/components/detail/DetailCard.vue";
import FormSaveButton from "@/components/form/FormSaveButton.vue";
import FormDatePicker from "@/components/form/input/FormDatePicker.vue";
import FormInputNumber from "@/components/form/input/FormInputNumber.vue";
import FormInputText from "@/components/form/input/FormInputText.vue";
import type { TournamentEditDto } from "@/utils/api/schemas";
import { strings } from "@/utils/strings.ts";
import { tournamentEditDtoSchema } from "@/utils/validation.ts";
import { toTypedSchema } from "@vee-validate/zod";
import { useForm } from "vee-validate";

const { initialValues } = defineProps<{
  edit?: boolean;
  initialValues: TournamentEditDto;
}>();

const emit = defineEmits<{
  (e: "submit", value: TournamentEditDto): void;
}>();

const { handleSubmit /*, values */ } = useForm({
  validationSchema: toTypedSchema(tournamentEditDtoSchema),
  initialValues: {
    name: initialValues.name,
    date: new Date(initialValues.date),
    teamSize: initialValues.teamSize,
  },
});

const onSubmit = handleSubmit(async (values) => {
  const data: TournamentEditDto = {
    name: values.name,
    date: values.date.getTime(),
    teamSize: values.teamSize,
    isTeamSizeFixed: true,
  };

  emit("submit", data);
});
</script>

<template>
  <form class="flex flex-grow flex-col" @submit="onSubmit">
    <HeadlineRow
      :title="edit ? strings.tournament.edit : strings.tournament.create"
    >
      <template #actions>
        <FormSaveButton />
      </template>
    </HeadlineRow>

    <CardContainer>
      <DetailCard :title="strings.general">
        <div class="flex flex-col gap-2">
          <FormInputText name="name" :label="strings.name" />
          <FormDatePicker name="date" :label="strings.date" />
        </div>
      </DetailCard>

      <DetailCard :title="strings.team.items">
        <div class="flex flex-col gap-2">
          <FormInputNumber
            name="teamSize"
            :label="strings.teamSize"
            :props="{
              min: 2,
              max: 25,
              showButtons: true,
              disabled: edit /*&& values.isTeamSizeFixed*/,
            }"
          />

          <!--<FormCheckbox
            name="isTeamSizeFixed"
            :label="strings.teamSizeFixed"
            :props="{
              disabled: edit,
            }"
          />-->
        </div>
      </DetailCard>
    </CardContainer>
  </form>
</template>
