import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", () => {
  const isAuthenticated = ref(false);
  const performedRequest = ref(false);

  async function checkAuth() {
    if (performedRequest.value) return;
    const result = await $fetch<AuthInfoDto>("/auth");

    isAuthenticated.value = result.isAuthenticated;
    performedRequest.value = true;
  }

  return { isAuthenticated, checkAuth };
});

export type AuthStore = ReturnType<typeof useAuthStore>;
