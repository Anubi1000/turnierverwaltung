<script setup lang="ts">
definePageMeta({
  middleware: "auth",
  layout: "tournament-navigation",
});
</script>

<template>
  <Teleport defer to="#navbar_end">
    <Button>
      <Icon name="material-symbols:save" />
      Tabelle speichern
    </Button>
  </Teleport>

  <ItemList
    :items="[]"
    :selected-item="undefined"
    load-failed-text="Das Laden der Disziplinen ist fehlgeschlagen"
    no-items-text="Es sind keine Disziplinen vorhanden"
  />

  <div class="bg-surface-50 flex flex-1">Hi</div>
</template>
