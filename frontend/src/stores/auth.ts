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

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { LoginResponse } from '@/types/auth'

function readStoredUserInfo(): LoginResponse | null {
  try {
    const raw = localStorage.getItem('userInfo')
    return raw ? JSON.parse(raw) : null
  } catch {
    // 清理损坏的本地缓存，避免应用启动崩溃
    localStorage.removeItem('userInfo')
    localStorage.removeItem('token')
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<LoginResponse | null>(readStoredUserInfo())

  const isLoggedIn = computed(() => !!token.value)
  const role = computed(() => userInfo.value?.role || '')
  const realName = computed(() => userInfo.value?.realName || '')

  const isGraduate = computed(() => role.value === 'GRADUATE')
  const isTeacher = computed(() => role.value === 'TEACHER')
  const isCollegeAdmin = computed(() => role.value === 'COLLEGE_ADMIN')
  const isSystemAdmin = computed(() => role.value === 'SYSTEM_ADMIN')

  // Allow register page to be shown to logged-out users
  const isAdmin = computed(() => isCollegeAdmin.value || isSystemAdmin.value)

  function setLogin(response: LoginResponse) {
    token.value = response.token
    userInfo.value = response
    localStorage.setItem('token', response.token)
    localStorage.setItem('userInfo', JSON.stringify(response))
  }

  function updateUserInfo(patch: Partial<LoginResponse>) {
    if (!userInfo.value) return
    userInfo.value = { ...userInfo.value, ...patch }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    role,
    realName,
    isGraduate,
    isTeacher,
    isCollegeAdmin,
    isSystemAdmin,
    isAdmin,
    setLogin,
    updateUserInfo,
    logout
  }
})
