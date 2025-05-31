<script setup lang="ts">
import { strings } from "../utils/strings.ts";
import { RouteNames } from "@/utils/routes.ts";
import Button from "primevue/button";
</script>

<template>
  <div class="flex h-screen w-screen bg-surface-50">
    <div
      class="m-auto flex w-96 flex-col justify-center rounded-xl border border-surface-200 bg-surface-0 p-6"
    >
      <h1 class="mb-8 text-center text-2xl font-bold select-none">
        {{ strings.notFound.pageNotFound }}
      </h1>

      <RouterLink class="w-full" :to="{ name: RouteNames.ROOT }">
        <Button class="w-full">{{ strings.notFound.backToStart }}</Button>
      </RouterLink>
    </div>
  </div>
</template>
