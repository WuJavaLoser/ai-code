/**
 * API 响应通用类型定义
 */

// 通用 API 响应结构
export interface BaseResponse<T = any> {
  code: number
  data: T
  message: string
}

// 分页响应结构
export interface PageResponse<T = any> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 用户信息类型
export interface User {
  id: number
  userAccount: string
  userName?: string
  userAvatar?: string
  userProfile?: string
  userRole: string
  createTime: string
  updateTime: string
}

// 登录请求参数
export interface LoginRequest {
  userAccount: string
  userPassword: string
}

// 注册请求参数
export interface RegisterRequest {
  userAccount: string
  userPassword: string
  checkPassword: string
}

// 用户更新请求参数
export interface UpdateUserRequest {
  id?: number
  userName?: string
  userAvatar?: string
  userProfile?: string
}

// 用户列表查询参数
export interface UserQueryRequest {
  current?: number
  pageSize?: number
  userAccount?: string
  userName?: string
  userRole?: string
}
