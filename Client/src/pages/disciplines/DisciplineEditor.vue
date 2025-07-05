<script setup lang="ts">
import CardContainer from "@/components/CardContainer.vue";
import HeadlineRow from "@/components/HeadlineRow.vue";
import DetailCard from "@/components/detail/DetailCard.vue";
import FormSaveButton from "@/components/form/FormSaveButton.vue";
import FormCheckbox from "@/components/form/input/FormCheckbox.vue";
import FormInputNumber from "@/components/form/input/FormInputNumber.vue";
import FormInputText from "@/components/form/input/FormInputText.vue";
import Message from "@/components/primevue/Message.vue";
import type { DisciplineEditDto } from "@/utils/api/schemas";
import { strings } from "@/utils/strings.ts";
import { disciplineEditDtoSchema } from "@/utils/validation.ts";
import { toTypedSchema } from "@vee-validate/zod";
import Button from "primevue/button";
import { FieldArray, useForm } from "vee-validate";
import Add from "~icons/material-symbols/add-2";
import Delete from "~icons/material-symbols/delete";

const { initialValues } = defineProps<{
  edit?: boolean;
  initialValues: DisciplineEditDto;
}>();

const emit = defineEmits<{
  (e: "submit", value: DisciplineEditDto): void;
}>();

const { handleSubmit, errors } = useForm({
  validationSchema: toTypedSchema(disciplineEditDtoSchema),
  // @ts-expect-error Type somehow not valid
  initialValues: structuredClone(initialValues),
});

const onSubmit = handleSubmit(async (values) => {
  const data: DisciplineEditDto = structuredClone(values);

  emit("submit", data);
});
</script>

<template>
  <form class="flex flex-grow flex-col" @submit="onSubmit">
    <HeadlineRow
      :title="edit ? strings.discipline.edit : strings.discipline.create"
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
            name="amountOfBestRoundsToShow"
            :label="strings.amountOfBestRoundsToShow"
            :props="{
              min: 1,
              max: 5,
              showButtons: true,
            }"
          />

          <FormCheckbox
            name="areGendersSeparated"
            :label="strings.gendersSeparated"
          />

          <FormCheckbox name="showInResults" :label="strings.isShownInResult" />
        </div>
      </DetailCard>

      <DetailCard :title="strings.value.items" class="w-[30rem]">
        <FieldArray name="values" v-slot="{ fields, push, remove }">
          <table class="w-full" v-if="fields.length !== 0">
            <thead>
              <tr>
                <th>{{ strings.name }}</th>
                <th>{{ strings.isAdded }}</th>
                <th v-if="!edit">{{ strings.actions.delete }}</th>
              </tr>
            </thead>

            <tbody>
              <tr v-for="(field, idx) in fields" :key="field.key">
                <td class="pb-2">
                  <FormInputText
                    :name="`values[${idx}].name`"
                    :label="strings.name"
                  />
                </td>

                <td class="pb-2 text-center">
                  <FormCheckbox
                    :name="`values[${idx}].isAdded`"
                    class="w-full justify-center"
                  />
                </td>

                <td v-if="!edit" class="pb-2 text-center">
                  <Button text severity="danger" @click="remove(idx)">
                    <template #icon>
                      <Delete />
                    </template>
                  </Button>
                </td>
              </tr>
            </tbody>
          </table>

          <Message
            v-if="errors.values"
            severity="error"
            variant="simple"
            class="mb-1"
          >
            {{ errors.values }}
          </Message>

          <Button
            :label="strings.value.create"
            class="w-full"
            @click="push({ name: '', isAdded: true })"
            :disabled="edit"
          >
            <template #icon>
              <Add />
            </template>
          </Button>
        </FieldArray>
      </DetailCard>
    </CardContainer>
  </form>
</template>
