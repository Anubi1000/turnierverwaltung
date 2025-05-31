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
import {
  getGetTeamDisciplineQueryKey,
  getGetTeamDisciplinesQueryKey,
  useDeleteTeamDiscipline,
  useGetTeamDiscipline,
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
const teamDisciplineId = getIdFromRoute("teamDisciplineId");

const { data, isPending, isError } = useGetTeamDiscipline(teamDisciplineId);

useSeoMeta({
  title: computed(
    () =>
      `${strings.teamDiscipline.item} - ${data.value?.data.name ?? strings.loading}`,
  ),
});

const confirm = useConfirm();
const deleteTeamDiscipline = useDeleteTeamDiscipline();

function onDeleteButtonClick() {
  confirm.require(
    createDeleteDialog(
      strings.teamDiscipline.deleteDlg.header,
      strings.teamDiscipline.deleteDlg.message(
        data.value?.data.name ?? strings.loading,
      ),
      async () => {
        await deleteTeamDiscipline.mutateAsync({
          teamDisciplineId: teamDisciplineId,
        });

        await Promise.all([
          router.push({
            name: RouteNames.TEAM_DISCIPLINE_LIST,
            params: { tournamentId: tournamentId },
          }),
          queryClient.refetchQueries({
            queryKey: getGetTeamDisciplinesQueryKey(tournamentId),
          }),
        ]);

        await queryClient.invalidateQueries({
          queryKey: getGetTeamDisciplineQueryKey(teamDisciplineId),
        });
      },
    ),
  );
}
</script>

<template>
  <div class="flex flex-grow flex-col">
    <ConfirmDialog :draggable="false" class="select-none" />

    <HeadlineRow :title="strings.teamDiscipline.item">
      <template #actions>
        <DeleteButton @click="onDeleteButtonClick" />

        <EditButton
          :link="{
            name: RouteNames.TEAM_DISCIPLINE_EDIT,
            params: {
              tournamentId: tournamentId,
              teamDisciplineId: teamDisciplineId,
            },
          }"
        />
      </template>
    </HeadlineRow>

    <LoadingMessage v-if="isPending" />

    <StatusMessage
      v-else-if="isError || !data"
      severity="error"
      :message="strings.teamDiscipline.loadingError"
    />

    <CardContainer v-else>
      <DetailCard :title="strings.general">
        <div class="flex flex-col gap-2">
          <DetailItem :label="strings.name" :content="data.data.name" />
          <DetailItem
            :label="strings.type"
            :content="
              strings.formatting.teamScoreDisplayType(data.data.displayType)
            "
          />
        </div>
      </DetailCard>

      <DetailCard :title="strings.discipline.items">
        <div class="flex flex-col gap-2">
          <DetailItem
            v-for="(item, index) in data.data.basedOn"
            :key="index"
            :label="strings.name"
            :content="item.name"
          >
            <template #trailing>
              <DetailLinkButton
                :to="{
                  name: RouteNames.DISCIPLINE_DETAIL,
                  params: {
                    tournamentId: tournamentId,
                    disciplineId: item.id,
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
