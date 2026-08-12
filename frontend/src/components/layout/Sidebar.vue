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
      </el-sub-menu>
      <el-menu-item index="/student/history">
        <el-icon><Clock /></el-icon>
        <span>审核历史</span>
      </el-menu-item>
      <el-menu-item index="/student/jobs">
        <el-icon><Search /></el-icon>
        <span>求职中心</span>
      </el-menu-item>
    </template>

    <!-- 教师 + 院系管理员 -->
    <template v-if="auth.isTeacher || auth.isCollegeAdmin">
      <el-menu-item index="/teacher/students">
        <el-icon><UserFilled /></el-icon>
        <span>学生列表</span>
      </el-menu-item>
      <el-menu-item index="/teacher/review">
        <el-icon><Checked /></el-icon>
        <span>就业审核</span>
      </el-menu-item>
      <el-menu-item index="/teacher/proxy">
        <el-icon><EditPen /></el-icon>
        <span>代录就业</span>
      </el-menu-item>
      <el-menu-item index="/teacher/statistics">
        <el-icon><PieChart /></el-icon>
        <span>统计分析</span>
      </el-menu-item>
    </template>

    <!-- 校级管理员 + 系统管理员 -->
    <template v-if="auth.isCollegeAdmin || auth.isSystemAdmin">
      <el-menu-item index="/admin/overview">
        <el-icon><DataAnalysis /></el-icon>
        <span>数据总览</span>
      </el-menu-item>
      <el-menu-item index="/admin/graduate">
        <el-icon><School /></el-icon>
        <span>毕业生管理</span>
      </el-menu-item>
      <el-menu-item index="/admin/review">
        <el-icon><Checked /></el-icon>
        <span>就业终审</span>
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
        <el-menu-item index="/admin/students">学生管理</el-menu-item>
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
  DataBoard, User, Document, Search, UserFilled, Clock,
  PieChart, Setting, Tools, Checked, EditPen, DataAnalysis, School
} from '@element-plus/icons-vue'

defineProps<{ isCollapse: boolean }>()

const route = useRoute()
const auth = useAuthStore()
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.sidebar-menu {
  height: 100%;
  border-right: 1px solid var(--color-border);
}
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
