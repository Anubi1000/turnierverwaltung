<script setup lang="ts">
import CardContainer from "@/components/CardContainer.vue";
import HeadlineRow from "@/components/HeadlineRow.vue";
import DetailCard from "@/components/detail/DetailCard.vue";
import FormSaveButton from "@/components/form/FormSaveButton.vue";
import FormInputText from "@/components/form/input/FormInputText.vue";
import FormMultiSelect from "@/components/form/input/FormMultiSelect.vue";
import LoadingMessage from "@/components/messages/LoadingMessage.vue";
import StatusMessage from "@/components/messages/StatusMessage.vue";
import { useGetDisciplines } from "@/utils/api/api.ts";
import type { TeamDisciplineEditDto } from "@/utils/api/schemas";
import { strings } from "@/utils/strings.ts";
import { getIdFromRoute } from "@/utils/utils.ts";
import { teamDisciplineEditDtoSchema } from "@/utils/validation.ts";
import { toTypedSchema } from "@vee-validate/zod";
import { useForm } from "vee-validate";
import { computed } from "vue";

const { initialValues } = defineProps<{
  edit?: boolean;
  initialValues: TeamDisciplineEditDto;
}>();

const emit = defineEmits<{
  (e: "submit", value: TeamDisciplineEditDto): void;
}>();

const tournamentId = getIdFromRoute("tournamentId");

const disciplinesQuery = useGetDisciplines(tournamentId);
const disciplines = computed(() => disciplinesQuery.data.value?.data ?? []);

const { handleSubmit } = useForm({
  validationSchema: toTypedSchema(teamDisciplineEditDtoSchema),
  initialValues: {
    name: initialValues.name,
    // @ts-expect-error Type somehow not valid
    basedOn: [...initialValues.basedOn],
  },
});

const onSubmit = handleSubmit(async (values) => {
  const data: TeamDisciplineEditDto = {
    name: values.name,
    displayType: "Normal",
    basedOn: [...values.basedOn],
  };

  emit("submit", data);
});
</script>

<template>
  <LoadingMessage v-if="disciplinesQuery.isPending.value" />

  <StatusMessage
    v-else-if="disciplinesQuery.isError.value"
    severity="error"
    :message="strings.discipline.loadingError"
  />

  <form v-else class="flex flex-grow flex-col" @submit="onSubmit">
    <HeadlineRow
      :title="
        edit ? strings.teamDiscipline.edit : strings.teamDiscipline.create
      "
    >
      <template #actions>
        <FormSaveButton />
      </template>
    </HeadlineRow>

    <CardContainer>
      <DetailCard :title="strings.general">
        <div class="flex flex-col gap-2">
          <FormInputText name="name" :label="strings.name" />

          <!--<FormSelect
            name="displayType"
            :label="strings.type"
            :props="{
              options: [
                {
                  value: 'Normal',
                  label: strings.formatting.teamScoreDisplayType('Normal'),
                },
                {
                  value: 'Nationcup',
                  label: strings.formatting.teamScoreDisplayType('Nationcup'),
                },
              ],
              optionLabel: 'label',
              optionValue: 'value',
            }"
          />-->

          <FormMultiSelect
            name="basedOn"
            :label="strings.basedOn"
            :props="{
              options: disciplines,
              optionLabel: 'name',
              optionValue: 'id',
              filter: true,
              filterPlaceholder: strings.searchTerm,
              showToggleAll: false,
            }"
          />
        </div>
      </DetailCard>
    </CardContainer>
  </form>
</template>
