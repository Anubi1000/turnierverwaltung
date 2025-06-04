<script setup lang="ts">
import FormSaveButton from "@/components/form/FormSaveButton.vue";
import FormCheckbox from "@/components/form/input/FormCheckbox.vue";
import FormMultiSelect from "@/components/form/input/FormMultiSelect.vue";
import { getTournamentScoreboardAsWord } from "@/utils/api/api.ts";
import type { ScoreboardData, WordDocGenerationDto } from "@/utils/api/schemas";
import { strings } from "@/utils/strings";
import { extractFileNameFromContentDisposition } from "@/utils/utils.ts";
import { wordDocGenerationDtoSchema } from "@/utils/validation";
import { toTypedSchema } from "@vee-validate/zod";
import { saveAs } from "file-saver";
import Dialog from "primevue/dialog";
import { useForm } from "vee-validate";

const { tournamentId } = defineProps<{
  data: ScoreboardData;
  tournamentId: number;
}>();

const visible = defineModel<boolean>("visible", { required: true });

const { handleSubmit } = useForm({
  validationSchema: toTypedSchema(wordDocGenerationDtoSchema),
  initialValues: {
    // @ts-expect-error Tables to export are initially empty
    tablesToExport: [],
    separateDocuments: false,
  },
});

const onSubmit = handleSubmit(async (values) => {
  const body: WordDocGenerationDto = {
    tablesToExport: [...values.tablesToExport],
    separateDocuments: values.separateDocuments,
  };

  const response = await getTournamentScoreboardAsWord(tournamentId, body, {
    responseType: "blob",
  });

  const header = response.headers["content-disposition"] as string;
  const filename =
    extractFileNameFromContentDisposition(header) ?? "Ergebnisse";

  const blob = response.data as Blob;

  saveAs(blob, filename);
  visible.value = false;
});
</script>

<template>
  <Dialog
    v-model:visible="visible"
    :draggable="false"
    modal
    :header="strings.scores.downloadOverview"
    class="select-none"
  >
    <form class="flex w-96 flex-col gap-2" @submit="onSubmit">
      <FormMultiSelect
        name="tablesToExport"
        :label="strings.scores.exportTables"
        :props="{
          options: data.tables,
          optionLabel: 'name',
          optionValue: 'id',
          filter: true,
          filterPlaceholder: strings.searchTerm,
          display: 'chip',
          pt: {
            label: {
              class:
                'flex flex-wrap whitespace-normal overflow-visible items-start',
            },
            chipItem: {
              class: 'first:pt-5',
            },
          },
        }"
      />

      <FormCheckbox
        name="separateDocuments"
        :label="strings.scores.separateDocuments"
      />

      <FormSaveButton />
    </form>
  </Dialog>
</template>
