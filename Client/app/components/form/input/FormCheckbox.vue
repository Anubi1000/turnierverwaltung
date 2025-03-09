<script setup lang="ts">
const { name } = defineProps<{
  name: string;
  label?: string;
}>();

const { value } = useField<boolean>(() => name, undefined, {
  type: "checkbox",
});
</script>

<template>
  <div class="flex flex-row items-center gap-2">
    <Checkbox v-model="value" :input-id="name" binary />
    <label v-if="label" :for="name">{{ label }}</label>
  </div>
</template>
