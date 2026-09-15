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

export interface GraduateVO {
  id: number
  studentNumber: string
  name: string
  gender: string
  deptId: number
  deptName: string
  majorId: number
  majorName: string
  classId: number
  className: string
  phone: string
  email: string
  status: string
  graduateStatus: number
  createTime: string
}

export interface GraduateCreateDTO {
  studentNumber: string
  name: string
  gender?: string
  deptId: number
  majorId: number
  classId: number
  phone?: string
  email?: string
}

export interface GraduateUpdateDTO {
  id: number
  name: string
  gender?: string
  deptId: number
  majorId: number
  classId: number
  phone?: string
  email?: string
}

export interface GraduateQueryDTO {
  deptId?: number
  majorId?: number
  classId?: number
  graduationYear?: number
  status?: string
  keyword?: string
  studentNumber?: string
  name?: string
  page?: number
  size?: number
}

/** 就业申请记录 */
export interface EmploymentRecordVO {
  id: number
  destination: string
  destinationName: string
  companyName: string
  position: string
  salaryRange: string
  evidenceUrl: string
  reviewStatus?: string
  reviewStatusName?: string
  stage?: string
  stageName?: string
  createTime: string
}

/** 审核进度 */
export interface ReviewProgressVO {
  reviewStatus: string
  reviewStatusName: string
  stage: string
  stageName: string
  teacherComment: string
  adminComment: string
  teacherReviewTime: string
  adminReviewTime: string
  createTime: string
}

/** 毕业生详情（基本信息 + 就业记录 + 审核进度） */
export interface GraduateDetailVO {
  basicInfo: GraduateVO
  employmentRecords: EmploymentRecordVO[]
  reviewProgress: ReviewProgressVO | null
}

/** 学生：获取我的毕业生档案信息（学号/姓名/专业/班级等） */
export function getMyProfile() {
  return request.get<GraduateVO>('/graduate/profile')
}

/** 学生：修改我的资料（电话/邮箱/性别） */
export function updateMyProfile(data: { phone?: string; email?: string; gender?: string }) {
  return request.put('/graduate/profile', data)
}

/** 分页查询毕业生 */
export function getGraduatePage(params: GraduateQueryDTO) {
  return request.get<PageResult<GraduateVO>>('graduate/page', { params })
}

/** 获取毕业生详情 */
export function getGraduateById(id: number) {
  return request.get<GraduateVO>(`/graduate/${id}`)
}

/** 获取毕业生完整详情（基本信息 + 就业记录 + 审核进度） */
export function getGraduateDetail(id: number) {
  return request.get<GraduateDetailVO>(`/graduate/${id}/detail`)
}

/** 分页查询某毕业生的就业申请历史记录 */
export function getGraduateEmploymentHistory(id: number, params: { page?: number; size?: number }) {
  return request.get<PageResult<EmploymentRecordVO>>(`/graduate/${id}/employment-history`, { params })
}

/** 新增毕业生 */
export function createGraduate(data: GraduateCreateDTO) {
  return request.post('graduate', data)
}

/** 更新毕业生信息 */
export function updateGraduate(id: number, data: GraduateUpdateDTO) {
  return request.put(`/graduate/${id}`, data)
}

/** 启用/禁用毕业生账号 */
export function toggleGraduateStatus(id: number) {
  return request.put(`/graduate/${id}/status`)
}
