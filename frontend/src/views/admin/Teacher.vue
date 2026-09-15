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
        <el-icon class="page-title__icon"><Avatar /></el-icon>
        <div>
          <h2 class="page-title__text">教师管理</h2>
        </div>
      </div>
    </div>

    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryForm" size="default">
        <el-form-item label="所属院系">
          <el-select
            v-model="queryForm.deptId"
            placeholder="请选择院系"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="d in deptList"
              :key="d.id"
              :label="d.name"
              :value="d.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input
            v-model="queryForm.keyword"
            placeholder="工号/姓名"
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
          <span><el-icon style="vertical-align: -2px; margin-right: 6px"><List /></el-icon>教师列表</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">
              <el-icon style="margin-right: 4px"><Plus /></el-icon>新增教师
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
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="jobNumber" label="工号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="deptName" label="所属院系" width="150" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">
              <el-icon style="vertical-align: -2px"><Edit /></el-icon>编辑
            </el-button>
            <el-popconfirm
              :title="row.status === 1 ? '确定要禁用该教师账号吗？' : '确定要启用该教师账号吗？'"
              confirm-button-text="确定"
              cancel-button-text="取消"
              @confirm="handleToggleStatus(row)"
            >
              <template #reference>
                <el-button
                  :type="row.status === 1 ? 'warning' : 'success'"
                  link
                  size="small"
                >
                  <el-icon style="vertical-align: -2px"><Switch /></el-icon>{{ row.status === 1 ? '禁用' : '启用' }}
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
      :title="isEdit ? '编辑教师' : '新增教师'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="90px"
      >
        <el-form-item label="工号" prop="jobNumber">
          <el-input
            v-model="formData.jobNumber"
            placeholder="请输入工号"
            maxlength="20"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" maxlength="20" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="请输入密码"
            show-password
            maxlength="30"
          />
        </el-form-item>
        <el-form-item label="所属院系" prop="deptId">
          <el-select
            v-model="formData.deptId"
            placeholder="请选择院系"
            style="width: 100%"
          >
            <el-option
              v-for="d in deptList"
              :key="d.id"
              :label="d.name"
              :value="d.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="formData.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="formData.email" placeholder="请输入邮箱" maxlength="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <ImportDialog
      v-model="importVisible"
      title="教师批量导入"
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
 * 教师管理页面（学院管理员 / 系统管理员）
 * - 按院系/姓名/工号筛选，分页展示
 * - 支持新增、编辑、启用/禁用教师账号
 * - 支持 CSV 模板批量导入与导出
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Avatar, List, Plus, Edit, Switch, Download, Upload } from '@element-plus/icons-vue'
import { downloadCsv } from '@/api/request'
import type { FormInstance, FormRules } from 'element-plus'
import { getDepartmentList, type DepartmentSimpleVO } from '@/api/department'
import {
  getTeacherPage,
  createTeacher,
  updateTeacher,
  toggleTeacherStatus,
  type TeacherVO,
  type TeacherCreateDTO,
  type TeacherQueryDTO
} from '@/api/teacher'
import ImportDialog, { type ImportColumn } from '@/components/ImportDialog.vue'

// 表格与加载状态
const loading = ref(false)
const submitLoading = ref(false)
const total = ref(0)
const tableData = ref<TeacherVO[]>([])

// 院系下拉数据与弹窗状态
const deptList = ref<DepartmentSimpleVO[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()

const queryForm = reactive<TeacherQueryDTO>({
  deptId: undefined,
  keyword: '',
  page: 1,
  size: 10
})

const formData = reactive<any>({
  jobNumber: '',
  name: '',
  password: '',
  deptId: undefined,
  phone: '',
  email: ''
})

const formRules: FormRules = {
  jobNumber: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  deptId: [{ required: true, message: '请选择所属院系', trigger: 'change' }]
}

async function loadDeptList() {
  try {
    const res = await getDepartmentList()
    deptList.value = res.data
  } catch { /* ignore */ }
}

// 批量导入：按院系名称匹配院系 ID
const importVisible = ref(false)
const importColumns: ImportColumn[] = [
  { prop: 'jobNumber', label: '工号', required: true, width: 14, text: true },
  { prop: 'name', label: '姓名', required: true, width: 12 },
  { prop: 'deptName', label: '所属院系', required: true, width: 18 },
  { prop: 'password', label: '初始密码', required: true, width: 14 },
  { prop: 'phone', label: '手机号', width: 15, text: true },
  { prop: 'email', label: '邮箱', width: 24 }
]
const templateRows = [
  {
    jobNumber: 'T2026001',
    name: '张三',
    deptName: '计算机学院',
    password: '123456',
    phone: '13800138000',
    email: 'zhangsan@example.com'
  }
]

function buildImportPayload(data: Record<string, string>) {
  const deptName = data.deptName?.trim()
  const dept = deptList.value.find((d) => d.name === deptName)
  if (!dept) return { error: `院系「${deptName}」不存在，请先确认院系名称` }
  const payload: any = { ...data, deptId: dept.id }
  delete payload.deptName
  if (!payload.phone) delete payload.phone
  if (!payload.email) delete payload.email
  return { payload }
}

async function submitImport(payload: any) {
  await createTeacher(payload)
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getTeacherPage(queryForm)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

// 导出教师信息（文档2.3导出按钮）
async function handleExport() {
  try {
    await downloadCsv('/export/teachers', {
      keyword: queryForm.keyword,
      deptId: queryForm.deptId,
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
  queryForm.deptId = undefined
  queryForm.keyword = ''
  queryForm.page = 1
  fetchData()
}

function handleAdd() {
  isEdit.value = false
  editId.value = null
  formData.jobNumber = ''
  formData.name = ''
  formData.password = ''
  formData.deptId = undefined
  formData.phone = ''
  formData.email = ''
  dialogVisible.value = true
  formRef.value?.resetFields()
}

function handleEdit(row: TeacherVO) {
  isEdit.value = true
  editId.value = row.id
  formData.jobNumber = row.jobNumber
  formData.name = row.name
  formData.password = ''
  formData.deptId = row.deptId
  formData.phone = row.phone
  formData.email = row.email
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value && editId.value) {
      await updateTeacher(editId.value, {
        id: editId.value,
        name: formData.name,
        deptId: formData.deptId,
        phone: formData.phone,
        email: formData.email
      })
      ElMessage.success('更新成功')
    } else {
      const payload: TeacherCreateDTO = {
        jobNumber: formData.jobNumber,
        name: formData.name,
        password: formData.password,
        deptId: formData.deptId,
        phone: formData.phone,
        email: formData.email
      }
      await createTeacher(payload)
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

async function handleToggleStatus(row: TeacherVO) {
  try {
    await toggleTeacherStatus(row.id)
    ElMessage.success(row.status === 1 ? '已禁用' : '已启用')
    fetchData()
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
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
