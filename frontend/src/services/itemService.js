import api from './api';

export const itemService = {
  getAll:    (params) => api.get('/items', { params }),
  search:    (params) => api.get('/items/search', { params }),
  getById:   (id)     => api.get(`/items/${id}`),
  getMyItems: ()      => api.get('/items/my'),

  // Plain JSON create (old, kept for compatibility)
  create: (data) => api.post('/items', data),

  // Multipart FormData create — used by ReportLost / ReportFound
  createWithFormData: (formData) =>
    api.post('/items', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }),

  update: (id, data) => api.put(`/items/${id}`, data),
  delete: (id)       => api.delete(`/items/${id}`),
};

export const messageService = {
  send:           (data)   => api.post('/messages', data),
  getMyMessages:  ()       => api.get('/messages'),
  getConversation:(userId) => api.get(`/messages/conversation/${userId}`),
  getItemMessages:(itemId) => api.get(`/messages/item/${itemId}`),
  markAsRead:     (id)     => api.patch(`/messages/${id}/read`),
  getUnreadCount: ()       => api.get('/messages/unread-count'),
};