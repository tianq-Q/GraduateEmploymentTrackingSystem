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

import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/auth/Login.vue'),
      meta: { guestOnly: true }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/auth/Register.vue'),
      meta: { guestOnly: true }
    },
    {
      path: '/',
      component: () => import('@/components/layout/AppLayout.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/dashboard/index.vue'),
          meta: { title: '工作台', roles: ['GRADUATE', 'TEACHER', 'COLLEGE_ADMIN', 'SYSTEM_ADMIN'] }
        },
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('@/views/profile/index.vue'),
          meta: { title: '个人中心', roles: ['GRADUATE', 'TEACHER', 'COLLEGE_ADMIN', 'SYSTEM_ADMIN'] }
        },
        // 毕业生：就业信息
        {
          path: 'student/employment/submit',
          name: 'EmploymentSubmit',
          component: () => import('@/views/student/employment/EmploymentSubmit.vue'),
          meta: { title: '就业登记', roles: ['GRADUATE'] }
        },
        {
          path: 'student/employment/status',
          name: 'EmploymentStatus',
          component: () => import('@/views/student/employment/EmploymentStatus.vue'),
          meta: { title: '就业状态', roles: ['GRADUATE'] }
        },
        {
          path: 'student/employment/attachment',
          name: 'EmploymentAttachment',
          component: () => import('@/views/student/employment/AttachmentManage.vue'),
          meta: { title: '附件管理', roles: ['GRADUATE'] }
        },
        {
          path: 'student/history',
          name: 'ReviewHistory',
          component: () => import('@/views/student/history/ReviewHistory.vue'),
          meta: { title: '审核历史', roles: ['GRADUATE'] }
        },
        // 教师
        {
          path: 'teacher/review',
          name: 'TeacherReview',
          component: () => import('@/views/teacher/review/ReviewList.vue'),
          meta: { title: '就业审核', roles: ['TEACHER', 'COLLEGE_ADMIN'] }
        },
        {
          path: 'teacher/proxy',
          name: 'TeacherProxy',
          component: () => import('@/views/teacher/proxy/ProxySubmit.vue'),
          meta: { title: '代录就业', roles: ['TEACHER', 'COLLEGE_ADMIN'] }
        },
        {
          path: 'teacher/log',
          name: 'AuditLogList',
          component: () => import('@/views/teacher/log/AuditLogList.vue'),
          meta: { title: '日志留存', roles: ['TEACHER', 'COLLEGE_ADMIN'] }
        },
        {
          path: 'teacher/track-reminders',
          name: 'TrackReminders',
          component: () => import('@/views/teacher/track/TrackReminders.vue'),
          meta: { title: '失业跟踪提醒', roles: ['TEACHER', 'COLLEGE_ADMIN'] }
        },
        // 校级管理员 + 系统管理员
        {
          path: 'admin/graduate',
          name: 'AdminGraduate',
          component: () => import('@/views/admin/Graduate.vue'),
          meta: { title: '毕业生管理', roles: ['COLLEGE_ADMIN'] }
        },
        {
          path: 'admin/review',
          name: 'AdminReview',
          component: () => import('@/views/admin/review/ReviewList.vue'),
          meta: { title: '就业终审', roles: ['COLLEGE_ADMIN'] }
        },
        {
          path: 'admin/employment',
          name: 'AdminEmployment',
          component: () => import('@/views/admin/Employment.vue'),
          meta: { title: '就业信息', roles: ['COLLEGE_ADMIN'] }
        },
        {
          path: 'admin/departments',
          name: 'Departments',
          component: () => import('@/views/admin/Department.vue'),
          meta: { title: '院系管理', roles: ['COLLEGE_ADMIN'] }
        },
        {
          path: 'admin/majors',
          name: 'Majors',
          component: () => import('@/views/admin/Major.vue'),
          meta: { title: '专业管理', roles: ['COLLEGE_ADMIN'] }
        },
        {
          path: 'admin/classes',
          name: 'Classes',
          component: () => import('@/views/admin/Class.vue'),
          meta: { title: '班级管理', roles: ['COLLEGE_ADMIN'] }
        },
        {
          path: 'admin/teachers',
          name: 'AdminTeachers',
          component: () => import('@/views/admin/Teacher.vue'),
          meta: { title: '教师管理', roles: ['COLLEGE_ADMIN'] }
        },
        // 系统管理员专属
        {
          path: 'admin/system/users',
          name: 'SystemUsers',
          component: () => import('@/views/admin/system/UserList.vue'),
          meta: { title: '用户管理', roles: ['SYSTEM_ADMIN'] }
        },
        {
          path: 'admin/system/logs',
          name: 'SystemLogs',
          component: () => import('@/views/admin/system/Logs.vue'),
          meta: { title: '日志管理', roles: ['SYSTEM_ADMIN'] }
        }
      ]
    }
  ]
})

const VALID_ROLES = ['GRADUATE', 'TEACHER', 'COLLEGE_ADMIN', 'SYSTEM_ADMIN']

router.beforeEach((to, _from, next) => {
  const store = useAuthStore()

  // 防御：token 存在但 role 缺失或非法（旧/损坏的 localStorage 数据）→ 清理并跳登录，避免无限重定向白屏
  if (store.isLoggedIn && !VALID_ROLES.includes(store.role)) {
    store.logout()
    return next('/login')
  }

  if (to.meta.guestOnly && store.isLoggedIn) {
    return next('/dashboard')
  }

  if (!to.meta.guestOnly && !store.isLoggedIn) {
    return next('/login')
  }

  // 系统管理员无业务看板，落地页固定为系统管理（避免访问 /dashboard 触发业务接口 403）
  if (store.role === 'SYSTEM_ADMIN' && (to.path === '/dashboard' || to.path === '/')) {
    return next('/admin/system/users')
  }

  const roles = to.meta.roles as string[] | undefined
  if (roles && roles.length > 0 && !roles.includes(store.role)) {
    return next(store.role === 'SYSTEM_ADMIN' ? '/admin/system/users' : '/dashboard')
  }

  next()
})

export default router
