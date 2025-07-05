<script setup lang="ts">
import FormFieldWrapper from "@/components/form/FormFieldWrapper.vue";
import MultiSelect, { type MultiSelectProps } from "primevue/multiselect";
import { useField } from "vee-validate";
import KeyboardArrowDown from "~icons/material-symbols/keyboard-arrow-down";
import Search from "~icons/material-symbols/search";

const { name } = defineProps<{
  name: string;
  label: string;
  props?: MultiSelectProps;
}>();

const { value, errorMessage } = useField<string[]>(() => name);
</script>

<template>
  <FormFieldWrapper :id="name" :label="label" :error="errorMessage">
    <MultiSelect
      :id="name"
      v-model="value"
      :invalid="errorMessage != undefined"
      fluid
      v-bind="props"
    >
      <template #filtericon>
        <Search class="-m-1" />
      </template>
      <template #dropdownicon>
        <KeyboardArrowDown class="scale-125" />
      </template>
    </MultiSelect>
  </FormFieldWrapper>
</template>
