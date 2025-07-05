<script setup lang="ts">
import Card from "@/components/card/Card.vue";
import LoadingMessage from "@/components/messages/LoadingMessage.vue";
import StatusMessage from "@/components/messages/StatusMessage.vue";
import { strings } from "@/utils/strings.ts";
import type { ItemListItem } from "@/utils/types.ts";
import Button from "primevue/button";
import IconField from "primevue/iconfield";
import InputGroup from "primevue/inputgroup";
import InputGroupAddon from "primevue/inputgroupaddon";
import InputIcon from "primevue/inputicon";
import InputText from "primevue/inputtext";
import { computed, ref } from "vue";
import { RouterLink } from "vue-router";
import Close from "~icons/material-symbols/close";
import Search from "~icons/material-symbols/search";

const { items, useRouterLink = true } = defineProps<{
  isLoading: boolean;
  isError: boolean;
  items: ItemListItem[] | undefined;
  selectedItemId: string | undefined;
  useRouterLink?: boolean;
}>();

const emit = defineEmits<{
  (e: "itemClick", item: ItemListItem): void;
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
  <div class="flex max-w-72 min-w-72 flex-col border-r-1 border-r-surface-300">
    <div class="flex flex-col gap-2 px-2 pt-1">
      <slot name="actionButton" />

      <InputGroup>
        <IconField>
          <InputIcon class="z-10">
            <Search class="-mt-1" />
          </InputIcon>

          <InputText
            :placeholder="strings.searchTerm"
            v-model="searchTerm"
            class="w-full"
          />
        </IconField>

        <InputGroupAddon>
          <Button variant="text" severity="secondary" @click="searchTerm = ''">
            <template #icon>
              <Close />
            </template>
          </Button>
        </InputGroupAddon>
      </InputGroup>
    </div>

    <div
      class="flex flex-grow flex-col gap-2 overflow-x-visible overflow-y-auto p-2"
    >
      <LoadingMessage v-if="isLoading" />

      <StatusMessage
        v-else-if="isError || !items || !filteredItems"
        severity="error"
        :message="strings.status.listLoadingFailed"
      />

      <StatusMessage
        v-else-if="items.length === 0"
        severity="info"
        :message="strings.status.noEntriesAvailable"
      />

      <StatusMessage
        v-else-if="filteredItems.length === 0"
        severity="info"
        :message="strings.status.noResultsFound"
      />

      <component
        v-else
        v-for="item in filteredItems"
        class="overflow-visible"
        :is="useRouterLink ? RouterLink : 'div'"
        :key="item.id"
        v-bind="useRouterLink ? { to: item.link } : {}"
        @click="!useRouterLink && emit('itemClick', item)"
      >
        <Card
          selectable
          :selected="item.id == selectedItemId"
          :title="item.title"
          :content="item.content"
        />
      </component>
    </div>
  </div>
</template>
