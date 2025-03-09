<script setup lang="ts">
const { selected } = defineProps<{
  label: string;
  icon: string;
  route: string;
  selected: boolean;
}>();

const rootClasses = computed(() => {
  const classes = [
    "p-3 select-none rounded-lg transition-colors flex items-center",
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
  <NuxtLink v-ripple :to="route" :class="rootClasses">
    <Icon :name="icon" />
    <span class="ml-2">{{ label }}</span>
  </NuxtLink>
</template>
