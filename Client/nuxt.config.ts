import Aura from '@primevue/themes/aura';
import {definePreset, palette} from "@primevue/themes";
import vueDevTools from "vite-plugin-vue-devtools";
import tailwindcss from "@tailwindcss/vite";

const colorPalette = palette("#1b5e20")

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
    }
  }
})

export default defineNuxtConfig({
  // Nuxt
  compatibilityDate: '2024-11-01',
  devtools: { 
    enabled: true
  },
  ssr: false,
  css: [
      "~/assets/global.css"
  ],
  components: [{
    path: '~/components',
    pathPrefix: false,
  }],
  srcDir: "./app",

  // Hooks
  hooks: {
    'prerender:routes'({ routes }) {
      routes.clear()
    }
  },

  // Nitro
  nitro: {
    hooks: {
      'prerender:generate'(route) {
        const routesToSkip = ['/200.html', '/404.html']
        if (routesToSkip.includes(route.route)) {
          route.skip = true
        }
      }
    }
  },

  // Modules
  modules: [
    '@primevue/nuxt-module',
    '@nuxt/fonts',
    '@nuxt/icon',
    '@pinia/nuxt',
    '@vee-validate/nuxt',
    '@nuxt/test-utils/module',
    "@nuxt/eslint"
  ],

  // Vite
  vite: {
    plugins: [
      vueDevTools(),
      tailwindcss()
    ]
  },

  // Nuxt Icon
  icon: {
    size: "20px",
    mode: "svg",
    clientBundle: {
      scan: true
    }
  },

  // PrimeVue
  primevue: {
    options: {
      ripple: true,
      theme: {
        preset: appPreset,
        options: {
          darkModeSelector: false,
          cssLayer: {
            name: "primevue",
            order: "theme, base, primevue",
          }
        }
      },
      locale: {
        "monthNames": [
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
          "Dezember"
        ],
        emptyMessage: "Keine Einträge vorhanden",
        emptySearchMessage: "Keine Ergebnisse gefunden"
      }
    }
  }
})