/**
 * 权限控制文件
 * 全局权限校验和控制
 */
import { useUserLoginStore } from '@/stores/UserLogin'
import { getLoginUser } from '@/api/userController'
import { message } from 'ant-design-vue'
import { ACCESS_ENUM } from '@/types/access'
import router from '@/router'

// 控制是否已经获取过用户信息（避免重复请求）
let firstFetchLoginUser = true

/**
 * 检查用户是否有指定权限
 * @param needAccess 需要的权限
 * @param userAccess 用户实际权限
 * @returns 是否有权限
 */
export const checkAccess = (needAccess: string, userAccess?: string): boolean => {
  console.log('权限检查:', { needAccess, userAccess })

  // 如果不需要权限，直接返回true
  if (!needAccess || needAccess === ACCESS_ENUM.NOT_LOGIN) {
    return true
  }

  // 如果用户未登录
  if (!userAccess || userAccess === ACCESS_ENUM.NOT_LOGIN) {
    return needAccess === ACCESS_ENUM.NOT_LOGIN
  }

  // 如果需要普通用户权限
  if (needAccess === ACCESS_ENUM.USER) {
    return userAccess === ACCESS_ENUM.USER || userAccess === ACCESS_ENUM.ADMIN
  }

  // 如果需要管理员权限
  if (needAccess === ACCESS_ENUM.ADMIN) {
    return userAccess === ACCESS_ENUM.ADMIN
  }

  return false
}

/**
 * 获取当前用户权限
 * @returns 用户权限级别
 */
export const getCurrentUserAccess = (): string => {
  const userLoginStore = useUserLoginStore()
  const userRole = userLoginStore.userlogin?.userRole

  if (!userRole) {
    return ACCESS_ENUM.NOT_LOGIN
  }

  // 根据用户角色返回对应权限
  if (userRole === 'admin') {
    return ACCESS_ENUM.ADMIN
  }

  if (userRole === 'user') {
    return ACCESS_ENUM.USER
  }

  return ACCESS_ENUM.NOT_LOGIN
}

/**
 * 从后端获取并更新用户登录信息
 * 确保页面刷新时能正确获取用户权限
 */
export const fetchAndUpdateLoginUser = async (): Promise<void> => {
  const userLoginStore = useUserLoginStore()

  // 如果已经获取过用户信息，且用户已登录，则不重复请求
  if (!firstFetchLoginUser && userLoginStore.userlogin?.id) {
    console.log('已有用户信息，无需重复获取')
    return
  }

  try {
    console.log('开始从后端获取用户信息...')
    firstFetchLoginUser = false

    const response = await getLoginUser()
    console.log('获取用户信息响应:', response)

    if (response && response.data && response.data.code === 0 && response.data.data) {
      // 更新用户登录状态
      userLoginStore.setLoginUser(response.data.data)
      console.log('用户信息更新成功:', response.data.data)
    } else {
      // 如果获取失败，清除本地登录状态
      console.log('获取用户信息失败，清除本地登录状态')
      userLoginStore.setLoginUser(null)
    }
  } catch (error: any) {
    console.error('获取用户信息出错:', error)

    // 如果是未登录错误，清除本地状态
    if (error?.response?.status === 401 || error?.response?.data?.code === 40100) {
      console.log('用户未登录，清除本地登录状态')
      userLoginStore.setLoginUser(null)
    }

    // 网络错误等情况，不清除本地状态，使用本地缓存
    if (error?.code === 'ERR_NETWORK') {
      console.log('网络错误，使用本地缓存的用户信息')
    }
  }
}

/**
 * 检查页面访问权限
 * @param routeAccess 路由需要的权限
 * @returns 是否有访问权限
 */
export const checkPageAccess = async (routeAccess?: string): Promise<boolean> => {
  // 先获取最新的用户信息
  await fetchAndUpdateLoginUser()

  // 获取当前用户权限
  const userAccess = getCurrentUserAccess()

  // 检查权限
  const hasAccess = checkAccess(routeAccess || ACCESS_ENUM.NOT_LOGIN, userAccess)

  console.log('页面权限检查结果:', {
    routeAccess,
    userAccess,
    hasAccess,
  })

  return hasAccess
}

/**
 * 检查用户是否为管理员
 * @returns 是否为管理员
 */
export const isAdmin = (): boolean => {
  const userAccess = getCurrentUserAccess()
  return userAccess === ACCESS_ENUM.ADMIN
}

/**
 * 检查用户是否已登录
 * @returns 是否已登录
 */
export const isLoggedIn = (): boolean => {
  const userAccess = getCurrentUserAccess()
  return userAccess !== ACCESS_ENUM.NOT_LOGIN
}

/**
 * 重置权限状态（用于退出登录时）
 */
export const resetAccessState = (): void => {
  firstFetchLoginUser = true
  console.log('权限状态已重置')
}

// 导出权限相关的工具函数
export default {
  ACCESS_ENUM,
  checkAccess,
  getCurrentUserAccess,
  fetchAndUpdateLoginUser,
  checkPageAccess,
  isAdmin,
  isLoggedIn,
  resetAccessState,
}

// --------------------
// 基于路径前缀的全局权限校验
// 规则：以 /admin 开头的路由仅管理员可访问；首次进入时等待后端返回用户信息再判断
// --------------------

let firstFetchLoginUserForGuard = true

router.beforeEach(async (to, from, next) => {
  const loginUserStore = useUserLoginStore()
  let loginUser = loginUserStore.userlogin

  // 只在首次访问时尝试获取用户信息，且不阻塞路由
  if (firstFetchLoginUserForGuard) {
    try {
      // 使用Promise.race添加超时保护，最多等待3秒
      await Promise.race([
        loginUserStore.fetchLoginUser(),
        new Promise((_, reject) => setTimeout(() => reject(new Error('timeout')), 3000)),
      ])
    } catch (e) {
      console.log('获取用户信息失败或超时，按未登录处理:', e)
      // 确保有默认的用户状态
      loginUserStore.setLoginUser(null)
    }
    loginUser = loginUserStore.userlogin
    firstFetchLoginUserForGuard = false
  }

  const toUrl = to.fullPath
  if (toUrl.startsWith('/admin')) {
    // 确保loginUser存在且有userRole属性
    if (!loginUser || !loginUser.userRole || loginUser.userRole !== 'admin') {
      message.error('没有权限')
      next(`/user/login?redirect=${to.fullPath}`)
      return
    }
  }

  next()
})
