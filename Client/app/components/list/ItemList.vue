<script setup lang="ts">
const { items } = defineProps<{
  items?: ItemListItem[];
  selectedItem?: ItemListItem;
  loadFailedText: string;
  noItemsText: string;
}>();

const searchValue = ref("");

const filteredItems = computed(() => {
  if (!items || !searchValue.value) return items;

  return items.filter((item) =>
    item.title.toLowerCase().includes(searchValue.value.toLowerCase()),
  );
});
</script>

<template>
  <div class="w-80 flex-shrink-0 overflow-y-auto">
    <div
      v-if="!items || !filteredItems"
      class="flex size-full items-center justify-center p-2"
    >
      <StaticMessage severity="error" class="text-center">
        {{ loadFailedText }}
      </StaticMessage>
    </div>

    <div
      v-else-if="items.length === 0"
      class="flex size-full items-center justify-center p-2"
    >
      <StaticMessage severity="info" class="text-center">
        {{ noItemsText }}
      </StaticMessage>
    </div>

    <div v-else class="flex-1">
      <div class="px-2">
        <InputText v-model="searchValue" class="w-full" placeholder="Suchen" />
      </div>

      <StaticMessage
        v-if="filteredItems.length === 0"
        severity="info"
        class="m-2 text-center"
      >
        Es wurden keine Einträge zu dem Suchbegriff gefunden
      </StaticMessage>

      <div v-for="item in filteredItems" :key="item.id" class="m-2">
        <NuxtLink :to="(item as ItemListItem).link">
          <ItemCard :selected="item == selectedItem">
            <template #title>{{ (item as ItemListItem).title }}</template>
            <template v-if="(item as ItemListItem).content" #content>{{
              (item as ItemListItem).content
            }}</template>
          </ItemCard>
        </NuxtLink>
      </div>
    </div>
  </div>
</template>
