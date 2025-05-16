import request from '@/utils/request'

// 获取每周场地安排
export const getWeeklySchedule = (params: {
    startDate: string;
    endDate: string;
    venueType?: string;
    venueId?: number;
}) => {
    return request({
        url: '/api/venue-schedules/weekly',
        method: 'get',
        params
    })
}

// 获取所有场地类型
export const getVenueTypes = () => {
    return request({
        url: '/api/venue-schedules/types',
        method: 'get'
    })
}

// 根据类型获取场地列表
export const getVenuesByType = (type?: string) => {
    return request({
        url: '/api/venue-schedules/venues',
        method: 'get',
        params: { type }
    })
} 