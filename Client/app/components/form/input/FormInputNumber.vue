<script setup lang="ts">
import type { InputNumberProps } from "primevue";

const { name } = defineProps<{
  name: string;
  label: string;
  props?: InputNumberProps;
}>();

const { value, errorMessage } = useField<number>(() => name);
</script>

<template>
  <BasicFormFieldWrapper :id="name" :label="label" :error="errorMessage">
    <InputNumber
      :id="name"
      v-model="value"
      type="text"
      :invalid="errorMessage != undefined"
      fluid
      v-bind="props"
    />
  </BasicFormFieldWrapper>
</template>
