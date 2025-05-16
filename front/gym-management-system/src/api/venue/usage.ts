import request from '@/http'
import type { Reservation } from '@/types/venue'

// 获取今日预约列表
export const getTodayReservations = () => {
  return request.get('/api/reservations/today')
}

// 开始使用场地
export const startVenueUsage = (reservationId: string) => {
  return request.post(`/api/reservations/${reservationId}/start`)
}

// 结束使用场地
export const endVenueUsage = (reservationId: string) => {
  return request.post(`/api/reservations/${reservationId}/end`)
}

// 结算场地使用
export const settleVenueUsage = (reservationId: string, data: {
  paymentMethod: string
  actualCost: number
}) => {
  return request.post(`/api/reservations/${reservationId}/settle`, data)
}

// 获取使用详情
export const getUsageDetail = (reservationId: string) => {
  return request.get(`/api/reservations/${reservationId}`)
} 