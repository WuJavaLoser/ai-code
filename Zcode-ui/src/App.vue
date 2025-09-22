<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'

import { healthCheck } from '@/api/healthController.ts'

healthCheck().then((res) => {
  console.log(res)
})

</script>

<template>
  <BasicLayout />
</template>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  line-height: 1.6;
  color: #333;
  background-color: #f0f2f5;
}

#app {
  height: 100%;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 链接样式 */
a {
  color: #1890ff;
  text-decoration: none;
  transition: color 0.3s ease;
}

a:hover {
  color: #40a9ff;
}

/* 响应式图片 */
img {
  max-width: 100%;
  height: auto;
}
</style>
