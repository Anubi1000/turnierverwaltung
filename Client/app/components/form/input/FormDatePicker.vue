<script setup lang="ts">
const { name } = defineProps<{
  name: string;
  label: string;
}>();

const { value, errorMessage } = useField<Date>(() => name);
</script>

<template>
  <BasicFormFieldWrapper :id="name" :label="label" :error="errorMessage">
    <DatePicker
      id="date"
      v-model="value"
      date-format="dd. MM yy"
      :invalid="errorMessage != undefined"
      fluid
    />
  </BasicFormFieldWrapper>
</template>
