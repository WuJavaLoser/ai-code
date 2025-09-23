<template>
  <div class="global-header">
    <component :is="contextHolder" />
    <div class="header-container">
      <!-- Logo和标题 -->
      <div class="logo-section">
        <img src="@/assets/logo.ico" alt="代码魔方" class="logo" />
        <h1 class="site-title">代码魔方</h1>
      </div>

      <!-- 导航菜单 -->
      <div class="nav-section">
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="horizontal"
          class="nav-menu"
          :items="filteredMenuItems"
          :triggerSubMenuAction="'click'"
          :inlineCollapsed="false"
          :overflowedIndicator="null"
          :forceSubMenuRender="false"
          :multiple="false"
          :selectable="true"
          @click="handleMenuClick"
        />
      </div>

      <!-- 用户操作区 -->
      <div class="user-section">
        <a-button v-if="!isLoggedIn" type="primary" @click="handleLogin"> 登录 </a-button>
        <a-dropdown v-else placement="bottomRight" :trigger="['hover']" class="user-dropdown">
          <div class="user-info">
            <a-avatar :src="getUserAvatar()" :size="32" class="user-avatar">
              {{ getUserDisplayName().charAt(0) }}
            </a-avatar>
            <span class="user-name">{{ getUserDisplayName() }}</span>
          </div>
          <template #overlay>
            <a-menu class="user-menu" @click="handleUserMenuClick">
              <a-menu-item key="settings" class="settings-item">
                <SettingOutlined />
                个人设置
              </a-menu-item>
              <a-menu-divider />
              <a-menu-item key="logout" class="logout-item">
                <LogoutOutlined />
                退出登录
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>

      <!-- 移动端菜单按钮 -->
      <div class="mobile-menu-btn">
        <a-button type="text" @click="toggleMobileMenu" :icon="h(MenuOutlined)" />
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
        :items="filteredMenuItems"
        @click="handleMobileMenuClick"
      />
      <div class="mobile-user-section">
        <!-- 未登录状态：显示登录按钮 -->
        <a-button v-if="!isLoggedIn" type="primary" block @click="handleLogin"> 登录 </a-button>

        <!-- 已登录状态：显示用户信息 -->
        <div v-else class="mobile-user-section-content">
          <div class="mobile-user-info">
            <a-avatar :src="getUserAvatar()" :size="40" class="mobile-user-avatar">
              {{ getUserDisplayName().charAt(0) }}
            </a-avatar>
            <span class="mobile-user-name">{{ getUserDisplayName() }}</span>
          </div>
          <a-button
            type="text"
            class="mobile-logout-btn"
            @click="handleLogout"
            :icon="h(LogoutOutlined)"
          >
            退出登录
          </a-button>
        </div>
      </div>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, h, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { MenuOutlined, LogoutOutlined, UserOutlined, FileOutlined, SettingOutlined } from '@ant-design/icons-vue'
import type { MenuProps } from 'ant-design-vue'
import { message } from 'ant-design-vue'
import { useUserLoginStore } from '@/stores/UserLogin'
import { userLogout } from '@/api/userController'
import { resetAccessState, isAdmin, isLoggedIn as isLoggedInFn } from '@/access'

const router = useRouter()
const selectedKeys = ref<string[]>([router.currentRoute.value.path])
const mobileMenuVisible = ref(false)

const userLoginStore = useUserLoginStore()

// 使用 message API
const [messageApi, contextHolder] = message.useMessage()

// 判断是否已登录
const isLoggedIn = computed(() => isLoggedInFn())

// 获取用户显示名称
const getUserDisplayName = () => {
  const userName = userLoginStore.userlogin.userName
  // 当昵称为 null、undefined、空字符串、"null"字符串或"未登录"时，显示默认文本"鸡贼"
  if (!userName || userName.trim() === '' || userName === '未登录' || userName === 'null') {
    return '鸡贼'
  }
  return userName
}

// 获取用户头像（包含默认头像逻辑）
const getUserAvatar = () => {
  const userAvatar = userLoginStore.userlogin.userAvatar
  // 如果用户有自定义头像，直接返回
  if (userAvatar && userAvatar.trim() !== '') {
    return userAvatar
  }

  // 没有自定义头像时，基于用户ID选择默认头像
  const userId = userLoginStore.userlogin.id || 1
  const avatarIndex = ((userId - 1) % 20) + 1
  const paddedIndex = avatarIndex.toString().padStart(2, '0')
  return `/unnamed_${paddedIndex}.png`
}

// 菜单配置（动态生成，根据用户权限显示不同菜单）
const baseMenuItems = computed(() => {
  const baseItems: any[] = [
    {
      key: '/',
      label: '首页',
    },
    {
      key: '/projects',
      label: '项目',
    },
    {
      key: '/docs',
      label: '文档',
      icon: h(FileOutlined),
    },
  ]

  // 如果当前用户是管理员，添加用户管理菜单
  if (isAdmin()) {
    baseItems.push({
      key: '/admin/userManage',
      label: '用户管理',
      icon: h(UserOutlined),
      class: 'admin-menu-item',
    })
  }

  return baseItems
})

// 过滤菜单项：未登录不显示“用户管理”
const filteredMenuItems = computed(() => {
  const loggedIn = isLoggedInFn()
  return baseMenuItems.value.filter((item: any) => {
    if (item.key === '/admin/userManage' && !loggedIn) return false
    return true
  })
})

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
    } else if (key === '/admin/userManage') {
      // 跳转到用户管理页面
      router.push(key)
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
  router.push('/user/login')
}

// 处理用户菜单点击
const handleUserMenuClick: MenuProps['onClick'] = async (e) => {
  if (e.key === 'logout') {
    await handleLogout()
  } else if (e.key === 'settings') {
    router.push('/user/update')
  }
}

// 处理退出登录
const handleLogout = async () => {
  // 关闭移动端菜单抽屉
  mobileMenuVisible.value = false

  try {
    const response = await userLogout()

    if (response.data.code === 0) {
      // 清空用户登录状态
      userLoginStore.setLoginUser(null)
      // 重置权限状态
      resetAccessState()

      messageApi.success('✋ 退出登录成功，期待您的再次光临！')

      // 延迟跳转到首页
      setTimeout(() => {
        router.push('/')
      }, 1500)
    } else {
      messageApi.error(response.data.message || '退出登录失败')
    }
  } catch (error) {
    console.error('退出登录失败:', error)
    messageApi.error('退出登录失败，请检查网络连接')
  }
}

// 监听路由变化，更新菜单选中状态
watch(
  () => router.currentRoute.value.path,
  (newPath) => {
    selectedKeys.value = [newPath]
  },
)
</script>

<style scoped>
.global-header {
  width: 100%;
  background: transparent;
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 60px;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo {
  height: 32px;
  width: 32px;
}

.site-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #000;
}

.nav-section {
  flex: 1;
  display: flex;
  justify-content: center;
  margin: 0 12px;
  min-width: 400px; /* 确保有足够空间显示所有菜单项 */
  max-width: none; /* 移除最大宽度限制 */
}

.nav-menu {
  border-bottom: none;
  background: transparent;
  font-size: 14px;
  white-space: nowrap; /* 防止菜单项换行 */
  display: flex;
  justify-content: center;
  width: auto; /* 自动宽度 */
  overflow: visible; /* 确保内容可见 */
}

.nav-menu :deep(.ant-menu-item) {
  color: #666;
  font-weight: 400;
  padding: 0 16px;
  height: 40px;
  line-height: 40px;
}

.nav-menu :deep(.ant-menu-item:hover) {
  color: #000;
}

.nav-menu :deep(.ant-menu-item-selected) {
  color: #000;
  font-weight: 500;
}

/* 强制禁用菜单溢出和折叠行为 */
.nav-menu :deep(.ant-menu-overflow) {
  display: none !important;
}

.nav-menu :deep(.ant-menu-overflow-item) {
  display: flex !important;
}

.nav-menu :deep(.ant-menu-submenu) {
  display: none !important;
}

.nav-menu :deep(.ant-menu-horizontal) {
  border-bottom: none;
}

.nav-menu :deep(.ant-menu-horizontal > .ant-menu-item),
.nav-menu :deep(.ant-menu-horizontal > .ant-menu-submenu) {
  display: inline-block !important;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-section :deep(.ant-btn) {
  height: 36px;
  border-radius: 18px;
  font-size: 14px;
  font-weight: 500;
  padding: 0 20px;
  border: 1px solid #d9d9d9;
  background: #fff;
  color: #000;
}

.user-section :deep(.ant-btn:hover) {
  border-color: #40a9ff;
  color: #40a9ff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 20px;
  transition: background-color 0.2s;
}

.user-info:hover {
  background: rgba(0, 0, 0, 0.04);
}

.user-avatar {
  flex-shrink: 0;
}

.user-name {
  font-size: 14px;
  color: #000;
  font-weight: 500;
}

.mobile-menu-btn {
  display: none;
}

.mobile-user-section {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.mobile-user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f5f5f5;
  border-radius: 8px;
}

.mobile-user-avatar {
  flex-shrink: 0;
}

.mobile-user-name {
  font-size: 16px;
  color: #262626;
  font-weight: 500;
}

/* 确保桌面端有足够空间 */
@media (min-width: 1200px) {
  .nav-section {
    min-width: 500px;
  }
}

@media (min-width: 900px) and (max-width: 1199px) {
  .nav-section {
    min-width: 450px;
  }
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

/* 用户下拉菜单样式 */
.user-dropdown :deep(.ant-dropdown) {
  border-radius: 8px !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
}

.user-menu {
  border-radius: 8px !important;
  background: #ffefd5 !important;
  border: 1px solid #f0e6d2 !important;
  padding: 4px 0 !important;
  min-width: 120px !important;
}

.user-menu :deep(.ant-menu-item) {
  padding: 8px 16px !important;
  margin: 0 4px !important;
  border-radius: 6px !important;
  color: #654321 !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  transition: all 0.2s !important;
}

.user-menu :deep(.ant-menu-item:hover) {
  background: rgba(139, 69, 19, 0.1) !important;
  color: #8b4513 !important;
}

.user-menu :deep(.ant-menu-item .anticon) {
  margin-right: 8px !important;
  color: #8b4513 !important;
}

.logout-item {
  color: #ff4d4f !important;
}

.user-menu :deep(.logout-item:hover) {
  background: rgba(255, 77, 79, 0.1) !important;
  color: #ff4d4f !important;
}

.user-menu :deep(.logout-item .anticon) {
  color: #ff4d4f !important;
}

/* 移动端用户区域样式 */
.mobile-user-section-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.mobile-logout-btn {
  color: #ff4d4f !important;
  border: 1px solid rgba(255, 77, 79, 0.3) !important;
  border-radius: 6px !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  transition: all 0.2s !important;
}

.mobile-logout-btn:hover {
  background: rgba(255, 77, 79, 0.1) !important;
  border-color: #ff4d4f !important;
  color: #ff4d4f !important;
}

.mobile-logout-btn :deep(.anticon) {
  color: #ff4d4f !important;
}

/* 管理员菜单项样式 */
:deep(.admin-menu-item) {
  position: relative;
}

:deep(.admin-menu-item .ant-menu-title-content) {
  color: #722ed1 !important;
  font-weight: 500 !important;
}

:deep(.admin-menu-item .anticon) {
  color: #722ed1 !important;
}

:deep(.admin-menu-item:hover .ant-menu-title-content) {
  color: #531dab !important;
}

:deep(.admin-menu-item:hover .anticon) {
  color: #531dab !important;
}

:deep(.admin-menu-item::after) {
  content: '';
  position: absolute;
  top: 2px;
  right: 2px;
  width: 6px;
  height: 6px;
  background: #f5222d;
  border-radius: 50%;
  opacity: 0.8;
}

/* 选中状态的管理员菜单 */
:deep(.ant-menu-item-selected.admin-menu-item .ant-menu-title-content) {
  color: #722ed1 !important;
}

:deep(.ant-menu-item-selected.admin-menu-item .anticon) {
  color: #722ed1 !important;
}
</style>
