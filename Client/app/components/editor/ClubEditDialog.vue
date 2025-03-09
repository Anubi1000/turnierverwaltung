<script setup lang="ts">
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import type { FetchError } from "ofetch";

const { edit = false, clubId = -1 } = defineProps<{
  edit?: boolean;
  clubId?: number;
}>();
const visible = defineModel<boolean>("visible", { default: false });

const queryClient = useQueryClient();
const toast = useToast();

const tournamentId = injectId("tournamentId");

const { data, isPending, suspense } = useQuery<ClubDetailDto>({
  ...QueryOptions.club(clubId),
  enabled: edit,
});
if (edit && isPending.value) await suspense();

const initialValues = computed(() => ({
  name: data.value?.name ?? "",
}));

const { handleSubmit, resetForm, setValues } = useForm({
  validationSchema: toTypedSchema(clubEditSchema),
  initialValues: initialValues.value,
  keepValuesOnUnmount: true,
});

watch(visible, (newVal, oldVal) => {
  if (newVal && !oldVal) {
    resetForm();
    setValues(initialValues.value);
  }
});

const { mutate } = useMutation<number | undefined, FetchError, ClubEditDto>({
  // Mutation
  mutationFn: edit
    ? (item) =>
        $fetch(`/api/clubs/${clubId}`, {
          method: "PUT",
          body: item,
        })
    : (item) =>
        $fetch(`/api/tournaments/${tournamentId}/clubs`, {
          method: "POST",
          body: item,
        }),

  // Success
  onSuccess: async (data?: number) => {
    toast.add(
      Toasts.itemModifySuccess("Verein", "Der", edit ? "edit" : "create"),
    );

    visible.value = false;
    await queryClient.invalidateQueries({
      queryKey: QueryKeys.clubs(tournamentId),
    });

    if (edit) {
      await queryClient.invalidateQueries({ queryKey: QueryKeys.club(clubId) });
    } else {
      navigateTo(`/dashboard/tournaments/${tournamentId}/clubs/${data}`);
    }
  },

  // Error
  onError: (error: FetchError) => {
    toast.add(
      Toasts.itemModifyFail(error, "Der Verein", edit ? "edit" : "create"),
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
    :header="!edit ? 'Neuer Verein' : 'Verein bearbeiten'"
    class="w-96"
  >
    <form @submit="onSubmit">
      <div class="flex flex-col gap-3">
        <FormInputText name="name" label="Name" />

        <div class="flex justify-end">
          <FormSaveButton />
        </div>
      </div>
    </form>
  </Dialog>
</template>
