import App from "./App.vue";
import "./style.css";
import { RouteNames, routes } from "./utils/routes.ts";
import { useAuthStore } from "@/utils/stores.ts";
import { strings } from "@/utils/strings.ts";
import { definePreset, palette } from "@primeuix/themes";
import Aura from "@primeuix/themes/aura";
import { VueQueryPlugin } from "@tanstack/vue-query";
import { createHead } from "@unhead/vue/client";
import { createPinia } from "pinia";
import { ConfirmationService } from "primevue";
import PrimeVue from "primevue/config";
import { createApp } from "vue";
import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(),
  routes: routes,
});

router.beforeEach(async (to) => {
  if (to.meta.requiresAuth) {
    const authStore = useAuthStore();

    if (!authStore.checkedAuth) {
      await authStore.checkAuthentication();
    }

    if (!authStore.isAuthenticated) {
      return {
        name: RouteNames.ROOT,
      };
    }
  }
});

const head = createHead({
  init: [
    {
      title: strings.appName,
    },
  ],
});

const pinia = createPinia();

const colorPalette = palette("#1b5e20");
const appPreset = definePreset(Aura, {
  semantic: {
    primary: {
      50: colorPalette[50],
      100: colorPalette[100],
      200: colorPalette[200],
      300: colorPalette[300],
      400: colorPalette[400],
      500: colorPalette[500],
      600: colorPalette[600],
      700: colorPalette[700],
      800: colorPalette[800],
      900: colorPalette[900],
      950: colorPalette[950],
    },
  },
});

createApp(App)
  .use(router)
  .use(head)
  .use(pinia)
  .use(VueQueryPlugin, {
    enableDevtoolsV6Plugin: true,
  })
  .use(PrimeVue, {
    theme: {
      preset: appPreset,
      options: {
        darkModeSelector: false,
        cssLayer: {
          name: "primevue",
          order: "theme, base, primevue",
        },
      },
    },
    locale: {
      monthNames: [
        "Januar",
        "Februar",
        "März",
        "April",
        "Mai",
        "Juni",
        "Juli",
        "August",
        "September",
        "Oktober",
        "November",
        "Dezember",
      ],
      monthNamesShort: [
        "Jan",
        "Feb",
        "Mär",
        "Apr",
        "Mai",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
        "Okt",
        "Nov",
        "Dez",
      ],
      emptyMessage: strings.status.noEntriesAvailable,
      emptySearchMessage: strings.status.noResultsFound,
    },
  })
  .use(ConfirmationService)
  .mount("#app");
