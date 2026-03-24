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
  send:           (data)   => api.post('/messages', data),
  getMyMessages:  ()       => api.get('/messages'),
  getConversation:(userId) => api.get(`/messages/conversation/${userId}`),
  getItemMessages:(itemId) => api.get(`/messages/item/${itemId}`),
  markAsRead:     (id)     => api.patch(`/messages/${id}/read`),
  getUnreadCount: ()       => api.get('/messages/unread-count'),
};