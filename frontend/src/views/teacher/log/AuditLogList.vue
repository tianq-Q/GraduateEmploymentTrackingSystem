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
  <div class="audit-log-page">
    <div class="page-header">
      <h2>操作日志留存</h2>
      <p class="subtitle">就业数据审计溯源，所有填报、修改、代录、审核操作均可查</p>
    </div>

    <!-- 筛选区 -->
    <div class="filter-bar">
      <el-select v-model="filterAction" placeholder="操作类型" clearable style="width: 180px" @change="handleSearch">
        <el-option v-for="(label, key) in actionMap" :key="key" :label="label" :value="key" />
      </el-select>
      <el-button type="primary" @click="handleSearch">查询</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%; margin-top: 16px">
      <el-table-column prop="id" label="ID" width="70" align="center" />
      <el-table-column prop="recordId" label="就业记录ID" width="110" align="center" />
      <el-table-column prop="action" label="操作类型" width="130" align="center">
        <template #default="{ row }">
          <el-tag :type="actionType(row.action)" size="small">{{ actionMap[row.action] || row.action }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="operatorName" label="操作人" width="120" align="center" />
      <el-table-column prop="operatorRole" label="操作人角色" width="110" align="center">
        <template #default="{ row }">
          <el-tag size="small" type="info">{{ row.operatorRole }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="comment" label="操作说明" min-width="220" show-overflow-tooltip />
      <el-table-column prop="createTime" label="操作时间" width="180" align="center" />
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSearch"
        @current-change="handleSearch"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 操作日志留存页面（教师/管理员）
 * - 审计溯源所有填报、修改、代录、审核操作
 * - 支持按操作类型筛选、分页查询
 */
import { ref, onMounted } from 'vue'
import { getAllAuditLogs, actionMap, type AuditLog } from '@/api/employment'

const tableData = ref<AuditLog[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterAction = ref('')

/** 操作类型对应的标签颜色 */
function actionType(action: string): string {
  if (action.includes('REJECT')) return 'danger'
  if (action.includes('PASS') || action.includes('APPROVED')) return 'success'
  if (action.includes('WITHDRAW')) return 'warning'
  if (action.includes('PROXY')) return 'primary'
  return 'info'
}

/** 分页拉取操作日志 */
async function fetchData() {
  loading.value = true
  try {
    const res = await getAllAuditLogs({
      action: filterAction.value || undefined,
      page: currentPage.value,
      size: pageSize.value,
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

/** 查询（重置到第一页） */
function handleSearch() {
  currentPage.value = 1
  fetchData()
}

/** 重置筛选条件 */
function handleReset() {
  filterAction.value = ''
  currentPage.value = 1
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.audit-log-page {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  min-height: calc(100vh - 140px);
}

.page-header {
  margin-bottom: 20px;
  h2 { font-size: 20px; font-weight: 600; color: #262626; margin: 0 0 4px; }
  .subtitle { font-size: 13px; color: #8c8c8c; margin: 0; }
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #fafafa;
  border-radius: 6px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
