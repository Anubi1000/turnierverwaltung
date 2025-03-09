<script setup lang="ts">
import TournamentNavigation from "~/layouts/tournamentNavigation.vue";
import { useQuery } from "@tanstack/vue-query";

const route = useRoute();

const tournamentId = provideIdFromRoute("tournamentId");

const { data, isPending, suspense } = useQuery<ListTeamDisciplineDto[]>(
  QueryOptions.teamDisciplines(tournamentId),
);
if (isPending) await suspense();

const items = computed(() => {
  if (!data.value) return undefined;
  return data.value.map<ItemListItem>((item) => ({
    id: item.id.toString(),
    link: `/dashboard/tournaments/${tournamentId}/team_disciplines/${item.id}`,
    title: item.name,
  }));
});

const selectedItem = computed(() =>
  items.value?.find(
    (item) => item.id.toString() === route.params.teamDisciplineId,
  ),
);

const showCreateDialog = ref(false);
</script>

<template>
  <TournamentNavigation>
    <Teleport defer to="#navbar_end">
      <Button @click="showCreateDialog = true">
        <Icon name="material-symbols:add-2" />
        Neue Team-Disziplin
      </Button>
    </Teleport>

    <TeamDisciplineEditDialog v-model:visible="showCreateDialog" />

    <ItemList
      :items="items"
      :selected-item="selectedItem"
      load-failed-text="Das Laden der Team-Disziplinen ist fehlgeschlagen"
      no-items-text="Es sind keine Team-Disziplinen vorhanden"
    />

    <div class="bg-surface-50 flex flex-1 rounded-tl-xl">
      <slot />
    </div>
  </TournamentNavigation>
</template>
