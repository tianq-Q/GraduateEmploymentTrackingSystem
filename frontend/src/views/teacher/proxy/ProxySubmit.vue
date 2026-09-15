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
  <div class="proxy-submit">
    <div class="page-header">
      <h2>信息代录</h2>
      <p class="subtitle">辅导员/管理员代替学生录入就业信息，代录后同样进入两级审核流程</p>
    </div>

    <el-card>
      <template #header>
        <span>选择学生</span>
      </template>
      <el-form :inline="true">
        <el-form-item label="目标学生">
          <el-select v-model="selectedStudent" filterable placeholder="搜索并选择学生" style="width: 350px"
            value-key="id" @change="onStudentChange">
            <el-option v-for="s in students" :key="s.id"
              :label="`${s.realName || s.username} (${s.username})`" :value="s" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="selectedStudent" class="form-card">
      <template #header>
        <span>代录就业信息 - {{ selectedStudent.realName || selectedStudent.username }}</span>
        <el-tag type="warning" size="small" style="margin-left:10px">代录模式</el-tag>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" :disabled="submitting">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="毕业去向" prop="destination">
              <el-select v-model="form.destination" placeholder="请选择" style="width:100%">
                <el-option label="签约就业" value="签约就业" />
                <el-option label="升学" value="升学" />
                <el-option label="出国" value="出国" />
                <el-option label="创业" value="创业" />
                <el-option label="灵活就业" value="灵活就业" />
                <el-option label="待就业" value="待就业" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位名称" prop="companyName">
              <el-input v-model="form.companyName" placeholder="就业单位名称" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="岗位">
              <el-input v-model="form.position" placeholder="岗位名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作城市">
              <el-input v-model="form.city" placeholder="工作城市" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单位性质">
              <el-select v-model="form.companyType" placeholder="请选择" style="width:100%">
                <el-option label="国有企业" value="国企" />
                <el-option label="民营企业" value="民企" />
                <el-option label="外资企业" value="外企" />
                <el-option label="事业单位" value="事业单位" />
                <el-option label="政府机关" value="政府机关" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="薪资范围">
              <el-select v-model="form.salaryRange" placeholder="请选择" style="width:100%">
                <el-option label="3000以下" value="3000以下" />
                <el-option label="3000-5000" value="3000-5000" />
                <el-option label="5000-8000" value="5000-8000" />
                <el-option label="8000-12000" value="8000-12000" />
                <el-option label="12000-20000" value="12000-20000" />
                <el-option label="20000以上" value="20000以上" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="所属行业">
          <el-input v-model="form.industry" placeholder="所属行业" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            提交代录
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 已代录记录 -->
    <el-card class="records-card">
      <template #header><span>最近代录记录</span></template>
      <el-table :data="proxyRecords" stripe>
        <el-table-column prop="id" label="编号" width="70" />
        <el-table-column prop="submitterName" label="提交人" width="100" />
        <el-table-column prop="companyName" label="单位名称" min-width="150" />
        <el-table-column prop="position" label="岗位" width="120" />
        <el-table-column prop="destination" label="去向" width="100" />
        <el-table-column label="审核状态" width="130">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.reviewStatus]?.type || 'info'">
              {{ statusMap[row.reviewStatus]?.label || row.reviewStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="160" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
/**
 * 信息代录页面（辅导员/管理员）
 * - 选择目标学生后代录就业信息，代录记录同样进入两级审核流程
 * - 底部展示最近代录记录及其审核状态
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getProxyStudents,
  proxySubmit,
  reviewStatusMap,
  type SysUser,
  type EmploymentRecord,
  type EmploymentSubmitDTO
} from '@/api/employment'

const formRef = ref()
const submitting = ref(false)
const students = ref<SysUser[]>([])
const selectedStudent = ref<SysUser | null>(null)
const proxyRecords = ref<EmploymentRecord[]>([])
const statusMap = reviewStatusMap

// 代录就业信息表单
const form = reactive<EmploymentSubmitDTO>({
  destination: '',
  companyName: '',
  companyType: '',
  industry: '',
  position: '',
  salaryRange: '',
  city: ''
})

const rules = {
  destination: [{ required: true, message: '请选择毕业去向', trigger: 'change' }],
  companyName: [{ required: true, message: '请输入单位名称', trigger: 'blur' }]
}

// 初始化：加载可代录的学生列表
onMounted(async () => {
  try {
    const res = await getProxyStudents()
    students.value = res.data || []
  } catch (e) {
    console.error('加载学生列表失败', e)
  }
})

/** 切换学生时重置表单 */
function onStudentChange() {
  resetForm()
}

/** 清空代录表单 */
function resetForm() {
  form.destination = ''
  form.companyName = ''
  form.companyType = ''
  form.industry = ''
  form.position = ''
  form.salaryRange = ''
  form.city = ''
}

/** 提交代录 */
async function handleSubmit() {
  if (!selectedStudent.value) {
    ElMessage.warning('请先选择学生')
    return
  }

  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // 附带目标学生的学号与姓名提交代录
    const data = {
      ...form,
      studentNo: selectedStudent.value.username,
      name: selectedStudent.value.realName
    }
    const res = await proxySubmit(data)
    ElMessage.success('代录成功，记录已进入审核流程')
    proxyRecords.value.unshift(res.data)
    resetForm()
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.message || '代录失败')
  } finally {
    submitting.value = false
  }
}

/** 手动重置表单 */
function handleReset() {
  resetForm()
}
</script>

<style scoped>
.proxy-submit {
  padding: 20px;
}
.page-header {
  margin-bottom: 20px;
}
.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}
.page-header .subtitle {
  margin: 4px 0 0;
  font-size: 13px;
  color: #909399;
}
.form-card {
  margin-bottom: 20px;
}
.records-card {
  margin-top: 20px;
}
</style>
