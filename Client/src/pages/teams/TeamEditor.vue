<script setup lang="ts">
import CardContainer from "@/components/CardContainer.vue";
import HeadlineRow from "@/components/HeadlineRow.vue";
import DetailCard from "@/components/detail/DetailCard.vue";
import FormSaveButton from "@/components/form/FormSaveButton.vue";
import FormInputNumber from "@/components/form/input/FormInputNumber.vue";
import FormInputText from "@/components/form/input/FormInputText.vue";
import FormMultiSelect from "@/components/form/input/FormMultiSelect.vue";
import LoadingMessage from "@/components/messages/LoadingMessage.vue";
import {
  useGetParticipants,
  useGetTeamDisciplines,
  useGetTournament,
} from "@/utils/api/api.ts";
import type { TeamEditDto } from "@/utils/api/schemas";
import { strings } from "@/utils/strings.ts";
import { getIdFromRoute } from "@/utils/utils.ts";
import { getTeamEditDtoSchema } from "@/utils/validation.ts";
import { toTypedSchema } from "@vee-validate/zod";
import { useForm } from "vee-validate";
import { computed } from "vue";

const { initialValues } = defineProps<{
  edit?: boolean;
  initialValues: TeamEditDto;
}>();

const emit = defineEmits<{
  (e: "submit", value: TeamEditDto): void;
}>();

const tournamentId = getIdFromRoute("tournamentId");

const tournamentQuery = useGetTournament(tournamentId);

const participantQuery = useGetParticipants(tournamentId);
const participants = computed(() => participantQuery.data.value?.data ?? []);

const teamDisciplineQuery = useGetTeamDisciplines(tournamentId);
const teamDisciplines = computed(
  () => teamDisciplineQuery.data.value?.data ?? [],
);

const { handleSubmit } = useForm({
  validationSchema: computed(() => {
    let schema;

    const tournament = tournamentQuery.data.value?.data;
    if (tournament) {
      schema = getTeamEditDtoSchema(
        tournament.teamSize,
        tournament.isTeamSizeFixed,
      );
    } else {
      schema = getTeamEditDtoSchema(3, false);
    }

    return toTypedSchema(schema);
  }),
  initialValues: initialValues,
});

const onSubmit = handleSubmit(async (values) => {
  const data: TeamEditDto = structuredClone(values);

  emit("submit", data);
});
</script>

<template>
  <LoadingMessage v-if="participantQuery.isPending.value" />

  <form class="flex flex-grow flex-col" @submit="onSubmit">
    <HeadlineRow :title="edit ? strings.team.edit : strings.team.create">
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

          <FormMultiSelect
            name="members"
            :label="strings.members"
            :props="{
              options: participants,
              optionLabel: 'name',
              optionValue: 'id',
              filter: true,
              filterPlaceholder: strings.searchTerm,
              showToggleAll: false,
            }"
          />

          <FormMultiSelect
            name="participatingDisciplines"
            :label="strings.teamDiscipline.items"
            :props="{
              options: teamDisciplines,
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
