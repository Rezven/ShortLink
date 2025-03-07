import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// 引入图标库
import {
    LinkIcon,
    ClipboardDocumentIcon
} from '@heroicons/vue/24/outline'

const app = createApp(App)

// 全局注册图标组件
app.component('LinkIcon', LinkIcon)
app.component('ClipboardDocumentIcon', ClipboardDocumentIcon)

app.use(router)
app.mount('#app')