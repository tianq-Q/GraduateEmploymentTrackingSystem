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

/** 账号信息（系统管理员 - 主任/教师/毕业生账号管理） */
export interface SystemUser {
  id: number
  username: string
  realName: string
  studentNumber: string
  role: string
  roleName: string
  deptId: number | null
  deptName: string | null
  status: number // 1=启用 0=禁用
  createTime: string
}

/** 账号分页查询参数 */
export interface UserQuery {
  page: number
  size: number
  role?: string
  /** 账号 / 姓名 / 学号 模糊检索 */
  keyword?: string
  /** 院系名称 模糊检索 */
  deptName?: string
  /** 账号状态：1=启用 0=禁用 */
  status?: number
}

/** 账号分页查询（仅系统管理员） */
export const getSystemUsers = (params: UserQuery): Promise<PageResult<SystemUser>> =>
  request.get<PageResult<SystemUser>>('/admin/system/users', { params }) as unknown as Promise<PageResult<SystemUser>>

/** 启用/禁用账号：status=1 启用，0 禁用（仅系统管理员） */
export const updateUserStatus = (id: number, status: number): Promise<void> =>
  request.put<void>(`/admin/system/users/${id}/status`, null, { params: { status } }) as unknown as Promise<void>
