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
  <div class="status-page">
    <!-- 当前状态卡片 -->
    <el-card shadow="never" class="current-card">
      <div class="card-header">
        <h3>当前就业跟踪状态</h3>
        <el-button type="primary" @click="openSubmitDialog">
          <el-icon><EditPen /></el-icon>&nbsp;提交状态变更
        </el-button>
      </div>
      <div class="status-body">
        <el-tag :type="statusType" size="large" effect="dark" class="status-tag">
          {{ currentStatus.statusName || '未就业' }}
        </el-tag>
        <span class="status-tip">状态变更需提交佐证材料；从「已就业」变更为「失业」时将自动通知辅导员跟踪帮扶</span>
      </div>
    </el-card>

    <!-- 六大状态说明 -->
    <el-card shadow="never" class="statuses-card">
      <h3>六大就业状态</h3>
      <div class="statuses-grid">
        <div v-for="item in statusList" :key="item.key" class="status-item"
             :class="{ active: item.key === currentStatus.status }">
          <div class="status-item-name">{{ item.label }}</div>
          <div class="status-item-desc">{{ item.desc }}</div>
        </div>
      </div>
    </el-card>

    <!-- 历史流转记录 -->
    <el-card shadow="never">
      <h3>状态流转记录</h3>
      <el-empty v-if="changes.length === 0" description="暂无状态流转记录" />
      <el-timeline v-else class="change-timeline">
        <el-timeline-item
          v-for="item in changes"
          :key="item.id"
          :timestamp="formatTime(item.createTime)"
          placement="top"
          :type="timelineType(item)"
        >
          <div class="change-item">
            <div class="change-main">
              <el-tag size="small" type="info">{{ item.fromStatusName }}</el-tag>
              <el-icon class="arrow"><Right /></el-icon>
              <el-tag size="small" :type="item.toStatus === 'UNEMPLOYED_AFTER' ? 'danger' : 'success'">
                {{ item.toStatusName }}
              </el-tag>
              <el-tag v-if="item.isReminder === 1" size="small" type="danger" effect="plain" class="reminder-tag">
                已触发跟踪提醒
              </el-tag>
            </div>
            <div v-if="item.reason" class="change-detail">失业原因：{{ reasonLabel(item.reason) }}</div>
            <div v-if="item.evidenceUrl" class="change-detail">
              佐证材料：
              <el-link type="primary" :href="getAttachmentUrl(Number(item.evidenceUrl))" target="_blank">
                查看佐证材料
              </el-link>
            </div>
            <div v-if="item.remark" class="change-detail">备注：{{ item.remark }}</div>
            <div class="change-meta">操作人：{{ item.operatorName || '-' }} · {{ formatTime(item.createTime) }}</div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <!-- 提交状态变更弹窗 -->
    <el-dialog v-model="dialogVisible" title="提交就业状态变更" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="目标状态" prop="toStatus">
          <el-select v-model="form.toStatus" placeholder="请选择目标状态" style="width: 100%">
            <el-option
              v-for="item in targetStatusList"
              :key="item.key"
              :label="item.label"
              :value="item.key"
            />
          </el-select>
        </el-form-item>

        <el-form-item v-if="isToUnemployed" label="失业原因" prop="reason">
          <el-select v-model="form.reason" placeholder="请选择失业原因" style="width: 100%">
            <el-option
              v-for="(label, value) in UNEMPLOYED_REASONS"
              :key="value"
              :label="label"
              :value="value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="佐证材料" prop="evidenceUrl">
          <el-upload
            :auto-upload="false"
            :limit="1"
            accept=".jpg,.jpeg,.png,.gif,.bmp,.pdf"
            :on-change="onFileChange"
            :on-remove="onFileRemove"
            :file-list="fileList"
          >
            <el-button type="primary" plain>
              <el-icon><Upload /></el-icon>&nbsp;选择佐证材料
            </el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持 jpg/png/pdf，不超过 10MB；升学/出国/失业必须上传（录取通知书、离职证明等）
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="补充说明（选填）" maxlength="200" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
/**
 * 就业状态跟踪页面（毕业生）
 * - 展示当前就业跟踪状态与六大就业状态说明
 * - 以时间线展示状态流转历史（含失业原因、佐证材料、跟踪提醒标记）
 * - 支持提交状态变更（如已就业→失业需上传佐证并触发教师跟踪提醒）
 */
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { EditPen, Right, Upload } from '@element-plus/icons-vue'
import type { FormInstance, FormRules, UploadFile, UploadUserFile } from 'element-plus'
import {
  UNEMPLOYED_REASONS,
  submitStatusChange,
  getMyStatusChanges,
  getMyTrackStatus
} from '@/api/track'
import { uploadAttachment, getAttachmentUrl } from '@/api/employment'

interface StatusItem {
  key: string
  label: string
  desc: string
}

// 六大就业状态定义
const statusList: StatusItem[] = [
  { key: 'UNEMPLOYED', label: '未就业', desc: '毕业离校后暂未就业' },
  { key: 'PENDING', label: '待审核', desc: '状态变更提交后待教师审核' },
  { key: 'EMPLOYED', label: '已就业', desc: '已签订劳动合同或就业协议' },
  { key: 'UNEMPLOYED_AFTER', label: '失业', desc: '已就业后失业，需跟踪帮扶' },
  { key: 'POSTGRADUATE', label: '升学', desc: '考取研究生继续深造' },
  { key: 'ABROAD', label: '出国', desc: '出国留学或工作' }
]

const currentStatus = ref<{ status: string; statusName: string }>({ status: 'UNEMPLOYED', statusName: '未就业' })
const changes = ref<any[]>([])
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()
const fileList = ref<UploadUserFile[]>([])
const uploadedAttachmentId = ref<number | null>(null)

// 状态变更表单
const form = reactive<{ toStatus: string; reason: string; evidenceUrl: string; remark: string }>({
  toStatus: '',
  reason: '',
  evidenceUrl: '',
  remark: ''
})

// 表单校验规则
const rules: FormRules = {
  toStatus: [{ required: true, message: '请选择目标状态', trigger: 'change' }],
  reason: [{ required: true, message: '请选择失业原因', trigger: 'change' }],
  evidenceUrl: [{ required: true, message: '请上传佐证材料', trigger: 'change' }]
}

// 目标状态是否为「失业」（失业时需填原因 + 佐证）
const isToUnemployed = computed(() => form.toStatus === 'UNEMPLOYED_AFTER')
// 当前状态对应的标签颜色
const statusType = computed(() => {
  const map: Record<string, string> = {
    UNEMPLOYED: 'info',
    PENDING: 'warning',
    EMPLOYED: 'success',
    UNEMPLOYED_AFTER: 'danger',
    POSTGRADUATE: 'primary',
    ABROAD: 'primary'
  }
  return (map[currentStatus.value.status] || 'info') as any
})

// 可选的目标状态（排除当前状态）
const targetStatusList = computed(() =>
  statusList.filter((s) => s.key !== currentStatus.value.status)
)

/** 失业原因中文文案 */
function reasonLabel(reason: string) {
  return UNEMPLOYED_REASONS[reason] || reason || '-'
}

/** 时间格式化（ISO → 可读文本） */
function formatTime(t?: string) {
  if (!t) return ''
  return t.replace('T', ' ').slice(0, 19)
}

/** 时间线节点颜色：触发提醒或变为失业时高亮为红色 */
function timelineType(item: any) {
  if (item.isReminder === 1) return 'danger'
  if (item.toStatus === 'UNEMPLOYED_AFTER') return 'danger'
  return 'primary'
}

/** 加载当前状态 + 状态流转历史 */
async function loadData() {
  try {
    const [statusRes, changesRes] = await Promise.all([getMyTrackStatus(), getMyStatusChanges()])
    currentStatus.value = statusRes.data || currentStatus.value
    changes.value = changesRes.data || []
  } catch (e) {
    ElMessage.error('加载就业状态失败，请稍后重试')
  }
}

/** 打开提交状态变更弹窗（重置表单） */
function openSubmitDialog() {
  form.toStatus = ''
  form.reason = ''
  form.evidenceUrl = ''
  form.remark = ''
  fileList.value = []
  uploadedAttachmentId.value = null
  dialogVisible.value = true
}

/** 选择佐证文件：先上传附件获得附件 ID 再回填表单 */
function onFileChange(file: UploadFile) {
  if (!file.raw) return
  if ((file.size ?? 0) > 10 * 1024 * 1024) {
    ElMessage.error('文件不能超过 10MB')
    fileList.value = []
    return
  }
  uploadAttachment(0, file.raw)
    .then((res) => {
      const data = res.data as any
      if (data && data.id) {
        uploadedAttachmentId.value = data.id
        form.evidenceUrl = String(data.id)
      } else {
        ElMessage.error('上传失败，请重试')
      }
    })
    .catch(() => ElMessage.error('上传失败，请重试'))
}

/** 移除已选文件：清空附件信息 */
function onFileRemove() {
  fileList.value = []
  uploadedAttachmentId.value = null
  form.evidenceUrl = ''
}

/** 提交状态变更 */
async function handleSubmit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  if (!form.evidenceUrl) {
    ElMessage.warning('请先上传佐证材料')
    return
  }
  submitting.value = true
  try {
    await submitStatusChange({
      toStatus: form.toStatus,
      reason: form.reason || undefined,
      evidenceUrl: form.evidenceUrl,
      remark: form.remark || undefined
    })
    ElMessage.success('状态变更提交成功')
    dialogVisible.value = false
    await loadData()
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.message || '提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.status-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

h3 {
  margin: 0 0 12px;
  font-size: 16px;
  color: #1a202c;
}

.status-body {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.status-tag {
  font-size: 16px;
  padding: 8px 20px;
}

.status-tip {
  color: #718096;
  font-size: 13px;
}

.statuses-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.status-item {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 12px 16px;
  transition: all 0.2s;
}

.status-item.active {
  border-color: #4f8cff;
  background: #e8f0fe;
}

.status-item-name {
  font-weight: 600;
  color: #1a202c;
  margin-bottom: 4px;
}

.status-item-desc {
  font-size: 12px;
  color: #718096;
}

.change-timeline {
  padding: 8px 4px;
}

.change-item {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 12px 16px;
}

.change-main {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.arrow {
  color: #a0aec0;
}

.reminder-tag {
  margin-left: 4px;
}

.change-detail {
  margin-top: 6px;
  font-size: 13px;
  color: #4a5568;
}

.change-meta {
  margin-top: 8px;
  font-size: 12px;
  color: #a0aec0;
}

@media (max-width: 768px) {
  .statuses-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
