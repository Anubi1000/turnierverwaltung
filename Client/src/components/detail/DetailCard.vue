<script setup lang="ts">
import Card from "primevue/card";

defineProps<{
  title: string;
}>();
</script>

<template>
  <Card class="w-96">
    <template #title>
      <div v-if="$slots.trailing" class="flex justify-between">
        <h6 class="select-none">{{ title }}</h6>

        <div>
          <slot name="trailing" />
        </div>
      </div>

      <h6 v-else class="select-none">{{ title }}</h6>
    </template>
    <template #content>
      <slot />
    </template>
  </Card>
</template>
