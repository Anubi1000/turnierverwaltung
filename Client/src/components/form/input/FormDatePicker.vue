<script setup lang="ts">
import FormFieldWrapper from "@/components/form/FormFieldWrapper.vue";
import DatePicker from "primevue/datepicker";
import { useField } from "vee-validate";

const { name } = defineProps<{
  name: string;
  label: string;
}>();

const { value, errorMessage } = useField<Date>(() => name);
</script>

<template>
  <FormFieldWrapper :id="name" :label="label" :error="errorMessage">
    <DatePicker
      id="date"
      v-model="value"
      date-format="dd. MM yy"
      :invalid="errorMessage != undefined"
      fluid
    />
  </FormFieldWrapper>
</template>
