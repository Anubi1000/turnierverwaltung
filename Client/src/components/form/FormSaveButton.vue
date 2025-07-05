<script setup lang="ts">
import { strings } from "@/utils/strings.ts";
import Button from "primevue/button";
import Save from "~icons/material-symbols/save";
</script>

<template>
  <Button :label="strings.actions.save" type="submit">
    <template #icon>
      <Save />
    </template>
  </Button>
</template>
