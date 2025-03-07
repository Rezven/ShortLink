import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
    plugins: [vue()],
    server: {
        historyApiFallback: true, // 404时自动跳转到 /index.html
        port: 3000, // 指定端口
        host: 'localhost', // 绑定到本地
        open: true,  // 启动后自动打开浏览器
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/api/, '')
            }
        }
    }
})