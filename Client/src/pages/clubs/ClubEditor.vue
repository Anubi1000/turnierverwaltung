<script setup lang="ts">
import CardContainer from "@/components/CardContainer.vue";
import HeadlineRow from "@/components/HeadlineRow.vue";
import DetailCard from "@/components/detail/DetailCard.vue";
import FormSaveButton from "@/components/form/FormSaveButton.vue";
import FormInputText from "@/components/form/input/FormInputText.vue";
import type { ClubEditDto } from "@/utils/api/schemas";
import { strings } from "@/utils/strings.ts";
import { clubEditDtoSchema } from "@/utils/validation.ts";
import { toTypedSchema } from "@vee-validate/zod";
import { useForm } from "vee-validate";

const { initialValues } = defineProps<{
  edit?: boolean;
  initialValues: ClubEditDto;
}>();

const emit = defineEmits<{
  (e: "submit", value: ClubEditDto): void;
}>();

const { handleSubmit } = useForm({
  validationSchema: toTypedSchema(clubEditDtoSchema),
  initialValues: initialValues,
});

const onSubmit = handleSubmit(async (values) => {
  const data: ClubEditDto = structuredClone(values);

  emit("submit", data);
});
</script>

<template>
  <form class="flex flex-grow flex-col" @submit="onSubmit">
    <HeadlineRow :title="edit ? strings.club.edit : strings.club.create">
      <template #actions>
        <FormSaveButton />
      </template>
    </HeadlineRow>

    <CardContainer>
      <DetailCard :title="strings.general">
        <div class="flex flex-col gap-2">
          <FormInputText name="name" :label="strings.name" />
        </div>
      </DetailCard>
    </CardContainer>
  </form>
</template>
