<script setup lang="ts">
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import type { FetchError } from "ofetch";

const { edit = false, teamDisciplineId = -1 } = defineProps<{
  edit?: boolean;
  teamDisciplineId?: number;
}>();
const visible = defineModel<boolean>("visible", { default: false });

const queryClient = useQueryClient();
const toast = useToast();

const tournamentId = injectId("tournamentId");

const { data: disciplines, isPending: areDisciplinesPending } = useQuery<
  ListDisciplineDto[]
>(disciplinesQueryOptions(tournamentId));

const { data, isPending, suspense } = useQuery<TeamDisciplineDetailDto>({
  ...teamDisciplineQueryOptions(teamDisciplineId),
  enabled: edit,
});
if (edit && isPending.value) await suspense();

const initialValues = computed<TeamDisciplineEditDto>(() => ({
  name: data.value?.name ?? "",
  basedOn: data.value?.basedOn.map(d => d.id) ?? [],
}));

const { handleSubmit, resetForm, setValues } = useForm({
  validationSchema: toTypedSchema(teamDisciplineEditSchema),
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
  TeamDisciplineEditDto
>({
  mutationFn: edit
    ? (item) =>
        $fetch(`/api/team_disciplines/${teamDisciplineId}`, {
          method: "PUT",
          body: item,
        })
    : (item) =>
        $fetch(`/api/tournaments/${tournamentId}/team_disciplines`, {
          method: "POST",
          body: item,
        }),

  onSuccess: async (data?: number) => {
    toast.add(
      Toasts.itemModifySuccess(
        "Team-Disziplin",
        "Die",
        edit ? "edit" : "create",
      ),
    );

    visible.value = false;
    await queryClient.invalidateQueries({
      queryKey: teamDisciplinesQueryKey(tournamentId),
    });

    if (edit) {
      await queryClient.invalidateQueries({
        queryKey: teamDisciplineQueryKey(teamDisciplineId),
      });
    } else {
      navigateTo(
        `/dashboard/tournaments/${tournamentId}/team_disciplines/${data}`,
      );
    }
  },

  onError: (error: FetchError) => {
    toast.add(
      Toasts.itemModifyFail(
        error,
        "Die Team-Disziplin",
        edit ? "edit" : "create",
      ),
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
    :header="!edit ? 'Neue Team-Disziplin' : 'Team-Disziplin bearbeiten'"
    class="w-96"
  >
    <ProgressBar
      v-if="areDisciplinesPending"
      mode="indeterminate"
      class="absolute top-16 left-0 z-10 h-1 w-full"
    />

    <form @submit="onSubmit">
      <div class="flex flex-col gap-3">
        <FormInputText name="name" label="Name" />

        <FormMultiSelect
          name="basedOn"
          label="Basiert auf"
          :props="{
            options: disciplines ?? [],
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
