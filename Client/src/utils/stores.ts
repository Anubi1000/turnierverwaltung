import { checkAuth } from "@/utils/api/api.ts";
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
