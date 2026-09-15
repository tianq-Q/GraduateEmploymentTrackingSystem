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
  <div class="dashboard">
    <!-- 顶部操作条 -->
    <div class="dashboard__toolbar">
      <div class="dashboard__title-wrap">
        <h2 class="dashboard__title">就业数据看板</h2>
        <el-tag class="freq-tag" type="info" effect="plain" size="small">每学期更新</el-tag>
        <el-tag class="freq-tag" effect="plain" size="small">当前角色：{{ role || '未登录' }}</el-tag>
      </div>
      <div class="dashboard__actions">
        <el-tag v-if="overview" type="info" effect="plain" class="sync-tag">
          <el-icon><Clock /></el-icon> 最近同步：{{ overview.syncTime }}
        </el-tag>
        <el-button class="toolbar-btn" :loading="syncing" @click="handleSync" v-if="canSync">
          <el-icon><Refresh /></el-icon> 同步刷新
        </el-button>
        <el-button class="toolbar-btn toolbar-btn--accent" :disabled="!canExport" @click="canExport && handleExport()"
          :title="canExport ? '' : '仅管理员/教师可导出'">
          <el-icon><Document /></el-icon> 导出报表
        </el-button>
        <el-button class="toolbar-btn toolbar-btn--accent" :disabled="!canExport" :loading="exportingCharts"
          @click="canExport && handleExportCharts()" :title="canExport ? '' : '仅管理员/教师可导出'">
          <el-icon><Picture /></el-icon> 导出图表
        </el-button>
      </div>
    </div>

    <!-- ① 就业率统计 数字卡片 -->
    <div class="stat-cards" v-loading="loadingOverview">
      <StatCard title="毕业生总数" :value="fmt(overview?.totalGraduates)" icon="UserFilled" color="#4f8cff" />
      <StatCard title="已落实去向" :value="fmt(overview?.employedCount)" icon="Briefcase" color="#52c41a" />
      <StatCard title="待就业" :value="fmt(overview?.waitingCount)" icon="WarningFilled" color="#fa8c16" />
      <StatCard title="总体就业率" :value="fmt(overview?.employmentRate)" suffix="%" icon="TrendCharts" color="#722ed1" />
    </div>

    <!-- ② 可视化图表 -->
    <div class="chart-grid">
      <el-card class="chart-card" shadow="hover">
        <div class="chart-header"><h3>院系就业率对比</h3><span class="chart-tag">柱状图</span></div>
        <BarChart ref="chartDept" title="各院系就业率(%)"
          :categories="deptCats" :series="[{ name: '就业率', data: deptRates }]" color="#4f8cff" />
      </el-card>

      <el-card class="chart-card" shadow="hover">
        <div class="chart-header"><h3>月度就业率趋势</h3><span class="chart-tag">折线图</span></div>
        <LineChartLite ref="chartTrend" :months="trendMonths" :rates="trendRates" />
      </el-card>
    </div>

    <div class="chart-grid">
      <el-card class="chart-card" shadow="hover">
        <div class="chart-header"><h3>就业去向分布</h3><span class="chart-tag">饼图</span></div>
        <PieChart ref="chartDest" title="就业去向" :data="destinationPie" />
      </el-card>

      <el-card class="chart-card" shadow="hover">
        <div class="chart-header"><h3>行业分布 Top10</h3><span class="chart-tag">横向柱状图</span></div>
        <BarChart ref="chartIndustry" title="行业分布" :horizontal="true"
          :categories="industryCats" :series="[{ name: '人数', data: industryCounts }]" color="#36cfc9" />
      </el-card>
    </div>

    <div class="chart-grid">
      <el-card class="chart-card" shadow="hover">
        <div class="chart-header"><h3>单位性质分布</h3><span class="chart-tag">饼图</span></div>
        <PieChart ref="chartCompany" title="单位性质" :data="companyTypePie" />
      </el-card>

      <!-- ④ 业务通知 -->
      <el-card class="chart-card" shadow="hover">
        <div class="chart-header">
          <h3>业务通知</h3>
          <div class="notify-filter">
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" type="danger">
              <el-button text size="small" @click="loadNotifications(false)">全部</el-button>
            </el-badge>
            <el-button text size="small" @click="loadNotifications(true)">未读</el-button>
          </div>
        </div>
        <el-scrollbar height="300px">
          <div v-if="notifications.length === 0" class="empty">暂无通知</div>
          <div v-for="n in notifications" :key="n.id" class="notify-item" :class="{ unread: n.isRead === 0 }">
            <div class="notify-title">
              <el-tag v-if="n.isRead === 0" size="small" type="danger" effect="dark">未读</el-tag>
              {{ n.title }}
            </div>
            <div class="notify-content">{{ n.content }}</div>
            <div class="notify-foot">
              <span>{{ n.createTime }}</span>
              <el-button v-if="n.isRead === 0" text type="primary" size="small" @click="readNotify(n.id)">标记已读</el-button>
            </div>
          </div>
        </el-scrollbar>
      </el-card>
    </div>

    <!-- ⑤ 操作日志 -->
    <el-card class="chart-card log-card" shadow="hover" style="margin-top: 4px">
      <div class="chart-header">
        <h3>操作日志</h3>
        <div>
          <el-select v-model="logQuery.module" placeholder="模块" clearable style="width: 140px; margin-right: 8px">
            <el-option label="数据看板" value="dashboard" />
            <el-option label="就业信息" value="employment" />
            <el-option label="审核" value="review" />
          </el-select>
          <el-button @click="loadLogs(1)">查询</el-button>
        </div>
      </div>
      <el-table :data="logList" stripe v-loading="loadingLogs">
        <el-table-column prop="module" label="模块" width="120" />
        <el-table-column prop="action" label="操作" width="120" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="operatorName" label="操作人" width="120" />
        <el-table-column prop="createTime" label="时间" width="180" />
      </el-table>
      <el-pagination
        v-model:current-page="logQuery.page"
        :page-size="logQuery.size"
        :total="logTotal"
        layout="total, prev, pager, next"
        style="margin-top: 12px; justify-content: flex-end"
        @current-change="(p: number) => loadLogs(p)"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { ComponentPublicInstance } from 'vue'
import StatCard from '@/components/charts/StatCard.vue'
import BarChart from '@/components/charts/BarChart.vue'
import PieChart from '@/components/charts/PieChart.vue'
import LineChartLite from '@/components/charts/LineChartLite.vue'
import { useAuthStore } from '@/stores/auth'
import {
  getOverview, getDepartmentCompare, getDestinationDistribution,
  getIndustryDistribution, getCompanyTypeDistribution, getMonthlyTrend,
  syncDashboard, exportDashboardCsv,
  getNotifications, getUnreadCount, markNotificationRead,
  getOperationLogs,
} from '@/api/dashboard'
import type {
  DashboardOverview, DepartmentCompare, DestinationChart,
  IndustryChart, TrendChart, NotificationItem, OperationLogItem,
} from '@/types/dashboard'

const auth = useAuthStore()

// 当前角色：兼容 ROLE_ 前缀与未登录空值
const role = computed(() =>
  ((auth.userInfo?.role || auth.role || '') + '').replace(/^ROLE_/, '').toUpperCase()
)
// 管理员（校管理员/系统管理员）可同步刷新；管理员+教师可导出
const canSync = computed(() => ['COLLEGE_ADMIN', 'SYSTEM_ADMIN'].includes(role.value))
const canExport = computed(() => ['COLLEGE_ADMIN', 'SYSTEM_ADMIN', 'TEACHER'].includes(role.value))

const loadingOverview = ref(false)
const overview = ref<DashboardOverview | null>(null)
const syncing = ref(false)

const deptCompare = ref<DepartmentCompare[]>([])
const destination = ref<DestinationChart[]>([])
const industry = ref<IndustryChart[]>([])
const companyType = ref<IndustryChart[]>([])
const trend = ref<TrendChart[]>([])

const notifications = ref<NotificationItem[]>([])
const unreadCount = ref(0)

const logList = ref<OperationLogItem[]>([])
const logTotal = ref(0)
const loadingLogs = ref(false)
const logQuery = reactive({ module: '', page: 1, size: 10 })

// 图表组件引用（用于批量导出图表图片）
type ChartComp = ComponentPublicInstance & { getImageDataURL: () => string | null }
const chartDept = ref<ChartComp>()
const chartTrend = ref<ChartComp>()
const chartDest = ref<ChartComp>()
const chartIndustry = ref<ChartComp>()
const chartCompany = ref<ChartComp>()
const exportingCharts = ref(false)

// ---- 派生图表数据 ----
const deptCats = computed(() => deptCompare.value.map(d => d.deptName))
const deptRates = computed(() => deptCompare.value.map(d => d.rate))
const destinationPie = computed(() => destination.value.map(d => ({ name: destLabel(d.destination), value: d.count })))
const industryCats = computed(() => industry.value.map(i => i.name))
const industryCounts = computed(() => industry.value.map(i => i.count))
const companyTypePie = computed(() => companyType.value.map(c => ({ name: c.name, value: c.count })))
const trendMonths = computed(() => trend.value.map(t => t.month))
const trendRates = computed(() => trend.value.map(t => t.rate))

function fmt(v?: number) {
  return v === undefined || v === null ? '-' : v.toLocaleString()
}

// 去向显示名（当前项目 destination 直接存中文，未知值原样透传）
const DEST_MAP: Record<string, string> = {
  EMPLOYED: '签约就业', FURTHER_STUDY: '升学深造', STUDY_ABROAD: '出国留学',
  ENTREPRENEURSHIP: '自主创业', FLEXIBLE: '灵活就业', MILITARY: '入伍',
  WAITING: '待就业',
}
function destLabel(code: string) { return DEST_MAP[code] || code }

// ---- 数据加载 ----
async function loadAll() {
  loadingOverview.value = true
  try {
    const [o, d, dist, ind, ct, tr] = await Promise.all([
      getOverview(), getDepartmentCompare(), getDestinationDistribution(),
      getIndustryDistribution(10), getCompanyTypeDistribution(), getMonthlyTrend(),
    ])
    overview.value = o.data
    deptCompare.value = d.data
    destination.value = dist.data
    industry.value = ind.data
    companyType.value = ct.data
    trend.value = tr.data
  } catch (e: any) {
    ElMessage.error(e?.message || '看板数据加载失败')
  } finally {
    loadingOverview.value = false
  }
}

async function loadNotifications(onlyUnread: boolean) {
  try {
    notifications.value = (await getNotifications(onlyUnread)).data
    unreadCount.value = (await getUnreadCount()).data
  } catch (e: any) {
    ElMessage.error(e?.message || '通知加载失败')
  }
}

async function readNotify(id: number) {
  await markNotificationRead(id)
  await loadNotifications(false)
}

async function loadLogs(page: number) {
  logQuery.page = page
  loadingLogs.value = true
  try {
    const res = await getOperationLogs({ module: logQuery.module || undefined, page, size: logQuery.size })
    logList.value = res.data.records
    logTotal.value = res.data.total
  } catch (e: any) {
    ElMessage.error(e?.message || '日志加载失败')
  } finally {
    loadingLogs.value = false
  }
}

async function handleSync() {
  syncing.value = true
  try {
    const r = await syncDashboard()
    ElMessage.success(r.data?.message || '同步完成')
    await loadAll()
  } catch (e: any) {
    ElMessage.error(e?.message || '同步失败')
  } finally {
    syncing.value = false
  }
}

async function handleExport() {
  try {
    const blob = await exportDashboardCsv()
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `就业看板统计报表_${new Date().toISOString().slice(0, 10)}.csv`
    a.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('报表已导出')
  } catch (e: any) {
    ElMessage.error(e?.message || '导出失败')
  }
}

/**
 * 导出图表：将所有 ECharts 图表逐一导出为 PNG 图片并下载
 */
async function handleExportCharts() {
  exportingCharts.value = true
  try {
    const charts: { ref: typeof chartDept, name: string }[] = [
      { ref: chartDept, name: '院系就业率对比' },
      { ref: chartTrend, name: '月度就业率趋势' },
      { ref: chartDest, name: '就业去向分布' },
      { ref: chartIndustry, name: '行业分布Top10' },
      { ref: chartCompany, name: '单位性质分布' },
    ]
    let exported = 0
    for (const c of charts) {
      const comp = c.ref.value
      const url = comp?.getImageDataURL()
      if (!url) continue
      const a = document.createElement('a')
      a.href = url
      a.download = `${c.name}_${new Date().toISOString().slice(0, 10)}.png`
      a.click()
      exported++
      await new Promise(r => setTimeout(r, 300)) // 间隔避免浏览器拦截批量下载
    }
    if (exported === 0) {
      ElMessage.warning('当前无图表可导出，请稍候数据加载完成')
    } else {
      ElMessage.success(`已导出 ${exported} 张图表`)
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '图表导出失败')
  } finally {
    exportingCharts.value = false
  }
}

onMounted(() => {
  loadAll()
  loadNotifications(false)
  loadLogs(1)
})
</script>

<style scoped lang="scss">
.dashboard {
  max-width: 1440px;
  margin: 0 auto;
  padding: 16px;
}

/* 顶部操作条 */
.dashboard__toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 14px;
  margin-bottom: 18px;
  padding: 18px 22px;
  background: linear-gradient(135deg, #2563eb 0%, #7c3aed 100%);
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(37, 99, 235, 0.22);
  color: #fff;
}
.dashboard__title-wrap { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }
.dashboard__title { font-size: 22px; font-weight: 700; margin: 0; letter-spacing: 1px; }
.dashboard__actions { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }
.toolbar-btn {
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.14) !important;
  border-color: rgba(255, 255, 255, 0.35) !important;
  color: #fff !important;
  transition: all 0.2s ease;
  &:hover {
    background: rgba(255, 255, 255, 0.24) !important;
    border-color: rgba(255, 255, 255, 0.55) !important;
  }
  &:active { background: rgba(255, 255, 255, 0.08) !important; }
}
/* 导出按钮：醒目高亮，与整体蓝紫主题协调 */
.toolbar-btn--accent {
  background: #fff !important;
  border-color: #fff !important;
  color: #4f3cc9 !important;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.18);
  &:hover {
    background: #f2eeff !important;
    border-color: #f2eeff !important;
    color: #3a2bb0 !important;
  }
  &:active { background: #e6e0ff !important; }
}
.sync-tag, .freq-tag {
  display: inline-flex; align-items: center; gap: 4px;
  background: rgba(255, 255, 255, 0.16) !important;
  border-color: rgba(255, 255, 255, 0.28) !important;
  color: #fff !important;
}

/* 统计卡片网格 */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px;
  margin-bottom: 18px;
}

/* 图表网格 */
.chart-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
  margin-bottom: 18px;
}
.chart-card {
  border-radius: 16px;
  border: none;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(31, 41, 55, 0.05);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  &:hover { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(37, 99, 235, 0.10); }
  :deep(.el-card__body) { padding: 18px 20px; }
}
.chart-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f2f4f7;
  h3 {
    font-size: 16px; font-weight: 600; margin: 0; color: #1f2937;
    &::before {
      content: ''; display: inline-block; width: 4px; height: 14px;
      margin-right: 8px; border-radius: 2px;
      background: linear-gradient(180deg, #2563eb, #7c3aed);
      vertical-align: -2px;
    }
  }
}
.chart-tag {
  font-size: 11px;
  color: #5b6b8c;
  background: #f2f5fa;
  padding: 2px 8px;
  border-radius: 10px;
}

/* 通知 */
.notify-filter { display: flex; align-items: center; gap: 6px; }
.notify-item {
  padding: 12px 14px;
  border-radius: 10px;
  border: 1px solid #f0f0f0;
  margin-bottom: 10px;
  transition: all 0.2s;
  &.unread { background: #f7faff; border-color: #dbe9ff; }
  &:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
}
.notify-title { font-size: 14px; font-weight: 600; color: #303133; display: flex; align-items: center; gap: 6px; }
.notify-content { font-size: 13px; color: #606266; margin: 6px 0; line-height: 1.5; }
.notify-foot { display: flex; justify-content: space-between; align-items: center; font-size: 12px; color: #909399; }
.empty { text-align: center; color: #c0c4cc; padding: 40px 0; }

/* 操作日志卡片 */
.log-card :deep(.el-card__body) { padding: 18px 20px; }

@media (max-width: 1200px) {
  .stat-cards { grid-template-columns: repeat(2, 1fr); }
  .chart-grid { grid-template-columns: 1fr; }
}
</style>
