<script setup lang="ts">
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import type { FetchError } from "ofetch";

const { edit = false, tournamentId = -1 } = defineProps<{
  edit?: boolean;
  tournamentId?: number;
}>();
const visible = defineModel<boolean>("visible", { default: false });

const queryClient = useQueryClient();
const toast = useToast();

const { data, isPending, suspense } = useQuery<TournamentDetailDto>({
  ...QueryOptions.tournament(tournamentId),
  enabled: edit,
});
if (edit && isPending.value) await suspense();

const initialValues = computed(() => ({
  name: data.value?.name ?? "",
  date: data.value?.date ? new Date(data.value?.date) : new Date(),
  teamSize: data.value?.teamSize ?? 3,
}));

const { handleSubmit, resetForm, setValues } = useForm({
  validationSchema: toTypedSchema(tournamentEditSchema),
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
  TournamentEditDto
>({
  // Mutation
  mutationFn: edit
    ? (item) =>
        $fetch(`/api/tournaments/${tournamentId}`, {
          method: "PUT",
          body: item,
        })
    : (item) =>
        $fetch("/api/tournaments", {
          method: "POST",
          body: item,
        }),

  // Success
  onSuccess: async (data?: number) => {
    toast.add(
      Toasts.itemModifySuccess("Turnier", "Das", edit ? "edit" : "create"),
    );

    visible.value = false;
    await queryClient.invalidateQueries({ queryKey: QueryKeys.tournaments() });

    if (edit) {
      await queryClient.invalidateQueries({
        queryKey: QueryKeys.tournament(tournamentId),
      });
    } else {
      navigateTo(`/dashboard/tournaments/${data}`);
    }
  },

  // Error
  onError: (error: FetchError) => {
    toast.add(
      Toasts.itemModifyFail(error, "Das Turnier", edit ? "edit" : "create"),
    );
  },
});

const onSubmit = handleSubmit((values) => {
  const tournamentData = {
    name: values.name,
    date: values.date.getTime(),
    teamSize: values.teamSize,
  };

  mutate(tournamentData);
});
</script>

<template>
  <Dialog
    v-model:visible="visible"
    :draggable="false"
    modal
    :header="!edit ? 'Neues Turnier' : 'Turnier bearbeiten'"
    class="w-96"
  >
    <form @submit="onSubmit">
      <div class="flex flex-col gap-3">
        <FormInputText name="name" label="Name" />
        <FormDatePicker name="date" label="Datum" />

        <FormInputNumber
          name="teamSize"
          label="Teamgröße"
          :props="{
            min: 2,
            max: 25,
            showButtons: true,
            disabled: edit,
          }"
        />

        <div class="flex justify-end">
          <FormSaveButton />
        </div>
      </div>
    </form>
  </Dialog>
</template>
