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
  <div class="navbar">
    <div class="left">
      <el-icon class="collapse-btn" @click="$emit('toggle')">
        <Fold v-if="!isCollapse" /><Expand v-else />
      </el-icon>
    </div>
    <div class="right">
      <el-tag :type="roleTagType" size="small" class="role-tag">
        {{ roleLabel }}
      </el-tag>
      <span class="username">{{ auth.realName || auth.userInfo?.username }}</span>
      <el-dropdown @command="handleCommand">
        <el-avatar :size="32" :src="auth.userInfo?.avatar">
          {{ (auth.realName || 'U').charAt(0) }}
        </el-avatar>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
            <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Fold, Expand } from '@element-plus/icons-vue'

/**
 * 顶部导航栏组件
 * - 左侧：折叠/展开侧边栏按钮（通过 emit('toggle') 通知父组件）
 * - 右侧：当前角色标签、用户名、用户头像下拉菜单（个人中心 / 退出登录）
 */
defineProps<{ isCollapse: boolean }>()
defineEmits<{ toggle: [] }>()

const router = useRouter()
const auth = useAuthStore()

// 角色中文名称映射
const roleLabel = computed(() => {
  const map: Record<string, string> = {
    GRADUATE: '毕业生',
    TEACHER: '教师',
    COLLEGE_ADMIN: '学院管理员',
    SYSTEM_ADMIN: '系统管理员'
  }
  return map[auth.role] || auth.role
})

// 角色对应的 el-tag 颜色类型映射
const roleTagType = computed(() => {
  const map: Record<string, string> = {
    GRADUATE: 'info',
    TEACHER: 'success',
    COLLEGE_ADMIN: 'warning',
    SYSTEM_ADMIN: 'danger'
  }
  return map[auth.role] || 'info'
})

/** 处理头像下拉菜单指令：跳转个人中心 / 退出登录 */
function handleCommand(cmd: string) {
  if (cmd === 'profile') router.push('/profile')
  else if (cmd === 'logout') { auth.logout(); router.push('/login') }
}
</script>

<style scoped>
.navbar {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}
.left { display: flex; align-items: center; }
.collapse-btn { font-size: 20px; cursor: pointer; }
.right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.role-tag { font-weight: bold; }
.username { color: #606266; }
</style>
