<script setup lang="ts">
import FormFieldWrapper from "@/components/form/FormFieldWrapper.vue";
import InputNumber, { type InputNumberProps } from "primevue/inputnumber";
import { useField } from "vee-validate";
import KeyboardArrowDown from "~icons/material-symbols/keyboard-arrow-down";
import KeyboardArrowUp from "~icons/material-symbols/keyboard-arrow-up";

const { name } = defineProps<{
  name: string;
  label: string;
  props?: InputNumberProps;
}>();

const { value, errorMessage } = useField<number>(() => name);
</script>

<template>
  <FormFieldWrapper :id="name" :label="label" :error="errorMessage">
    <InputNumber
      :id="name"
      v-model="value"
      type="text"
      :invalid="errorMessage != undefined"
      fluid
      v-bind="props"
    >
      <template #decrementicon>
        <KeyboardArrowDown />
      </template>
      <template #incrementicon>
        <KeyboardArrowUp />
      </template>
    </InputNumber>
  </FormFieldWrapper>
</template>
