<script setup lang="ts">
import Checkbox, { type CheckboxProps } from "primevue/checkbox";
import { useField } from "vee-validate";

const { name } = defineProps<{
  name: string;
  label?: string;
  props?: CheckboxProps;
}>();

const { value } = useField<boolean>(() => name);
</script>

<template>
  <div class="flex flex-row items-center gap-2">
    <Checkbox v-model="value" :input-id="name" binary v-bind="props" />
    <label v-if="label" :for="name">{{ label }}</label>
  </div>
</template>
