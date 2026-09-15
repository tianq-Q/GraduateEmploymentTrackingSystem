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

export interface TeacherVO {
  id: number
  jobNumber: string
  name: string
  deptId: number
  deptName: string
  phone: string
  email: string
  role: string
  status: number
  createTime: string
}

export interface TeacherCreateDTO {
  jobNumber: string
  name: string
  password: string
  deptId: number
  phone?: string
  email?: string
}

export interface TeacherUpdateDTO {
  id: number
  name: string
  deptId: number
  phone?: string
  email?: string
}

export interface TeacherQueryDTO {
  deptId?: number
  keyword?: string
  jobNumber?: string
  name?: string
  page?: number
  size?: number
}

/** 分页查询教师 */
export function getTeacherPage(params: TeacherQueryDTO) {
  return request.get<PageResult<TeacherVO>>('teacher/page', { params })
}

/** 新增教师 */
export function createTeacher(data: TeacherCreateDTO) {
  return request.post('teacher', data)
}

/** 更新教师信息 */
export function updateTeacher(id: number, data: TeacherUpdateDTO) {
  return request.put(`/teacher/${id}`, data)
}

/** 启用/禁用教师账号 */
export function toggleTeacherStatus(id: number) {
  return request.put(`/teacher/${id}/status`)
}
