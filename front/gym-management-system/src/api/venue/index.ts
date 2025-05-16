import request from '@/http'

// 获取场馆列表
export const getVenues = (params: any) => {
    return request.get('/api/venues', params)
}

// 获取场馆详情
export const getVenueDetail = (id: string) => {
    return request.get(`/api/venues/${id}`)
}

// 添加场馆
export const addVenue = (data: any) => {
    return request.post('/venue/add', data)
}

// 更新场馆
export const updateVenue = (data: any) => {
    return request.put('/venue/update', data)
}

// 删除场馆
export const deleteVenue = (id: string) => {
    return request.delete(`/venue/delete/${id}`)
}

// 获取场馆类型列表
export const getVenueTypes = () => {
    return request.get('/api/venues/types')
}

// 获取场馆可用时段
export const getVenueAvailableTimeSlots = (params: any) => {
    return request.get('/api/venues/time-slots', params)
} 