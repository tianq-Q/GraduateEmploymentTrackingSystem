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
  <div class="register-page">
    <div class="register-card">
      <div class="register-header">
        <div class="register-icon">
          <el-icon :size="28"><UserFilled /></el-icon>
        </div>
        <h2>创建新账号</h2>
        <p>填写信息完成注册</p>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" class="register-form">
        <el-form-item prop="realName">
          <el-input
            v-model="form.realName"
            placeholder="请输入真实姓名"
            size="large"
            :prefix-icon="EditPen"
          />
        </el-form-item>
        <el-form-item prop="studentNumber">
          <el-input
            v-model="form.studentNumber"
            placeholder="请输入学号/工号"
            size="large"
            :prefix-icon="EditPen"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码（6-20个字符）"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请确认密码"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input
            v-model="form.phone"
            placeholder="手机号（选填）"
            size="large"
            :prefix-icon="Phone"
          />
        </el-form-item>
        <el-form-item prop="email">
          <el-input
            v-model="form.email"
            placeholder="邮箱（选填）"
            size="large"
            :prefix-icon="Message"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="register-btn"
            @click="handleRegister"
          >
            注 册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-tips">
        <span>已有账号？<el-link type="primary" @click="goLogin">返回登录</el-link></span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 注册页面（毕业生自助注册）
 * - 填写姓名、学号/工号、密码、联系方式完成注册（调用 /auth/register）
 * - 注册成功后自动登录并跳转到 /dashboard
 */
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { UserFilled, EditPen, Lock, Phone, Message } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { register } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'
import type { FormRules } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)

// 注册表单数据
const form = reactive({
  realName: '',
  studentNumber: '',
  password: '',
  confirmPassword: '',
  phone: '',
  email: '',
})

/** 自定义校验：确认密码必须与密码一致 */
const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (value !== form.password) {
    callback(new Error('两次密码输入不一致'))
  } else {
    callback()
  }
}

// 表单校验规则
const rules: FormRules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  studentNumber: [{ required: true, message: '请输入学号/工号', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

/** 提交注册 */
async function handleRegister() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res: any = await register({
      studentNumber: form.studentNumber,
      password: form.password,
      realName: form.realName,
      phone: form.phone || undefined,
      email: form.email || undefined,
    })
    if (res.code === 200) {
      // 注册成功自动登录
      authStore.setLogin(res.data)
      ElMessage.success('注册成功，已自动登录')
      router.push('/dashboard')
    } else {
      ElMessage.error(res.message || '注册失败')
    }
  } catch {
    ElMessage.error('注册失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

/** 返回登录页 */
function goLogin() {
  router.push('/login')
}
</script>

<style scoped lang="scss">
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e8f0fe 0%, #f0f2f5 50%, #e6f7e9 100%);
}

.register-card {
  width: 420px;
  background: #fff;
  border-radius: 16px;
  padding: 40px 42px 28px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #36cfc9, #4f8cff);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin: 0 auto 14px;
}

.register-header h2 {
  font-size: 18px;
  color: #333;
  margin-bottom: 6px;
}

.register-header p {
  font-size: 13px;
  color: #999;
}

.register-btn {
  width: 100%;
  font-size: 15px;
  letter-spacing: 2px;
}

.register-tips {
  text-align: center;
  font-size: 14px;
  color: #999;
}
</style>
