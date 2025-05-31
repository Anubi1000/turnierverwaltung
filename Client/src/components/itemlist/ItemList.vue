<script setup lang="ts">
import Card from "@/components/card/Card.vue";
import LoadingMessage from "@/components/messages/LoadingMessage.vue";
import StatusMessage from "@/components/messages/StatusMessage.vue";
import { strings } from "@/utils/strings.ts";
import type { ItemListItem } from "@/utils/types.ts";
import IconField from "primevue/iconfield";
import InputIcon from "primevue/inputicon";
import InputText from "primevue/inputtext";
import { computed, ref } from "vue";
import { RouterLink } from "vue-router";
import Search from "~icons/material-symbols/search";

const { items } = defineProps<{
  isLoading: boolean;
  isError: boolean;
  items: ItemListItem[] | undefined;
  selectedItem: ItemListItem | undefined;
}>();

const searchTerm = ref("");

const filteredItems = computed(() => {
  const unfiltered = items;
  if (!unfiltered) return undefined;
  return unfiltered.filter((item) =>
    item.title
      .toLocaleLowerCase("de-DE")
      .includes(searchTerm.value.toLocaleLowerCase("de-DE")),
  );
});
</script>

<template>
  <div class="flex max-w-72 min-w-72 flex-col overflow-y-auto">
    <div class="sticky top-[0] flex flex-col gap-2 bg-surface-50 px-2 pb-1">
      <slot name="actionButton" />

      <IconField>
        <InputIcon>
          <Search class="-mt-1" />
        </InputIcon>
        <InputText
          :placeholder="strings.searchTerm"
          v-model="searchTerm"
          class="w-full"
        />
      </IconField>
    </div>

    <LoadingMessage v-if="isLoading" />

    <StatusMessage
      v-else-if="isError || !items || !filteredItems"
      severity="error"
      :message="strings.listLoadingFailed"
    />

    <StatusMessage
      v-else-if="items.length === 0"
      severity="info"
      :message="strings.noEntriesAvailable"
    />

    <StatusMessage
      v-else-if="filteredItems.length === 0"
      severity="info"
      :message="strings.noResultsFound"
    />

    <RouterLink
      v-else
      v-for="item in filteredItems"
      class="mx-2 my-1"
      :key="item.id"
      :to="item.link"
    >
      <Card
        selectable
        :selected="item.id === selectedItem?.id"
        :title="item.title"
        :content="item.content"
      />
    </RouterLink>
  </div>
</template>
