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
  <div class="page-container">
    <!-- 页面标题 -->
    <div class="page-title">
      <div class="page-title__main">
        <el-icon class="page-title__icon"><OfficeBuilding /></el-icon>
        <div>
          <h2 class="page-title__text">院系管理</h2>
        </div>
      </div>
    </div>

    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryForm" size="default">
        <el-form-item label="关键词">
          <el-input
            v-model="queryForm.keyword"
            placeholder="院系名称/编码"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span><el-icon style="vertical-align: -2px; margin-right: 6px"><List /></el-icon>院系列表</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">
              <el-icon style="margin-right: 4px"><Plus /></el-icon>新增院系
            </el-button>
            <el-button type="primary" plain @click="importVisible = true">
              <el-icon style="margin-right: 4px"><Upload /></el-icon>批量导入
            </el-button>
            <el-button type="success" @click="handleExport">
              <el-icon style="margin-right: 4px"><Download /></el-icon>导出
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="院系名称" min-width="150" />
        <el-table-column prop="code" label="院系编码" width="120" />
        <el-table-column prop="majorCount" label="专业数" width="80" align="center" />
        <el-table-column prop="graduateCount" label="毕业生数" width="100" align="center" />
        <el-table-column prop="sortOrder" label="排序" width="70" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleStatistics(row)">
              <el-icon style="vertical-align: -2px"><DataAnalysis /></el-icon>统计
            </el-button>
            <el-button type="primary" link size="small" @click="handleEdit(row)">
              <el-icon style="vertical-align: -2px"><Edit /></el-icon>编辑
            </el-button>
            <el-popconfirm
              title="确定要删除该院系吗？"
              confirm-button-text="确定"
              cancel-button-text="取消"
              @confirm="handleDelete(row)"
            >
              <template #reference>
                <el-button type="danger" link size="small">
                  <el-icon style="vertical-align: -2px"><Delete /></el-icon>删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>

        <template #empty>
          <el-empty description="暂无数据" />
        </template>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑院系' : '新增院系'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="90px"
      >
        <el-form-item label="院系名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入院系名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="院系编码" prop="code">
          <el-input v-model="formData.code" placeholder="请输入院系编码" maxlength="20" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            placeholder="请输入描述（可选）"
            maxlength="200"
            show-word-limit
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="排序号">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 统计对话框 -->
    <el-dialog
      v-model="statsVisible"
      title="就业统计"
      width="600px"
      :close-on-click-modal="false"
    >
      <template v-if="statistics">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="院系">{{ statistics.deptName }}</el-descriptions-item>
          <el-descriptions-item label="就业率">{{ statistics.employmentRate }}</el-descriptions-item>
          <el-descriptions-item label="毕业生总数">{{ statistics.totalGraduates }}</el-descriptions-item>
          <el-descriptions-item label="已签约">{{ statistics.employedCount }}</el-descriptions-item>
          <el-descriptions-item label="升学">{{ statistics.furtherStudyCount }}</el-descriptions-item>
          <el-descriptions-item label="创业">{{ statistics.entrepreneurshipCount }}</el-descriptions-item>
          <el-descriptions-item label="出国">{{ statistics.abroadCount }}</el-descriptions-item>
          <el-descriptions-item label="待就业">{{ statistics.unemployedCount }}</el-descriptions-item>
        </el-descriptions>
      </template>
      <div v-else v-loading="statsLoading" style="height: 100px" />
    </el-dialog>

    <!-- 批量导入对话框 -->
    <ImportDialog
      v-model="importVisible"
      title="院系批量导入"
      :columns="importColumns"
      :template-rows="templateRows"
      :build-payload="buildImportPayload"
      :submit-one="submitImport"
      @success="fetchData"
    />
  </div>
</template>

<script setup lang="ts">
/**
 * 院系管理页面（学院管理员 / 系统管理员）
 * - 按院系编码/名称/关键词筛选，分页展示
 * - 支持新增、编辑、删除院系；按院系查看就业统计
 * - 支持 CSV 模板批量导入与导出
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { OfficeBuilding, List, Plus, DataAnalysis, Edit, Delete, Download, Upload } from '@element-plus/icons-vue'
import { downloadCsv } from '@/api/request'
import {
  getDepartmentPage,
  createDepartment,
  updateDepartment,
  deleteDepartment,
  getDepartmentStatistics,
  type DepartmentVO,
  type DepartmentCreateDTO,
  type DepartmentQueryDTO,
  type DepartmentStatisticsVO
} from '@/api/department'
import ImportDialog, { type ImportColumn } from '@/components/ImportDialog.vue'

// 表格与加载状态
const loading = ref(false)
const submitLoading = ref(false)
const total = ref(0)
const tableData = ref<DepartmentVO[]>([])
// 新增/编辑弹窗状态
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()

// 就业统计弹窗状态
const statsVisible = ref(false)
const statsLoading = ref(false)
const statistics = ref<DepartmentStatisticsVO | null>(null)

const queryForm = reactive<DepartmentQueryDTO>({
  keyword: '',
  page: 1,
  size: 10
})

const formData = reactive<DepartmentCreateDTO>({
  name: '',
  code: '',
  description: '',
  sortOrder: 0
})

const formRules: FormRules = {
  name: [
    { required: true, message: '请输入院系名称', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入院系编码', trigger: 'blur' }
  ]
}

// 批量导入
const importVisible = ref(false)
const importColumns: ImportColumn[] = [
  { prop: 'name', label: '院系名称', required: true, width: 20 },
  { prop: 'code', label: '院系编码', required: true, width: 16 },
  { prop: 'sortOrder', label: '排序号', width: 10 },
  { prop: 'description', label: '描述', width: 30 }
]
const templateRows = [
  { name: '计算机学院', code: 'CS', sortOrder: 1, description: '示例行，可删除' }
]

function buildImportPayload(data: Record<string, string>): { payload?: any; error?: string } {
  const payload: any = { ...data }
  if (payload.sortOrder === '') {
    payload.sortOrder = 0
  } else {
    const n = Number(payload.sortOrder)
    if (!Number.isInteger(n) || n < 0) return { error: '排序号必须是非负整数' }
    payload.sortOrder = n
  }
  if (!payload.description) delete payload.description
  return { payload }
}

async function submitImport(payload: any) {
  await createDepartment(payload)
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getDepartmentPage(queryForm)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

// 导出院系信息（文档2.3导出按钮）
async function handleExport() {
  try {
    await downloadCsv('/export/departments', { keyword: queryForm.keyword })
    ElMessage.success('导出成功')
  } catch (e) {
    ElMessage.error('导出失败')
  }
}

function handleSearch() {
  queryForm.page = 1
  fetchData()
}

function handleReset() {
  queryForm.keyword = ''
  queryForm.page = 1
  fetchData()
}

function handleAdd() {
  isEdit.value = false
  editId.value = null
  formData.name = ''
  formData.code = ''
  formData.description = ''
  formData.sortOrder = 0
  dialogVisible.value = true
  formRef.value?.resetFields()
}

function handleEdit(row: DepartmentVO) {
  isEdit.value = true
  editId.value = row.id
  formData.name = row.name
  formData.code = row.code
  formData.description = row.description
  formData.sortOrder = row.sortOrder
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value && editId.value) {
      await updateDepartment(editId.value, { ...formData, id: editId.value })
      ElMessage.success('更新成功')
    } else {
      await createDepartment({ ...formData })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row: DepartmentVO) {
  try {
    await deleteDepartment(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e: any) {
    ElMessage.error(e?.message || '删除失败')
  }
}

async function handleStatistics(row: DepartmentVO) {
  statsVisible.value = true
  statsLoading.value = true
  statistics.value = null
  try {
    const res = await getDepartmentStatistics(row.id)
    statistics.value = res.data
  } finally {
    statsLoading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 16px;
}
.table-card {
  margin-bottom: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
