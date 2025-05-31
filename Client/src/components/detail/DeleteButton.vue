<script setup lang="ts">
import { strings } from "@/utils/strings.ts";
import Button from "primevue/button";
import Delete from "~icons/material-symbols/delete";
</script>

<template>
  <Button :label="strings.delete" severity="danger">
    <template #icon>
      <Delete />
    </template>
  </Button>
</template>
