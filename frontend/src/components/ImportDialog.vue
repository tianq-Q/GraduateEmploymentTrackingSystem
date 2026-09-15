<script lang="ts">
/** 导入列定义 */
export interface ImportColumn {
  prop: string
  label: string
  required?: boolean
  width?: number
  /** 文本列：模板中设为文本格式，防止学号/手机号等长数字变成科学计数法 */
  text?: boolean
  /** 兼容别名表头：如「所属院系」同时识别「院系」 */
  aliases?: string[]
}

/** buildPayload 返回值：payload=提交的数据；error=非空则该行校验失败 */
export interface BuildPayloadResult {
  payload?: any
  error?: string
}
</script>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Download, Upload } from '@element-plus/icons-vue'
import * as XLSX from 'xlsx'

interface ParseRow {
  data: Record<string, string>
  payload?: any
  error?: string
  status: 'pending' | 'success' | 'fail'
  failReason?: string
}

const props = withDefaults(
  defineProps<{
    modelValue: boolean
    title: string
    columns: ImportColumn[]
    templateRows?: Record<string, any>[]
    buildPayload?: (
      data: Record<string, string>
    ) => Promise<BuildPayloadResult> | BuildPayloadResult
    submitOne: (payload: any) => Promise<void>
  }>(),
  { templateRows: () => [], buildPayload: undefined }
)

const emit = defineEmits<{
  (e: 'update:modelValue', v: boolean): void
  (e: 'success'): void
}>()

const parsedRows = ref<ParseRow[]>([])
const importing = ref(false)
const successCount = ref(0)
const fileName = ref('')
const fileInputRef = ref<HTMLInputElement>()
/** 是否已执行过导入，用于区分「校验错误」与「导入失败」两种展示 */
const hasImported = ref(false)
const errorCount = computed(() => parsedRows.value.filter((r) => r.error).length)
/** 校验失败的行明细（含行号与原因），直接展示在预览区下方，便于定位问题 */
const errorRows = computed(() =>
  parsedRows.value
    .map((r, idx) => ({
      rowNo: idx + 1,
      name: r.data.name || r.data.studentNumber || `第${idx + 1}行`,
      reason: r.error || ''
    }))
    .filter((r) => r.reason)
)
/** 导入失败的行明细（含行号、姓名与具体原因），导入完成后展示 */
const failRows = computed(() =>
  parsedRows.value
    .map((r, idx) => ({
      rowNo: idx + 1,
      name: r.data.name || r.data.studentNumber || `第${idx + 1}行`,
      reason: r.failReason || r.error || '',
      failed: r.status === 'fail'
    }))
    .filter((r) => r.failed && r.reason)
)

function onOpen() {
  parsedRows.value = []
  importing.value = false
  successCount.value = 0
  fileName.value = ''
  hasImported.value = false
}

/** 表头归一化：去掉 "(必填)"、"*"、空格 */
function normalizeHeader(h: string): string {
  return String(h)
    .replace(/[（(].*?[)）]/g, '')
    .replace(/\*/g, '')
    .replace(/\s+/g, '')
    .trim()
}

/** 还原科学计数法文本（如 1.39E+10 -> 13900000000），应对用户未按文本格式填写的情况 */
function normalizeCellText(v: any): string {
  const s = String(v ?? '').trim()
  const m = s.match(/^(\d+(?:\.\d+)?)[eE]([+-]?\d+)$/)
  if (m) {
    const num = Number(s)
    if (Number.isFinite(num) && Number.isInteger(num)) return String(num)
  }
  return s
}

/** 下载导入模板 */
function downloadTemplate() {
  const header = props.columns.map((c) => c.label + (c.required ? '(必填)' : ''))
  const aoa: any[][] = [header]
  props.templateRows.forEach((r) => {
    aoa.push(props.columns.map((c) => r[c.prop] ?? ''))
  })
  const ws = XLSX.utils.aoa_to_sheet(aoa)
  ws['!cols'] = props.columns.map((c) => ({ wch: c.width || 16 }))
  // 文本列强制为文本格式，避免学号/手机号等长数字被 Excel 显示为科学计数法
  props.columns.forEach((c, idx) => {
    if (!c.text) return
    for (let r = 1; r < aoa.length; r++) {
      const cell = ws[XLSX.utils.encode_cell({ r, c: idx })]
      if (!cell) continue
      if (cell.t === 'n') {
        cell.t = 's'
        cell.v = String(cell.v)
      }
      cell.z = '@'
    }
  })
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, '导入数据')
  XLSX.writeFile(wb, `${props.title}模板.xlsx`)
  ElMessage.success('模板已下载，请按表头填写数据')
}

async function handleFileSelected(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  fileName.value = file.name
  try {
    const buf = await file.arrayBuffer()
    const wb = XLSX.read(buf, { type: 'array' })
    const ws = wb.Sheets[wb.SheetNames[0]]
    if (!ws) throw new Error('文件中没有工作表')
    const aoa: any[][] = XLSX.utils.sheet_to_json<any[]>(ws, {
      header: 1,
      defval: '',
      raw: false
    })

    const colMap = new Map<string, string>()
    props.columns.forEach((c) => {
      colMap.set(normalizeHeader(c.label), c.prop)
      ;(c.aliases || []).forEach((a) => colMap.set(normalizeHeader(a), c.prop))
    })

    // 在前几行中定位表头行
    let headerIdx = -1
    for (let i = 0; i < Math.min(aoa.length, 5); i++) {
      const row = aoa[i] || []
      const matched = row.filter((cell) => colMap.has(normalizeHeader(String(cell ?? '')))).length
      if (matched >= Math.ceil(props.columns.length / 2)) {
        headerIdx = i
        break
      }
    }
    if (headerIdx === -1) {
      // 表头识别失败：在前几行中找匹配最多的一行作为「疑似表头」，帮助用户对比差异
      let bestIdx = 0
      let bestMatched = 0
      for (let i = 0; i < Math.min(aoa.length, 5); i++) {
        const row = aoa[i] || []
        const m = row.filter((cell) => colMap.has(normalizeHeader(String(cell ?? '')))).length
        if (m > bestMatched) {
          bestMatched = m
          bestIdx = i
        }
      }
      const suspect = (aoa[bestIdx] || [])
        .map((c) => normalizeHeader(String(c ?? '')))
        .filter((h) => h !== '')
      const detected = suspect.slice(0, 12)
      const matchedCols = props.columns.filter(
        (c) =>
          suspect.includes(normalizeHeader(c.label)) ||
          (c.aliases || []).some((a) => suspect.includes(normalizeHeader(a)))
      )
      const missingRequired = props.columns.filter((c) => c.required && !matchedCols.includes(c))
      let msg = `未能识别表头（仅匹配到 ${bestMatched}/${props.columns.length} 列）`
      if (detected.length) msg += `；检测到表头：${detected.join('、')}`
      if (missingRequired.length) msg += `；缺少必填列：${missingRequired.map((c) => c.label).join('、')}`
      msg += '。请使用下载的模板填写数据'
      ElMessage({ message: msg, type: 'error', duration: 10000 })
      parsedRows.value = []
      return
    }

    const headers: string[] = (aoa[headerIdx] || []).map((c) =>
      normalizeHeader(String(c ?? ''))
    )

    parsedRows.value = []
    for (let r = headerIdx + 1; r < aoa.length; r++) {
      const row = aoa[r] || []
      if (!row.some((c) => String(c ?? '').trim() !== '')) continue // 跳过空行
      const data: Record<string, string> = {}
      headers.forEach((h, idx) => {
        const prop = colMap.get(h)
        if (prop) data[prop] = normalizeCellText(row[idx])
      })
      const item: ParseRow = { data, status: 'pending' }
      // 必填校验
      for (const c of props.columns) {
        if (c.required && !data[c.prop]) {
          item.error = `缺少${c.label}`
          break
        }
      }
      // 自定义转换/校验（名称→ID 映射等）
      if (!item.error && props.buildPayload) {
        try {
          const result = await props.buildPayload(data)
          if (result.error) item.error = result.error
          else item.payload = result.payload
        } catch (e: any) {
          item.error = e?.message || '数据校验失败'
        }
      }
      parsedRows.value.push(item)
    }

    if (!parsedRows.value.length) {
      ElMessage.warning('文件中没有有效数据行')
    } else {
      ElMessage.success(`解析成功，共 ${parsedRows.value.length} 行数据`)
    }
  } catch (e: any) {
    ElMessage.error('文件解析失败：' + (e?.message || '格式不支持'))
    parsedRows.value = []
  } finally {
    input.value = '' // 允许重复选择同一文件
  }
}

function statusLabel(row: ParseRow): string {
  if (row.error) return '校验失败'
  if (row.status === 'success') return '导入成功'
  if (row.status === 'fail') return '导入失败'
  return '待导入'
}

function statusTagType(row: ParseRow): 'danger' | 'success' | 'info' {
  if (row.error) return 'danger'
  if (row.status === 'success') return 'success'
  if (row.status === 'fail') return 'danger'
  return 'info'
}

/** 逐条导入 */
async function startImport() {
  if (importing.value) return
  importing.value = true
  hasImported.value = true
  successCount.value = 0
  let fail = 0
  for (const row of parsedRows.value) {
    if (row.status === 'success') continue
    if (row.error) {
      row.status = 'fail'
      row.failReason = row.error
      fail++
      continue
    }
    try {
      await props.submitOne(row.payload)
      row.status = 'success'
      successCount.value++
    } catch (e: any) {
      row.status = 'fail'
      row.failReason = e?.message || '导入失败'
      fail++
    }
  }
  importing.value = false
  if (successCount.value > 0) {
    ElMessage.success(`导入完成：成功 ${successCount.value} 条，失败 ${fail} 条`)
    emit('success')
  } else {
    ElMessage.warning(`导入完成：全部失败（共 ${fail} 条）`)
  }
}
</script>

<template>
  <el-dialog
    :model-value="modelValue"
    :title="title"
    width="880px"
    :close-on-click-modal="false"
    @update:model-value="emit('update:modelValue', $event)"
    @open="onOpen"
  >
    <el-alert type="info" :closable="false" show-icon style="margin-bottom: 12px">
      <template #title>
        请先下载模板填写数据，再上传 Excel 文件（.xlsx / .xls）。系统将自动解析校验，确认后批量导入。
      </template>
    </el-alert>

    <div class="import-toolbar">
      <el-button @click="downloadTemplate">
        <el-icon style="margin-right: 4px"><Download /></el-icon>下载模板
      </el-button>
      <el-button type="primary" :disabled="importing" @click="fileInputRef?.click()">
        <el-icon style="margin-right: 4px"><Upload /></el-icon>选择 Excel 文件
      </el-button>
      <input
        ref="fileInputRef"
        type="file"
        accept=".xlsx,.xls"
        style="display: none"
        @change="handleFileSelected"
      />
      <span v-if="fileName" class="file-name">{{ fileName }}</span>
    </div>

    <template v-if="parsedRows.length">
      <el-divider content-position="left">数据预览（共 {{ parsedRows.length }} 行）</el-divider>
      <el-table :data="parsedRows" border size="small" max-height="380" style="width: 100%">
        <el-table-column type="index" label="#" width="55" align="center" />
        <el-table-column
          v-for="col in columns"
          :key="col.prop"
          :prop="'data.' + col.prop"
          :label="col.label"
          min-width="110"
          show-overflow-tooltip
        />
        <el-table-column label="校验结果" width="130" align="center" fixed="right">
          <template #default="{ row }">
            <el-tooltip
              v-if="row.error || row.failReason"
              :content="row.error || row.failReason"
              placement="top"
            >
              <el-tag :type="statusTagType(row)" size="small">{{ statusLabel(row) }}</el-tag>
            </el-tooltip>
            <el-tag v-else :type="statusTagType(row)" size="small">{{ statusLabel(row) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-alert
        v-if="errorCount > 0 && !hasImported"
        :closable="false"
        type="warning"
        show-icon
        style="margin-top: 12px"
      >
        <template #title>
          {{ errorCount }} 行存在校验错误，导入时将被跳过，请修正后重新上传：
        </template>
        <ul class="error-detail">
          <li v-for="item in errorRows" :key="item.rowNo">
            第 {{ item.rowNo }} 行（{{ item.name }}）：{{ item.reason }}
          </li>
        </ul>
      </el-alert>
      <el-alert
        v-if="hasImported && failRows.length"
        :closable="false"
        type="error"
        show-icon
        style="margin-top: 12px"
      >
        <template #title>
          {{ failRows.length }} 行导入失败，原因如下：
        </template>
        <ul class="error-detail is-error">
          <li v-for="item in failRows" :key="item.rowNo">
            第 {{ item.rowNo }} 行（{{ item.name }}）：{{ item.reason }}
          </li>
        </ul>
      </el-alert>
    </template>

    <template #footer>
      <el-button @click="emit('update:modelValue', false)">关闭</el-button>
      <el-button
        type="primary"
        :disabled="!parsedRows.length || importing"
        :loading="importing"
        @click="startImport"
      >
        {{ importing ? `正在导入 ${successCount}/${parsedRows.length} ...` : '开始导入' }}
      </el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.import-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
}
.file-name {
  color: #909399;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 240px;
}
.error-detail {
  margin: 6px 0 0;
  padding-left: 18px;
  color: #e6a23c;
  font-size: 13px;
  line-height: 1.8;
  max-height: 120px;
  overflow-y: auto;
}
.error-detail.is-error {
  color: #f56c6c;
}
</style>
