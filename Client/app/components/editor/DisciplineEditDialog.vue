<script setup lang="ts">
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import type { FetchError } from "ofetch";
import { cloneDeep } from "lodash-es";

const { edit = false, disciplineId = -1 } = defineProps<{
  edit?: boolean;
  disciplineId?: number;
}>();
const visible = defineModel<boolean>("visible", { default: false });

const queryClient = useQueryClient();
const toast = useToast();

const tournamentId = inject<number>("tournamentId", -1);

const { data, isPending, suspense } = useQuery<DisciplineDetailDto>({
  ...QueryOptions.discipline(disciplineId),
  enabled: edit,
});
if (edit && isPending.value) await suspense();

const initialValues = computed<DisciplineEditDto>(() => ({
  name: data.value?.name ?? "",
  amountOfBestRoundsToShow: data.value?.amountOfBestRoundsToShow ?? 1,
  areGendersSeparated: data.value?.areGendersSeparated ?? false,
  values: data.value?.values ?? [{ name: "", isAdded: true }],
}));

const { handleSubmit, resetForm, setValues, errors } = useForm({
  validationSchema: toTypedSchema(disciplineEditSchema),
  initialValues: cloneDeep(initialValues.value),
  keepValuesOnUnmount: true,
});

watch(visible, (newVal, oldVal) => {
  if (newVal && !oldVal) {
    resetForm();
    setValues(cloneDeep(initialValues.value));
  }
});

const generalError = computed(() => {
  return (
    (errors.value.name ||
      errors.value.amountOfBestRoundsToShow ||
      errors.value.areGendersSeparated) !== undefined
  );
});

const valuesError = computed(() => {
  if (errors.value.values !== undefined) return true;

  for (const key of Object.keys(errors.value)) {
    if (key.match("^values\\[\\d+\\]\\.name$")) return true;
  }

  return false;
});

const {
  remove: removeValue,
  push: addValue,
  fields: values,
} = useFieldArray<DisciplineEditDtoValue>("values");

const { mutate } = useMutation<
  number | undefined,
  FetchError,
  DisciplineEditDto
>({
  mutationFn: edit
    ? (item) =>
        $fetch(`/api/disciplines/${disciplineId}`, {
          method: "PUT",
          body: item,
        })
    : (item) =>
        $fetch(`/api/tournaments/${tournamentId}/disciplines`, {
          method: "POST",
          body: item,
        }),

  onSuccess: async (data?: number) => {
    toast.add(
      Toasts.itemModifySuccess("Disziplin", "Die", edit ? "edit" : "create"),
    );

    visible.value = false;
    await queryClient.invalidateQueries({
      queryKey: QueryKeys.disciplines(tournamentId),
    });

    if (edit) {
      await queryClient.invalidateQueries({
        queryKey: QueryKeys.discipline(disciplineId),
      });
    } else {
      navigateTo(`/dashboard/tournaments/${tournamentId}/disciplines/${data}`);
    }
  },

  onError: (error: FetchError) => {
    toast.add(
      Toasts.itemModifyFail(error, "Die Disziplin", edit ? "edit" : "create"),
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
    :header="!edit ? 'Neue Disziplin' : 'Disziplin bearbeiten'"
    class="w-[30rem]"
  >
    <form @submit="onSubmit">
      <Tabs value="general">
        <TabList>
          <Tab value="general" :class="generalError ? 'text-red-500' : ''"
            >Allgemein</Tab
          >
          <Tab value="values" :class="valuesError ? 'text-red-500' : ''"
            >Werte</Tab
          >
        </TabList>

        <TabPanels class="px-0">
          <TabPanel value="general">
            <div class="flex flex-col gap-3">
              <FormInputText name="name" label="Name" />
              <FormInputNumber
                name="amountOfBestRoundsToShow"
                label="Anzahl der besten Runden auf Anzeige"
                :props="{ min: 1, max: 5, showButtons: true }"
              />
              <FormCheckbox
                name="areGendersSeparated"
                label="Geschlechter getrennt"
              />
            </div>
          </TabPanel>

          <TabPanel value="values">
            <div class="flex flex-col gap-3">
              <Button
                v-if="!edit"
                @click="addValue({ name: '', isAdded: true })"
              >
                Neuer Wert
              </Button>

              <table>
                <thead>
                  <tr>
                    <th>Name</th>
                    <th>Wird addiert</th>
                    <th v-if="!edit">Löschen</th>
                  </tr>
                </thead>

                <tbody>
                  <tr v-for="(field, idx) in values" :key="field.key">
                    <td>
                      <FormInputText
                        :name="`values[${idx}].name`"
                        label="Name"
                      />
                    </td>

                    <td class="text-center">
                      <FormCheckbox
                        :name="`values[${idx}].isAdded`"
                        class="w-full justify-center"
                      />
                    </td>

                    <td v-if="!edit" class="text-center">
                      <PButton
                        icon="material-symbols:delete"
                        variant="text"
                        severity="danger"
                        @click="removeValue(idx)"
                      />
                    </td>
                  </tr>
                </tbody>

                <tfoot>
                  <Message
                    v-if="errors.values"
                    severity="error"
                    variant="simple"
                  >
                    {{ errors.values }}
                  </Message>
                </tfoot>
              </table>
            </div>
          </TabPanel>
        </TabPanels>
      </Tabs>
      <div class="flex justify-end">
        <FormSaveButton />
      </div>
    </form>
  </Dialog>
</template>
