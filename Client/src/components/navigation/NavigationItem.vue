<script setup lang="ts">
import {
  type NavigationItemProps,
  navigationItemStyles,
} from "@/components/navigation/navigationItem.ts";

const props = defineProps<NavigationItemProps>();
</script>

<template>
  <div :class="navigationItemStyles({ selected: props.selected })">
    <slot />
  </div>
</template>
