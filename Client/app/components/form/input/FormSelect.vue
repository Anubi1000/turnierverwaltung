<script setup lang="ts">
import type { SelectProps } from "primevue";

const { name } = defineProps<{
  name: string;
  label: string;
  props?: SelectProps;
}>();

const { value, errorMessage } = useField<string>(() => name);
</script>

<template>
  <BasicFormFieldWrapper :id="name" :label="label" :error="errorMessage">
    <Select
      :id="name"
      v-model="value"
      :invalid="errorMessage != undefined"
      fluid
      v-bind="props"
    />
  </BasicFormFieldWrapper>
</template>
