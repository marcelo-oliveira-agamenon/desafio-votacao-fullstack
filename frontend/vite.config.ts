import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";

// O front consome a API em /api; em dev fazemos proxy para o backend Spring.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      "/api": {
        target: process.env.VITE_API_TARGET ?? "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
