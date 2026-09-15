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
  <el-menu
    :default-active="activeMenu"
    :collapse="isCollapse"
    :router="true"
    background-color="#ffffff"
    text-color="#4a5568"
    active-text-color="#4f8cff"
    class="sidebar-menu"
  >
    <div class="logo">
      <span v-if="!isCollapse">就业跟踪系统</span>
      <span v-else>ET</span>
    </div>

    <!-- 所有角色 -->
    <el-menu-item index="/dashboard">
      <el-icon><DataBoard /></el-icon>
      <span>工作台</span>
    </el-menu-item>

    <el-menu-item index="/profile">
      <el-icon><User /></el-icon>
      <span>个人中心</span>
    </el-menu-item>

    <!-- 毕业生 -->
    <template v-if="auth.isGraduate">
      <el-sub-menu index="student-employment">
        <template #title>
          <el-icon><Document /></el-icon>
          <span>就业信息</span>
        </template>
        <el-menu-item index="/student/employment/submit">就业登记</el-menu-item>
        <el-menu-item index="/student/employment/status">就业状态</el-menu-item>
        <el-menu-item index="/student/employment/attachment">附件管理</el-menu-item>
      </el-sub-menu>
      <el-menu-item index="/student/history">
        <el-icon><Clock /></el-icon>
        <span>审核历史</span>
      </el-menu-item>
    </template>

    <!-- 教师 + 院系管理员 -->
    <template v-if="auth.isTeacher || auth.isCollegeAdmin">
      <el-menu-item index="/teacher/review">
        <el-icon><Checked /></el-icon>
        <span>就业审核</span>
      </el-menu-item>
      <el-menu-item index="/teacher/proxy">
        <el-icon><EditPen /></el-icon>
        <span>代录就业</span>
      </el-menu-item>
      <el-menu-item index="/teacher/log">
        <el-icon><Document /></el-icon>
        <span>日志留存</span>
      </el-menu-item>
      <el-menu-item index="/teacher/track-reminders">
        <el-icon><Warning /></el-icon>
        <span>失业跟踪提醒</span>
      </el-menu-item>
    </template>

    <!-- 主任（校级管理员）业务管理 -->
    <template v-if="auth.isCollegeAdmin">
      <el-menu-item index="/admin/graduate">
        <el-icon><School /></el-icon>
        <span>毕业生管理</span>
      </el-menu-item>
      <el-menu-item index="/admin/review">
        <el-icon><Checked /></el-icon>
        <span>就业终审</span>
      </el-menu-item>
      <el-menu-item index="/admin/employment">
        <el-icon><Document /></el-icon>
        <span>就业信息</span>
      </el-menu-item>
      <el-sub-menu index="admin-mgmt">
        <template #title>
          <el-icon><Setting /></el-icon>
          <span>院系管理</span>
        </template>
        <el-menu-item index="/admin/departments">院系管理</el-menu-item>
        <el-menu-item index="/admin/majors">专业管理</el-menu-item>
        <el-menu-item index="/admin/classes">班级管理</el-menu-item>
        <el-menu-item index="/admin/teachers">教师管理</el-menu-item>
      </el-sub-menu>
    </template>

    <!-- 系统管理员专属 -->
    <template v-if="auth.isSystemAdmin">
      <el-sub-menu index="system-mgmt">
        <template #title>
          <el-icon><Tools /></el-icon>
          <span>系统管理</span>
        </template>
        <el-menu-item index="/admin/system/users">用户管理</el-menu-item>
        <el-menu-item index="/admin/system/logs">日志管理</el-menu-item>
      </el-sub-menu>
    </template>
  </el-menu>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  DataBoard, User, Document, Clock,
  Setting, Tools, Checked, EditPen, School, Warning
} from '@element-plus/icons-vue'

/**
 * 侧边栏菜单组件
 * - 根据当前登录用户的角色（毕业生 / 教师 / 学院管理员 / 系统管理员）
 *   动态渲染对应的菜单项
 * - 支持折叠模式（collapse），折叠时仅显示图标
 */
defineProps<{ isCollapse: boolean }>()

const route = useRoute()
const auth = useAuthStore()

// 当前高亮的菜单项：始终以路由路径为准
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.sidebar-menu {
  height: calc(100vh - 60px);   /* 减去 logo 高度，让超长菜单可滚动 */
  border-right: 1px solid var(--color-border);
  overflow-y: auto;             /* 菜单项过多时显示纵向滚动条 */
  overflow-x: hidden;
}
.sidebar-menu::-webkit-scrollbar { width: 6px; }
.sidebar-menu::-webkit-scrollbar-thumb { background: #cbd5e0; border-radius: 3px; }
.sidebar-menu::-webkit-scrollbar-track { background: transparent; }
.sidebar-menu:not(.el-menu--collapse) { width: 220px; }
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #4f8cff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid var(--color-border);
  background: #fff;
}
.sidebar-menu .el-menu-item:hover,
.sidebar-menu .el-sub-menu__title:hover {
  background-color: #e8f0fe !important;
}
.sidebar-menu .el-menu-item.is-active {
  background-color: #e8f0fe !important;
  font-weight: 600;
}
</style>
