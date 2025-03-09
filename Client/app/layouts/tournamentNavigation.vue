<script setup lang="ts">
import TournamentNavMenu from "~/components/menu/TournamentNavMenu.vue";

const route = useRoute();
</script>

<template>
  <div class="flex h-dvh w-dvw flex-col">
    <Toolbar class="rounded-none border-none">
      <template #start>
        <NuxtLink :to="`/dashboard/tournaments/${route.params.tournamentId}`">
          <h1 class="text-2xl font-bold select-none">Turnierverwaltung</h1>
        </NuxtLink>
      </template>

      <template #end>
        <div id="navbar_end" />
      </template>
    </Toolbar>

    <div class="flex h-screen flex-row overflow-y-hidden">
      <TournamentNavMenu />

      <slot />
    </div>
  </div>
</template>
