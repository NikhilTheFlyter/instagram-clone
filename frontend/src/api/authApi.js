import api from './axios';

export const register = (data) => api.post('/auth/register', data);
export const login = (data) => api.post('/auth/login', data);
export const forgotPassword = (data) => api.post('/auth/forgot-password', data);
export const resetPassword = (data) => api.post('/auth/reset-password', data);
export const getProfile = (userId) => api.get(`/auth/profile/${userId}`);
export const updateProfile = (userId, data) => api.put(`/auth/profile/${userId}`, data);
export const searchUsers = (query, page = 0, size = 10) =>
  api.get('/auth/search/users', { params: { q: query, page, size } });
export const getLoginStatus = () => api.get('/auth/login/status');

export const authApi = { register, login, forgotPassword, resetPassword, getProfile, updateProfile, searchUsers, getLoginStatus };
