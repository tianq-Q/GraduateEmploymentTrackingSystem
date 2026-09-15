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

export interface MajorVO {
  id: number
  name: string
  code: string
  deptId: number
  deptName: string
  description: string
  status: number
  sortOrder: number
  classCount: number
  graduateCount: number
  createTime: string
}

export interface MajorSimpleVO {
  id: number
  name: string
  deptId: number
  deptName: string
}

export interface MajorCreateDTO {
  name: string
  code: string
  deptId: number
  description?: string
  sortOrder?: number
}

export interface MajorUpdateDTO {
  id: number
  name: string
  code: string
  deptId: number
  description?: string
  sortOrder?: number
}

export interface MajorQueryDTO {
  deptId?: number
  keyword?: string
  name?: string
  code?: string
  page?: number
  size?: number
}

export interface MajorStatisticsVO {
  majorId: number
  majorName: string
  deptName: string
  totalGraduates: number
  employedCount: number
  unemployedCount: number
  employmentRate: string
}

/** 分页查询专业 */
export function getMajorPage(params: MajorQueryDTO) {
  return request.get<PageResult<MajorVO>>('major/page', { params })
}

/** 查询专业列表 */
export function getMajorList(deptId?: number) {
  return request.get<MajorSimpleVO[]>('major/list', { params: { deptId } })
}

/** 新增专业 */
export function createMajor(data: MajorCreateDTO) {
  return request.post('major', data)
}

/** 更新专业 */
export function updateMajor(id: number, data: MajorUpdateDTO) {
  return request.put(`/major/${id}`, data)
}

/** 删除专业 */
export function deleteMajor(id: number) {
  return request.delete(`/major/${id}`)
}

/** 专业统计 */
export function getMajorStatistics(id: number) {
  return request.get<MajorStatisticsVO>(`/major/${id}/statistics`)
}
