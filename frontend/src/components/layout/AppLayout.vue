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

<!--
  整体布局组件（Layout）
  - 左侧：侧边栏菜单（Sidebar），支持折叠/展开
  - 右侧：顶部导航栏（Navbar）+ 路由内容区域（router-view）
  - 折叠状态由全局 appStore.sidebarCollapsed 统一管理
-->
<template>
  <div class="app-layout" :class="{ collapsed: appStore.sidebarCollapsed }">
    <!-- 侧边栏菜单 -->
    <Sidebar :is-collapse="appStore.sidebarCollapsed" />
    <div class="main-wrapper">
      <!-- 顶部导航栏 -->
      <Navbar :is-collapse="appStore.sidebarCollapsed" />
      <main class="main-content">
        <!-- 路由页面切换（带淡入淡出过渡动画） -->
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAppStore } from '@/stores/app'
import Sidebar from './Sidebar.vue'
import Navbar from './Navbar.vue'

// 全局应用状态（持有侧边栏折叠标志）
const appStore = useAppStore()
</script>

<style scoped lang="scss">
.app-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  transition: margin-left 0.3s;
}

.main-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: var(--color-bg);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
