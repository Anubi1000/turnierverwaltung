<script setup lang="ts">
import CardContainer from "@/components/CardContainer.vue";
import HeadlineRow from "@/components/HeadlineRow.vue";
import DeleteButton from "@/components/detail/DeleteButton.vue";
import DetailCard from "@/components/detail/DetailCard.vue";
import DetailItem from "@/components/detail/DetailItem.vue";
import DetailLinkButton from "@/components/detail/DetailLinkButton.vue";
import EditButton from "@/components/detail/EditButton.vue";
import LoadingMessage from "@/components/messages/LoadingMessage.vue";
import StatusMessage from "@/components/messages/StatusMessage.vue";
import Message from "@/components/primevue/Message.vue";
import {
  getGetTeamQueryKey,
  getGetTeamsQueryKey,
  useDeleteTeam,
  useGetTeam,
} from "@/utils/api/api.ts";
import { createDeleteDialog } from "@/utils/dialogs.ts";
import { RouteNames } from "@/utils/routes.ts";
import { strings } from "@/utils/strings.ts";
import { getIdFromRoute } from "@/utils/utils.ts";
import { useQueryClient } from "@tanstack/vue-query";
import { useSeoMeta } from "@unhead/vue";
import { useConfirm } from "primevue";
import ConfirmDialog from "primevue/confirmdialog";
import { computed } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const queryClient = useQueryClient();

const tournamentId = getIdFromRoute("tournamentId");
const teamId = getIdFromRoute("teamId");

const { data, isPending, isError } = useGetTeam(teamId);

useSeoMeta({
  title: computed(
    () => `${strings.team.item} - ${data.value?.data.name ?? strings.loading}`,
  ),
});

const confirm = useConfirm();
const deleteTeam = useDeleteTeam();

function onDeleteButtonClick() {
  confirm.require(
    createDeleteDialog(
      strings.team.deleteDlg.header,
      strings.team.deleteDlg.message(data.value?.data.name ?? strings.loading),
      async () => {
        await deleteTeam.mutateAsync({
          teamId: teamId,
        });

        await Promise.all([
          router.push({
            name: RouteNames.TEAM_LIST,
            params: { tournamentId: tournamentId },
          }),
          queryClient.refetchQueries({
            queryKey: getGetTeamsQueryKey(tournamentId),
          }),
        ]);

        await queryClient.invalidateQueries({
          queryKey: getGetTeamQueryKey(teamId),
        });
      },
    ),
  );
}
</script>

<template>
  <div class="flex flex-grow flex-col">
    <ConfirmDialog :draggable="false" class="select-none" />

    <HeadlineRow :title="strings.team.item">
      <template #actions>
        <DeleteButton @click="onDeleteButtonClick" />

        <EditButton
          :link="{
            name: RouteNames.TEAM_EDIT,
            params: { tournamentId: tournamentId, teamId: teamId },
          }"
        />
      </template>
    </HeadlineRow>

    <LoadingMessage v-if="isPending" />

    <StatusMessage
      v-else-if="isError || !data"
      severity="error"
      :message="strings.team.loadingError"
    />

    <CardContainer v-else>
      <DetailCard :title="strings.general">
        <div class="flex flex-col gap-2">
          <DetailItem :label="strings.name" :content="data.data.name" />
        </div>
      </DetailCard>

      <DetailCard :title="strings.members">
        <div class="flex flex-col gap-2">
          <DetailItem
            v-for="item in data.data.members"
            :key="item.id"
            :label="strings.name"
            :content="item.name"
          >
            <template #trailing>
              <DetailLinkButton
                :to="{
                  name: RouteNames.PARTICIPANT_DETAIL,
                  params: {
                    tournamentId: tournamentId,
                    participantId: item.id,
                  },
                }"
              />
            </template>
          </DetailItem>
        </div>
      </DetailCard>

      <DetailCard :title="strings.teamDiscipline.items">
        <div v-if="data.data.participatingDisciplines.length === 0">
          <Message class="text-center">{{
            strings.team.notParticipatingInDisciplines
          }}</Message>
        </div>

        <div v-else class="flex flex-col gap-2">
          <DetailItem
            v-for="item in data.data.participatingDisciplines"
            :key="item.id"
            :label="strings.name"
            :content="item.name"
          >
            <template #trailing>
              <DetailLinkButton
                :to="{
                  name: RouteNames.TEAM_DISCIPLINE_DETAIL,
                  params: {
                    tournamentId: tournamentId,
                    teamDisciplineId: item.id,
                  },
                }"
              />
            </template>
          </DetailItem>
        </div>
      </DetailCard>
    </CardContainer>
  </div>
</template>
