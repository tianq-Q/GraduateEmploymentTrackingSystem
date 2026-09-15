/*
 * MIT License
 *
 * Copyright (c) 2026 Employment Tracking System
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useAuthStore } from '@/stores/auth'

// 401 已处理标志：防止并发请求重复弹错、重复跳转
let handling401 = false

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (res) => {
    const body = res.data
    // 后端全局异常处理器返回 HTTP 200 + code!=200，这里必须校验 code，
    // 否则业务失败会被误判为成功（例如提交就业记录失败却提示"提交成功"）
    if (body && typeof body === 'object' && 'code' in body && body.code !== 200) {
      ElMessage.error(body.message || '操作失败')
      return Promise.reject(new Error(body.message || '操作失败'))
    }
    return body
  },
  (error) => {
    // 登录态失效（token 缺失/过期/密钥变更）：清掉本地登录信息并跳登录页，
    // 避免反复 401 但用户不知道要重新登录
    if (error.response?.status === 401) {
      try {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        useAuthStore().logout()
      } catch {
        /* ignore */
      }
      if (!handling401) {
        handling401 = true
        if (router.currentRoute.value.path !== '/login') {
          ElMessage.error('登录已过期，请重新登录')
          router.push('/login')
        }
        setTimeout(() => {
          handling401 = false
        }, 800)
      }
      return Promise.reject(new Error('登录已过期'))
    }
    ElMessage.error(error.response?.data?.message || '请求失败，请重试')
    return Promise.reject(error)
  },
)

// ===================== 导出下载（独立实例） =====================
// 默认实例的响应拦截器会把响应解包为 res.data，破坏 Blob。
// 这里使用独立 axios 实例，保留完整响应以便读取 content-disposition 文件名。
const csvClient = axios.create({
  baseURL: '/api',
  timeout: 30000,
})

csvClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

/**
 * 下载导出 CSV 文件（基础信息模块导出按钮）
 * @param url 接口路径（如 /export/graduates，无需 /api 前缀）
 * @param params 查询参数
 */
export async function downloadCsv(url: string, params?: Record<string, unknown>): Promise<void> {
  const res = await csvClient.get(url, { params, responseType: 'blob' })
  const blob = res.data as Blob
  const disposition: string = res.headers['content-disposition'] || ''
  const match =
    disposition.match(/filename\*=UTF-8''([^;]+)/) ||
    disposition.match(/filename="?([^";]+)"?/)
  const fileName = match ? decodeURIComponent(match[1]) : 'export.csv'
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = fileName
  document.body.appendChild(link)
  link.click()
  URL.revokeObjectURL(link.href)
  link.remove()
}

export default request
