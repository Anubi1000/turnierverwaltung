<script setup lang="ts">
import type { MultiSelectProps } from "primevue";

const { name } = defineProps<{
  name: string;
  label: string;
  props?: MultiSelectProps;
}>();

const { value, errorMessage } = useField<string[]>(() => name);
</script>

<template>
  <BasicFormFieldWrapper :id="name" :label="label" :error="errorMessage">
    <MultiSelect
      :id="name"
      v-model="value"
      :invalid="errorMessage != undefined"
      fluid
      v-bind="props"
    />
  </BasicFormFieldWrapper>
</template>
