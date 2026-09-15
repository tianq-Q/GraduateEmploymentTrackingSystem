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

<script setup lang="ts">
/**
 * 通用柱状图组件
 * props:
 *  - title: 标题
 *  - categories: X轴类目数组
 *  - series: [{ name, data }]
 *  - horizontal: 是否横向
 */
import { onMounted, onBeforeUnmount, ref, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps<{
  title?: string
  categories: string[]
  series: { name: string; data: number[] }[]
  horizontal?: boolean
  color?: string
}>()

const el = ref<HTMLDivElement>()
let chart: echarts.ECharts | null = null

function render() {
  if (!el.value) return
  if (!chart) chart = echarts.init(el.value)
  const themeColor = props.color || '#409eff'
  const truncate = (v: string, n = 5) => (v.length > n ? v.slice(0, n - 1) + '…' : v)
  const option: echarts.EChartsOption = {
    title: props.title ? { text: props.title, left: 'center', textStyle: { fontSize: 15, color: '#303133' } } : undefined,
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.96)',
      borderColor: '#ebeef5',
      textStyle: { color: '#303133' }
    },
    grid: { left: 50, right: 24, top: props.title ? 54 : 24, bottom: props.horizontal ? 32 : 56 },
    xAxis: props.horizontal
      ? { type: 'value', splitLine: { lineStyle: { type: 'dashed', color: '#f0f0f0' } } }
      : {
          type: 'category',
          data: props.categories,
          axisLine: { lineStyle: { color: '#e4e7ed' } },
          axisTick: { alignWithLabel: true, lineStyle: { color: '#e4e7ed' } },
          axisLabel: {
            interval: 0,
            rotate: 22,
            color: '#606266',
            fontSize: 11,
            hideOverlap: true,
            formatter: (value: string) => truncate(value, 5)
          }
        },
    yAxis: props.horizontal
      ? {
          type: 'category',
          data: props.categories,
          axisLine: { show: false },
          axisTick: { show: false },
          axisLabel: { color: '#606266', fontSize: 11, formatter: (value: string) => truncate(value, 6) }
        }
      : {
          type: 'value',
          splitLine: { lineStyle: { type: 'dashed', color: '#f0f0f0' } },
          axisLabel: { color: '#909399' }
        },
    series: props.series.map(s => ({
      name: s.name,
      type: 'bar',
      data: s.data,
      itemStyle: {
        color: themeColor,
        borderRadius: props.horizontal ? [0, 6, 6, 0] : [6, 6, 0, 0],
        shadowColor: 'rgba(0,0,0,0.08)',
        shadowBlur: 6,
        shadowOffsetY: 3
      },
      barMaxWidth: 44,
      label: {
        show: true,
        position: props.horizontal ? 'right' : 'top',
        formatter: props.horizontal ? '{c}' : '{c}%',
        fontSize: 11,
        color: '#606266'
      }
    }))
  }
  chart.setOption(option, true)
}

function resize() { chart?.resize() }

/** 导出当前图表为 PNG dataURL（供看板批量导出图表使用） */
function getImageDataURL(): string | null {
  if (!chart) return null
  return chart.getDataURL({ type: 'png', pixelRatio: 2, backgroundColor: '#fff' })
}

defineExpose({ getImageDataURL })

onMounted(() => { nextTick(render); window.addEventListener('resize', resize) })
onBeforeUnmount(() => {
  window.removeEventListener('resize', resize)
  chart?.dispose()
  chart = null
})
watch(() => [props.categories, props.series, props.title], () => nextTick(render), { deep: true })
</script>

<template>
  <div ref="el" class="bar-chart"></div>
</template>

<style scoped>
.bar-chart { width: 100%; height: 320px; }
</style>
