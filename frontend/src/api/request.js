import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  console.log('[REQUEST]', config.method.toUpperCase(), config.url, '| hasToken:', !!token)
  if (token && !config.url.includes('/auth/')) {
    config.headers.Authorization = `Bearer ${token}`
    console.log('[REQUEST] Added Authorization header')
  }
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data
    console.log('[RESPONSE]', response.config.url, '| status:', response.status, '| code:', res.code)
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    console.error('[ERROR]', error.config?.url, '| status:', error.response?.status, '| data:', error.response?.data)
    const status = error.response?.status
    if (status === 401 || status === 403) {
      console.warn('[AUTH] Token expired or invalid, redirecting to login...')
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      sessionStorage.clear()
      window.location.href = '/login?expired=1'
    }
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request