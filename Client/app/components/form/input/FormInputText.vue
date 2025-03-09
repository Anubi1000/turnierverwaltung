<script setup lang="ts">
const { name } = defineProps<{
  name: string;
  label: string;
}>();

const { value, errorMessage } = useField<string>(() => name);
</script>

<template>
  <BasicFormFieldWrapper :id="name" :label="label" :error="errorMessage">
    <InputText
      :id="name"
      v-model="value"
      type="text"
      :invalid="errorMessage != undefined"
      fluid
    />
  </BasicFormFieldWrapper>
</template>
