<template>
  <div class="page-container">
    <h2>个人中心</h2>
    <p>查看您的账户信息。</p>

    <el-descriptions :column="1" border style="max-width: 560px; margin-top: 16px">
      <el-descriptions-item label="用户名">
        {{ auth.userInfo?.username ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="姓名">
        {{ auth.userInfo?.realName ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="角色">
        <el-tag size="small">{{ auth.userInfo?.role ?? '-' }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="用户ID">
        {{ auth.userInfo?.userId ?? '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="部门ID">
        {{ auth.userInfo?.deptId ?? '-' }}
      </el-descriptions-item>
    </el-descriptions>

    <div style="margin-top: 24px">
      <el-button type="danger" plain @click="handleLogout">退出登录</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

function handleLogout() {
  auth.logout()
  router.push('/login')
}
</script>
