<!--
MIT License

Copyright (c) 2026 Employment Tracking System

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
-->

<template>
  <div class="page user-page">
    <div class="page-header">
      <h2>账号管理</h2>
      <p class="page-desc">管理系统主任、教师、毕业生账号的启用与禁用。禁用后该账号将无法登录系统。</p>
    </div>

    <div class="toolbar">
      <el-radio-group v-model="query.role" @change="handleSearch">
        <el-radio-button value="">全部账号</el-radio-button>
        <el-radio-button value="COLLEGE_ADMIN">主任</el-radio-button>
        <el-radio-button value="TEACHER">教师</el-radio-button>
        <el-radio-button value="GRADUATE">毕业生</el-radio-button>
      </el-radio-group>
      <div class="toolbar-right">
        <el-input
          v-model="query.keyword"
          placeholder="账号 / 姓名 / 学号"
          clearable
          style="width: 200px"
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        />
        <el-input
          v-model="query.deptName"
          placeholder="院系名称"
          clearable
          style="width: 140px"
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        />
        <el-select
          v-model="query.status"
          placeholder="账号状态"
          clearable
          style="width: 110px"
          @change="handleSearch"
        >
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          <span>查询</span>
        </el-button>
      </div>
    </div>

    <el-table v-loading="loading" :data="rows" border stripe>
      <el-table-column prop="realName" label="姓名" min-width="110" />
      <el-table-column prop="username" label="登录账号" min-width="130" />
      <el-table-column label="学号 / 工号" min-width="140">
        <template #default="{ row }">
          {{ row.studentNumber || '—' }}
        </template>
      </el-table-column>
      <el-table-column label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="roleTagType(row.role)" size="small">{{ row.roleName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="所属院系" min-width="140">
        <template #default="{ row }">
          {{ row.deptName || '—' }}
        </template>
      </el-table-column>
      <el-table-column label="账号状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="160" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 1"
            type="danger"
            link
            @click="handleToggle(row, 0)"
          >
            禁用
          </el-button>
          <el-button
            v-else
            type="success"
            link
            @click="handleToggle(row, 1)"
          >
            启用
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="load"
        @current-change="load"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 账号管理页面（系统管理员）
 * - 按角色（主任/教师/毕业生）筛选全部账号
 * - 支持模糊检索：账号/姓名/学号、院系名称
 * - 支持按账号状态（启用/禁用）筛选
 * - 可启用/禁用账号，禁用后无法登录系统
 */
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getSystemUsers, updateUserStatus, type SystemUser } from '@/api/system'
import type { PageResult } from '@/types/api'

const loading = ref(false)
const rows = ref<SystemUser[]>([])
const total = ref(0)

// 查询条件
const query = reactive({
  page: 1,
  size: 10,
  role: '',
  keyword: '',
  deptName: '',
  status: null as number | null,
})

/** 角色对应的标签颜色 */
const roleTagType = (role: string): 'primary' | 'warning' | 'info' => {
  if (role === 'COLLEGE_ADMIN') return 'warning'
  if (role === 'TEACHER') return 'primary'
  return 'info'
}

/** 加载账号列表 */
const load = async () => {
  loading.value = true
  try {
    const data = (await getSystemUsers({
      page: query.page,
      size: query.size,
      role: query.role || undefined,
      keyword: query.keyword.trim() || undefined,
      deptName: query.deptName.trim() || undefined,
      status: query.status ?? undefined,
    })) as unknown as { data: PageResult<SystemUser> }
    rows.value = data.data.records
    total.value = data.data.total
  } finally {
    loading.value = false
  }
}

/** 查询（重置到第一页） */
const handleSearch = () => {
  query.page = 1
  load()
}

/** 启用/禁用账号（需二次确认） */
const handleToggle = async (row: SystemUser, status: 0 | 1) => {
  const action = status === 1 ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(
      `确定要${action}账号「${row.realName}（${row.username}）」吗？${status === 0 ? '禁用后该账号将无法登录系统。' : ''}`,
      `${action}账号`,
      { type: 'warning', confirmButtonText: action, cancelButtonText: '取消' },
    )
  } catch {
    return
  }
  await updateUserStatus(row.id, status)
  ElMessage.success(`${action}成功`)
  load()
}

onMounted(load)
</script>

<style scoped>
.page-header {
  margin-bottom: 16px;
}
.page-header h2 {
  margin: 0 0 4px;
  font-size: 20px;
}
.page-desc {
  margin: 0;
  color: #909399;
  font-size: 13px;
}
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}
.toolbar-right {
  display: flex;
  gap: 8px;
}
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
