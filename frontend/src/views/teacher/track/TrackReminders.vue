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
  <div class="reminders-page">
    <el-card shadow="never">
      <div class="page-header">
        <div>
          <h3>失业学生跟踪提醒</h3>
          <p class="page-desc">学生从「已就业」变更为「失业」时系统自动推送提醒，请及时开展跟踪帮扶</p>
        </div>
        <el-button type="primary" plain @click="loadData">
          <el-icon><Refresh /></el-icon>&nbsp;刷新
        </el-button>
      </div>

      <el-empty v-if="!loading && reminders.length === 0" description="暂无失业跟踪提醒" />

      <el-table v-else v-loading="loading" :data="reminders" stripe>
        <el-table-column label="学生" min-width="120">
          <template #default="{ row }">
            <div class="student-name">{{ row.studentName }}</div>
            <div class="student-no">{{ row.studentNo }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="deptName" label="院系" min-width="120" />
        <el-table-column label="状态变更" min-width="150">
          <template #default="{ row }">
            <el-tag size="small" type="info">{{ row.fromStatusName }}</el-tag>
            <el-icon class="arrow"><Right /></el-icon>
            <el-tag size="small" type="danger">{{ row.toStatusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="失业原因" min-width="120">
          <template #default="{ row }">
            {{ reasonLabel(row.reason) }}
          </template>
        </el-table-column>
        <el-table-column label="佐证材料" min-width="110">
          <template #default="{ row }">
            <el-link v-if="row.evidenceUrl" type="primary" :href="getAttachmentUrl(Number(row.evidenceUrl))" target="_blank">
              查看佐证材料
            </el-link>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="变更时间" min-width="150">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain @click="viewDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="学生跟踪详情" width="720px">
      <el-descriptions v-if="current" :column="2" border>
        <el-descriptions-item label="姓名">{{ current.studentName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ current.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="院系">{{ current.deptName }}</el-descriptions-item>
        <el-descriptions-item label="变更时间">{{ formatTime(current.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="状态变更">
          <el-tag size="small" type="info">{{ current.fromStatusName }}</el-tag>
          <el-icon class="arrow"><Right /></el-icon>
          <el-tag size="small" type="danger">{{ current.toStatusName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="失业原因">{{ reasonLabel(current.reason) }}</el-descriptions-item>
        <el-descriptions-item label="佐证材料" :span="2">
          <el-link v-if="current.evidenceUrl" type="primary" :href="getAttachmentUrl(Number(current.evidenceUrl))" target="_blank">
            查看佐证材料
          </el-link>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ current.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
/**
 * 失业学生跟踪提醒页面（辅导员）
 * - 学生从「已就业」变更为「失业」时系统自动推送提醒
 * - 展示学生、院系、状态变更、失业原因、佐证材料，支持查看详情
 */
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Right } from '@element-plus/icons-vue'
import { getUnemployedReminders, UNEMPLOYED_REASONS } from '@/api/track'
import { getAttachmentUrl } from '@/api/employment'

const reminders = ref<any[]>([])
const loading = ref(false)
const detailVisible = ref(false)
const current = ref<any>(null)

/** 失业原因中文文案 */
function reasonLabel(reason: string) {
  return UNEMPLOYED_REASONS[reason] || reason || '-'
}

/** 时间格式化（ISO → 可读文本） */
function formatTime(t?: string) {
  if (!t) return ''
  return t.replace('T', ' ').slice(0, 19)
}

/** 加载失业提醒列表 */
async function loadData() {
  loading.value = true
  try {
    const res = await getUnemployedReminders()
    reminders.value = res.data || []
  } catch (e) {
    ElMessage.error('加载提醒列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

/** 打开学生跟踪详情弹窗 */
function viewDetail(row: any) {
  current.value = row
  detailVisible.value = true
}

onMounted(loadData)
</script>

<style scoped>
.reminders-page {
  max-width: 1100px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

h3 {
  margin: 0;
  font-size: 16px;
  color: #1a202c;
}

.page-desc {
  margin: 6px 0 0;
  color: #718096;
  font-size: 13px;
}

.student-name {
  font-weight: 600;
  color: #1a202c;
}

.student-no {
  font-size: 12px;
  color: #a0aec0;
}

.arrow {
  color: #a0aec0;
  margin: 0 4px;
  vertical-align: middle;
}

.text-muted {
  color: #a0aec0;
}
</style>
