<template>
  <div class="home-container">
    <div class="home-content">
      <h1 class="welcome-title" v-html="welcomeTitle"></h1>
      <p class="welcome-subtitle">智能编程助手，让代码开发更简单</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useUserLoginStore } from '@/stores/UserLogin'
import { isLoggedIn } from '@/access'

const userLoginStore = useUserLoginStore()

// 获取用户显示名称
const getUserDisplayName = () => {
  const userName = userLoginStore.userlogin.userName
  // 当昵称为 null、undefined、空字符串、"null"字符串或"未登录"时，显示默认文本"鸡贼"
  if (!userName || userName.trim() === '' || userName === '未登录' || userName === 'null') {
    return '鸡贼'
  }
  return userName
}

// 动态欢迎标题
const welcomeTitle = computed(() => {
  if (isLoggedIn()) {
    const displayName = getUserDisplayName()
    return `欢迎 <span class="user-name-highlight">${displayName}</span> 来到代码魔方`
  }
  return '欢迎来到代码魔方'
})
</script>

<style scoped>
.home-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 140px);
  padding: 40px 20px;
}

.home-content {
  text-align: center;
  max-width: 800px;
}

.welcome-title {
  font-size: 3rem;
  font-weight: 700;
  color: #262626;
  margin-bottom: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.welcome-title :deep(.user-name-highlight) {
  display: inline-block;
  padding: 4px 12px;
  margin: 0 8px;
  background: linear-gradient(135deg, #ff9a56 0%, #ff6b6b 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  border-radius: 8px;
  font-weight: 800;
  position: relative;
  animation: glow 2s ease-in-out infinite alternate;
}

.welcome-title :deep(.user-name-highlight)::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 154, 86, 0.1) 0%, rgba(255, 107, 107, 0.1) 100%);
  border-radius: 8px;
  z-index: -1;
}

@keyframes glow {
  from {
    filter: brightness(1) saturate(1);
  }
  to {
    filter: brightness(1.1) saturate(1.2);
  }
}

.welcome-subtitle {
  font-size: 1.25rem;
  color: #8c8c8c;
  margin-bottom: 32px;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .welcome-title {
    font-size: 2.5rem;
  }

  .welcome-title :deep(.user-name-highlight) {
    padding: 3px 10px;
    margin: 0 6px;
  }

  .welcome-subtitle {
    font-size: 1.1rem;
  }
}

@media (max-width: 480px) {
  .welcome-title {
    font-size: 2rem;
  }

  .welcome-title :deep(.user-name-highlight) {
    padding: 2px 8px;
    margin: 0 4px;
    font-size: 0.95em;
  }

  .welcome-subtitle {
    font-size: 1rem;
  }
}
</style>
