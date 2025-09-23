import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import UserLoginView from '../views/user/UserLoginView.vue'
import UserRegisterView from '../views/user/UserRegisterView.vue'
import UserUpdateView from '../views/user/UserUpdateView.vue'
import UserManageView from '../views/admin/UserManageView.vue'
import { ACCESS_ENUM } from '@/types/access'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: {
        access: ACCESS_ENUM.NOT_LOGIN, // 首页无需权限
      },
    },
    {
      path: '/user/login',
      name: '用户登录',
      component: UserLoginView,
      meta: {
        access: ACCESS_ENUM.NOT_LOGIN, // 登录页无需权限
        hideInMenu: true, // 不在菜单中显示
      },
    },
    {
      path: '/user/register',
      name: '用户注册',
      component: UserRegisterView,
      meta: {
        access: ACCESS_ENUM.NOT_LOGIN, // 注册页无需权限
        hideInMenu: true, // 不在菜单中显示
      },
    },
    {
      path: '/user/update',
      name: '个人信息编辑',
      component: UserUpdateView,
      meta: {
        access: ACCESS_ENUM.USER, // 需要用户登录
        hideInMenu: true, // 不在菜单中显示
      },
    },
    {
      path: '/admin/userManage',
      name: '用户管理',
      component: UserManageView,
      meta: {
        access: ACCESS_ENUM.ADMIN, // 需要管理员权限
      },
    },
    {
      path: '/403',
      name: '权限不足',
      component: () => import('../views/error/403View.vue'),
      meta: {
        access: ACCESS_ENUM.NOT_LOGIN,
        hideInMenu: true,
      },
    },
    {
      path: '/404',
      name: '页面不存在',
      component: () => import('../views/error/404View.vue'),
      meta: {
        access: ACCESS_ENUM.NOT_LOGIN,
        hideInMenu: true,
      },
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      redirect: '/404',
    },
  ],
})

// 全局守卫逻辑已迁移至 src/access.ts

// 路由后置守卫
router.afterEach((to) => {
  // 设置页面标题
  const title = to.name as string
  if (title) {
    document.title = `${title} - 代码魔方`
  } else {
    document.title = '代码魔方'
  }
})

export default router
