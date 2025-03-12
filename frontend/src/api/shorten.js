import { ref } from 'vue'

const baseURL = 'http://43.139.1.172:8080/api'
const handleAPIError = (response) => {
    const errorMessages = {
        400: '请求参数格式错误',
        404: '短链不存在',
        500: '服务器内部错误'
    }
    return errorMessages[response.status] || `未知错误 (${response.status})`
}

export const useShortenAPI = () => {
    const loading = ref(false)

    const generateShortUrl = async (originalUrl, ttl) => {
        try {
            loading.value = true
            const response = await fetch(`${baseURL}/shorten`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    url: originalUrl,
                    ttl: ttl
                })
            })

            if (!response.ok) {
                const errorMsg = await response.text()
                throw new Error(handleAPIError(response) + (errorMsg ? `: ${errorMsg}` : ''))
            }

            return await response.json()
        } catch (error) {
            throw new Error(`生成失败: ${error.message}`)
        } finally {
            loading.value = false
        }
    }

    return { generateShortUrl, loading }
}