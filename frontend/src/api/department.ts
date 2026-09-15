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

export interface DepartmentVO {
  id: number
  name: string
  code: string
  description: string
  status: number
  sortOrder: number
  majorCount: number
  graduateCount: number
  createTime: string
}

export interface DepartmentSimpleVO {
  id: number
  name: string
}

export interface DepartmentCreateDTO {
  name: string
  code: string
  description?: string
  sortOrder?: number
}

export interface DepartmentUpdateDTO {
  id: number
  name: string
  code: string
  description?: string
  sortOrder?: number
}

export interface DepartmentQueryDTO {
  keyword?: string
  name?: string
  code?: string
  page?: number
  size?: number
}

export interface DepartmentStatisticsVO {
  deptId: number
  deptName: string
  totalGraduates: number
  employedCount: number
  unemployedCount: number
  furtherStudyCount: number
  entrepreneurshipCount: number
  abroadCount: number
  employmentRate: string
}

/** 分页查询院系 */
export function getDepartmentPage(params: DepartmentQueryDTO) {
  return request.get<PageResult<DepartmentVO>>('department/page', { params })
}

/** 查询所有院系 */
export function getDepartmentList() {
  return request.get<DepartmentSimpleVO[]>('department/list')
}

/** 获取院系详情 */
export function getDepartmentById(id: number) {
  return request.get<DepartmentVO>(`/department/${id}`)
}

/** 新增院系 */
export function createDepartment(data: DepartmentCreateDTO) {
  return request.post('department', data)
}

/** 更新院系 */
export function updateDepartment(id: number, data: DepartmentUpdateDTO) {
  return request.put(`/department/${id}`, data)
}

/** 删除院系 */
export function deleteDepartment(id: number) {
  return request.delete(`/department/${id}`)
}

/** 院系统计 */
export function getDepartmentStatistics(id: number) {
  return request.get<DepartmentStatisticsVO>(`/department/${id}/statistics`)
}
