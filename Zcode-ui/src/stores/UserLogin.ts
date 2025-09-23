import { ref } from 'vue'
import { defineStore } from 'pinia'
import { getLoginUser } from '@/api/userController'

export const useUserLoginStore = defineStore('userlogin', () => {
  const userlogin = ref<API.UserVo>({
    userName: '未登录',
  })

  // 获取登录用户信息
  async function fetchLoginUser() {
    try {
      const res = await getLoginUser()
      if (res?.data?.code === 0 && res.data.data) {
        userlogin.value = res.data.data
      } else {
        // 如果响应格式不正确，重置为未登录状态
        userlogin.value = {
          userName: '未登录',
        }
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      // 发生错误时重置为未登录状态
      userlogin.value = {
        userName: '未登录',
      }
    }
  }

  // 更新登录用户信息
  function setLoginUser(newLoginUser: API.UserVo | null) {
    if (newLoginUser) {
      userlogin.value = newLoginUser
    } else {
      // 清空登录状态，设置为默认值
      userlogin.value = {
        userName: '未登录',
      }
    }
  }

  // 返回要暴露的状态和方法
  return {
    userlogin,
    fetchLoginUser,
    setLoginUser,
  }
})
