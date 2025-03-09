<script setup lang="ts">
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import type { FetchError } from "ofetch";

definePageMeta({
  middleware: "auth",
  layout: "participant-list",
});

const queryClient = useQueryClient();
const confirm = useConfirm();
const toast = useToast();

const showEditDialog = ref(false);

const participantId = getIdFromRoute("participantId");
const tournamentId = injectId("tournamentId");

const {
  data: participant,
  isPending,
  suspense,
} = useQuery<ParticipantDetailDto>(QueryOptions.participant(participantId));
if (isPending.value) await suspense();

watchEffect(() => {
  if (participant.value) {
    useSeoMeta({ title: `Teilnehmer - ${participant.value.name}` });
  }
});

const { mutate: mutateDelete } = useMutation<undefined, FetchError, undefined>({
  mutationFn: () =>
    $fetch(`/api/participants/${participantId}`, {
      method: "DELETE",
    }),
  onSuccess: async () => {
    toast.add(Toasts.itemDeleteSuccess("Teilnehmer", "Der"));
    navigateTo(".");

    await queryClient.refetchQueries({
      queryKey: QueryKeys.participants(tournamentId),
    });
    await queryClient.invalidateQueries({
      queryKey: QueryKeys.participant(participantId),
    });
  },
  onError: (error) => {
    toast.add(Toasts.itemDeleteFail(error, "Der Teilnehmer"));
  },
});

function deleteParticipant() {
  confirm.require(Dialogs.confirmDelete(participant.value?.name, mutateDelete));
}
</script>

<template>
  <!--Error Message-->
  <div v-if="!participant" class="m-auto">
    <StaticMessage severity="error"
      >Der Teilnehmer konnte nicht geladen werden</StaticMessage
    >
  </div>

  <!--Main Wrapper-->
  <div v-else class="flex flex-1 flex-col">
    <!--Edit Dialog-->
    <ParticipantEditDialog
      v-model:visible="showEditDialog"
      :participant-id="participantId"
      edit
    />

    <!--Actions-->
    <div class="flex w-full justify-end gap-2 px-3 pt-2">
      <ButtonGroup>
        <Button
          v-tooltip.bottom="'Löschen'"
          severity="danger"
          @click="deleteParticipant"
        >
          <Icon name="mdi:delete" />
        </Button>

        <Button v-tooltip.bottom="'Bearbeiten'" @click="showEditDialog = true">
          <Icon name="mdi:edit" />
        </Button>
      </ButtonGroup>
    </div>

    <!--Detail Cards-->
    <div class="flex flex-wrap gap-3 overflow-y-auto px-3 py-2">
      <!--General-->
      <DetailCard title="Allgemein">
        <div class="space-y-3">
          <DetailItem title="Name" :content="participant.name" />
          <DetailItem
            title="Startnummer"
            :content="participant.startNumber.toString()"
          />
          <DetailItem
            title="Geschlecht"
            :content="participant.gender == 'Male' ? 'Männlich' : 'Weiblich'"
          />

          <ListItem>
            <template #title>Verein</template>
            <template #content>{{ participant.clubName }}</template>

            <template #trailing>
              <NuxtLink
                :to="`/dashboard/tournaments/${tournamentId}/clubs/${participant.clubId}`"
              >
                <Button text rounded>
                  <template #icon>
                    <Icon name="material-symbols:chevron-right" />
                  </template>
                </Button>
              </NuxtLink>
            </template>
          </ListItem>
        </div>
      </DetailCard>

      <!--Disciplines-->
      <DetailCard title="Ergebnisse">
        <div class="space-y-3">
          <ListItem
            v-for="discipline in participant.disciplines"
            :key="discipline.id"
          >
            <template #title>Name</template>
            <template #content>{{ discipline.name }}</template>

            <template #trailing>
              <NuxtLink :to="`./${participantId}/results/${discipline.id}`">
                <Button text rounded>
                  <template #icon>
                    <Icon name="material-symbols:chevron-right" />
                  </template>
                </Button>
              </NuxtLink>
            </template>
          </ListItem>
        </div>
      </DetailCard>
    </div>
  </div>
</template>
