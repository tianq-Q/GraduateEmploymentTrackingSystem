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
        <el-icon class="page-title__icon"><School /></el-icon>
        <div>
          <h2 class="page-title__text">班级管理</h2>
        </div>
      </div>
    </div>

    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryForm" size="default">
        <el-form-item label="所属院系">
          <el-select
            v-model="selectedDeptId"
            placeholder="请选择院系"
            clearable
            style="width: 180px"
            @change="onDeptChange"
          >
            <el-option
              v-for="d in deptList"
              :key="d.id"
              :label="d.name"
              :value="d.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属专业">
          <el-select
            v-model="queryForm.majorId"
            placeholder="请选择专业"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="m in majorList"
              :key="m.id"
              :label="m.name"
              :value="m.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input
            v-model="queryForm.keyword"
            placeholder="班级名称"
            clearable
            style="width: 180px"
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
          <span><el-icon style="vertical-align: -2px; margin-right: 6px"><List /></el-icon>班级列表</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">
              <el-icon style="margin-right: 4px"><Plus /></el-icon>新增班级
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
        <el-table-column prop="name" label="班级名称" min-width="150" />
        <el-table-column prop="deptName" label="所属院系" width="150" />
        <el-table-column prop="majorName" label="所属专业" width="150" />
        <el-table-column prop="enrollmentYear" label="入学年份" width="100" align="center" />
        <el-table-column prop="graduationYear" label="毕业年份" width="100" align="center" />
        <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
        <el-table-column prop="graduateCount" label="毕业生数" width="100" align="center" />
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
              title="确定要删除该班级吗？"
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
      :title="isEdit ? '编辑班级' : '新增班级'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="所属院系" prop="deptId">
          <el-select
            v-model="formData.deptId"
            placeholder="请选择院系"
            style="width: 100%"
            @change="onFormDeptChange"
          >
            <el-option
              v-for="d in deptList"
              :key="d.id"
              :label="d.name"
              :value="d.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属专业" prop="majorId">
          <el-select
            v-model="formData.majorId"
            placeholder="请先选择院系"
            style="width: 100%"
          >
            <el-option
              v-for="m in formMajorList"
              :key="m.id"
              :label="m.name"
              :value="m.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="班级名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入班级名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="入学年份" prop="enrollmentYear">
          <el-date-picker
            v-model="formData.enrollmentYear"
            type="year"
            placeholder="选择入学年份"
            value-format="YYYY"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="毕业年份" prop="graduationYear">
          <el-date-picker
            v-model="formData.graduationYear"
            type="year"
            placeholder="选择毕业年份"
            value-format="YYYY"
            style="width: 100%"
          />
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
      width="500px"
      :close-on-click-modal="false"
    >
      <template v-if="statistics">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="班级">{{ statistics.className }}</el-descriptions-item>
          <el-descriptions-item label="专业">{{ statistics.majorName }}</el-descriptions-item>
          <el-descriptions-item label="院系">{{ statistics.deptName }}</el-descriptions-item>
          <el-descriptions-item label="就业率">{{ statistics.employmentRate }}</el-descriptions-item>
          <el-descriptions-item label="毕业生总数">{{ statistics.totalGraduates }}</el-descriptions-item>
          <el-descriptions-item label="已就业">{{ statistics.employedCount }}</el-descriptions-item>
          <el-descriptions-item label="未就业">{{ statistics.unemployedCount }}</el-descriptions-item>
        </el-descriptions>
      </template>
      <div v-else v-loading="statsLoading" style="height: 100px" />
    </el-dialog>

    <!-- 批量导入对话框 -->
    <ImportDialog
      v-model="importVisible"
      title="班级批量导入"
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
 * 班级管理页面（学院管理员 / 系统管理员）
 * - 按院系/专业/关键词筛选班级，分页展示
 * - 支持新增、编辑、删除班级；按班级查看就业统计
 * - 支持 CSV 模板批量导入与导出
 */
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { School, List, Plus, DataAnalysis, Edit, Delete, Download, Upload } from '@element-plus/icons-vue'
import { downloadCsv } from '@/api/request'
import type { FormInstance, FormRules } from 'element-plus'
import { getDepartmentList, type DepartmentSimpleVO } from '@/api/department'
import { getMajorList, type MajorSimpleVO } from '@/api/major'
import {
  getClassPage,
  createClass,
  updateClass,
  deleteClass,
  getClassStatistics,
  type ClassVO,
  type ClassCreateDTO,
  type ClassQueryDTO,
  type ClassStatisticsVO
} from '@/api/class'
import ImportDialog, { type ImportColumn } from '@/components/ImportDialog.vue'

// 表格与加载状态
const loading = ref(false)
const submitLoading = ref(false)
const total = ref(0)
const tableData = ref<ClassVO[]>([])

// 院系/专业级联数据（搜索栏 + 表单）
const deptList = ref<DepartmentSimpleVO[]>([])
const majorList = ref<MajorSimpleVO[]>([])
const selectedDeptId = ref<number>()

// 新增/编辑弹窗状态
const formMajorList = ref<MajorSimpleVO[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()

// 就业统计弹窗状态
const statsVisible = ref(false)
const statsLoading = ref(false)
const statistics = ref<ClassStatisticsVO | null>(null)

const queryForm = reactive<ClassQueryDTO>({
  majorId: undefined,
  keyword: '',
  page: 1,
  size: 10
})

const formData = reactive<any>({
  name: '',
  deptId: undefined,
  majorId: undefined,
  enrollmentYear: '',
  graduationYear: '',
  description: ''
})

const formRules: FormRules = {
  name: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
  deptId: [{ required: true, message: '请选择所属院系', trigger: 'change' }],
  majorId: [{ required: true, message: '请选择所属专业', trigger: 'change' }],
  enrollmentYear: [{ required: true, message: '请选择入学年份', trigger: 'change' }],
  graduationYear: [{ required: true, message: '请选择毕业年份', trigger: 'change' }]
}

async function loadDeptList() {
  try {
    const res = await getDepartmentList()
    deptList.value = res.data
  } catch { /* ignore */ }
}

async function loadMajorList(deptId?: number) {
  try {
    const res = await getMajorList(deptId)
    return res.data
  } catch {
    return []
  }
}

async function onDeptChange(deptId?: number) {
  selectedDeptId.value = deptId
  queryForm.majorId = undefined
  majorList.value = await loadMajorList(deptId)
  fetchData()
}

async function onFormDeptChange(deptId?: number) {
  formData.majorId = undefined
  formMajorList.value = await loadMajorList(deptId)
}

// 批量导入：按专业名称匹配专业 ID
const importVisible = ref(false)
const allMajorList = ref<MajorSimpleVO[]>([])
const importColumns: ImportColumn[] = [
  { prop: 'name', label: '班级名称', required: true, width: 20 },
  { prop: 'majorName', label: '所属专业', required: true, width: 22 },
  { prop: 'enrollmentYear', label: '入学年份', required: true, width: 12, text: true },
  { prop: 'graduationYear', label: '毕业年份', required: true, width: 12, text: true },
  { prop: 'description', label: '描述', width: 30 }
]
const templateRows = [
  {
    name: '计算机2301班',
    majorName: '计算机科学与技术',
    enrollmentYear: 2023,
    graduationYear: 2027,
    description: '示例行，可删除'
  }
]

watch(importVisible, async (v) => {
  if (v && !allMajorList.value.length) {
    allMajorList.value = await loadMajorList()
  }
})

async function buildImportPayload(data: Record<string, string>) {
  const majorName = data.majorName?.trim()
  const major = allMajorList.value.find((m) => m.name === majorName)
  if (!major) return { error: `专业「${majorName}」不存在，请先确认专业名称` }
  const payload: any = { ...data, majorId: major.id }
  delete payload.majorName
  const enrollmentYear = Number(payload.enrollmentYear)
  const graduationYear = Number(payload.graduationYear)
  if (!Number.isInteger(enrollmentYear) || enrollmentYear < 2000 || enrollmentYear > 2100) {
    return { error: '入学年份格式不正确（如 2023）' }
  }
  if (!Number.isInteger(graduationYear) || graduationYear < 2000 || graduationYear > 2100) {
    return { error: '毕业年份格式不正确（如 2027）' }
  }
  payload.enrollmentYear = enrollmentYear
  payload.graduationYear = graduationYear
  if (!payload.description) delete payload.description
  return { payload }
}

async function submitImport(payload: any) {
  await createClass(payload)
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getClassPage(queryForm)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

// 导出班级信息（文档2.3导出按钮）
async function handleExport() {
  try {
    await downloadCsv('/export/classes', {
      keyword: queryForm.keyword,
      majorId: queryForm.majorId,
    })
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
  selectedDeptId.value = undefined
  queryForm.majorId = undefined
  queryForm.keyword = ''
  queryForm.page = 1
  fetchData()
}

async function handleAdd() {
  isEdit.value = false
  editId.value = null
  formData.name = ''
  formData.deptId = undefined
  formData.majorId = undefined
  formData.enrollmentYear = ''
  formData.graduationYear = ''
  formData.description = ''
  formMajorList.value = []
  dialogVisible.value = true
  formRef.value?.resetFields()
}

async function handleEdit(row: ClassVO) {
  isEdit.value = true
  editId.value = row.id
  formData.name = row.name
  formData.majorId = row.majorId
  formData.deptId = row.deptId
  formData.enrollmentYear = row.enrollmentYear
  formData.graduationYear = row.graduationYear
  formData.description = row.description
  formMajorList.value = await loadMajorList(row.deptId)
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    const payload: ClassCreateDTO = {
      name: formData.name,
      majorId: formData.majorId,
      enrollmentYear: Number(formData.enrollmentYear),
      graduationYear: Number(formData.graduationYear),
      description: formData.description
    }
    if (isEdit.value && editId.value) {
      await updateClass(editId.value, { ...payload, id: editId.value })
      ElMessage.success('更新成功')
    } else {
      await createClass(payload)
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

async function handleDelete(row: ClassVO) {
  try {
    await deleteClass(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e: any) {
    ElMessage.error(e?.message || '删除失败')
  }
}

async function handleStatistics(row: ClassVO) {
  statsVisible.value = true
  statsLoading.value = true
  statistics.value = null
  try {
    const res = await getClassStatistics(row.id)
    statistics.value = res.data
  } finally {
    statsLoading.value = false
  }
}

onMounted(async () => {
  await loadDeptList()
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
