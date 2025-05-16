import axios from 'axios'

export function getAnnouncements(params: Record<string, any>) {
  return axios.get('/api/announcements', { params })
}

export function addAnnouncement(data: Record<string, any>) {
  return axios.post('/api/announcements', data)
}

export function updateAnnouncement(data: Record<string, any>) {
  return axios.put(`/api/announcements/${data.id}`, data)
}

export function deleteAnnouncement(id: number | string) {
  return axios.delete(`/api/announcements/${id}`)
} 