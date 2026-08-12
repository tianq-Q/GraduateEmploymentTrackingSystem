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

defineProps<{ isCollapse: boolean }>()
defineEmits<{ toggle: [] }>()

const router = useRouter()
const auth = useAuthStore()

const roleLabel = computed(() => {
  const map: Record<string, string> = {
    GRADUATE: '毕业生',
    TEACHER: '教师',
    COLLEGE_ADMIN: '学院管理员',
    SYSTEM_ADMIN: '系统管理员'
  }
  return map[auth.role] || auth.role
})

const roleTagType = computed(() => {
  const map: Record<string, string> = {
    GRADUATE: 'info',
    TEACHER: 'success',
    COLLEGE_ADMIN: 'warning',
    SYSTEM_ADMIN: 'danger'
  }
  return map[auth.role] || 'info'
})

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
