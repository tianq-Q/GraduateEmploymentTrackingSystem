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
  <div class="attachment-manage">
    <div class="page-header">
      <h2>附件材料管理</h2>
      <p class="subtitle">上传就业佐证材料：三方协议、劳动合同、录用证明扫描件（支持 JPG/PNG/PDF）</p>
    </div>

    <!-- 选择就业记录 -->
    <el-card class="select-card">
      <el-form :inline="true">
        <el-form-item label="选择就业记录">
          <el-select v-model="selectedRecordId" placeholder="请选择一条就业记录" @change="loadAttachments" style="width:400px">
            <el-option v-for="r in records" :key="r.id" :label="`#${r.id} - ${r.companyName || '未填单位'} (${statusMap[r.reviewStatus]?.label || r.reviewStatus})`" :value="r.id" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 附件操作区 -->
    <el-card v-if="selectedRecordId" class="upload-card">
      <template #header>
        <span>附件列表</span>
        <span class="hint-text">（支持 JPG、PNG、PDF 格式，单文件最大 10MB）</span>
      </template>

      <div class="upload-section">
        <el-upload
          :action="uploadUrl"
          :headers="uploadHeaders"
          :data="{ recordId: selectedRecordId }"
          :before-upload="beforeUpload"
          :on-success="onUploadSuccess"
          :on-error="onUploadError"
          :show-file-list="false"
          accept=".jpg,.jpeg,.png,.gif,.bmp,.pdf"
          multiple
        >
          <el-button type="primary">
            <el-icon><Upload /></el-icon>
            上传附件
          </el-button>
        </el-upload>
      </div>

      <el-table :data="attachments" stripe v-loading="loading">
        <el-table-column prop="id" label="编号" width="70" />
        <el-table-column prop="fileName" label="文件名" min-width="200" show-overflow-tooltip />
        <el-table-column label="文件大小" width="100">
          <template #default="{ row }">{{ formatSize(row.fileSize) }}</template>
        </el-table-column>
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.fileType?.startsWith('image/')" type="success" size="small">图片</el-tag>
            <el-tag v-else-if="row.fileType === 'application/pdf'" type="warning" size="small">PDF</el-tag>
            <el-tag v-else size="small" type="info">文件</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="上传时间" width="160" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="previewFile(row)">
              预览
            </el-button>
            <el-button type="primary" size="small" link @click="downloadFile(row)">
              下载
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && attachments.length === 0" description="暂无附件" />
    </el-card>

    <!-- 文件预览弹窗 -->
    <el-dialog v-model="previewVisible" title="文件预览" width="800px" destroy-on-close>
      <div v-if="previewFileInfo" class="preview-container">
        <img v-if="previewFileInfo.fileType?.startsWith('image/')"
          :src="previewUrl" class="preview-image" />
        <iframe v-else-if="previewFileInfo.fileType === 'application/pdf'"
          :src="previewUrl" class="preview-pdf" />
        <div v-else class="preview-unsupported">不支持预览此文件类型</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
/**
 * 附件材料管理页面（毕业生）
 * - 选择一条就业记录后，可上传/预览/下载/删除佐证附件
 * - 支持 JPG/PNG/PDF 等格式，单文件最大 10MB
 */
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import {
  getMyRecords,
  getAttachments,
  uploadAttachment,
  deleteAttachment,
  getAttachmentUrl,
  reviewStatusMap,
  type EmploymentRecord,
  type Attachment
} from '@/api/employment'

const records = ref<EmploymentRecord[]>([])
const selectedRecordId = ref<number | null>(null)
const attachments = ref<Attachment[]>([])
const loading = ref(false)
const previewVisible = ref(false)
const previewFileInfo = ref<Attachment | null>(null)
const statusMap = reviewStatusMap

// 附件上传接口地址与鉴权请求头
const uploadUrl = '/api/attachment/upload'
const uploadHeaders = {
  Authorization: 'Bearer ' + (localStorage.getItem('token') || '')
}

const previewUrl = ref('')

// 允许上传的扩展名与大小限制（10MB）
const ALLOWED_EXTENSIONS = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'pdf']
const MAX_SIZE = 10 * 1024 * 1024 // 10MB

// 初始化：加载当前用户的就业记录列表
onMounted(async () => {
  try {
    const res = await getMyRecords()
    records.value = res.data || []
  } catch (e) {
    console.error('加载就业记录失败', e)
  }
})

/** 加载选中记录的附件列表 */
async function loadAttachments() {
  if (!selectedRecordId.value) return
  loading.value = true
  try {
    const res = await getAttachments(selectedRecordId.value)
    attachments.value = res.data || []
  } catch (e) {
    console.error('加载附件失败', e)
  } finally {
    loading.value = false
  }
}

/** 上传前校验：扩展名与文件大小 */
function beforeUpload(file: File) {
  const ext = file.name.split('.').pop()?.toLowerCase() || ''
  if (!ALLOWED_EXTENSIONS.includes(ext)) {
    ElMessage.error(`不支持的文件格式: .${ext}，仅支持 ${ALLOWED_EXTENSIONS.join(', ')}`)
    return false
  }
  if (file.size > MAX_SIZE) {
    ElMessage.error(`文件大小超过限制（最大 10MB），当前: ${(file.size / 1024 / 1024).toFixed(1)}MB`)
    return false
  }
  return true
}

/** 上传成功回调：提示并刷新列表 */
function onUploadSuccess() {
  ElMessage.success('上传成功')
  loadAttachments()
}

/** 上传失败回调 */
function onUploadError(err: any) {
  ElMessage.error('上传失败: ' + (err?.message || '未知错误'))
}

/** 打开附件预览弹窗 */
function previewFile(file: Attachment) {
  previewFileInfo.value = file
  previewUrl.value = getAttachmentUrl(file.id)
  previewVisible.value = true
}

/** 下载附件（通过临时 a 标签触发下载） */
function downloadFile(file: Attachment) {
  const url = getAttachmentUrl(file.id)
  const link = document.createElement('a')
  link.href = url
  link.download = file.fileName
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

/** 删除附件（需二次确认） */
async function handleDelete(file: Attachment) {
  try {
    await ElMessageBox.confirm(`确定要删除 "${file.fileName}" 吗？此操作不可恢复。`, '确认删除', { type: 'warning' })
    await deleteAttachment(file.id)
    ElMessage.success('删除成功')
    loadAttachments()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e?.response?.data?.message || '删除失败')
    }
  }
}

/** 格式化文件大小显示 */
function formatSize(bytes: number): string {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / 1024 / 1024).toFixed(1) + ' MB'
}
</script>

<style scoped>
.attachment-manage {
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
.select-card {
  margin-bottom: 20px;
}
.upload-card {
  margin-bottom: 20px;
}
.hint-text {
  font-size: 12px;
  color: #909399;
  margin-left: 8px;
}
.upload-section {
  margin-bottom: 16px;
}
.preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}
.preview-image {
  max-width: 100%;
  max-height: 500px;
}
.preview-pdf {
  width: 100%;
  height: 500px;
  border: none;
}
.preview-unsupported {
  color: #909399;
  font-size: 16px;
}
</style>
