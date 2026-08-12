import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/member': 'http://localhost:8080',
      '/setmeal': 'http://localhost:8080',
      '/checkgroup': 'http://localhost:8080',
      '/checkitem': 'http://localhost:8080',
      '/checkgroup-checkitem': 'http://localhost:8080',
      '/order': 'http://localhost:8080',
      '/user': 'http://localhost:8080',
      '/ordersetting': 'http://localhost:8080',
      '/report': 'http://localhost:8080',
      '/ai': 'http://localhost:8080',
      '/file': 'http://localhost:8080',
      '/uploads': 'http://localhost:8080'
    }
  }
})
