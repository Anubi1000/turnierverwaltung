import { defineConfig } from "orval";

export default defineConfig({
  turnierverwaltung: {
    output: {
      mode: "single",
      target: "src/utils/api/api.ts",
      client: "vue-query",
      schemas: "src/utils/api/schemas",
      prettier: true,
    },
    input: {
      target: "http://localhost:5097/openapi/v1.json",
    },
  },
});
