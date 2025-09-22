// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 GET /health/ */
export async function healthCheck(options?: { [key: string]: any }) {
  return request<API.ResultString>('/health/', {
    method: 'GET',
    ...(options || {}),
  })
}
