<script setup lang="ts">
import { useRouterViewKey } from "@/utils/utils.ts";

const routerViewKey = useRouterViewKey(1);
</script>

<template>
  <RouterView :key="routerViewKey" />
</template>
