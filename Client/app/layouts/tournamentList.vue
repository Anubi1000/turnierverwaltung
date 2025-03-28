<script setup lang="ts">
import { useQuery } from "@tanstack/vue-query";

const route = useRoute();

const { data, isPending, suspense } = useQuery<ListTournamentDto[]>(
  tournamentsQueryOptions(),
);
if (isPending) await suspense();

const items = computed(() => {
  if (!data.value) return undefined;
  return data.value.map<ItemListItem>((item) => ({
    id: item.id.toString(),
    link: `/dashboard/tournaments/${item.id}`,
    title: item.name,
    content: formatDate(item.date),
  }));
});

const selectedItem = computed(() =>
  items.value?.find((item) => item.id.toString() === route.params.tournamentId),
);

const showCreateDialog = ref(false);
</script>

<template>
  <div class="flex h-dvh w-dvw flex-col">
    <Toolbar class="rounded-none border-none">
      <template #start>
        <NuxtLink to="/">
          <h1 class="text-2xl font-bold select-none">Turnierverwaltung</h1>
        </NuxtLink>
      </template>

      <template #end>
        <Button @click="showCreateDialog = true">
          <Icon name="material-symbols:add-2" />
          Neues Turnier
        </Button>
      </template>
    </Toolbar>

    <div class="flex h-screen flex-row overflow-y-hidden">
      <ItemList
        :items="items"
        :selected-item="selectedItem"
        load-failed-text="Das Laden der Turniere ist fehlgeschlagen"
        no-items-text="Es sind keine Turniere vorhanden"
      />

      <div class="bg-surface-50 flex flex-1 rounded-tl-xl">
        <slot />
      </div>
    </div>

    <TournamentEditDialog v-model:visible="showCreateDialog" />
  </div>
</template>
