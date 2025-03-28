<script setup lang="ts">
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import type { FetchError } from "ofetch";

definePageMeta({
  middleware: "auth",
  layout: "club-list",
});

const queryClient = useQueryClient();
const confirm = useConfirm();
const toast = useToast();

const showEditDialog = ref(false);

const clubId = getIdFromRoute("clubId");
const tournamentId = injectId("tournamentId");

const {
  data: club,
  isPending,
  suspense,
} = useQuery<ClubDetailDto>(clubQueryOptions(clubId));
if (isPending.value) await suspense();

watchEffect(() => {
  if (club.value) {
    useSeoMeta({ title: `Verein - ${club.value.name}` });
  }
});

const { mutate: mutateDelete } = useMutation<undefined, FetchError, undefined>({
  mutationFn: () =>
    $fetch(`/api/clubs/${clubId}`, {
      method: "DELETE",
    }),
  onSuccess: async () => {
    toast.add(Toasts.itemDeleteSuccess("Verein", "Der"));
    navigateTo(".");

    await queryClient.refetchQueries({
      queryKey: clubsQueryKey(tournamentId),
    });
    await queryClient.invalidateQueries({
      queryKey: clubQueryKey(clubId),
    });
  },
  onError: (error) => {
    toast.add(Toasts.itemDeleteFail(error, "Der Verein"));
  },
});

function deleteClub() {
  confirm.require(Dialogs.confirmDelete(club.value?.name, mutateDelete));
}
</script>

<template>
  <div v-if="!club" class="m-auto">
    <StaticMessage severity="error"
      >Der Verein konnte nicht geladen werden</StaticMessage
    >
  </div>

  <div v-else class="flex flex-1 flex-col">
    <ClubEditDialog v-model:visible="showEditDialog" :club-id="clubId" edit />

    <div class="flex w-full justify-end gap-2 px-3 pt-2">
      <ButtonGroup>
        <Button
          v-tooltip.bottom="'Löschen'"
          severity="danger"
          @click="deleteClub"
        >
          <Icon name="mdi:delete" />
        </Button>

        <Button v-tooltip.bottom="'Bearbeiten'" @click="showEditDialog = true">
          <Icon name="mdi:edit" />
        </Button>
      </ButtonGroup>
    </div>

    <div class="flex flex-wrap gap-3 overflow-y-auto px-3 py-2">
      <DetailCard title="Allgemein">
        <div class="flex flex-col gap-3">
          <DetailItem title="Name" :content="club.name" />
          <DetailItem
            title="Anzahl Mitglieder"
            :content="club.memberCount.toString()"
          />
        </div>
      </DetailCard>
    </div>
  </div>
</template>
