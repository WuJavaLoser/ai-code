<template>
  <a-layout class="basic-layout">
    <!-- 头部导航 -->
    <a-layout-header class="layout-header">
      <GlobalHeader />
    </a-layout-header>
    
    <!-- 主体内容 -->
    <a-layout-content class="layout-content">
      <div class="content-wrapper">
        <RouterView />
      </div>
    </a-layout-content>
    
    <!-- 底部版权 -->
    <a-layout-footer class="layout-footer">
      <GlobalFooter />
    </a-layout-footer>
  </a-layout>
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
}

.layout-header {
  padding: 0;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.layout-content {
  flex: 1;
  padding: 12px;
  background: #f0f2f5;
}

.content-wrapper {
  max-width: 1400px;
  margin: 0 auto;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  min-height: calc(100vh - 200px);
  padding: 16px;
}

.layout-footer {
  background: #fff;
  border-top: 1px solid #e8e8e8;
  padding: 16px 24px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .layout-content {
    padding: 8px;
  }
  
  .content-wrapper {
    padding: 12px;
    border-radius: 0;
    box-shadow: none;
  }
  
  .layout-footer {
    padding: 12px 16px;
  }
}

@media (max-width: 576px) {
  .layout-content {
    padding: 4px;
  }
  
  .content-wrapper {
    padding: 8px;
  }
}
</style>
