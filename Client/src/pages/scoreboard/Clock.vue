<script setup lang="ts">
import { onMounted, onUnmounted, ref } from "vue";

const time = ref("00:00:00");

function updateTime() {
  const now = new Date();
  const hours = String(now.getHours()).padStart(2, "0");
  const minutes = String(now.getMinutes()).padStart(2, "0");
  const seconds = String(now.getSeconds()).padStart(2, "0");
  time.value = `${hours}:${minutes}:${seconds}`;
}

let intervalId: number;

onMounted(() => {
  updateTime();
  intervalId = setInterval(updateTime, 1000);
});

onUnmounted(() => {
  clearInterval(intervalId);
});
</script>

<template>
  <div class="font-mono">{{ time }}</div>
</template>
