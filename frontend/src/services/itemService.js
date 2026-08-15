import api from './api';

export const itemService = {
	// GET ALL
	getAll: (params) => api.get('/api/items', { params }),

	// SEARCH
	search: (params) => api.get('/api/items/search', { params }),

	// GET BY ID
	getById: (id) => api.get(`/api/items/${id}`),

	// MY ITEMS
	getMyItems: () => api.get('/api/items/my'),

	// CREATE
	create: (data) => api.post('/api/items', data),

	// CREATE WITH FILE
	createWithFormData: (formData) =>
	  api.post('/api/items', formData, {
	    headers: { 'Content-Type': 'multipart/form-data' },
	  }),

	// UPDATE
	update: (id, data) => api.put(`/api/items/${id}`, data),

	// DELETE
	delete: (id) => api.delete(`/api/items/${id}`),
};

export const messageService = {
  send:           (data)   => api.post('/api/messages', data),
  getMyMessages:  ()       => api.get('/api/messages'),
  getConversation:(userId) => api.get(`/api/messages/conversation/${userId}`),
  getItemMessages:(itemId) => api.get(`/api/messages/item/${itemId}`),
  markAsRead:     (id)     => api.patch(`/api/messages/${id}/read`),
  getUnreadCount: ()       => api.get('/api/messages/unread-count'),
};