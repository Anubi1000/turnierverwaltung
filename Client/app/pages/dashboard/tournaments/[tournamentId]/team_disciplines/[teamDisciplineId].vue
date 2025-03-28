<script setup lang="ts">
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query";
import type { FetchError } from "ofetch";

definePageMeta({
  middleware: "auth",
  layout: "team-discipline-list",
});

const queryClient = useQueryClient();
const confirm = useConfirm();
const toast = useToast();

const showEditDialog = ref(false);

const teamDisciplineId = getIdFromRoute("teamDisciplineId");
const tournamentId = injectId("tournamentId");

const {
  data: teamDiscipline,
  isPending,
  suspense,
} = useQuery<TeamDisciplineDetailDto>(teamDisciplineQueryOptions(teamDisciplineId));
if (isPending.value) await suspense();

watchEffect(() => {
  if (teamDiscipline.value) {
    useSeoMeta({ title: `Team-Disziplin - ${teamDiscipline.value.name}` });
  }
});

const { mutate: mutateDelete } = useMutation<undefined, FetchError, undefined>({
  mutationFn: () =>
    $fetch(`/api/team_disciplines/${teamDisciplineId}`, {
      method: "DELETE",
    }),
  onSuccess: async () => {
    toast.add(Toasts.itemDeleteSuccess("Team-Disziplin", "Die"));
    navigateTo(".");

    await queryClient.refetchQueries({
      queryKey: teamDisciplinesQueryKey(tournamentId),
    });
    await queryClient.invalidateQueries({
      queryKey: teamDisciplineQueryKey(teamDisciplineId),
    });
  },
  onError: (error) => {
    toast.add(Toasts.itemDeleteFail(error, "Die Team-Disziplin"));
  },
});

function deleteTeamDiscipline() {
  confirm.require(Dialogs.confirmDelete(teamDiscipline.value?.name, mutateDelete));
}
</script>

<template>
  <!--Error Message-->
  <div v-if="!teamDiscipline" class="m-auto">
    <StaticMessage severity="error"
      >Die Team-Disziplin konnte nicht geladen werden</StaticMessage
    >
  </div>

  <!--Main Wrapper-->
  <div v-else class="flex flex-1 flex-col">
    <!--Edit Dialog-->
    <TeamDisciplineEditDialog
      v-model:visible="showEditDialog"
      :team-discipline-id="teamDisciplineId"
      edit
    />

    <!--Actions-->
    <div class="flex w-full justify-end gap-2 px-3 pt-2">
      <ButtonGroup>
        <Button
          v-tooltip.bottom="'Löschen'"
          severity="danger"
          @click="deleteTeamDiscipline"
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
        <div class="flex flex-col gap-3">
          <DetailItem title="Name" :content="teamDiscipline.name" />
        </div>
      </DetailCard>

      <!--Disciplines-->
      <DetailCard title="Basiert auf">
        <div class="space-y-3">
          <ListItem
            v-for="discipline in teamDiscipline.basedOn"
            :key="discipline.id"
          >
            <template #title>Name</template>
            <template #content>{{ discipline.name }}</template>

            <template #trailing>
              <NuxtLink :to="`/dashboard/tournaments/${tournamentId}/disciplines/${discipline.id}`">
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
