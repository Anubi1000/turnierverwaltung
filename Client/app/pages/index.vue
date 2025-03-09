<script setup lang="ts">
const authStore = useAuthStore();

await useAsyncData(() => authStore.checkAuth(), {
  server: false,
});

useSeoMeta({
  title: "Turnierverwaltung",
});
</script>

<template>
  <div class="flex h-screen items-center justify-center gap-8">
    <NuxtLink to="/scoreboard">
      <Button label="Scoreboard" size="large" class="p-6" raised />
    </NuxtLink>

    <NuxtLink v-if="authStore.isAuthenticated" to="/dashboard/tournaments">
      <Button label="Anmelden" size="large" class="p-6" raised />
    </NuxtLink>
  </div>
</template>
