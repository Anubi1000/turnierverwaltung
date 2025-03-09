<script setup lang="ts">
const route = computed(() => useRoute());
const basePath = ref(
  `/dashboard/tournaments/${route.value.params.tournamentId}`,
);

const menuItems = ref([
  {
    label: "Übersicht",
    icon: "material-symbols:dashboard",
    route: `${basePath.value}/overview`,
  },
  {
    label: "Teilnehmer",
    icon: "material-symbols:person",
    route: `${basePath.value}/participants`,
  },
  {
    label: "Vereine",
    icon: "material-symbols:groups",
    route: `${basePath.value}/clubs`,
  },
  {
    label: "Disziplinen",
    icon: "material-symbols:add-box",
    route: `${basePath.value}/disciplines`,
  },

  {
    label: "Team-Disziplinen",
    icon: "material-symbols:library-add",
    route: `${basePath.value}/team_disciplines`,
  },
]);

const selectedItem = computed(() =>
  menuItems.value.find((item) => route.value.path.startsWith(item.route)),
);
</script>

<template>
  <aside>
    <nav>
      <ul class="m-2 w-48 flex-shrink-0 space-y-2 overflow-y-auto">
        <li v-for="item in menuItems" :key="item.route">
          <MenuItem v-bind="item" :selected="selectedItem == item" />
        </li>
      </ul>
    </nav>
  </aside>
</template>
