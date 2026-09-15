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
      <h2>学校终审</h2>
      <p class="subtitle">学院初审通过后流转至此，由学校管理员进行复审</p>
    </div>

    <!-- 搜索筛选栏 -->
    <div class="search-bar">
      <el-input v-model="searchForm.name" placeholder="学生姓名" clearable style="width: 140px" />
      <el-select v-model="searchForm.destination" placeholder="就业去向" clearable style="width: 130px">
        <el-option v-for="d in destinationOptions" :key="d" :label="d" :value="d" />
      </el-select>
      <el-select v-model="searchForm.reviewStatus" placeholder="审核状态" clearable style="width: 130px">
        <el-option label="待终审" value="FIRST_PASSED" />
        <el-option label="终审通过" value="APPROVED" />
        <el-option label="终审驳回" value="FINAL_REJECTED" />
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
      <p>确认通过该条就业信息？通过后该记录正式生效，并作为数据看板的统计数据源。</p>
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
        placeholder="请填写驳回理由，记录将退回学生修改后重新走审核流程..."
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
 * 学校终审页面（学院管理员 / 系统管理员）
 * - 展示学院初审通过、待学校终审的就业记录
 * - 支持单个通过/驳回（驳回必须填理由）及批量通过/批量驳回
 */
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { CircleCheck, CircleClose, View, Search, Refresh, Document } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import {
  getFinalPendingList,
  adminApprove,
  adminReject,
  adminBatchReview
} from '@/api/employment'
import type { EmploymentRecord } from '@/api/employment'

const authStore = useAuthStore()

const loading = ref(false)
const submitting = ref(false)
const records = ref<EmploymentRecord[]>([])
const selectedRows = ref<EmploymentRecord[]>([])
const tableRef = ref()

// 当前用户角色判断
const userRole = computed(() => authStore.userInfo?.role || '')
const isAdmin = computed(() => userRole.value === 'ADMIN' || userRole.value === 'ROLE_ADMIN')

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

/** 管理员仅可终审初审通过（FIRST_PASSED）的记录 */
function canApprove(row: EmploymentRecord) {
  return isAdmin.value && row.reviewStatus === 'FIRST_PASSED'
}

/** 管理员仅可驳回初审通过（FIRST_PASSED）的记录 */
function canReject(row: EmploymentRecord) {
  return isAdmin.value && row.reviewStatus === 'FIRST_PASSED'
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

/** 加载待终审列表 */
async function loadData() {
  loading.value = true
  try {
    const res = await getFinalPendingList(buildParams())
    records.value = res.data || []
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

/** 确认通过（终审通过后记录生效） */
async function confirmApprove() {
  if (!currentRecord.value) return
  submitting.value = true
  try {
    await adminApprove(currentRecord.value.id)
    ElMessage.success('审核通过，记录已生效')
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
    await adminReject(currentRecord.value.id, rejectForm.value.comment)
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
    await adminBatchReview(selectedRows.value.map(r => r.id), 'PASS')
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
    await adminBatchReview(selectedRows.value.map(r => r.id), 'REJECT', value)
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
  margin-bottom: 16px;
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
