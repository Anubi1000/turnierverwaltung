import { checkAuth } from "@/utils/api/api.ts";
import type { ScoreboardData } from "@/utils/api/schemas";
import { HubConnection, HubConnectionBuilder } from "@microsoft/signalr";
import { defineStore } from "pinia";
import { readonly, ref } from "vue";

export const useAuthStore = defineStore("auth", () => {
  const isAuthenticated = ref(false);
  const checkedAuth = ref(false);

  async function checkAuthentication() {
    const res = await checkAuth();
    isAuthenticated.value = res.data.isAuthenticated;
    checkedAuth.value = true;
  }

  return {
    isAuthenticated,
    checkedAuth: readonly(checkedAuth),
    checkAuthentication,
  };
});

export const useScoreboardStore = defineStore("scoreboard", () => {
  const connection = ref<HubConnection | null>(null);
  const scoreboardData = ref<ScoreboardData | null>(null);

  async function connect() {
    if (connection.value) return;

    const con = new HubConnectionBuilder()
      .withUrl("/api/scoreboard/hub")
      .withAutomaticReconnect()
      .build();

    con.on("SendUpdate", async (data: ScoreboardData) => {
      scoreboardData.value = data;
    });

    con.on("ClearScoreboard", async () => {
      scoreboardData.value = null;
    });

    try {
      await con.start();
      connection.value = con;
    } catch (err) {
      console.error("Connection to server failed: ", err);
    }
  }

  async function disconnect() {
    if (connection.value) {
      await connection.value.stop();
      connection.value = null;
    }
  }

  return {
    connection: readonly(connection),
    scoreboardData: readonly(scoreboardData),
    connect,
    disconnect,
  };
});
