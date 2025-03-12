import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
    build: {
        rollupOptions: {
            input: './src/main.js'  // 指向实际入口文件
        }
    },
    plugins: [vue()],
    server: {
        historyApiFallback: true, // 404时自动跳转到 /index.html
        port: 3000, // 指定端口
        host: '43.139.1.172', // 绑定到公网ip
        open: true,  // 启动后自动打开浏览器
        proxy: {
            '/api': {
                target: 'http://43.139.1.172:8080/',
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/api/, '')
            }
        }
    }
})