<script setup lang="ts">
import { strings } from "../utils/strings.ts";
import { RouteNames } from "@/utils/routes.ts";
import { useAuthStore } from "@/utils/stores.ts";
import Button from "primevue/button";
import Login from "~icons/material-symbols/login";
import Scoreboard from "~icons/material-symbols/scoreboard";

const authStore = useAuthStore();
</script>

<template>
  <div class="flex h-screen w-screen bg-surface-50">
    <div
      class="m-auto flex w-96 flex-col justify-center rounded-xl border border-surface-200 bg-surface-0 p-6"
    >
      <h1 class="mb-8 text-center text-2xl font-bold select-none">
        {{ strings.appName }}
      </h1>

      <RouterLink
        v-if="authStore.isAuthenticated"
        class="w-full"
        :to="{ name: RouteNames.TOURNAMENT_LIST }"
      >
        <Button class="w-full" :label="strings.login">
          <template #icon>
            <Login class="p-button-icon-right" />
          </template>
        </Button>
      </RouterLink>

      <div
        v-if="authStore.isAuthenticated"
        class="my-6 h-[1px] border-t-2 border-dotted border-gray-500"
      ></div>

      <RouterLink class="w-full" :to="{ name: RouteNames.SCOREBOARD }">
        <Button class="w-full" :label="strings.scoreboard" severity="secondary">
          <template #icon>
            <Scoreboard class="p-button-icon-right" />
          </template>
        </Button>
      </RouterLink>
    </div>
  </div>
</template>
