<script setup lang="ts">
import { strings } from "../utils/strings.ts";
import LinkButton from "@/components/LinkButton.vue";
import CenterCard from "@/components/layout/CenterCard.vue";
import ScreenContainer from "@/components/layout/ScreenContainer.vue";
import { RouteNames } from "@/utils/routes.ts";
import ArrowBack from "~icons/material-symbols/arrow-back";
</script>

<template>
  <ScreenContainer>
    <CenterCard :title="strings.notFound.pageNotFound">
      <LinkButton
        :label="strings.notFound.backToStart"
        :icon="ArrowBack"
        :to="{ name: RouteNames.ROOT }"
        full-width
      />
    </CenterCard>
  </ScreenContainer>
</template>
