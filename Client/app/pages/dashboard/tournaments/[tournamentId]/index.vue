<script setup lang="ts">
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import type { FetchError } from "ofetch";

definePageMeta({
  middleware: "auth",
  layout: "tournament-list",
});

const route = useRoute();
const queryClient = useQueryClient();
const confirm = useConfirm();
const toast = useToast();

const showEditDialog = ref(false);

const tournamentId = getIdFromRoute("tournamentId");

const {
  data: tournament,
  isPending,
  suspense,
} = useQuery<TournamentDetailDto>(tournamentQueryOptions(tournamentId));
if (isPending.value) await suspense();

watchEffect(() => {
  if (tournament.value) {
    useSeoMeta({ title: `Turnier - ${tournament.value.name}` });
  }
});

const { mutate: mutateDelete } = useMutation<undefined, FetchError, undefined>({
  mutationFn: () =>
    $fetch(`/api/tournaments/${tournamentId}`, {
      method: "DELETE",
    }),
  onSuccess: async () => {
    toast.add(Toasts.itemDeleteSuccess("Turnier", "Das"));
    navigateTo(".");
    await queryClient.refetchQueries({ queryKey: tournamentsQueryKey() });
    await queryClient.invalidateQueries({
      queryKey: tournamentQueryKey(tournamentId),
    });
  },
  onError: (error) => {
    toast.add(Toasts.itemDeleteFail(error, "Das Turnier"));
  },
});

function deleteTournament() {
  confirm.require(Dialogs.confirmDelete(tournament.value?.name, mutateDelete));
}
</script>

<template>
  <div v-if="!tournament" class="m-auto">
    <StaticMessage severity="error"
      >Das Turnier konnte nicht geladen werden</StaticMessage
    >
  </div>

  <div v-else class="flex flex-1 flex-col">
    <TournamentEditDialog
      v-model:visible="showEditDialog"
      :tournament-id="tournamentId"
      edit
    />

    <div class="flex w-full justify-end gap-2 px-3 pt-2">
      <ButtonGroup>
        <Button
          v-tooltip.bottom="'Löschen'"
          severity="danger"
          @click="deleteTournament"
        >
          <Icon name="mdi:delete" />
        </Button>

        <Button v-tooltip.bottom="'Bearbeiten'" @click="showEditDialog = true">
          <Icon name="mdi:edit" />
        </Button>

        <Button @click="navigateTo(`${route.path}/overview`)">
          <Icon name="mdi:open-in-new" />
          Turnier öffnen
        </Button>
      </ButtonGroup>
    </div>

    <div class="flex flex-wrap gap-3 overflow-y-auto px-3 py-2">
      <DetailCard title="Allgemein">
        <div class="flex flex-col gap-3">
          <DetailItem title="Name" :content="tournament.name" />
          <DetailItem title="Datum" :content="formatDate(tournament.date)" />
          <DetailItem
            title="Teamgröße"
            :content="tournament.teamSize.toString()"
          />
        </div>
      </DetailCard>

      <DetailCard title="Größe">
        <div class="grid grid-cols-2 gap-3">
          <DetailItem
            title="Anzahl Vereine"
            :content="tournament.clubCount.toString()"
          />
          <DetailItem
            title="Anzahl Teilnehmer"
            :content="tournament.participantCount.toString()"
          />
          <DetailItem
            title="Anzahl Disziplinen"
            :content="tournament.disciplineCount.toString()"
          />
          <DetailItem
            title="Anzahl Teams"
            :content="tournament.teamCount.toString()"
          />
        </div>
      </DetailCard>
    </div>
  </div>
</template>
