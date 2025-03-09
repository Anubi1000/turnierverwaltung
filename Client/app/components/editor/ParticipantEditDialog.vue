<script setup lang="ts">
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import { participantEditSchema } from "~/utils/validation";
import type { FetchError } from "ofetch";

const { edit = false, participantId = -1 } = defineProps<{
  edit?: boolean;
  participantId?: number;
}>();
const visible = defineModel<boolean>("visible", { default: false });

const queryClient = useQueryClient();
const toast = useToast();

const tournamentId = injectId("tournamentId");

const { data: clubs, isPending: areClubsPending } = useQuery<ListClubDto[]>(
  QueryOptions.clubs(tournamentId),
);

const { data, isPending, suspense } = useQuery<ParticipantDetailDto>({
  ...QueryOptions.participant(participantId),
  enabled: edit,
});
if (edit && isPending.value) await suspense();

const initialValues = computed(() => ({
  name: data.value?.name ?? "",
  startNumber: data.value?.startNumber ?? 1,
  gender: data.value?.gender ?? "Male",
  clubId: data.value?.clubId ?? -1,
}));

const { handleSubmit, resetForm, setValues } = useForm({
  validationSchema: toTypedSchema(participantEditSchema),
  initialValues: initialValues.value,
  keepValuesOnUnmount: true,
});

watch(visible, (newVal, oldVal) => {
  if (newVal && !oldVal) {
    resetForm();
    setValues(initialValues.value);
  }
});

const { mutate } = useMutation<
  number | undefined,
  FetchError,
  ParticipantEditDto
>({
  // Mutation
  mutationFn: edit
    ? (item) =>
        $fetch(`/api/participants/${participantId}`, {
          method: "PUT",
          body: item,
        })
    : (item) =>
        $fetch(`/api/tournaments/${tournamentId}/participants`, {
          method: "POST",
          body: item,
        }),

  // Success
  onSuccess: async (data?: number) => {
    toast.add(
      Toasts.itemModifySuccess("Teilnehmer", "Der", edit ? "edit" : "create"),
    );

    visible.value = false;
    await queryClient.invalidateQueries({
      queryKey: QueryKeys.participants(tournamentId),
    });

    if (edit) {
      await queryClient.invalidateQueries({
        queryKey: QueryKeys.participant(participantId),
      });
    } else {
      navigateTo(`/dashboard/tournaments/${tournamentId}/participants/${data}`);
    }
  },

  // Error
  onError: (error: FetchError) => {
    toast.add(
      Toasts.itemModifyFail(error, "Der Teilnehmer", edit ? "edit" : "create"),
    );
  },
});

const onSubmit = handleSubmit((values) => {
  mutate(values);
});
</script>

<template>
  <Dialog
    v-model:visible="visible"
    :draggable="false"
    modal
    :header="!edit ? 'Neuer Teilnehmer' : 'Teilnehmer bearbeiten'"
    class="w-96"
  >
    <ProgressBar
      v-if="areClubsPending"
      mode="indeterminate"
      class="absolute top-16 left-0 z-10 h-1 w-full"
    />

    <form @submit="onSubmit">
      <div class="flex flex-col gap-3">
        <FormInputText name="name" label="Name" />

        <FormInputNumber
          name="startNumber"
          label="Startnummer"
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
            { id: 'Male', label: 'Männlich' },
            { id: 'Female', label: 'Weiblich' },
          ]"
        />

        <FormSelect
          name="clubId"
          label="Verein"
          :props="{
            options: clubs ?? [],
            optionLabel: 'name',
            optionValue: 'id',
            filter: true,
          }"
        />

        <div class="flex justify-end">
          <FormSaveButton />
        </div>
      </div>
    </form>
  </Dialog>
</template>
