<script setup lang="ts">
import ScreenContainer from "@/components/layout/ScreenContainer.vue";
import Sidebar from "@/pages/sidebar/Sidebar.vue";
import { ref } from "vue";

const menuExpanded = ref(true);

function onMenuButtonClick() {
  console.log("Hi");
  menuExpanded.value = !menuExpanded.value;
}
</script>

<template>
  <ScreenContainer>
    <Sidebar :expanded="menuExpanded" @menuClick="onMenuButtonClick" />
  </ScreenContainer>
</template>
