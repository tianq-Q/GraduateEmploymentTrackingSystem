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

// 数据看板模块类型定义

/** 概览统计（数字卡片） */
export interface DashboardOverview {
  totalGraduates: number;     // 毕业生总数
  employedCount: number;      // 已落实去向人数
  waitingCount: number;       // 待就业人数
  furtherStudyCount: number;  // 升学深造人数
  entrepreneurshipCount: number; // 自主创业人数
  employmentRate: number;     // 总体就业率(%)
  syncTime: string;           // 同步时间
}

/** 名称-数值对（图表分布） */
export interface NameValue {
  name: string;
  value: number;
}

/** 教师端本院系就业总览统计 */
export interface TeacherDashboardStat {
  deptId: number;
  deptName: string;
  totalGraduates: number;
  employedCount: number;
  unemployedCount: number;
  employmentRate: number;
  pendingReviews: number;
  statusDistribution: NameValue[];
}

/** 院系就业率对比 */
export interface DepartmentCompare {
  deptId: number;
  deptName: string;
  total: number;
  employed: number;
  rate: number;
}

/** 就业去向分布 */
export interface DestinationChart {
  destination: string;
  count: number;
}

/** 行业 / 单位性质分布 */
export interface IndustryChart {
  name: string;
  count: number;
}

/** 月度趋势 */
export interface TrendChart {
  month: string;
  rate: number;
  increment: number;
}

/** 同步结果 */
export interface SyncResult {
  success: boolean;
  syncedCount: number;
  syncTime: string;
  message: string;
}

/** 通知 */
export interface NotificationItem {
  id: number;
  userId: number;
  title: string;
  content: string;
  isRead: number; // 0未读 1已读
  createTime: string;
}

/** 操作日志 */
export interface OperationLogItem {
  id: number;
  module: string;
  action: string;
  description: string;
  operatorId: number;
  operatorName: string;
  ip: string;
  createTime: string;
}

/** 操作日志查询条件 */
export interface LogQuery {
  module?: string;
  operatorName?: string;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
}

/** 分页结果 */
export interface PageResult<T> {
  total: number;
  page: number;
  size: number;
  records: T[];
}
