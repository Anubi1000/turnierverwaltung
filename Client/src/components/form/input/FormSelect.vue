<script setup lang="ts">
import FormFieldWrapper from "@/components/form/FormFieldWrapper.vue";
import Select, { type SelectProps } from "primevue/select";
import { useField } from "vee-validate";
import KeyboardArrowDown from "~icons/material-symbols/keyboard-arrow-down";
import Search from "~icons/material-symbols/search";

const { name } = defineProps<{
  name: string;
  label: string;
  props?: SelectProps;
}>();

const { value, errorMessage } = useField<string>(() => name);
</script>

<template>
  <FormFieldWrapper :id="name" :label="label" :error="errorMessage">
    <Select
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
    </Select>
  </FormFieldWrapper>
</template>
