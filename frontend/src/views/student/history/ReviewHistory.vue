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
  <div class="review-history">
    <div class="page-header">
      <h2>审核记录</h2>
      <p class="subtitle">查看就业信息审核状态、驳回意见，审核通过后记录生效</p>
    </div>

    <!-- 记录卡片列表 -->
    <el-card v-for="record in records" :key="record.id" class="record-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="card-header-left">
            <span class="record-id">#{{ record.id }}</span>
            <el-tag :type="statusTagType(record.reviewStatus)" size="default">
              {{ statusTagLabel(record.reviewStatus) }}
            </el-tag>
            <el-tag v-if="record.isProxy" type="warning" size="small">代录</el-tag>
          </div>
          <span class="record-time">{{ record.createTime }}</span>
        </div>
      </template>

      <!-- 记录详情 -->
      <el-descriptions :column="3" border size="small">
        <el-descriptions-item label="单位名称" :span="2">{{ record.companyName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="去向">{{ record.destination || '-' }}</el-descriptions-item>
        <el-descriptions-item label="岗位">{{ record.position || '-' }}</el-descriptions-item>
        <el-descriptions-item label="城市">{{ record.city || '-' }}</el-descriptions-item>
        <el-descriptions-item label="行业">{{ record.industry || '-' }}</el-descriptions-item>
        <el-descriptions-item label="单位类型">{{ record.companyType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="薪资">{{ record.salaryRange || '-' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 驳回意见（醒目展示） -->
      <div v-if="isRejected(record.reviewStatus) && record.reviewComment" class="reject-comment">
        <div class="reject-label">
          <el-icon><WarningFilled /></el-icon>
          {{ record.reviewStatus === 'FIRST_REJECTED' ? '学院初审驳回意见' : '学校终审驳回意见' }}
        </div>
        <div class="reject-text">{{ record.reviewComment }}</div>
      </div>

      <!-- 通过确认 -->
      <div v-if="record.reviewStatus === 'APPROVED'" class="approved-info">
        <el-icon color="#67c23a"><CircleCheckFilled /></el-icon>
        <span>终审通过，记录已生效，纳入数据看板统计</span>
      </div>

      <!-- 操作按钮 -->
      <div class="card-actions">
        <!-- 可重新提交 -->
        <el-button
          v-if="canResubmit(record.reviewStatus)"
          type="primary"
          @click="openEditDialog(record)"
        >修改并重新提交</el-button>

        <!-- 可撤回 -->
        <el-button
          v-if="record.reviewStatus === 'PENDING'"
          type="warning"
          plain
          @click="handleWithdraw(record)"
        >撤回</el-button>

        <!-- 查看日志 -->
        <el-button
          type="info"
          plain
          size="small"
          @click="showAuditLogs(record)"
        >操作日志</el-button>
      </div>
    </el-card>

    <!-- 空状态 -->
    <el-empty v-if="!loading && records.length === 0" description="暂无就业记录">
      <el-button type="primary" @click="$router.push('/student/employment/submit')">去填报</el-button>
    </el-empty>

    <!-- 修改重提交弹窗 -->
    <el-dialog v-model="editVisible" title="修改就业信息" width="600px" destroy-on-close>
      <el-form ref="editFormRef" :model="editForm" label-width="100px" :rules="editRules">
        <el-form-item label="去向类型" prop="destination">
          <el-select v-model="editForm.destination" placeholder="请选择" style="width:100%">
            <el-option v-for="d in destinations" :key="d" :label="d" :value="d" />
          </el-select>
        </el-form-item>
        <el-form-item label="单位名称">
          <el-input v-model="editForm.companyName" placeholder="如未就业可留空" />
        </el-form-item>
        <el-form-item label="岗位">
          <el-input v-model="editForm.position" placeholder="请输入岗位名称" />
        </el-form-item>
        <el-form-item label="单位类型">
          <el-select v-model="editForm.companyType" placeholder="请选择" style="width:100%">
            <el-option label="国有企业" value="国有企业" />
            <el-option label="事业单位" value="事业单位" />
            <el-option label="民营企业" value="民营企业" />
            <el-option label="外资企业" value="外资企业" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="行业">
          <el-input v-model="editForm.industry" placeholder="请输入行业" />
        </el-form-item>
        <el-form-item label="薪资范围">
          <el-input v-model="editForm.salaryRange" placeholder="如 5K-8K" />
        </el-form-item>
        <el-form-item label="工作城市" prop="city">
          <el-input v-model="editForm.city" placeholder="请输入工作城市" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="doResubmit">提交</el-button>
      </template>
    </el-dialog>

    <!-- 操作日志弹窗 -->
    <el-dialog v-model="logVisible" title="操作日志" width="700px" destroy-on-close>
      <el-timeline>
        <el-timeline-item v-for="log in auditLogs" :key="log.id"
          :timestamp="log.createTime" placement="top"
          :type="logActionType(log.action)"
          :hollow="log.action === 'SUBMIT' || log.action === 'PROXY_SUBMIT'">
          <div class="log-item">
            <div class="log-title">
              <el-tag :type="logActionTag(log.action)" size="small">
                {{ actionMap[log.action] || log.action }}
              </el-tag>
              <span class="log-operator">{{ log.operatorName }}（{{ log.operatorRole }}）</span>
            </div>
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
 * 审核记录页面（毕业生）
 * - 以卡片列表展示本人就业记录的审核状态与详细内容
 * - 驳回时醒目展示驳回意见；终审通过后提示记录生效
 * - 支持「修改并重新提交」「撤回」「查看操作日志」
 */
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { WarningFilled, CircleCheckFilled } from '@element-plus/icons-vue'
import { getMyRecords, withdrawEmployment, resubmitEmployment, getReviewAuditLogs, reviewStatusMap, actionMap, type EmploymentRecord, type AuditLog } from '@/api/employment'

const records = ref<EmploymentRecord[]>([])
const loading = ref(false)

// 操作日志弹窗状态
const logVisible = ref(false)
const auditLogs = ref<AuditLog[]>([])

// 编辑重提交弹窗状态
const editVisible = ref(false)
const editLoading = ref(false)
const editFormRef = ref()
const currentRecord = ref<EmploymentRecord | null>(null)
const destinations = ['就业', '升学', '待业', '创业', '其他']

// 修改重提交表单
const editForm = ref({
  destination: '',
  companyName: '',
  position: '',
  companyType: '',
  industry: '',
  salaryRange: '',
  city: ''
})

const editRules = {
  destination: [{ required: true, message: '请选择去向类型', trigger: 'change' }],
  city: [{ required: true, message: '请输入工作城市', trigger: 'blur' }]
}

/** 审核状态对应的标签颜色 */
function statusTagType(status: string) {
  return reviewStatusMap[status]?.type || 'info'
}

/** 审核状态的中文文案 */
function statusTagLabel(status: string) {
  return reviewStatusMap[status]?.label || status
}

/** 是否处于驳回状态（展示驳回意见） */
function isRejected(status: string) {
  return status === 'FIRST_REJECTED' || status === 'FINAL_REJECTED'
}

/** 是否允许修改重提交（驳回/已撤回均可） */
function canResubmit(status: string) {
  return status === 'FIRST_REJECTED' || status === 'FINAL_REJECTED' || status === 'WITHDRAWN'
}

/** 日志时间线节点颜色 */
function logActionType(action: string) {
  if (action.includes('REJECT')) return 'danger'
  if (action.includes('PASS')) return 'success'
  return 'primary'
}

/** 日志操作标签颜色 */
function logActionTag(action: string) {
  if (action.includes('REJECT')) return 'danger'
  if (action.includes('PASS')) return 'success'
  return ''
}

/** 加载我的就业记录 */
async function loadRecords() {
  loading.value = true
  try {
    const res = await getMyRecords()
    records.value = (res as any).data?.list || (res as any).data || []
  } catch (e) {
    console.error('加载审核记录失败', e)
    records.value = []
  } finally {
    loading.value = false
  }
}

/** 撤回记录（需二次确认） */
async function handleWithdraw(row: EmploymentRecord) {
  try {
    await ElMessageBox.confirm('确认撤回该就业记录？撤回后可修改重新提交。', '撤回确认', {
      confirmButtonText: '确认撤回',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await withdrawEmployment(row.id)
    ElMessage.success('已撤回')
    loadRecords()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e?.response?.data?.message || '撤回失败')
    }
  }
}

/** 打开修改弹窗并回填记录数据 */
function openEditDialog(row: EmploymentRecord) {
  currentRecord.value = row
  editForm.value = {
    destination: row.destination || '',
    companyName: row.companyName || '',
    position: row.position || '',
    companyType: row.companyType || '',
    industry: row.industry || '',
    salaryRange: row.salaryRange || '',
    city: row.city || ''
  }
  editVisible.value = true
}

/** 提交修改并重新送审 */
async function doResubmit() {
  const valid = await editFormRef.value?.validate().catch(() => false)
  if (!valid || !currentRecord.value) return

  editLoading.value = true
  try {
    await resubmitEmployment(currentRecord.value.id, editForm.value)
    ElMessage.success('修改已提交，正在重新审核')
    editVisible.value = false
    loadRecords()
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.message || '提交失败')
  } finally {
    editLoading.value = false
  }
}

/** 查看该记录的完整操作日志 */
async function showAuditLogs(row: EmploymentRecord) {
  try {
    const res = await getReviewAuditLogs(row.id)
    auditLogs.value = (res as any).data || []
    logVisible.value = true
  } catch (e) {
    console.error('加载操作日志失败', e)
  }
}

// 页面初始化加载记录
loadRecords()
</script>

<style scoped>
.review-history {
  padding: 0;
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
.record-card {
  margin-bottom: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}
.record-id {
  font-weight: 600;
  color: #909399;
}
.record-time {
  font-size: 13px;
  color: #909399;
}
.reject-comment {
  margin-top: 16px;
  background: #fef0f0;
  border: 1px solid #fde2e2;
  border-radius: 6px;
  padding: 12px 16px;
}
.reject-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #f56c6c;
  margin-bottom: 6px;
}
.reject-text {
  font-size: 14px;
  color: #606266;
  padding-left: 22px;
  line-height: 1.6;
}
.approved-info {
  margin-top: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
  border-radius: 6px;
  padding: 12px 16px;
  font-size: 14px;
  color: #67c23a;
  font-weight: 500;
}
.card-actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}
.log-item {
  padding: 4px 0;
}
.log-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}
.log-operator {
  font-size: 13px;
  color: #606266;
}
.log-comment {
  margin-top: 6px;
  color: #606266;
  background: #f5f7fa;
  padding: 6px 10px;
  border-radius: 4px;
  font-size: 13px;
  border-left: 3px solid #4f8cff;
}
</style>
