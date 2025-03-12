<script setup>
import { ref } from 'vue'
import BaseButton from '../components/BaseButton.vue'
import { ClipboardDocumentIcon, LinkIcon } from '@heroicons/vue/24/outline'
import { useShortenAPI } from '../api/shorten'

// 响应式数据定义
const url = ref('')             // 用户输入的长链接
const shortUrl = ref('')        // 生成的短链接
const error = ref('')           // 错误提示信息
const ttl = ref(86400)          // 默认有效期24小时（单位：秒）
const { generateShortUrl, loading } = useShortenAPI() // API调用方法

/**
 * 表单提交处理
 * 1. 验证URL格式
 * 2. 调用生成接口
 * 3. 处理响应结果
 */
const handleSubmit = async () => {
  // 清空之前的错误和结果
  error.value = ''
  shortUrl.value = ''

  // 基本格式验证
  if (!isValidUrl(url.value)) {
    error.value = '请输入有效的URL地址（需包含http/https）'
    return
  }

  try {
    // 调用API生成短链
    const data = await generateShortUrl(url.value, ttl.value)
    // 拼接完整短链地址（假设后端返回shortCode字段）
    shortUrl.value = `${window.location.origin}/${data.shortCode}`
  } catch (err) {
    // 统一错误处理
    error.value = err.message || '生成失败，请稍后重试'
  }
}

/**
 * URL格式验证
 * @param {string} url - 需要验证的URL
 * @returns {boolean} 是否有效
 */
const isValidUrl = (url) => {
  const pattern = /^(https?):\/\/[^\s/$.?#].[^\s]*$/i
  return pattern.test(url)
}

/**
 * 复制到剪贴板功能
 * 1. 使用现代浏览器API
 * 2. 处理可能的异常
 */
const copyToClipboard = async () => {
  try {
    await navigator.clipboard.writeText(shortUrl.value)
    // 可以添加复制成功反馈（如Toast提示）
  } catch (err) {
    error.value = '复制失败，请手动复制'
  }
}
</script>

<template>
  <div class="shorten-page">
    <!-- 标题区域 -->
    <header class="header">
      <LinkIcon class="icon" />
      <h1>清新短链生成器</h1>
    </header>

    <!-- 主表单区域 -->
    <div class="form-container">
      <!-- URL输入组 -->
      <div class="input-group">
        <input
            v-model.trim="url"
            type="url"
            placeholder="输入需要缩短的长链接"
            class="url-input"
            :disabled="loading"
            @keyup.enter="handleSubmit"
        >
      </div>

      <!-- 有效期选择 -->
      <div class="options-group">
        <label>有效期设置：</label>
        <select v-model.number="ttl" :disabled="loading">
          <option :value="3600">1小时</option>
          <option :value="86400">24小时</option>
          <option :value="604800">7天</option>
        </select>
      </div>

      <!-- 错误提示 -->
      <div v-if="error" class="error-message">
        {{ error }}
      </div>

      <!-- 生成按钮 -->
      <BaseButton
          @click="handleSubmit"
          :disabled="loading"
          class="generate-btn"
      >
        <template v-if="loading">生成中...</template>
        <template v-else>立即生成</template>
      </BaseButton>

      <!-- 生成结果展示 -->
      <div v-if="shortUrl" class="result-container">
        <div class="result-header">生成成功！</div>
        <div class="result-content">
          <a :href="shortUrl" target="_blank" class="short-url">
            {{ shortUrl }}
          </a>
          <BaseButton
              @click="copyToClipboard"
              variant="outline"
              class="copy-btn"
          >
            <ClipboardDocumentIcon class="icon" />
            一键复制
          </BaseButton>
        </div>
        <div class="expire-info">
          该链接将在{{ ttl / 86400 }}天后过期
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 主容器样式 */
.shorten-page {
  max-width: 680px;
  margin: 2rem auto;
  padding: 2rem;
}

/* 标题样式 */
.header {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  margin-bottom: 2.5rem;
  color: var(--color-primary);

  h1 {
    font-size: 2rem;
    font-weight: 600;
  }

  .icon {
    width: 2rem;
    height: 2rem;
  }
}

/* 表单容器 */
.form-container {
  background: white;
  padding: 2rem;
  border-radius: 1rem;
  box-shadow: 0 4px 20px rgba(136, 216, 176, 0.15);
}

/* 输入框样式 */
.url-input {
  width: 100%;
  padding: 1rem;
  border: 2px solid var(--color-border);
  border-radius: 0.8rem;
  font-size: 1rem;
  transition: all 0.3s ease;

  &:focus {
    outline: none;
    border-color: var(--color-primary);
    box-shadow: 0 0 0 3px rgba(136, 216, 176, 0.2);
  }
}

/* 选项组样式 */
.options-group {
  margin: 1.5rem 0;

  label {
    margin-right: 1rem;
    color: var(--color-text);
  }

  select {
    padding: 0.5rem;
    border-radius: 0.5rem;
    border: 1px solid var(--color-border);
  }
}

/* 生成按钮 */
.generate-btn {
  width: 100%;
  padding: 1rem;
  font-size: 1.1rem;
  margin-top: 1rem;
}

/* 结果容器 */
.result-container {
  margin-top: 2rem;
  padding: 1.5rem;
  background: var(--color-primary-light);
  border-radius: 0.8rem;
  animation: fadeIn 0.3s ease-out;

  .result-header {
    color: var(--color-primary);
    font-weight: 600;
    margin-bottom: 1rem;
  }

  .short-url {
    color: var(--color-primary);
    word-break: break-all;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }

  .copy-btn {
    margin-top: 1rem;
    width: 100%;
  }

  .expire-info {
    margin-top: 1rem;
    color: #666;
    font-size: 0.9rem;
  }
}

/* 错误提示样式 */
.error-message {
  color: #ff6b6b;
  margin: 1rem 0;
  padding: 0.8rem;
  background: #fff0f0;
  border-radius: 0.5rem;
}

/* 动画定义 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>