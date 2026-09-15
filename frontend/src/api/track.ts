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

/** 跟踪设置（按毕业年份） */
export interface TrackSettingVO {
  graduateYear: string
  trackYears: number
  studentCount: number
}

/** 年度就业状态记录 */
export interface TrackYearRecordVO {
  year: number
  destination: string
  destinationName: string
  companyName: string
  position: string
  city: string
  salaryRange: string
  recordTime: string
}

/** 毕业生跟踪详情 */
export interface TrackDetailVO {
  graduateId: number
  studentNumber: string
  name: string
  graduateYear: string
  trackYears: number
  startYear: number
  endYear: number
  currentYear: number
  trackedYears: number
  remainingYears: number
  status: 'TRACKING' | 'FINISHED'
  statusName: string
  progress: number
  yearRecords: TrackYearRecordVO[]
}

/** 查询所有届的跟踪设置 */
export function getTrackSettings() {
  return request.get<TrackSettingVO[]>('/track/settings')
}

/** 新增/更新某届跟踪年限 */
export function updateTrackSetting(data: { graduateYear: string; trackYears: number }) {
  return request.put('/track/settings', data)
}

/** 删除某届跟踪设置（恢复默认3年） */
export function deleteTrackSetting(graduateYear: string) {
  return request.delete(`/track/settings/${graduateYear}`)
}

/** 查询某毕业生的跟踪详情（进度 + 年度就业状态时间线） */
export function getGraduateTrack(id: number) {
  return request.get<TrackDetailVO>(`/track/graduate/${id}`)
}

/** 学生查询本人的跟踪详情 */
export function getMyTrack() {
  return request.get<TrackDetailVO>('/track/my')
}

/** ==================== 就业状态流转管理 ==================== */

/** 就业跟踪状态（六大类） */
export const TRACK_STATUS = {
  UNEMPLOYED: '未就业',
  PENDING: '待审核',
  EMPLOYED: '已就业',
  UNEMPLOYED_AFTER: '失业',
  POSTGRADUATE: '升学',
  ABROAD: '出国'
} as const

export type TrackStatusKey = keyof typeof TRACK_STATUS

/** 失业原因字典 */
export const UNEMPLOYED_REASONS: Record<string, string> = {
  VOLUNTARY_QUIT: '主动离职',
  LAYOFF: '企业裁员',
  OTHER: '其他'
}

/** 提交状态变更 */
export interface StatusChangeSubmitDTO {
  graduateId?: number
  toStatus: string
  reason?: string
  evidenceUrl?: string
  remark?: string
}

/** 状态变更记录 */
export interface StatusChangeVO {
  id: number
  graduateId: number
  studentNo: string
  studentName: string
  deptId: number
  deptName: string
  fromStatus: string
  fromStatusName: string
  toStatus: string
  toStatusName: string
  reason: string
  evidenceUrl: string
  remark: string
  operatorName: string
  isReminder: number
  createTime: string
}

/** 提交就业状态变更（学生本人/教师代录） */
export function submitStatusChange(data: StatusChangeSubmitDTO) {
  return request.post<StatusChangeVO>('/track/status-change/submit', data)
}

/** 学生查询本人的状态变更历史 */
export function getMyStatusChanges() {
  return request.get<StatusChangeVO[]>('/track/status-change/my')
}

/** 学生当前就业跟踪状态 */
export function getMyTrackStatus() {
  return request.get<{ graduateId: number; studentNo: string; name: string; status: string; statusName: string }>(
    '/track/status-change/my/status'
  )
}

/** 教师/管理员查询状态变更记录 */
export function getStatusChangeList() {
  return request.get<StatusChangeVO[]>('/track/status-change/list')
}

/** 教师端：失业学生跟踪提醒列表 */
export function getUnemployedReminders() {
  return request.get<StatusChangeVO[]>('/track/status-change/reminders')
}

/** 查询某毕业生的状态变更历史 */
export function getGraduateStatusChanges(graduateId: number) {
  return request.get<StatusChangeVO[]>(`/track/status-change/graduate`, { params: { graduateId } })
}
