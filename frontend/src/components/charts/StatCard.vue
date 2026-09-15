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
 * 统计卡片组件（看板顶部指标卡）
 * props:
 *  - title: 指标名称
 *  - value: 指标数值
 *  - suffix: 数值后缀（如 人 / %）
 *  - icon: Element Plus 图标组件名
 *  - color: 主题色（默认 #4f8cff）
 */
import { computed } from 'vue'

const props = defineProps<{
  title: string
  value: string | number
  suffix?: string
  icon?: string
  color?: string
}>()

// 主色调（未指定时使用默认蓝色）
const accent = computed(() => props.color || '#4f8cff')
</script>

<template>
  <el-card class="stat-card" shadow="hover">
    <div class="stat-card__body">
      <div class="stat-card__icon" :style="{ background: accent + '1a', color: accent }">
        <el-icon v-if="icon"><component :is="icon" /></el-icon>
      </div>
      <div class="stat-card__main">
        <div class="stat-card__title">{{ title }}</div>
        <div class="stat-card__value" :style="{ color: accent }">
          {{ value }}<span v-if="suffix" class="stat-card__suffix">{{ suffix }}</span>
        </div>
      </div>
    </div>
  </el-card>
</template>

<style scoped lang="scss">
.stat-card {
  border-radius: 14px;
  border: none;
  :deep(.el-card__body) { padding: 18px 20px; }
  &__body { display: flex; align-items: center; gap: 16px; }
  &__icon {
    width: 52px; height: 52px;
    border-radius: 12px;
    display: flex; align-items: center; justify-content: center;
    font-size: 24px;
    flex-shrink: 0;
  }
  &__title { font-size: 13px; color: #909399; }
  &__value {
    margin-top: 4px;
    font-size: 26px;
    font-weight: 700;
    line-height: 1.1;
  }
  &__suffix { font-size: 15px; margin-left: 3px; }
}
</style>
