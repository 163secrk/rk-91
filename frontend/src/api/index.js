import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

export const relicApi = {
  getAllRelics: () => api.get('/relics'),
  getRelicById: (id) => api.get(`/relics/${id}`),
  searchRelics: (params) => api.get('/relics/search', { params }),
  createRelic: (data) => api.post('/relics', data),
  updateRelic: (id, data) => api.put(`/relics/${id}`, data),
  deleteRelic: (id) => api.delete(`/relics/${id}`),
  getRestorationRecords: (relicId) => api.get(`/relics/${relicId}/restoration-records`),
  addRestorationRecord: (relicId, data) => api.post(`/relics/${relicId}/restoration-records`, data),
  deleteRestorationRecord: (recordId) => api.delete(`/relics/restoration-records/${recordId}`),
  getStatistics: () => api.get('/relics/statistics')
}

export default api
