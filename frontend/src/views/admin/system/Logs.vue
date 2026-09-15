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
  <div class="page logs-page">
    <div class="page-header">
      <h2>系统日志</h2>
      <p class="page-desc">系统操作日志查询，用于问题排查与审计。</p>
    </div>

    <div class="toolbar">
      <el-select v-model="query.module" placeholder="全部模块" clearable style="width: 160px" @change="handleSearch">
        <el-option label="全部模块" value="" />
        <el-option label="看板" value="dashboard" />
        <el-option label="就业" value="employment" />
        <el-option label="系统" value="system" />
        <el-option label="基础数据" value="basedata" />
        <el-option label="账号" value="account" />
      </el-select>
      <el-input
        v-model="query.operatorName"
        placeholder="操作人姓名"
        clearable
        style="width: 200px"
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      />
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
        style="width: 260px"
      />
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
        <span>查询</span>
      </el-button>
    </div>

    <el-table v-loading="loading" :data="rows" border stripe>
      <el-table-column prop="createTime" label="操作时间" min-width="165" />
      <el-table-column label="模块" width="110">
        <template #default="{ row }">
          <el-tag size="small">{{ row.module }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="action" label="操作类型" width="110" />
      <el-table-column prop="description" label="操作内容" min-width="220" show-overflow-tooltip />
      <el-table-column prop="operatorName" label="操作人" width="120" />
      <el-table-column prop="ip" label="IP 地址" width="140" />
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
 * 系统日志查询页面（管理员）
 * - 支持按模块、操作人姓名、日期范围筛选
 * - 分页展示操作时间、模块、操作类型、操作内容、操作人、IP 地址
 */
import { onMounted, reactive, ref } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getOperationLogs } from '@/api/dashboard'
import type { OperationLogItem } from '@/types/dashboard'

const loading = ref(false)
const rows = ref<OperationLogItem[]>([])
const total = ref(0)
const dateRange = ref<[string, string] | null>(null)

// 查询条件
const query = reactive({
  page: 1,
  size: 10,
  module: '',
  operatorName: '',
})

/** 加载日志列表 */
const load = async () => {
  loading.value = true
  try {
    const [startDate, endDate] = dateRange.value || []
    const res = await getOperationLogs({
      page: query.page,
      size: query.size,
      module: query.module || undefined,
      operatorName: query.operatorName.trim() || undefined,
      startDate,
      endDate,
    })
    const data = res as unknown as { data: { total: number; records: OperationLogItem[] } }
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
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
