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
  getStatistics: (params) => api.get('/relics/statistics', { params }),
  getAvailableYears: () => api.get('/relics/statistics/years')
}

export const excavationUnitApi = {
  getAllUnits: () => api.get('/excavation-units'),
  getUnitById: (id) => api.get(`/excavation-units/${id}`),
  searchUnits: (params) => api.get('/excavation-units/search', { params }),
  createUnit: (data) => api.post('/excavation-units', data),
  updateUnit: (id, data) => api.put(`/excavation-units/${id}`, data),
  deleteUnit: (id) => api.delete(`/excavation-units/${id}`),
  getUnitRelics: (id) => api.get(`/excavation-units/${id}/relics`),
  getStatistics: () => api.get('/excavation-units/statistics')
}

export const repairOrderApi = {
  getAllOrders: () => api.get('/repair-orders'),
  getOrderById: (id) => api.get(`/repair-orders/${id}`),
  searchOrders: (params) => api.get('/repair-orders/search', { params }),
  createOrder: (data) => api.post('/repair-orders', data),
  updateOrder: (id, data) => api.put(`/repair-orders/${id}`, data),
  deleteOrder: (id) => api.delete(`/repair-orders/${id}`),
  startRepair: (id, data) => api.post(`/repair-orders/${id}/start`, data),
  completeRepair: (id, data) => api.post(`/repair-orders/${id}/complete`, data),
  getStatusStatistics: () => api.get('/repair-orders/statistics')
}

export default api
