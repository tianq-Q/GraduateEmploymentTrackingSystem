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
 * 轻量折线图组件（月度就业率趋势）
 * props: months(string[]) rates(number[])
 */
import { onMounted, onBeforeUnmount, ref, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps<{
  months: string[]
  rates: number[]
  title?: string
}>()

const el = ref<HTMLDivElement>()
let chart: echarts.ECharts | null = null

function render() {
  if (!el.value) return
  if (!chart) chart = echarts.init(el.value)
  const option: echarts.EChartsOption = {
    title: props.title ? { text: props.title, left: 'center', textStyle: { fontSize: 15 } } : undefined,
    tooltip: { trigger: 'axis', valueFormatter: (v) => `${v}%` },
    grid: { left: 50, right: 20, top: props.title ? 50 : 20, bottom: 40 },
    xAxis: { type: 'category', data: props.months, boundaryGap: false },
    yAxis: { type: 'value', axisLabel: { formatter: '{value}%' } },
    series: [{
      name: '就业率',
      type: 'line',
      smooth: true,
      data: props.rates,
      symbolSize: 6,
      lineStyle: { color: '#722ed1', width: 3 },
      itemStyle: { color: '#722ed1' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(114,46,209,0.25)' },
          { offset: 1, color: 'rgba(114,46,209,0.02)' },
        ]),
      },
    }],
  }
  chart.setOption(option, true)
}

function resize() { chart?.resize() }

/** 导出当前图表为 PNG dataURL */
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
watch(() => [props.months, props.rates, props.title], () => nextTick(render), { deep: true })
</script>

<template>
  <div ref="el" class="line-chart"></div>
</template>

<style scoped>
.line-chart { width: 100%; height: 320px; }
</style>
