<script setup lang="ts">
defineSlots<{
  title(): VNode[];
  content(): VNode[];
  leading(): VNode[];
  trailing(): VNode[];
}>();

defineProps<{
  clickable?: boolean;
}>();
</script>

<template>
  <div class="flex items-center rounded-md p-0.5">
    <div v-if="$slots.leading" class="mr-2">
      <slot name="leading" />
    </div>

    <div class="grow">
      <div class="text-sm font-bold select-none">
        <slot name="title" />
      </div>
      <div>
        <slot name="content" />
      </div>
    </div>

    <div v-if="$slots.trailing" class="ml-2">
      <slot name="trailing" />
    </div>
  </div>
</template>
