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
          path: 'student/history',
          name: 'ReviewHistory',
          component: () => import('@/views/student/history/ReviewHistory.vue'),
          meta: { title: '审核历史', roles: ['GRADUATE'] }
        },
        {
          path: 'student/jobs',
          name: 'Jobs',
          component: () => import('@/views/student/Jobs.vue'),
          meta: { title: '求职中心', roles: ['GRADUATE'] }
        },
        // 教师
        {
          path: 'teacher/students',
          name: 'TeacherStudents',
          component: () => import('@/views/teacher/StudentList.vue'),
          meta: { title: '学生列表', roles: ['TEACHER', 'COLLEGE_ADMIN'] }
        },
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
          path: 'teacher/statistics',
          name: 'TeacherStatistics',
          component: () => import('@/views/teacher/Statistics.vue'),
          meta: { title: '统计分析', roles: ['TEACHER', 'COLLEGE_ADMIN', 'SYSTEM_ADMIN'] }
        },
        // 校级管理员 + 系统管理员
        {
          path: 'admin/overview',
          name: 'AdminOverview',
          component: () => import('@/views/admin/Overview.vue'),
          meta: { title: '数据总览', roles: ['COLLEGE_ADMIN', 'SYSTEM_ADMIN'] }
        },
        {
          path: 'admin/graduate',
          name: 'AdminGraduate',
          component: () => import('@/views/admin/graduate/GraduateList.vue'),
          meta: { title: '毕业生管理', roles: ['COLLEGE_ADMIN', 'SYSTEM_ADMIN'] }
        },
        {
          path: 'admin/review',
          name: 'AdminReview',
          component: () => import('@/views/admin/review/ReviewList.vue'),
          meta: { title: '就业终审', roles: ['COLLEGE_ADMIN', 'SYSTEM_ADMIN'] }
        },
        {
          path: 'admin/departments',
          name: 'Departments',
          component: () => import('@/views/admin/Department.vue'),
          meta: { title: '院系管理', roles: ['COLLEGE_ADMIN', 'SYSTEM_ADMIN'] }
        },
        {
          path: 'admin/majors',
          name: 'Majors',
          component: () => import('@/views/admin/Major.vue'),
          meta: { title: '专业管理', roles: ['COLLEGE_ADMIN', 'SYSTEM_ADMIN'] }
        },
        {
          path: 'admin/classes',
          name: 'Classes',
          component: () => import('@/views/admin/Class.vue'),
          meta: { title: '班级管理', roles: ['COLLEGE_ADMIN', 'SYSTEM_ADMIN'] }
        },
        {
          path: 'admin/teachers',
          name: 'AdminTeachers',
          component: () => import('@/views/admin/TeacherList.vue'),
          meta: { title: '教师管理', roles: ['COLLEGE_ADMIN', 'SYSTEM_ADMIN'] }
        },
        {
          path: 'admin/students',
          name: 'AdminStudents',
          component: () => import('@/views/admin/StudentList.vue'),
          meta: { title: '学生管理', roles: ['COLLEGE_ADMIN', 'SYSTEM_ADMIN'] }
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

  const roles = to.meta.roles as string[] | undefined
  if (roles && roles.length > 0 && !roles.includes(store.role)) {
    return next('/dashboard')
  }

  next()
})

export default router
