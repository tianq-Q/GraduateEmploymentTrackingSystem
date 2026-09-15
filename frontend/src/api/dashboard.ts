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

import request from './request'
import type { PageResult } from '@/types/api'
import type {
  DashboardOverview,
  DepartmentCompare,
  DestinationChart,
  IndustryChart,
  TrendChart,
  SyncResult,
  NotificationItem,
  OperationLogItem,
  LogQuery,
  TeacherDashboardStat,
} from '@/types/dashboard'

// ===================== ① 就业率统计 =====================
/** 看板概览统计 */
export const getOverview = () =>
  request.get<DashboardOverview>('/dashboard/overview')

/** 教师端本院系就业总览统计（仅教师可访问） */
export const getTeacherDashboardStat = () =>
  request.get<TeacherDashboardStat>('/dashboard/teacher/stat')

// ===================== ② 可视化图表 =====================
/** 院系就业率对比 */
export const getDepartmentCompare = () =>
  request.get<DepartmentCompare[]>('/dashboard/charts/department')

/** 就业去向分布 */
export const getDestinationDistribution = () =>
  request.get<DestinationChart[]>('/dashboard/charts/destination')

/** 行业分布 Top N */
export const getIndustryDistribution = (limit = 10) =>
  request.get<IndustryChart[]>('/dashboard/charts/industry', { params: { limit } })

/** 单位性质分布 */
export const getCompanyTypeDistribution = () =>
  request.get<IndustryChart[]>('/dashboard/charts/company-type')

/** 月度就业率趋势 */
export const getMonthlyTrend = () =>
  request.get<TrendChart[]>('/dashboard/charts/trend')

// ===================== 数据同步 =====================
/** 触发看板数据同步刷新 */
export const syncDashboard = () =>
  request.post<SyncResult>('/dashboard/sync')

// ===================== ③ 报表导出 =====================
/** 导出看板统计报表(CSV)，返回 Blob 触发浏览器下载 */
export const exportDashboardCsv = () =>
  request.get('/dashboard/export', { responseType: 'blob' }) as unknown as Promise<Blob>

// ===================== ④ 业务通知 =====================
/** 当前用户通知列表 */
export const getNotifications = (onlyUnread = false) =>
  request.get<NotificationItem[]>('/dashboard/notifications', { params: { onlyUnread } })

/** 未读通知数量 */
export const getUnreadCount = () =>
  request.get<number>('/dashboard/notifications/unread-count')

/** 标记通知已读 */
export const markNotificationRead = (id: number) =>
  request.post<void>(`/dashboard/notifications/${id}/read`)

// ===================== ⑤ 操作日志 =====================
/** 操作日志分页查询 */
export const getOperationLogs = (query: LogQuery) =>
  request.get<PageResult<OperationLogItem>>('/dashboard/logs', { params: query })
