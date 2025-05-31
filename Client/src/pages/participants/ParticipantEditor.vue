<script setup lang="ts">
import CardContainer from "@/components/CardContainer.vue";
import HeadlineRow from "@/components/HeadlineRow.vue";
import DetailCard from "@/components/detail/DetailCard.vue";
import FormSaveButton from "@/components/form/FormSaveButton.vue";
import FormInputNumber from "@/components/form/input/FormInputNumber.vue";
import FormInputText from "@/components/form/input/FormInputText.vue";
import FormRadioButton from "@/components/form/input/FormRadioButton.vue";
import FormSelect from "@/components/form/input/FormSelect.vue";
import LoadingMessage from "@/components/messages/LoadingMessage.vue";
import StatusMessage from "@/components/messages/StatusMessage.vue";
import { useGetClubs } from "@/utils/api/api.ts";
import type { ParticipantEditDto } from "@/utils/api/schemas";
import { strings } from "@/utils/strings.ts";
import { getIdFromRoute } from "@/utils/utils.ts";
import { participantEditDtoSchema } from "@/utils/validation.ts";
import { toTypedSchema } from "@vee-validate/zod";
import { useForm } from "vee-validate";
import { computed } from "vue";

const { initialValues } = defineProps<{
  edit?: boolean;
  initialValues: ParticipantEditDto;
}>();

const emit = defineEmits<{
  (e: "submit", value: ParticipantEditDto): void;
}>();

const tournamentId = getIdFromRoute("tournamentId");

const clubsQuery = useGetClubs(tournamentId);
const clubs = computed(() => clubsQuery.data.value?.data ?? []);

const { handleSubmit } = useForm({
  validationSchema: toTypedSchema(participantEditDtoSchema),
  initialValues: initialValues,
});

const onSubmit = handleSubmit(async (values) => {
  const data: ParticipantEditDto = structuredClone(values);

  emit("submit", data);
});
</script>

<template>
  <LoadingMessage v-if="clubsQuery.isPending.value" />

  <StatusMessage
    v-else-if="clubsQuery.isError.value"
    severity="error"
    :message="strings.club.loadingError"
  />

  <form v-else class="flex flex-grow flex-col" @submit="onSubmit">
    <HeadlineRow
      :title="edit ? strings.participant.edit : strings.participant.create"
    >
      <template #actions>
        <FormSaveButton />
      </template>
    </HeadlineRow>

    <CardContainer>
      <DetailCard :title="strings.general">
        <div class="flex flex-col gap-2">
          <FormInputText name="name" :label="strings.name" />

          <FormInputNumber
            name="startNumber"
            :label="strings.startNumber"
            :props="{
              min: 1,
              max: 100_000,
              showButtons: true,
            }"
          />

          <FormRadioButton
            class="grid-cols-2"
            name="gender"
            :options="[
              { id: 'Male', label: strings.formatting.gender('Male') },
              { id: 'Female', label: strings.formatting.gender('Female') },
            ]"
          />

          <FormSelect
            name="clubId"
            :label="strings.club.item"
            :props="{
              options: clubs,
              optionLabel: 'name',
              optionValue: 'id',
              filter: true,
              filterPlaceholder: strings.searchTerm,
            }"
          />
        </div>
      </DetailCard>
    </CardContainer>
  </form>
</template>
