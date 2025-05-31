import { type Config } from "prettier";

const config: Config = {
  trailingComma: "all",
  tabWidth: 2,
  semi: true,
  singleQuote: false,
  plugins: [
    "@trivago/prettier-plugin-sort-imports",
    "prettier-plugin-tailwindcss",
  ],
  tailwindStylesheet: "./src/style.css",
};

export default config;
