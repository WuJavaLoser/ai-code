<template>
  <div class="global-header">
    <div class="header-container">
      <!-- Logo和标题 -->
      <div class="logo-section">
        <img
          src="@/assets/logo.ico"
          alt="代码魔方"
          class="logo"
        />
        <h1 class="site-title">代码魔方</h1>
      </div>

      <!-- 导航菜单 -->
      <div class="nav-section">
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="horizontal"
          class="nav-menu"
          :items="menuItems"
          @click="handleMenuClick"
        />
      </div>

      <!-- 用户操作区 -->
      <div class="user-section">
        <a-button type="primary" @click="handleLogin">
          登录
        </a-button>
      </div>

      <!-- 移动端菜单按钮 -->
      <div class="mobile-menu-btn">
        <a-button
          type="text"
          @click="toggleMobileMenu"
          :icon="h(MenuOutlined)"
        />
      </div>
    </div>

    <!-- 移动端菜单抽屉 -->
    <a-drawer
      v-model:open="mobileMenuVisible"
      title="菜单"
      placement="right"
      :width="280"
      class="mobile-drawer"
    >
      <a-menu
        v-model:selectedKeys="selectedKeys"
        mode="vertical"
        :items="menuItems"
        @click="handleMobileMenuClick"
      />
      <div class="mobile-user-section">
        <a-button type="primary" block @click="handleLogin">
          登录
        </a-button>
      </div>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, h, watch } from 'vue'
import { useRouter } from 'vue-router'
import { MenuOutlined } from '@ant-design/icons-vue'
import type { MenuProps } from 'ant-design-vue'

const router = useRouter()
const selectedKeys = ref<string[]>([router.currentRoute.value.path])
const mobileMenuVisible = ref(false)

// 菜单配置
const menuItems: MenuProps['items'] = [
  {
    key: '/',
    label: '首页',
  },
  {
    key: '/about',
    label: '关于',
  },
  {
    key: '/projects',
    label: '项目',
  },
  {
    key: '/docs',
    label: '文档',
  },
]

// 处理菜单点击
const handleMenuClick: MenuProps['onClick'] = (e) => {
  const key = e.key as string
  selectedKeys.value = [key]
  console.log('菜单点击:', key)

  // 跳转到对应页面
  if (key.startsWith('/')) {
    if (key === '/projects' || key === '/docs') {
      // 这些页面还未创建，显示提示信息
      console.log(`${key === '/projects' ? '项目' : '文档'}页面待开发`)
    } else {
      router.push(key)
    }
  }
}

// 处理移动端菜单点击
const handleMobileMenuClick: MenuProps['onClick'] = (e) => {
  handleMenuClick(e)
  mobileMenuVisible.value = false
}

// 切换移动端菜单
const toggleMobileMenu = () => {
  mobileMenuVisible.value = !mobileMenuVisible.value
}

// 处理登录
const handleLogin = () => {
  console.log('登录功能待实现')
  // 这里可以跳转到登录页面或打开登录弹窗
}

// 监听路由变化，更新菜单选中状态
watch(
  () => router.currentRoute.value.path,
  (newPath) => {
    selectedKeys.value = [newPath]
  }
)
</script>

<style scoped>
.global-header {
  width: 100%;
  background: #fff;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 64px;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  height: 40px;
  width: 40px;
}

.site-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1890ff;
}

.nav-section {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-menu {
  border-bottom: none;
  background: transparent;
}

.user-section {
  display: flex;
  align-items: center;
}

.mobile-menu-btn {
  display: none;
}

.mobile-user-section {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-container {
    padding: 0 16px;
  }

  .nav-section {
    display: none;
  }

  .user-section {
    display: none;
  }

  .mobile-menu-btn {
    display: block;
  }

  .site-title {
    font-size: 18px;
  }

  .logo {
    height: 32px;
    width: 32px;
  }
}

@media (max-width: 576px) {
  .header-container {
    padding: 0 12px;
  }

  .site-title {
    font-size: 16px;
  }

  .logo {
    height: 28px;
    width: 28px;
  }
}

/* 自定义抽屉样式 */
:deep(.mobile-drawer .ant-drawer-body) {
  padding: 24px 0;
}

:deep(.mobile-drawer .ant-menu) {
  border-right: none;
}
</style>
