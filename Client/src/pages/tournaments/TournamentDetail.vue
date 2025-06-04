<script setup lang="ts">
import CardContainer from "@/components/CardContainer.vue";
import HeadlineRow from "@/components/HeadlineRow.vue";
import DeleteButton from "@/components/detail/DeleteButton.vue";
import DetailCard from "@/components/detail/DetailCard.vue";
import DetailItem from "@/components/detail/DetailItem.vue";
import EditButton from "@/components/detail/EditButton.vue";
import LoadingMessage from "@/components/messages/LoadingMessage.vue";
import StatusMessage from "@/components/messages/StatusMessage.vue";
import {
  getGetTournamentQueryKey,
  getGetTournamentsQueryKey,
  useDeleteTournament,
  useGetTournament,
} from "@/utils/api/api.ts";
import { createDeleteDialog } from "@/utils/dialogs.ts";
import { RouteNames } from "@/utils/routes.ts";
import { strings } from "@/utils/strings.ts";
import { getIdFromRoute } from "@/utils/utils.ts";
import { useQueryClient } from "@tanstack/vue-query";
import { useSeoMeta } from "@unhead/vue";
import { useConfirm } from "primevue";
import Button from "primevue/button";
import ConfirmDialog from "primevue/confirmdialog";
import { computed } from "vue";
import { useRouter } from "vue-router";
import OpenInNew from "~icons/material-symbols/open-in-new";

const router = useRouter();
const queryClient = useQueryClient();

const tournamentId = getIdFromRoute("tournamentId");

const { data, isPending, isError } = useGetTournament(tournamentId);

useSeoMeta({
  title: computed(
    () =>
      `${strings.tournament.item} - ${data.value?.data.name ?? strings.status.loading}`,
  ),
});

const confirm = useConfirm();
const deleteTournament = useDeleteTournament();

function onDeleteButtonClick() {
  confirm.require(
    createDeleteDialog(
      strings.tournament.deleteDlg.header,
      strings.tournament.deleteDlg.message(
        data.value?.data.name ?? strings.status.loading,
      ),
      async () => {
        await deleteTournament.mutateAsync({
          tournamentId: tournamentId,
        });

        await Promise.all([
          router.push({
            name: RouteNames.TOURNAMENT_LIST,
          }),
          queryClient.refetchQueries({
            queryKey: getGetTournamentsQueryKey(),
          }),
        ]);

        await queryClient.invalidateQueries({
          queryKey: getGetTournamentQueryKey(tournamentId),
        });
      },
    ),
  );
}
</script>

<template>
  <div class="flex flex-grow flex-col">
    <ConfirmDialog :draggable="false" class="select-none" />

    <HeadlineRow :title="strings.tournament.item">
      <template #actions>
        <DeleteButton @click="onDeleteButtonClick" />

        <EditButton
          :link="{
            name: RouteNames.TOURNAMENT_EDIT,
            params: { tournamentId: tournamentId },
          }"
        />

        <!--Open Button-->
        <RouterLink
          :to="{
            name: RouteNames.TOURNAMENT_SCORES,
            params: { tournamentId: tournamentId },
          }"
        >
          <Button :label="strings.tournament.open">
            <template #icon>
              <OpenInNew />
            </template>
          </Button>
        </RouterLink>
      </template>
    </HeadlineRow>

    <LoadingMessage v-if="isPending" />

    <StatusMessage
      v-else-if="isError || !data"
      severity="error"
      :message="strings.tournament.loadingError"
    />

    <CardContainer v-else>
      <DetailCard :title="strings.general">
        <div class="flex flex-col gap-2">
          <DetailItem :label="strings.name" :content="data.data.name" />
          <DetailItem
            :label="strings.date"
            :content="strings.formatting.date(data.data.date)"
          />
          <div class="flex">
            <DetailItem
              class="flex-1"
              :label="strings.teamSize"
              :content="data.data.teamSize"
            />
            <DetailItem
              class="flex-1"
              :label="strings.teamSizeFixed"
              :content="strings.formatting.boolean(data.data.isTeamSizeFixed)"
            />
          </div>
        </div>
      </DetailCard>

      <DetailCard :title="strings.overview">
        <div class="grid grid-cols-2 gap-y-2">
          <DetailItem
            :label="strings.club.count"
            :content="data.data.clubCount"
          />
          <DetailItem
            :label="strings.participant.count"
            :content="data.data.participantCount"
          />
          <DetailItem
            :label="strings.team.count"
            :content="data.data.teamCount"
          />
          <DetailItem
            :label="strings.discipline.count"
            :content="data.data.disciplineCount"
          />
        </div>
      </DetailCard>
    </CardContainer>
  </div>
</template>
