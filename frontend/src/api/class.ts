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

export interface ClassVO {
  id: number
  name: string
  majorId: number
  majorName: string
  deptId: number
  deptName: string
  enrollmentYear: number
  graduationYear: number
  description: string
  status: number
  sortOrder: number
  graduateCount: number
  createTime: string
}

export interface ClassSimpleVO {
  id: number
  name: string
  majorId: number
  majorName: string
}

export interface ClassCreateDTO {
  name: string
  majorId: number
  enrollmentYear: number
  graduationYear: number
  description?: string
  sortOrder?: number
}

export interface ClassUpdateDTO {
  id: number
  name: string
  majorId: number
  enrollmentYear: number
  graduationYear: number
  description?: string
  sortOrder?: number
}

export interface ClassQueryDTO {
  majorId?: number
  keyword?: string
  name?: string
  enrollmentYear?: number
  graduationYear?: number
  page?: number
  size?: number
}

export interface ClassStatisticsVO {
  classId: number
  className: string
  majorName: string
  deptName: string
  totalGraduates: number
  employedCount: number
  unemployedCount: number
  employmentRate: string
}

/** 分页查询班级 */
export function getClassPage(params: ClassQueryDTO) {
  return request.get<PageResult<ClassVO>>('class/page', { params })
}

/** 查询班级列表 */
export function getClassList(majorId?: number) {
  return request.get<ClassSimpleVO[]>('class/list', { params: { majorId } })
}

/** 新增班级 */
export function createClass(data: ClassCreateDTO) {
  return request.post('class', data)
}

/** 更新班级 */
export function updateClass(id: number, data: ClassUpdateDTO) {
  return request.put(`/class/${id}`, data)
}

/** 删除班级 */
export function deleteClass(id: number) {
  return request.delete(`/class/${id}`)
}

/** 班级统计 */
export function getClassStatistics(id: number) {
  return request.get<ClassStatisticsVO>(`/class/${id}/statistics`)
}
