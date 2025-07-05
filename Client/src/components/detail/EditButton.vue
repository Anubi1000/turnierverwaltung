<script setup lang="ts">
import LinkButton from "@/components/LinkButton.vue";
import { strings } from "@/utils/strings.ts";
import type { RouteLocationRaw } from "vue-router";
import Edit from "~icons/material-symbols/edit";

defineProps<{
  link: RouteLocationRaw;
}>();
</script>

<template>
  <LinkButton :label="strings.actions.edit" :icon="Edit" :to="link" />
</template>
