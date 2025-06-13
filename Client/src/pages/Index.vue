<script setup lang="ts">
import { strings } from "../utils/strings.ts";
import LinkButton from "@/components/LinkButton.vue";
import CenterCard from "@/components/layout/CenterCard.vue";
import ScreenContainer from "@/components/layout/ScreenContainer.vue";
import { RouteNames } from "@/utils/routes.ts";
import { useAuthStore } from "@/utils/stores.ts";
import Login from "~icons/material-symbols/login";
import Scoreboard from "~icons/material-symbols/scoreboard";

const authStore = useAuthStore();
</script>

<template>
  <ScreenContainer>
    <CenterCard :title="strings.appName">
      <template v-if="authStore.isAuthenticated">
        <LinkButton
          :label="strings.actions.login"
          :icon="Login"
          :to="{ name: RouteNames.TOURNAMENT_LIST }"
          full-width
        />

        <div class="my-6 h-[1px] border-t-2 border-dotted border-surface-500" />
      </template>

      <LinkButton
        :label="strings.scoreboard"
        :icon="Scoreboard"
        :to="{ name: RouteNames.SCOREBOARD }"
        severity="secondary"
        full-width
      />
    </CenterCard>
  </ScreenContainer>
</template>
