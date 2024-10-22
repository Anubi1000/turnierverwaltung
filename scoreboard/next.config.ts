import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: "export",
  distDir: "dist/scoreboard",
  images: {
    unoptimized: true,
  },
};

export default nextConfig;
