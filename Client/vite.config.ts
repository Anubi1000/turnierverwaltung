import TailwindCSS from "@tailwindcss/vite";
import Vue from "@vitejs/plugin-vue";
import { minify } from "html-minifier-terser";
import path from "node:path";
import Icons from "unplugin-icons/vite";
import { defineConfig } from "vite";
import VueDevTools from "vite-plugin-vue-devtools";

export default defineConfig({
  plugins: [
    Vue(),
    VueDevTools(),
    TailwindCSS(),
    // @ts-ignore
    Icons({
      compiler: "vue3",
      iconCustomizer(collection, _, props) {
        if (collection === "material-symbols") {
          props.width = "1.5rem";
          props.height = "1.5rem";
        }
      },
    }),
    {
      name: "html-minify",
      apply: "build",
      async transformIndexHtml(html) {
        return await minify(html, {
          collapseWhitespace: true,
          removeAttributeQuotes: true,
          removeComments: true,
        });
      },
    },
  ],
  build: {
    rollupOptions: {
      output: {
        assetFileNames: "assets/[hash][extname]",
        chunkFileNames: "assets/[hash].js",
        entryFileNames: "assets/[hash].js",
      },
    },
    reportCompressedSize: false,
    target: "es2022",
  },
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
});
