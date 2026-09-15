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

// ========== 类型定义 ==========

export interface EmploymentSubmitDTO {
  studentNo?: string
  name?: string
  destination: string
  companyName?: string
  companyType?: string
  industry?: string
  position?: string
  salaryRange?: string
  city?: string
}

export interface EmploymentRecord {
  id: number
  graduateId: number
  studentNo?: string
  companyName: string
  companyType: string
  industry: string
  position: string
  salaryRange: string
  city: string
  destination: string
  contactPhone?: string
  reviewStatus: string
  reviewComment: string
  reviewerId: number
  reviewTime: string
  isProxy: boolean
  submitterId: number
  submitterName: string
  createTime: string
  updateTime: string
}

export interface AuditLog {
  id: number
  recordId: number
  action: string
  comment: string
  operatorId: number
  operatorName: string
  operatorRole: string
  createTime: string
}

export interface Attachment {
  id: number
  recordId: number
  fileName: string
  filePath: string
  fileSize: number
  fileType: string
  createTime: string
}

export interface SysUser {
  id: number
  username: string
  realName: string
  phone: string
  email: string
  role: string
  deptId: number
  status: number
}

export interface Graduate {
  id: number
  studentNo: string
  name: string
  deptId: number
  majorId: number
  classId: number
  grade: string
  graduateYear: string
  employmentStatus: string
}

export interface PageResult<T> {
  total: number
  page: number
  size: number
  list: T[]
}

// ========== 学生端 API ==========

/** 学生提交就业信息 */
export function submitEmployment(data: EmploymentSubmitDTO) {
  return request.post<EmploymentRecord>('/employment/submit', data)
}

/** 学生撤回就业记录 */
export function withdrawEmployment(recordId: number) {
  return request.post<EmploymentRecord>(`/employment/withdraw/${recordId}`)
}

/** 学生修改并重新提交 */
export function resubmitEmployment(recordId: number, data: EmploymentSubmitDTO) {
  return request.put<EmploymentRecord>(`/employment/resubmit/${recordId}`, data)
}

/** 查询当前学生的就业记录 */
export function getMyRecords() {
  return request.get<EmploymentRecord[]>('/employment/my-records')
}

// ========== 管理端 API ==========

/** 分页查询就业信息 */
export function getEmploymentList(params: {
  studentNo?: string
  name?: string
  companyName?: string
  destination?: string
  reviewStatus?: string
  deptId?: number
  page?: number
  size?: number
}) {
  return request.get<PageResult<EmploymentRecord>>('/employment/list', { params })
}

/** 批量查询毕业生信息 */
export function getGraduatesByIds(ids: number[]) {
  return request.get<Graduate[]>('/employment/graduates', { params: { ids: ids.join(',') } })
}

/** 查询就业记录的操作日志 */
export function getAuditLogs(recordId: number) {
  return request.get<AuditLog[]>(`/employment/audit-logs/${recordId}`)
}

/** 查询所有操作日志（分页） */
export function getAllAuditLogs(params: {
  action?: string
  page?: number
  size?: number
}) {
  return request.get<PageResult<AuditLog>>('/employment/all-audit-logs', { params })
}

// ========== 附件 API ==========

/** 上传附件 */
export function uploadAttachment(recordId: number, file: File) {
  const formData = new FormData()
  formData.append('recordId', String(recordId))
  formData.append('file', file)
  return request.post<Attachment>('/attachment/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/** 获取附件下载/预览地址 */
export function getAttachmentUrl(id: number) {
  return `/api/attachment/download/${id}`
}

/** 获取就业记录的附件列表 */
export function getAttachments(recordId: number) {
  return request.get<Attachment[]>(`/attachment/list/${recordId}`)
}

/** 删除附件 */
export function deleteAttachment(id: number) {
  return request.delete(`/attachment/${id}`)
}

// ========== 代录 API ==========

/** 获取可代录的学生列表 */
export function getProxyStudents() {
  return request.get<SysUser[]>('/teacher/proxy/students')
}

/** 代录提交 */
export function proxySubmit(data: EmploymentSubmitDTO) {
  return request.post<EmploymentRecord>('/teacher/proxy/submit', data)
}

// ========== 审核 API ==========

/** 教师初审通过 */
export function teacherApprove(recordId: number, comment?: string) {
  return request.post('/teacher/review/approve', null, { params: { recordId, comment } })
}

/** 教师初审驳回 */
export function teacherReject(recordId: number, comment?: string) {
  return request.post('/teacher/review/reject', null, { params: { recordId, comment } })
}

/** 管理员终审通过 */
export function adminApprove(recordId: number, comment?: string) {
  return request.post('/admin/review/approve', null, { params: { recordId, comment } })
}

/** 管理员终审驳回 */
export function adminReject(recordId: number, comment?: string) {
  return request.post('/admin/review/reject', null, { params: { recordId, comment } })
}

/** 获取待初审列表（支持筛选） */
export function getFirstPendingList(params?: {
  name?: string
  companyName?: string
  destination?: string
  startTime?: string
  endTime?: string
}) {
  return request.get<EmploymentRecord[]>('/teacher/review/pending', { params })
}

/** 获取待终审列表（支持筛选） */
export function getFinalPendingList(params?: {
  name?: string
  companyName?: string
  destination?: string
  startTime?: string
  endTime?: string
}) {
  return request.get<EmploymentRecord[]>('/admin/review/pending', { params })
}

/** 获取教师所在院系的待终审列表（初审已通过） */
export function getTeacherFinalPendingList(params?: {
  name?: string
  companyName?: string
  destination?: string
  startTime?: string
  endTime?: string
}) {
  return request.get<EmploymentRecord[]>('/teacher/review/final-pending', { params })
}

/** 教师批量审核 */
export function teacherBatchReview(recordIds: number[], action: 'PASS' | 'REJECT', comment?: string) {
  return request.post('/teacher/review/batch', { recordIds, action, comment })
}

/** 管理员批量审核 */
export function adminBatchReview(recordIds: number[], action: 'PASS' | 'REJECT', comment?: string) {
  return request.post('/admin/review/batch', { recordIds, action, comment })
}

/** 查询审核操作日志 */
export function getReviewAuditLogs(recordId: number) {
  return request.get<AuditLog[]>(`/review/audit-logs/${recordId}`)
}

// ========== 状态映射工具 ==========

export const reviewStatusMap: Record<string, { label: string; type: string }> = {
  PENDING: { label: '待学院初审', type: 'warning' },
  FIRST_PASSED: { label: '待学校终审', type: 'primary' },
  FIRST_REJECTED: { label: '已驳回待修改', type: 'danger' },
  APPROVED: { label: '审核通过', type: 'success' },
  FINAL_REJECTED: { label: '已驳回待修改', type: 'danger' },
  WITHDRAWN: { label: '已撤回', type: 'info' }
}

export const actionMap: Record<string, string> = {
  SUBMIT: '提交填报',
  WITHDRAW: '撤回记录',
  UPDATE: '修改重提',
  FIRST_PASS: '初审通过',
  FIRST_REJECT: '初审驳回',
  FINAL_PASS: '终审通过',
  FINAL_REJECT: '终审驳回',
  PROXY_SUBMIT: '管理员代录'
}
