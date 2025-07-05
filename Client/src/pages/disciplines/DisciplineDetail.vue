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
  getGetDisciplineQueryKey,
  getGetDisciplinesQueryKey,
  useDeleteDiscipline,
  useGetDiscipline,
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
const disciplineId = getIdFromRoute("disciplineId");

const { data, isPending, isError } = useGetDiscipline(disciplineId);

useSeoMeta({
  title: computed(
    () =>
      `${strings.discipline.item} - ${data.value?.data.name ?? strings.status.loading}`,
  ),
});

const confirm = useConfirm();
const deleteDiscipline = useDeleteDiscipline();

function onDeleteButtonClick() {
  confirm.require(
    createDeleteDialog(
      strings.discipline.deleteDlg.header,
      strings.discipline.deleteDlg.message(
        data.value?.data.name ?? strings.status.loading,
      ),
      async () => {
        await deleteDiscipline.mutateAsync({
          disciplineId: disciplineId,
        });

        await Promise.all([
          router.push({
            name: RouteNames.DISCIPLINE_LIST,
            params: { tournamentId: tournamentId },
          }),
          queryClient.refetchQueries({
            queryKey: getGetDisciplinesQueryKey(tournamentId),
          }),
        ]);

        await queryClient.invalidateQueries({
          queryKey: getGetDisciplineQueryKey(disciplineId),
        });
      },
    ),
  );
}
</script>

<template>
  <div class="flex flex-grow flex-col">
    <ConfirmDialog :draggable="false" class="select-none" />

    <HeadlineRow :title="strings.discipline.item">
      <template #actions>
        <DeleteButton @click="onDeleteButtonClick" />

        <EditButton
          :link="{
            name: RouteNames.DISCIPLINE_EDIT,
            params: { tournamentId: tournamentId, disciplineId: disciplineId },
          }"
        />
      </template>
    </HeadlineRow>

    <LoadingMessage v-if="isPending" />

    <StatusMessage
      v-else-if="isError || !data"
      severity="error"
      :message="strings.discipline.loadingError"
    />

    <CardContainer v-else>
      <DetailCard :title="strings.general">
        <div class="flex flex-col gap-2">
          <DetailItem :label="strings.name" :content="data.data.name" />
          <DetailItem
            :label="strings.gendersSeparated"
            :content="strings.formatting.boolean(data.data.areGendersSeparated)"
          />
          <DetailItem
            :label="strings.amountOfBestRoundsToShow"
            :content="data.data.amountOfBestRoundsToShow"
          />
          <DetailItem
            :label="strings.isShownInResult"
            :content="strings.formatting.boolean(data.data.showInResults)"
          />
        </div>
      </DetailCard>

      <DetailCard :title="strings.value.items">
        <div class="flex flex-col gap-2">
          <div
            v-for="(value, index) in data.data.values"
            :key="index"
            class="flex flex-row gap-3"
          >
            <DetailItem
              :label="strings.name"
              :content="value.name"
              class="w-full"
            />
            <DetailItem
              :label="strings.isAdded"
              :content="strings.formatting.boolean(value.isAdded)"
              class="w-full"
            />
          </div>
        </div>
      </DetailCard>
    </CardContainer>
  </div>
</template>
