<script setup lang="ts">
import FormFieldWrapper from "@/components/form/FormFieldWrapper.vue";
import InputText from "primevue/inputtext";
import { useField } from "vee-validate";

const { name } = defineProps<{
  name: string;
  label: string;
}>();

const { value, errorMessage } = useField<string>(() => name);
</script>

<template>
  <FormFieldWrapper :id="name" :label="label" :error="errorMessage">
    <InputText
      :id="name"
      v-model="value"
      type="text"
      :invalid="errorMessage != undefined"
      class="w-full"
    />
  </FormFieldWrapper>
</template>
