/** @type {import('next').NextConfig} */
const nextConfig = {
  output: "export",
  distDir: "dist/scoreboard",
  images: {
    unoptimized: true
  }
};

export default nextConfig;
