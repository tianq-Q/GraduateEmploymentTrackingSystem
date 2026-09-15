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
  <div class="admin-review-page">
    <div class="page-header">
      <h2>审核管理</h2>
      <p class="subtitle">学校管理员复审已通过学院初审的就业记录，终审通过后数据生效</p>
    </div>

    <!-- 流程说明 -->
    <el-alert type="info" :closable="false" style="margin-bottom: 20px">
      <template #title>
        <div class="flow-desc">
          <el-tag type="warning" size="small">学生提交</el-tag>
          <span class="flow-arrow">→</span>
          <el-tag type="primary" size="small">学院初审</el-tag>
          <span class="flow-arrow">→</span>
          <el-tag type="success" size="small">学校复审</el-tag>
          <span class="flow-arrow">→</span>
          <el-tag type="info" size="small">数据生效</el-tag>
        </div>
      </template>
    </el-alert>

    <!-- 嵌入终审列表组件 -->
    <ReviewList />
  </div>
</template>

<script setup lang="ts">
/**
 * 审核管理页面（管理员）
 * - 顶部展示「学生提交 → 学院初审 → 学校复审 → 数据生效」流程说明
 * - 内嵌终审列表组件 ReviewList 完成复审操作
 */
import ReviewList from './review/ReviewList.vue'
</script>

<script lang="ts">
export default { name: 'AdminReview' }
</script>

<style scoped>
.admin-review-page {
  padding: 0;
}
.page-header {
  margin-bottom: 16px;
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
.flow-desc {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}
.flow-arrow {
  color: #909399;
  font-weight: 600;
}
</style>
