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
  <div class="review-list-container">
    <!-- 页头 -->
    <div class="page-header">
      <h2>两级审核</h2>
      <p class="subtitle">学院初审 → 学校复审，状态严格流转，审核通过后数据生效</p>
    </div>

    <!-- 搜索筛选栏 -->
    <div class="search-bar">
      <el-input v-model="searchForm.name" placeholder="学生姓名" clearable style="width: 140px" />
      <el-select v-model="searchForm.destination" placeholder="就业去向" clearable style="width: 130px">
        <el-option v-for="d in destinationOptions" :key="d" :label="d" :value="d" />
      </el-select>
      <el-select v-model="searchForm.reviewStatus" placeholder="审核状态" clearable style="width: 130px">
        <el-option label="待初审" value="PENDING" />
        <el-option label="初审通过" value="FIRST_PASSED" />
        <el-option label="终审通过" value="APPROVED" />
        <el-option label="已驳回" value="REJECTED" />
      </el-select>
      <el-date-picker
        v-model="searchForm.dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
        style="width: 250px"
      />
      <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      <el-button :icon="Refresh" @click="handleReset">重置</el-button>
    </div>

    <!-- 标签页（带待办角标） -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="review-tabs">
      <el-tab-pane name="first">
        <template #label>
          <span>
            初审（学院）
            <el-badge v-if="stats.firstPending > 0" :value="stats.firstPending" class="tab-badge" />
          </span>
        </template>
      </el-tab-pane>
      <el-tab-pane name="final">
        <template #label>
          <span>
            终审（学校）
            <el-badge v-if="stats.finalPending > 0" :value="stats.finalPending" class="tab-badge" />
          </span>
        </template>
      </el-tab-pane>
      <el-tab-pane name="all" label="全部记录" />
    </el-tabs>

    <!-- 批量操作栏 -->
    <div v-if="selectedRows.length > 0" class="batch-bar">
      <span class="batch-text">已选择 {{ selectedRows.length }} 条记录</span>
      <el-button type="success" size="small" :icon="CircleCheck" @click="handleBatchApprove">批量通过</el-button>
      <el-button type="danger" size="small" :icon="CircleClose" @click="handleBatchReject">批量驳回</el-button>
      <el-button size="small" @click="clearSelection">取消选择</el-button>
    </div>

    <!-- 表格 -->
    <el-table
      ref="tableRef"
      v-loading="loading"
      :data="filteredRecords"
      stripe
      border
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="45" align="center" />
      <el-table-column type="index" label="序号" width="55" align="center" />
      <el-table-column prop="submitterName" label="提交人" width="90" show-overflow-tooltip />
      <el-table-column prop="companyName" label="单位" min-width="150" show-overflow-tooltip />
      <el-table-column prop="position" label="岗位" width="110" show-overflow-tooltip />
      <el-table-column prop="city" label="城市" width="120" show-overflow-tooltip />
      <el-table-column prop="destination" label="就业去向" width="100" show-overflow-tooltip />
      <el-table-column label="是否代录" width="90" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.isProxy" type="warning" size="small" effect="plain">代录</el-tag>
          <span v-else class="text-muted">-</span>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" width="105" align="center">
        <template #default="{ row }">
          <el-tag :type="getStatusTagType(row.reviewStatus)" size="small" effect="light">
            {{ getStatusLabel(row.reviewStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审核意见" min-width="130" show-overflow-tooltip>
        <template #default="{ row }">
          <span v-if="row.reviewComment" class="text-warning">{{ row.reviewComment }}</span>
          <span v-else class="text-muted">-</span>
        </template>
      </el-table-column>
      <el-table-column label="提交时间" width="165">
        <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="210" fixed="right">
        <template #default="{ row }">
          <el-button v-if="canApprove(row)" type="success" size="small" :icon="CircleCheck" @click="handleApprove(row)">通过</el-button>
          <el-button v-if="canReject(row)" type="danger" size="small" :icon="CircleClose" @click="openReject(row)">驳回</el-button>
          <el-button type="primary" size="small" link :icon="View" @click="openDetail(row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 空状态 -->
    <el-empty v-if="!loading && filteredRecords.length === 0" description="暂无审核记录">
      <template #image>
        <el-icon :size="60" color="#dcdfe6"><Document /></el-icon>
      </template>
      <p class="empty-hint">当前筛选条件下没有找到记录，请调整筛选条件</p>
    </el-empty>

    <!-- 通过确认 -->
    <el-dialog v-model="approveVisible" title="确认通过" width="420px" align-center>
      <p>确认通过该条就业信息？{{ isTeacher ? '通过后将流转至学校管理员进行终审。' : '通过后该记录正式生效。' }}</p>
      <template #footer>
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="success" :loading="submitting" @click="confirmApprove">确认通过</el-button>
      </template>
    </el-dialog>

    <!-- 驳回理由弹窗 -->
    <el-dialog v-model="rejectVisible" title="填写驳回理由" width="480px" align-center>
      <el-input
        v-model="rejectForm.comment"
        type="textarea"
        :rows="4"
        maxlength="200"
        show-word-limit
        placeholder="请填写驳回理由，学生将收到此意见并修改后重新提交..."
      />
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" :loading="submitting" @click="confirmReject">确认驳回</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="就业信息详情" width="640px" align-center>
      <el-descriptions v-if="currentRecord" :column="2" border>
        <el-descriptions-item label="学生姓名">{{ currentRecord.submitterName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ currentRecord.studentNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="就业去向">{{ currentRecord.destination || '-' }}</el-descriptions-item>
        <el-descriptions-item label="是否代录">
          <el-tag v-if="currentRecord.isProxy" type="warning" size="small">代录</el-tag>
          <span v-else>否</span>
        </el-descriptions-item>
        <el-descriptions-item label="单位名称" :span="2">{{ currentRecord.companyName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="岗位名称">{{ currentRecord.position || '-' }}</el-descriptions-item>
        <el-descriptions-item label="单位性质">{{ currentRecord.companyType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="所属行业">{{ currentRecord.industry || '-' }}</el-descriptions-item>
        <el-descriptions-item label="工作城市" :span="2">{{ currentRecord.city || '-' }}</el-descriptions-item>
        <el-descriptions-item label="薪资范围">{{ currentRecord.salaryRange || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentRecord.contactPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="getStatusTagType(currentRecord.reviewStatus)" size="small">
            {{ getStatusLabel(currentRecord.reviewStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核意见">
          <span v-if="currentRecord.reviewComment" class="text-warning">{{ currentRecord.reviewComment }}</span>
          <span v-else class="text-muted">-</span>
        </el-descriptions-item>
        <el-descriptions-item label="提交时间" :span="2">{{ formatDateTime(currentRecord.createTime) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button v-if="currentRecord && canApprove(currentRecord)" type="success" @click="detailVisible = false; handleApprove(currentRecord)">通过</el-button>
        <el-button v-if="currentRecord && canReject(currentRecord)" type="danger" @click="detailVisible = false; openReject(currentRecord)">驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
/**
 * 两级审核页面（教师 / 学院管理员）
 * - 标签页切换：初审（学院）→ 终审（学校）→ 全部记录，带待办角标
 * - 支持单个通过/驳回（驳回必须填理由），以及批量通过/批量驳回
 * - 根据当前角色自动调用对应的审核接口（教师初审 / 管理员终审）
 */
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { CircleCheck, CircleClose, View, Search, Refresh, Document } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import {
  getFirstPendingList,
  getTeacherFinalPendingList,
  getEmploymentList,
  teacherApprove,
  teacherReject,
  adminApprove,
  adminReject,
  teacherBatchReview,
  adminBatchReview
} from '@/api/employment'
import type { EmploymentRecord } from '@/api/employment'

const authStore = useAuthStore()

const loading = ref(false)
const submitting = ref(false)
const activeTab = ref('first')
const records = ref<EmploymentRecord[]>([])
const selectedRows = ref<EmploymentRecord[]>([])
const tableRef = ref()

// 当前用户角色判断（教师 / 管理员）
const userRole = computed(() => authStore.userInfo?.role || '')
const isTeacher = computed(() => userRole.value === 'TEACHER' || userRole.value === 'ROLE_TEACHER')
const isAdmin = computed(() => userRole.value === 'COLLEGE_ADMIN' || userRole.value === 'SYSTEM_ADMIN' || userRole.value === 'ROLE_COLLEGE_ADMIN' || userRole.value === 'ROLE_SYSTEM_ADMIN')

const destinationOptions = ['签约就业', '灵活就业', '自主创业', '升学深造', '出国（境）', '暂未就业', '其他']

// 搜索筛选条件
const searchForm = ref({
  name: '',
  destination: '',
  reviewStatus: '',
  dateRange: [] as string[]
})

// 弹窗状态
const approveVisible = ref(false)
const rejectVisible = ref(false)
const detailVisible = ref(false)
const currentRecord = ref<EmploymentRecord | null>(null)
const rejectForm = ref({ comment: '' })

// 各审核状态数量统计（用于标签页角标）
const stats = computed(() => {
  const list = records.value
  return {
    total: list.length,
    firstPending: list.filter(r => r.reviewStatus === 'PENDING').length,
    firstPassed: list.filter(r => r.reviewStatus === 'FIRST_PASSED').length,
    finalPending: list.filter(r => r.reviewStatus === 'FIRST_PASSED').length,
    rejected: list.filter(r => r.reviewStatus === 'REJECTED' || r.reviewStatus === 'FIRST_REJECTED' || r.reviewStatus === 'FINAL_REJECTED').length
  }
})

// 前端过滤：按姓名/去向/状态/日期范围过滤列表
const filteredRecords = computed(() => {
  let result = records.value
  if (searchForm.value.name) {
    result = result.filter(r => (r.submitterName || '').includes(searchForm.value.name))
  }
  if (searchForm.value.destination) {
    result = result.filter(r => r.destination === searchForm.value.destination)
  }
  if (searchForm.value.reviewStatus) {
    result = result.filter(r => r.reviewStatus === searchForm.value.reviewStatus)
  }
  if (searchForm.value.dateRange && searchForm.value.dateRange.length === 2) {
    const start = new Date(searchForm.value.dateRange[0] + 'T00:00:00').getTime()
    const end = new Date(searchForm.value.dateRange[1] + 'T23:59:59').getTime()
    result = result.filter(r => {
      const t = new Date(r.createTime || 0).getTime()
      return t >= start && t <= end
    })
  }
  return result
})

/** 审核状态对应的标签颜色 */
function getStatusTagType(status: string) {
  const map: Record<string, string> = {
    PENDING: 'warning',
    FIRST_PASSED: 'primary',
    APPROVED: 'success',
    REJECTED: 'danger',
    FIRST_REJECTED: 'danger',
    FINAL_REJECTED: 'danger'
  }
  return map[status] || 'info'
}

/** 审核状态中文文案 */
function getStatusLabel(status: string) {
  const map: Record<string, string> = {
    PENDING: '待学院初审',
    FIRST_PASSED: '待学校终审',
    APPROVED: '审核通过',
    REJECTED: '已驳回待修改',
    FIRST_REJECTED: '已驳回待修改',
    FINAL_REJECTED: '已驳回待修改'
  }
  return map[status] || status
}

/** 日期时间格式化（ISO → yyyy-MM-dd HH:mm:ss） */
function formatDateTime(dateStr?: string) {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  if (isNaN(d.getTime())) return dateStr
  const pad = (n: number) => n.toString().padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

/** 当前角色对该记录是否可「通过」 */
function canApprove(row: EmploymentRecord) {
  if (isTeacher.value && row.reviewStatus === 'PENDING') return true
  if (isAdmin.value && row.reviewStatus === 'FIRST_PASSED') return true
  return false
}

/** 当前角色对该记录是否可「驳回」 */
function canReject(row: EmploymentRecord) {
  if (isTeacher.value && row.reviewStatus === 'PENDING') return true
  if (isAdmin.value && row.reviewStatus === 'FIRST_PASSED') return true
  return false
}

/** 组装搜索参数（传给后端） */
function buildParams() {
  const params: Record<string, string> = {}
  if (searchForm.value.name) params.name = searchForm.value.name
  if (searchForm.value.destination) params.destination = searchForm.value.destination
  if (searchForm.value.dateRange?.length === 2) {
    params.startTime = searchForm.value.dateRange[0]
    params.endTime = searchForm.value.dateRange[1]
  }
  return params
}

/** 按当前标签页加载数据（初审/终审/全部） */
async function loadData() {
  loading.value = true
  try {
    const params = buildParams()
    if (activeTab.value === 'first') {
      const res = await getFirstPendingList(params)
      records.value = res.data || []
    } else if (activeTab.value === 'final') {
      const res = await getTeacherFinalPendingList(params)
      records.value = res.data || []
    } else {
      const res = await getEmploymentList({ page: 1, size: 1000 })
      records.value = res.data?.list || []
    }
  } catch (err: any) {
    ElMessage.error(err?.response?.data?.message || '加载数据失败')
  } finally {
    loading.value = false
  }
}

/** 搜索 */
function handleSearch() {
  loadData()
}

/** 重置筛选条件 */
function handleReset() {
  searchForm.value = { name: '', destination: '', reviewStatus: '', dateRange: [] }
  loadData()
}

/** 切换标签页：清空状态筛选与选择项后重新加载 */
function handleTabChange() {
  searchForm.value.reviewStatus = ''
  clearSelection()
  loadData()
}

/** 表格多选变化 */
function handleSelectionChange(val: EmploymentRecord[]) {
  selectedRows.value = val
}

/** 清空表格选择 */
function clearSelection() {
  tableRef.value?.clearSelection()
  selectedRows.value = []
}

/** 打开通过确认弹窗 */
function handleApprove(row: EmploymentRecord) {
  currentRecord.value = row
  approveVisible.value = true
}

/** 确认通过（按角色调教师/管理员接口） */
async function confirmApprove() {
  if (!currentRecord.value) return
  submitting.value = true
  try {
    if (isTeacher.value) {
      await teacherApprove(currentRecord.value.id)
    } else {
      await adminApprove(currentRecord.value.id)
    }
    ElMessage.success('审核通过')
    approveVisible.value = false
    loadData()
  } catch (err: any) {
    ElMessage.error(err?.response?.data?.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

/** 打开驳回弹窗 */
function openReject(row: EmploymentRecord) {
  currentRecord.value = row
  rejectForm.value.comment = ''
  rejectVisible.value = true
}

/** 确认驳回（理由必填） */
async function confirmReject() {
  if (!rejectForm.value.comment.trim()) {
    ElMessage.warning('请填写驳回理由')
    return
  }
  if (!currentRecord.value) return
  submitting.value = true
  try {
    if (isTeacher.value) {
      await teacherReject(currentRecord.value.id, rejectForm.value.comment)
    } else {
      await adminReject(currentRecord.value.id, rejectForm.value.comment)
    }
    ElMessage.success('已驳回')
    rejectVisible.value = false
    loadData()
  } catch (err: any) {
    ElMessage.error(err?.response?.data?.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

/** 打开记录详情弹窗 */
function openDetail(row: EmploymentRecord) {
  currentRecord.value = row
  detailVisible.value = true
}

/** 批量通过（需二次确认） */
async function handleBatchApprove() {
  if (selectedRows.value.length === 0) return
  try {
    await ElMessageBox.confirm(
      `确认批量通过 ${selectedRows.value.length} 条记录？`,
      '批量通过确认',
      { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }
    )
    submitting.value = true
    const ids = selectedRows.value.map(r => r.id)
    if (isTeacher.value) {
      await teacherBatchReview(ids, 'PASS')
    } else {
      await adminBatchReview(ids, 'PASS')
    }
    ElMessage.success('批量通过成功')
    clearSelection()
    loadData()
  } catch (err: any) {
    if (err !== 'cancel') ElMessage.error(err?.response?.data?.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

/** 批量驳回（输入统一理由） */
async function handleBatchReject() {
  if (selectedRows.value.length === 0) return
  try {
    const { value } = await ElMessageBox.prompt(
      '请输入批量驳回的通用理由（所有选中记录将使用同一理由）：',
      '批量驳回',
      {
        confirmButtonText: '确认驳回',
        cancelButtonText: '取消',
        inputType: 'textarea',
        inputValidator: (v: string) => (v ? true : '请填写驳回理由')
      }
    )
    submitting.value = true
    const ids = selectedRows.value.map(r => r.id)
    if (isTeacher.value) {
      await teacherBatchReview(ids, 'REJECT', value)
    } else {
      await adminBatchReview(ids, 'REJECT', value)
    }
    ElMessage.success('批量驳回成功')
    clearSelection()
    loadData()
  } catch (err: any) {
    if (err !== 'cancel') ElMessage.error(err?.response?.data?.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.review-list-container {
  padding: 20px;
}
.page-header {
  margin-bottom: 16px;
}
.page-header h2 {
  margin: 0 0 6px 0;
  font-size: 20px;
  font-weight: 600;
}
.subtitle {
  color: #909399;
  font-size: 13px;
  margin: 0;
}
.search-bar {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 8px;
}
.review-tabs {
  margin-bottom: 8px;
}
.tab-badge :deep(.el-badge__content) {
  transform: translateY(-2px) translateX(4px);
}
.batch-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  padding: 10px 14px;
  background: #f0f9ff;
  border-radius: 6px;
  border: 1px solid #c6e2ff;
}
.batch-text {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}
.text-muted {
  color: #c0c4cc;
}
.text-warning {
  color: #e6a23c;
}
.empty-hint {
  color: #909399;
  font-size: 13px;
  margin-top: 8px;
}
</style>
