<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
// 移除重复的用户信息获取逻辑，已在 access.ts 中统一处理
</script>

<template>
  <a-config-provider
    :theme="{
      token: {
        colorPrimary: '#1890ff',
      },
    }"
  >
    <BasicLayout />
  </a-config-provider>
</template>

<style>
:root {
  --radius: 0.625rem;
  --background: 255 255 255;
  --foreground: 15 23 42;
  --card: 255 255 255;
  --card-foreground: 15 23 42;
  --popover: 255 255 255;
  --popover-foreground: 15 23 42;
  --primary: 37 99 235;
  --primary-foreground: 248 250 252;
  --secondary: 241 245 249;
  --secondary-foreground: 15 23 42;
  --muted: 241 245 249;
  --muted-foreground: 100 116 139;
  --accent: 241 245 249;
  --accent-foreground: 15 23 42;
  --destructive: 239 68 68;
  --border: 226 232 240;
  --input: 226 232 240;
  --ring: 37 99 235;
  --chart-1: 220 38 127;
  --chart-2: 59 130 246;
  --chart-3: 34 197 94;
  --chart-4: 251 191 36;
  --chart-5: 249 115 22;
  --sidebar: 248 250 252;
  --sidebar-foreground: 15 23 42;
  --sidebar-primary: 37 99 235;
  --sidebar-primary-foreground: 248 250 252;
  --sidebar-accent: 241 245 249;
  --sidebar-accent-foreground: 15 23 42;
  --sidebar-border: 226 232 240;
  --sidebar-ring: 37 99 235;
  --nocode-dark-background: rgb(24, 24, 24);
  --nocode-dark-border-color: rgb(43, 43, 43);
  --press: rgba(41, 98, 255, 0.05);
  --tw-border-spacing-x: 0;
  --tw-border-spacing-y: 0;
  --tw-translate-x: 0;
  --tw-translate-y: 0;
  --tw-rotate: 0;
  --tw-skew-x: 0;
  --tw-skew-y: 0;
  --tw-scale-x: 1;
  --tw-scale-y: 1;
  --tw-pan-x: ;
  --tw-pan-y: ;
  --tw-pinch-zoom: ;
  --tw-scroll-snap-strictness: proximity;
  --tw-gradient-from-position: ;
  --tw-gradient-via-position: ;
  --tw-gradient-to-position: ;
  --tw-ordinal: ;
  --tw-slashed-zero: ;
  --tw-numeric-figure: ;
  --tw-numeric-spacing: ;
  --tw-numeric-fraction: ;
  --tw-ring-inset: ;
  --tw-ring-offset-width: 0px;
  --tw-ring-offset-color: #fff;
  --tw-ring-color: rgb(59 130 246 / 0.5);
  --tw-ring-offset-shadow: 0 0 #0000;
  --tw-ring-shadow: 0 0 #0000;
  --tw-shadow: 0 0 #0000;
  --tw-shadow-colored: 0 0 #0000;
  --tw-blur: ;
  --tw-brightness: ;
  --tw-contrast: ;
  --tw-grayscale: ;
  --tw-hue-rotate: ;
  --tw-invert: ;
  --tw-saturate: ;
  --tw-sepia: ;
  --tw-drop-shadow: ;
  --tw-backdrop-blur: ;
  --tw-backdrop-brightness: ;
  --tw-backdrop-contrast: ;
  --tw-backdrop-grayscale: ;
  --tw-backdrop-hue-rotate: ;
  --tw-backdrop-invert: ;
  --tw-backdrop-opacity: ;
  --tw-backdrop-saturate: ;
  --tw-backdrop-sepia: ;
  --tw-contain-size: ;
  --tw-contain-layout: ;
  --tw-contain-paint: ;
  --tw-content: '';
}

html,
:host {
  line-height: 1.5;
  -webkit-text-size-adjust: 100%;
  -moz-tab-size: 4;
  -o-tab-size: 4;
  tab-size: 4;
  font-family:
    ui-sans-serif,
    system-ui,
    sans-serif,
    'Apple Color Emoji',
    'Segoe UI Emoji',
    Segoe UI Symbol,
    'Noto Color Emoji';
  font-feature-settings: normal;
  font-variation-settings: normal;
  -webkit-tap-highlight-color: transparent;
}

* {
  border-color: rgb(var(--border));
  outline-color: rgb(var(--ring) / 0.5);
}

*,
:before,
:after {
  box-sizing: border-box;
  border-width: 0;
  border-style: solid;
  border-color: #e5e7eb;
}

body {
  background: #fffff0;
  color: rgb(var(--foreground));
  min-height: 100vh;
  position: relative;
  margin: 0;
  padding: 0;
}

#app {
  min-height: 100vh;
  position: relative;
}

/* 全局消息提示上方居中配置 - 提高层级 */
.ant-message {
  position: fixed !important;
  top: 24px !important; /* 距离顶部24px，更符合现代UI设计 */
  left: 50% !important;
  transform: translateX(-50%) !important; /* 只需要水平居中 */
  z-index: 10000 !important; /* 提高层级确保显示在最上层 */
  pointer-events: none;
  width: auto !important;
  margin: 0 !important;
}

.ant-message .ant-message-notice {
  text-align: center !important;
  margin: 8px auto !important;
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
  pointer-events: auto;
}

.ant-message .ant-message-notice-content {
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  gap: 8px !important;
  padding: 12px 24px !important;
  border-radius: 8px !important;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2) !important;
  background: #fff !important;
  border: 1px solid #d9d9d9 !important;
  font-weight: 500 !important;
  font-size: 14px !important;
  min-width: 120px !important;
  white-space: nowrap !important;
}

/* 成功消息样式 */
.ant-message-success .ant-message-notice-content {
  border-color: #52c41a !important;
  background: #f6ffed !important;
  color: #52c41a !important;
}

/* 错误消息样式 */
.ant-message-error .ant-message-notice-content {
  border-color: #ff4d4f !important;
  background: #fff2f0 !important;
  color: #ff4d4f !important;
}

/* 警告消息样式 */
.ant-message-warning .ant-message-notice-content {
  border-color: #faad14 !important;
  background: #fffbe6 !important;
  color: #faad14 !important;
}

/* 信息消息样式 */
.ant-message-info .ant-message-notice-content {
  border-color: #1890ff !important;
  background: #f0f9ff !important;
  color: #1890ff !important;
}

/* 加载消息样式 */
.ant-message-loading .ant-message-notice-content {
  border-color: #1890ff !important;
  background: #f0f9ff !important;
  color: #1890ff !important;
}

/* 消息动画优化 - 从上方滑入 */
.ant-message .ant-message-notice {
  animation: messageSlideInFromTop 0.4s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

@keyframes messageSlideInFromTop {
  0% {
    opacity: 0;
    transform: translateY(-30px) scale(0.95);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 消息退出动画 */
.ant-message .ant-message-notice.ant-message-notice-close {
  animation: messageSlideOutToTop 0.3s ease-in forwards !important;
}

@keyframes messageSlideOutToTop {
  0% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
  100% {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
}

/* 确保在移动端也能正确显示 */
@media (max-width: 768px) {
  .ant-message {
    top: 20px !important; /* 移动端距离顶部20px */
  }

  .ant-message .ant-message-notice-content {
    padding: 10px 20px !important;
    font-size: 13px !important;
    min-width: 100px !important;
    max-width: calc(100vw - 40px) !important; /* 限制最大宽度，避免超出屏幕 */
  }
}
</style>
