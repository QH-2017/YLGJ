import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig(({ mode }) => {
  // 支持通过环境变量覆盖后端地址与前端端口（默认 8080/5173）
  const env = loadEnv(mode, process.cwd(), '')
  const backendTarget = env.VITE_BACKEND_URL || 'http://localhost:8080'
  const serverPort = Number(env.VITE_PORT) || 5173

  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': resolve(__dirname, 'src')
      }
    },
    server: {
      port: serverPort,
      proxy: {
        '/member': backendTarget,
        '/setmeal': backendTarget,
        '/checkgroup': backendTarget,
        '/checkitem': backendTarget,
        '/checkgroup-checkitem': backendTarget,
        '/order': backendTarget,
        '/user': backendTarget,
        '/ordersetting': backendTarget,
        '/report': backendTarget,
        '/ai': backendTarget,
        '/file': backendTarget,
        '/uploads': backendTarget,
        '/static': backendTarget
      }
    }
  }
})
