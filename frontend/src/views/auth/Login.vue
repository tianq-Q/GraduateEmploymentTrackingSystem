<!--
MIT License

Copyright (c) 2026 Employment Tracking System

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
-->

<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="login-icon">
          <el-icon :size="28"><Monitor /></el-icon>
        </div>
        <h2>高校毕业生就业跟踪与分析系统</h2>
        <p>请使用您的账号登录</p>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入学号/工号"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-tips">
        <span>还没有账号？<el-link type="primary" @click="goRegister">立即注册</el-link></span>
      </div>

      <div class="login-footer">
        <span>© 2026 高校就业跟踪系统</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 登录页面
 * - 通过学号/工号 + 密码登录（调用 /auth/login）
 * - 登录成功后将用户信息写入 authStore，并跳转到 redirect 参数指定的页面或默认 /dashboard
 * - 支持回车提交、跳转注册页
 */
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)

// 登录表单数据
const form = reactive({
  username: '',
  password: '',
})

// 表单校验规则
const rules = {
  username: [{ required: true, message: '请输入学号/工号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

/** 提交登录 */
async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res: any = await login({ username: form.username, password: form.password })
    if (res.code === 200) {
      // 保存登录态并跳转（优先跳转来源页面）
      authStore.setLogin(res.data)
      ElMessage.success('登录成功')
      const redirect = (route.query.redirect as string) || '/dashboard'
      router.push(redirect)
    } else {
      ElMessage.error(res.message || '登录失败')
    }
    } catch (e) {
      // 业务错误/网络异常已由 axios 响应拦截器统一提示，避免重复弹窗
      console.error(e)
    } finally {
    loading.value = false
  }
}

/** 跳转注册页 */
function goRegister() {
  router.push('/register')
}
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e8f0fe 0%, #f0f2f5 50%, #e6f7e9 100%);
}

.login-card {
  width: 400px;
  background: #fff;
  border-radius: 16px;
  padding: 48px 40px 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.login-icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #4f8cff, #36cfc9);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin: 0 auto 16px;
}

.login-header h2 {
  font-size: 18px;
  color: var(--color-text);
  margin-bottom: 8px;
}

.login-header p {
  font-size: 13px;
  color: #999;
}

.login-btn {
  width: 100%;
  font-size: 15px;
  letter-spacing: 2px;
}

.login-tips {
  text-align: center;
  margin-top: 0;
  font-size: 14px;
  color: #999;
}

.login-footer {
  text-align: center;
  margin-top: 16px;
  font-size: 12px;
  color: #ccc;
}
</style>
