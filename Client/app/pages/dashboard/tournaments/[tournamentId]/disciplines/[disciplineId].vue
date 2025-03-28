<script setup lang="ts">
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import type { FetchError } from "ofetch";

definePageMeta({
  middleware: "auth",
  layout: "discipline-list",
});

const queryClient = useQueryClient();
const confirm = useConfirm();
const toast = useToast();

const showEditDialog = ref(false);

const disciplineId = getIdFromRoute("disciplineId");
const tournamentId = injectId("tournamentId");

const {
  data: discipline,
  isPending,
  suspense,
} = useQuery<DisciplineDetailDto>(disciplineQueryOptions(disciplineId));
if (isPending.value) await suspense();

watchEffect(() => {
  if (discipline.value) {
    useSeoMeta({ title: `Disziplin - ${discipline.value.name}` });
  }
});

const { mutate: mutateDelete } = useMutation<undefined, FetchError, undefined>({
  mutationFn: () =>
    $fetch(`/api/disciplines/${disciplineId}`, {
      method: "DELETE",
    }),
  onSuccess: async () => {
    toast.add(Toasts.itemDeleteSuccess("Disziplin", "Die"));
    navigateTo(".");

    await queryClient.refetchQueries({
      queryKey: disciplinesQueryKey(tournamentId),
    });
    await queryClient.invalidateQueries({
      queryKey: disciplineQueryKey(disciplineId),
    });
  },
  onError: (error) => {
    toast.add(Toasts.itemDeleteFail(error, "Die Disziplin"));
  },
});

function deleteDiscipline() {
  confirm.require(Dialogs.confirmDelete(discipline.value?.name, mutateDelete));
}
</script>

<template>
  <div v-if="!discipline" class="m-auto">
    <StaticMessage severity="error"
      >Die Disziplin konnte nicht geladen werden</StaticMessage
    >
  </div>

  <div v-else class="flex flex-1 flex-col">
    <DisciplineEditDialog
      v-model:visible="showEditDialog"
      :discipline-id="disciplineId"
      edit
    />

    <div class="flex w-full justify-end gap-2 px-3 pt-2">
      <ButtonGroup>
        <Button
          v-tooltip.bottom="'Löschen'"
          severity="danger"
          @click="deleteDiscipline"
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
          <DetailItem title="Name" :content="discipline.name" />
          <DetailItem
            title="Anzahl der besten Runden auf Anzeige"
            :content="discipline.amountOfBestRoundsToShow.toString()"
          />
          <DetailItem
            title="Geschlechter getrennt"
            :content="discipline.areGendersSeparated ? 'Ja' : 'Nein'"
          />
        </div>
      </DetailCard>

      <DetailCard title="Werte" class="min-w-96">
        <div class="flex flex-col gap-3">
          <div
            v-for="(value, index) in discipline.values"
            :key="index"
            class="flex flex-row gap-3"
          >
            <DetailItem title="Name" :content="value.name" class="w-full" />
            <DetailItem
              title="Wird addiert"
              :content="value.isAdded ? 'Ja' : 'Nein'"
              class="w-full"
            />
          </div>
        </div>
      </DetailCard>
    </div>
  </div>
</template>
