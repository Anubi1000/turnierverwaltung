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
  getGetParticipantQueryKey,
  getGetParticipantsQueryKey,
  useDeleteParticipant,
  useGetParticipant,
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
const participantId = getIdFromRoute("participantId");

const { data, isPending, isError } = useGetParticipant(participantId);

useSeoMeta({
  title: computed(
    () =>
      `${strings.participant.item} - ${data.value?.data.name ?? strings.loading}`,
  ),
});

const confirm = useConfirm();
const deleteParticipant = useDeleteParticipant();

function onDeleteButtonClick() {
  confirm.require(
    createDeleteDialog(
      strings.participant.deleteDlg.header,
      strings.participant.deleteDlg.message(
        data.value?.data.name ?? strings.loading,
      ),
      async () => {
        await deleteParticipant.mutateAsync({
          participantId: participantId,
        });

        await Promise.all([
          router.push({
            name: RouteNames.PARTICIPANT_LIST,
            params: { tournamentId: tournamentId },
          }),
          queryClient.refetchQueries({
            queryKey: getGetParticipantsQueryKey(tournamentId),
          }),
        ]);

        await queryClient.invalidateQueries({
          queryKey: getGetParticipantQueryKey(participantId),
        });
      },
    ),
  );
}
</script>

<template>
  <div class="flex flex-grow flex-col">
    <ConfirmDialog :draggable="false" class="select-none" />

    <HeadlineRow :title="strings.participant.item">
      <template #actions>
        <DeleteButton @click="onDeleteButtonClick" />

        <EditButton
          :link="{
            name: RouteNames.PARTICIPANT_EDIT,
            params: {
              tournamentId: tournamentId,
              participantId: participantId,
            },
          }"
        />
      </template>
    </HeadlineRow>

    <LoadingMessage v-if="isPending" />

    <StatusMessage
      v-else-if="isError || !data"
      severity="error"
      :message="strings.participant.loadingError"
    />

    <CardContainer v-else>
      <DetailCard :title="strings.general">
        <div class="flex flex-col gap-2">
          <DetailItem :label="strings.name" :content="data.data.name" />
          <DetailItem
            :label="strings.startNumber"
            :content="data.data.startNumber"
          />
          <DetailItem
            :label="strings.gender"
            :content="strings.formatting.gender(data.data.gender)"
          />

          <DetailItem :label="strings.club.item" :content="data.data.clubName">
            <template #trailing>
              <DetailLinkButton
                :to="{
                  name: RouteNames.CLUB_DETAIL,
                  params: {
                    tournamentId: tournamentId,
                    clubId: data.data.clubId,
                  },
                }"
              />
            </template>
          </DetailItem>
        </div>
      </DetailCard>

      <DetailCard :title="strings.results">
        <div v-if="data.data.disciplines.length === 0">
          <Message class="text-center">{{
            strings.discipline.noAvailable
          }}</Message>
        </div>

        <div v-else class="flex flex-col gap-2">
          <DetailItem
            v-for="item in data.data.disciplines"
            :key="item.id"
            :label="strings.name"
            :content="item.name"
          >
            <template #trailing>
              <DetailLinkButton
                :to="{
                  name: RouteNames.PARTICIPANT_RESULTS,
                  params: {
                    tournamentId: tournamentId,
                    participantId: data.data.id,
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
