import request from '@/utils/request'

// 获取公告列表
export const getAnnouncements = (params: any) => {
    return request({
        url: '/announcement/list',
        method: 'get',
        params
    })
}

// 添加公告
export const addAnnouncement = (data: any) => {
    return request({
        url: '/announcement/add',
        method: 'post',
        data
    })
}

// 更新公告
export const updateAnnouncement = (data: any) => {
    return request({
        url: '/announcement/update',
        method: 'put',
        data
    })
}

// 删除公告
export const deleteAnnouncement = (id: string) => {
    return request({
        url: `/announcement/delete/${id}`,
        method: 'delete'
    })
}

// 获取公告详情
export const getAnnouncementDetail = (id: string) => {
    return request({
        url: `/announcement/detail/${id}`,
        method: 'get'
    })
} 