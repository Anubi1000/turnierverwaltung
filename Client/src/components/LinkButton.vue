<script setup lang="ts">
import Button, { type ButtonProps } from "primevue/button";
import { computed, type FunctionalComponent } from "vue";
import type { RouteLocationRaw } from "vue-router";

const { fullWidth } = defineProps<{
  label: string;
  icon: FunctionalComponent;
  to: RouteLocationRaw;
  fullWidth?: boolean;
  severity?: ButtonProps["severity"];
  variant?: ButtonProps["variant"];
}>();

const buttonClass = computed(() => (fullWidth ? "w-full" : ""));
</script>

<template>
  <RouterLink :to="to">
    <Button
      :class="buttonClass"
      :label="label"
      :severity="severity"
      :variant="variant"
    >
      <template #icon>
        <component :is="icon" />
      </template>
    </Button>
  </RouterLink>
</template>
