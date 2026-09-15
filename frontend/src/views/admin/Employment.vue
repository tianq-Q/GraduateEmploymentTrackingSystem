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
  <div class="employment-page">
    <!-- 筛选区域 -->
    <div class="filter-bar">
      <el-input v-model="filters.studentNo" placeholder="学号" clearable style="width: 140px" @keyup.enter="handleSearch" />
      <el-input v-model="filters.name" placeholder="姓名" clearable style="width: 120px" @keyup.enter="handleSearch" />
      <el-input v-model="filters.companyName" placeholder="单位名称" clearable style="width: 160px" @keyup.enter="handleSearch" />
      <el-select v-model="filters.destination" placeholder="毕业去向" clearable style="width: 140px">
        <el-option label="签约就业" value="签约就业" />
        <el-option label="升学" value="升学" />
        <el-option label="出国" value="出国" />
        <el-option label="创业" value="创业" />
        <el-option label="灵活就业" value="灵活就业" />
        <el-option label="待就业" value="待就业" />
      </el-select>
      <el-select v-model="filters.reviewStatus" placeholder="审核状态" clearable style="width: 150px">
        <el-option v-for="(v, k) in reviewStatusMap" :key="k" :label="v.label" :value="k" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      <el-button :icon="Refresh" @click="handleReset">重置</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="tableData" stripe border style="width: 100%">
      <el-table-column prop="id" label="ID" width="70" align="center" />
      <el-table-column label="学号" width="120">
        <template #default="{ row }">{{ getGraduateInfo(row.graduateId)?.studentNo || '-' }}</template>
      </el-table-column>
      <el-table-column label="姓名" width="100">
        <template #default="{ row }">{{ getGraduateInfo(row.graduateId)?.name || '-' }}</template>
      </el-table-column>
      <el-table-column prop="companyName" label="单位名称" min-width="180" show-overflow-tooltip />
      <el-table-column prop="companyType" label="单位性质" width="100" />
      <el-table-column prop="position" label="职位" width="120" show-overflow-tooltip />
      <el-table-column prop="city" label="工作城市" width="110" />
      <el-table-column prop="destination" label="毕业去向" width="100" />
      <el-table-column label="审核状态" width="130" align="center">
        <template #default="{ row }">
          <el-tag :type="reviewStatusMap[row.reviewStatus]?.type || 'info'" size="small">
            {{ reviewStatusMap[row.reviewStatus]?.label || row.reviewStatus }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="来源" width="80" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.isProxy" type="warning" size="small">代录</el-tag>
          <el-tag v-else type="primary" size="small">自主</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="submitterName" label="提交人" width="100" />
      <el-table-column prop="createTime" label="提交时间" width="170" align="center" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" link @click="showAuditLogs(row)">操作日志</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 操作日志弹窗 -->
    <el-dialog v-model="logVisible" title="操作日志" width="700px" destroy-on-close>
      <el-timeline>
        <el-timeline-item v-for="log in auditLogs" :key="log.id"
          :timestamp="log.createTime" placement="top"
          :type="log.action.includes('REJECT') ? 'danger' : log.action.includes('PASS') || log.action.includes('APPROVED') ? 'success' : 'primary'">
          <div class="log-item">
            <div class="log-action">{{ actionMap[log.action] || log.action }}</div>
            <div class="log-operator">{{ log.operatorName }} ({{ log.operatorRole }})</div>
            <div class="log-comment" v-if="log.comment">{{ log.comment }}</div>
          </div>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-if="auditLogs.length === 0" description="暂无操作日志" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
/**
 * 就业信息总览页面（管理员）
 * - 按学号/姓名/单位名称/毕业去向/审核状态筛选全部就业记录
 * - 通过 graduateId 映射展示毕业生学号与姓名
 * - 支持查看单条记录的完整操作日志
 */
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getEmploymentList, getAuditLogs, reviewStatusMap, actionMap, type EmploymentRecord, type AuditLog } from '@/api/employment'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

interface GraduateInfo {
  id: number
  studentNo: string
  name: string
}

const loading = ref(false)
const tableData = ref<EmploymentRecord[]>([])
// graduateId → 毕业生信息缓存（避免重复请求）
const graduateCache = ref<Record<number, GraduateInfo>>({})

// 操作日志弹窗状态
const logVisible = ref(false)
const auditLogs = ref<AuditLog[]>([])

const filters = reactive({
  studentNo: '',
  name: '',
  companyName: '',
  destination: '',
  reviewStatus: '',
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0,
})

function getGraduateInfo(graduateId: number): GraduateInfo | undefined {
  return graduateCache.value[graduateId]
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getEmploymentList({
      ...filters,
      page: pagination.page,
      size: pagination.size,
    })
    const data = res.data
    if (data) {
      tableData.value = data.list || []
      pagination.total = data.total || 0
      await fetchGraduates(data.list || [])
    }
  } catch {
    ElMessage.error('加载就业信息失败')
  } finally {
    loading.value = false
  }
}

async function fetchGraduates(records: EmploymentRecord[]) {
  const ids = [...new Set(records.map(r => r.graduateId))]
  const uncachedIds = ids.filter(id => !graduateCache.value[id])
  if (uncachedIds.length === 0) return

  try {
    const res = await request.get<GraduateInfo[]>('/employment/graduates', {
      params: { ids: uncachedIds.join(',') }
    })
    const graduates = (res as any).data || []
    for (const g of graduates) {
      graduateCache.value[g.id] = g
    }
  } catch {
    for (const id of uncachedIds) {
      graduateCache.value[id] = { id, studentNo: `#${id}`, name: '-' }
    }
  }
}

async function showAuditLogs(row: EmploymentRecord) {
  try {
    const res = await getAuditLogs(row.id)
    auditLogs.value = res.data || []
    logVisible.value = true
  } catch {
    ElMessage.error('加载操作日志失败')
  }
}

function handleSearch() {
  pagination.page = 1
  fetchData()
}

function handleReset() {
  filters.studentNo = ''
  filters.name = ''
  filters.companyName = ''
  filters.destination = ''
  filters.reviewStatus = ''
  pagination.page = 1
  fetchData()
}

function handlePageChange() { fetchData() }
function handleSizeChange() { pagination.page = 1; fetchData() }

onMounted(() => { fetchData() })
</script>

<style scoped>
.employment-page {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}
.filter-bar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 16px;
  align-items: center;
}
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
.log-item { padding: 4px 0; }
.log-action { font-weight: 600; margin-bottom: 4px; }
.log-operator { font-size: 12px; color: #909399; }
.log-comment { margin-top: 4px; color: #606266; background: #f5f7fa; padding: 4px 8px; border-radius: 4px; }
</style>
