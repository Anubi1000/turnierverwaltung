<script setup lang="ts">
import { type CardProps, cardStyles } from "@/components/card/card.ts";
import { computed } from "vue";

const props = defineProps<CardProps>();

const styles = computed(() =>
  cardStyles({ selectable: props.selectable, selected: props.selected }),
);
</script>

<template>
  <div :class="styles.root({ class: props.class })">
    <div :class="styles.title()">{{ props.title }}</div>
    <div v-if="props.content">{{ props.content }}</div>
  </div>
</template>
