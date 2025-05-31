<script setup lang="ts">
import {
  type MessageProps,
  messageStyles,
} from "@/components/primevue/message.ts";
import { computed } from "vue";

const props = defineProps<MessageProps>();

const styles = computed(() =>
  messageStyles({ severity: props.severity, variant: props.variant }),
);
</script>

<template>
  <div :class="styles.root({ class: props.class })">
    <div :class="styles.content()">
      <slot />
    </div>
  </div>
</template>
