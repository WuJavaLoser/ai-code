<template>
  <div class="basic-layout">
    <!-- 头部导航 -->
    <header class="layout-header">
      <GlobalHeader />
    </header>

    <!-- 主体内容 -->
    <main class="layout-content">
      <div class="content-container">
        <RouterView />
      </div>
    </main>

    <!-- 底部版权 -->
    <footer class="layout-footer">
      <GlobalFooter />
    </footer>
  </div>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'
</script>

<style scoped>
.basic-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: transparent;
}

.layout-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: transparent;
  border-bottom: none;
}

.layout-content {
  flex: 1;
  position: relative;
  padding: 0;
}

.content-container {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
  min-height: calc(100vh - 140px);
}

.layout-footer {
  background: transparent;
  padding: 40px 0;
  margin-top: auto;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .content-container {
    max-width: 960px;
    padding: 0 20px;
  }
}

@media (max-width: 768px) {
  .content-container {
    padding: 0 16px;
  }

  .layout-footer {
    padding: 32px 0;
  }
}

@media (max-width: 640px) {
  .content-container {
    padding: 0 12px;
  }

  .layout-header {
    background: rgba(255, 255, 255, 0.95);
  }
}
</style>
