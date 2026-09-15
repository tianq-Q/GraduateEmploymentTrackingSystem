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
    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card blue">
        <div class="card-inner">
          <div class="card-info">
            <div class="card-label">毕业生总数</div>
            <div class="card-value">2,486</div>
            <div class="card-trend up">
              <el-icon><Top /></el-icon>
              <span>较去年 +12%</span>
            </div>
          </div>
          <div class="card-icon">
            <div class="icon-bg">
              <el-icon :size="28"><School /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <div class="stat-card green">
        <div class="card-inner">
          <div class="card-info">
            <div class="card-label">已就业人数</div>
            <div class="card-value">2,103</div>
            <div class="card-trend up">
              <el-icon><Top /></el-icon>
              <span>较去年 +8%</span>
            </div>
          </div>
          <div class="card-icon">
            <div class="icon-bg">
              <el-icon :size="28"><Briefcase /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <div class="stat-card orange">
        <div class="card-inner">
          <div class="card-info">
            <div class="card-label">就业率</div>
            <div class="card-value">84.6%</div>
            <div class="card-trend up">
              <el-icon><Top /></el-icon>
              <span>较去年 +3.2%</span>
            </div>
          </div>
          <div class="card-icon">
            <div class="icon-bg">
              <el-icon :size="28"><TrendCharts /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <div class="stat-card red">
        <div class="card-inner">
          <div class="card-info">
            <div class="card-label">待审核信息</div>
            <div class="card-value">56</div>
            <div class="card-trend down">
              <el-icon><Bottom /></el-icon>
              <span>较上周 -25%</span>
            </div>
          </div>
          <div class="card-icon">
            <div class="icon-bg">
              <el-icon :size="28"><DocumentChecked /></el-icon>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="chart-grid">
      <!-- 就业趋势 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>就业趋势</h3>
          <span class="chart-subtitle">近12个月</span>
        </div>
        <div class="chart-body">
          <v-chart :option="trendOption" autoresize />
        </div>
      </div>

      <!-- 院系分布 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>各院系就业统计</h3>
          <span class="chart-subtitle">本年度</span>
        </div>
        <div class="chart-body">
          <v-chart :option="departmentOption" autoresize />
        </div>
      </div>
    </div>

    <!-- 底部区域 -->
    <div class="bottom-grid">
      <!-- 就业去向分布 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>就业去向分布</h3>
        </div>
        <div class="chart-body pie-body">
          <v-chart :option="pieOption" autoresize />
        </div>
      </div>

      <!-- 最新动态 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>最新动态</h3>
        </div>
        <div class="timeline-list">
          <div class="timeline-item" v-for="item in activities" :key="item.id">
            <div class="timeline-dot" :class="item.type"></div>
            <div class="timeline-content">
              <p class="timeline-text">{{ item.content }}</p>
              <span class="timeline-time">{{ item.time }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 就业数据看板页面（管理员视图）
 * - 顶部指标卡：毕业生总数 / 已就业 / 就业率 / 待审核
 * - ECharts 图表：就业率趋势折线图、院系毕业生/已就业柱状图、就业去向环形图
 */
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart, PieChart } from 'echarts/charts'
import {
  TitleComponent, TooltipComponent, LegendComponent, GridComponent,
} from 'echarts/components'

// 注册 ECharts 渲染器与所需图表组件
use([CanvasRenderer, LineChart, BarChart, PieChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

// 就业趋势
const trendOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    backgroundColor: '#fff',
    borderColor: '#e5e7eb',
    borderWidth: 1,
    textStyle: { color: '#333', fontSize: 12 },
    boxShadow: '0 2px 8px rgba(0,0,0,0.08)',
  },
  grid: { top: 10, left: 0, right: 10, bottom: 0, containLabel: true },
  xAxis: {
    type: 'category',
    data: ['8月', '9月', '10月', '11月', '12月', '1月', '2月', '3月', '4月', '5月', '6月', '7月'],
    axisLine: { lineStyle: { color: '#e5e7eb' } },
    axisTick: { show: false },
    axisLabel: { color: '#999', fontSize: 12 },
  },
  yAxis: {
    type: 'value',
    min: 60,
    max: 100,
    splitLine: { lineStyle: { color: '#f0f0f0' } },
    axisLabel: { color: '#999', fontSize: 12, formatter: '{value}%' },
  },
  series: [{
    data: [72, 75, 78, 76, 80, 79, 81, 83, 85, 84, 86, 84.6],
    type: 'line',
    smooth: true,
    lineStyle: { color: '#4f8cff', width: 3 },
    itemStyle: { color: '#4f8cff' },
    symbol: 'circle',
    symbolSize: 6,
    areaStyle: {
      color: {
        type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
        colorStops: [
          { offset: 0, color: 'rgba(79,140,255,0.25)' },
          { offset: 1, color: 'rgba(79,140,255,0.02)' },
        ],
      },
    },
  }],
}))

// 院系统计
const departmentOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    backgroundColor: '#fff',
    borderColor: '#e5e7eb',
    borderWidth: 1,
    textStyle: { color: '#333', fontSize: 12 },
    boxShadow: '0 2px 8px rgba(0,0,0,0.08)',
  },
  grid: { top: 10, left: 0, right: 10, bottom: 0, containLabel: true },
  xAxis: {
    type: 'category',
    data: ['计科', '电子', '机械', '经管', '文法', '艺术', '理学'],
    axisLine: { lineStyle: { color: '#e5e7eb' } },
    axisTick: { show: false },
    axisLabel: { color: '#999', fontSize: 12 },
  },
  yAxis: {
    type: 'value',
    splitLine: { lineStyle: { color: '#f0f0f0' } },
    axisLabel: { color: '#999', fontSize: 12 },
  },
  series: [
    {
      name: '毕业生',
      type: 'bar',
      data: [420, 380, 350, 400, 280, 260, 396],
      barWidth: 16,
      itemStyle: {
        color: '#b4c8e8',
        borderRadius: [4, 4, 0, 0],
      },
    },
    {
      name: '已就业',
      type: 'bar',
      data: [378, 326, 280, 340, 220, 205, 354],
      barWidth: 16,
      itemStyle: {
        color: '#4f8cff',
        borderRadius: [4, 4, 0, 0],
      },
    },
  ],
}))

// 就业去向饼图
const pieOption = computed(() => ({
  tooltip: {
    trigger: 'item',
    backgroundColor: '#fff',
    borderColor: '#e5e7eb',
    textStyle: { color: '#333', fontSize: 12 },
  },
  legend: { bottom: 0, textStyle: { color: '#666', fontSize: 12 } },
  series: [{
    type: 'pie',
    radius: ['55%', '78%'],
    center: ['50%', '45%'],
    avoidLabelOverlap: false,
    itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 },
    label: { show: false },
    data: [
      { value: 735, name: '签约就业' },
      { value: 420, name: '升学深造' },
      { value: 315, name: '灵活就业' },
      { value: 290, name: '自主创业' },
      { value: 210, name: '出国留学' },
      { value: 133, name: '待就业' },
    ],
    color: ['#4f8cff', '#52c41a', '#faad14', '#ff7a45', '#36cfc9', '#d9d9d9'],
  }],
}))

const activities = [
  { id: 1, type: 'success', content: '计算机学院 张三 提交就业信息（腾讯科技）', time: '10分钟前' },
  { id: 2, type: 'primary', content: '审核通过：电子工程学院 李四 就业去向', time: '28分钟前' },
  { id: 3, type: 'warning', content: '待审核：机械工程学院 王五 提交三方协议', time: '1小时前' },
  { id: 4, type: 'info', content: '系统通知：2026届毕业生就业信息开始填报', time: '2小时前' },
  { id: 5, type: 'success', content: '经济管理学院就业率突破 85%', time: '4小时前' },
  { id: 6, type: 'primary', content: '艺术设计学院 赵六 确认创业信息', time: '6小时前' },
]
</script>

<style scoped lang="scss">
.dashboard {
  max-width: 1400px;
}

/* 统计卡片 */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: default;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }
}

.card-inner {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-label {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-bottom: 8px;
}

.card-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text);
  margin-bottom: 6px;
}

.card-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;

  &.up { color: #52c41a; }
  &.down { color: #ff4d4f; }
}

.icon-bg {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-card.blue .icon-bg { background: #e8f0fe; color: #4f8cff; }
.stat-card.green .icon-bg { background: #e6f7e9; color: #52c41a; }
.stat-card.orange .icon-bg { background: #fff7e6; color: #fa8c16; }
.stat-card.red .icon-bg { background: #fff1f0; color: #ff4d4f; }

/* 图表区域 */
.chart-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.bottom-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.chart-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.chart-header {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 16px;

  h3 {
    font-size: 16px;
    font-weight: 600;
    color: var(--color-text);
  }
}

.chart-subtitle {
  font-size: 12px;
  color: #999;
}

.chart-body {
  height: 300px;
}

.pie-body {
  height: 280px;
}

/* 动态时间线 */
.timeline-list {
  padding-top: 4px;
}

.timeline-item {
  display: flex;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;

  &:last-child {
    border-bottom: none;
  }
}

.timeline-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-top: 6px;
  flex-shrink: 0;

  &.success { background: #52c41a; }
  &.primary { background: #4f8cff; }
  &.warning { background: #faad14; }
  &.info { background: #909399; }
}

.timeline-text {
  font-size: 13px;
  color: var(--color-text);
  line-height: 1.5;
}

.timeline-time {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

@media (max-width: 1200px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  .chart-grid,
  .bottom-grid {
    grid-template-columns: 1fr;
  }
}
</style>
