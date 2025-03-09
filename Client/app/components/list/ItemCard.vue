<script setup lang="ts">
const { selected = false } = defineProps<{
  selected?: boolean;
}>();

defineSlots<{
  title(): VNode[];
  content(): VNode[];
}>();

const rootClasses = computed(() => {
  const classes = [
    "p-5 rounded-xl gap-2 flex flex-col shadow select-none transition-colors",
  ];
  if (selected) {
    classes.push("bg-primary/15 hover:bg-primary-emphasis/20");
  } else {
    classes.push("bg-white hover:bg-highlight");
  }
  return classes;
});
</script>

<template>
  <div v-ripple :class="rootClasses">
    <div v-if="$slots.title" class="text-xl">
      <slot name="title" />
    </div>
    <div v-if="$slots.content">
      <slot name="content" />
    </div>
  </div>
</template>
