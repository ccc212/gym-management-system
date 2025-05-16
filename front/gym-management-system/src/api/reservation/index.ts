import request from '@/utils/request'

// 创建预约
export const createReservation = (data: any) => {
    return request({
        url: '/reservation/create',
        method: 'post',
        data
    })
}

// 获取预约列表
export const getReservations = (params: any) => {
    return request({
        url: '/reservation/list',
        method: 'get',
        params
    })
}

// 获取预约详情
export const getReservationDetail = (id: string) => {
    return request({
        url: `/reservation/detail/${id}`,
        method: 'get'
    })
}

// 取消预约
export const cancelReservation = (id: string) => {
    return request({
        url: `/reservation/cancel/${id}`,
        method: 'put'
    })
}

// 获取用户预约历史
export const getUserReservationHistory = (params: any) => {
    return request({
        url: '/reservation/user-history',
        method: 'get',
        params
    })
}

// 获取场馆预约记录
export const getVenueReservations = (params: any) => {
    return request({
        url: `/api/reservations/venue/${params.venueId}`,
        method: 'get',
        params: {
            date: params.date
        }
    })
}

// 检查时段是否可预约
export const checkTimeSlotAvailability = (params: any) => {
    return request({
        url: '/reservation/check-availability',
        method: 'get',
        params
    })
} 