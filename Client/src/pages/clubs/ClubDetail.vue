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
  getGetClubQueryKey,
  getGetClubsQueryKey,
  useDeleteClub,
  useGetClub,
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
const clubId = getIdFromRoute("clubId");

const { data, isPending, isError } = useGetClub(clubId);

useSeoMeta({
  title: computed(
    () =>
      `${strings.club.item} - ${data.value?.data.name ?? strings.status.loading}`,
  ),
});

const confirm = useConfirm();
const deleteClub = useDeleteClub();

function onDeleteButtonClick() {
  confirm.require(
    createDeleteDialog(
      strings.club.deleteDlg.header,
      strings.club.deleteDlg.message(
        data.value?.data.name ?? strings.status.loading,
      ),
      async () => {
        await deleteClub.mutateAsync({
          clubId: clubId,
        });

        await Promise.all([
          router.push({
            name: RouteNames.CLUB_LIST,
            params: { tournamentId: tournamentId },
          }),
          queryClient.refetchQueries({
            queryKey: getGetClubsQueryKey(tournamentId),
          }),
        ]);

        await queryClient.invalidateQueries({
          queryKey: getGetClubQueryKey(clubId),
        });
      },
    ),
  );
}
</script>

<template>
  <div class="flex flex-grow flex-col">
    <ConfirmDialog :draggable="false" class="select-none" />

    <HeadlineRow :title="strings.club.item">
      <template #actions>
        <DeleteButton @click="onDeleteButtonClick" />

        <EditButton
          :link="{
            name: RouteNames.CLUB_EDIT,
            params: { tournamentId: tournamentId, clubId: clubId },
          }"
        />
      </template>
    </HeadlineRow>

    <LoadingMessage v-if="isPending" />

    <StatusMessage
      v-else-if="isError || !data"
      severity="error"
      :message="strings.club.loadingError"
    />

    <CardContainer v-else>
      <DetailCard :title="strings.general">
        <div class="flex flex-col gap-2">
          <DetailItem :label="strings.name" :content="data.data.name" />
          <DetailItem
            :label="strings.memberCount"
            :content="data.data.memberCount"
          />
        </div>
      </DetailCard>
    </CardContainer>
  </div>
</template>
