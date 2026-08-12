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
    logout
  }
})
