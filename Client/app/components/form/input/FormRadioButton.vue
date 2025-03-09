<script setup lang="ts">
const { name } = defineProps<{
  name: string;
  options: { id: string; label: string }[];
}>();

const { value } = useField<string>(() => name);
</script>

<template>
  <div class="grid gap-2">
    <div
      v-for="{ id, label } in options"
      :key="id"
      class="flex flex-row items-center gap-2"
    >
      <RadioButton v-model="value" :input-id="id" :name="name" :value="id" />
      <label :for="id">{{ label }}</label>
    </div>
  </div>
</template>
