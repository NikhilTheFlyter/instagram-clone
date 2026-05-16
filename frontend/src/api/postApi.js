import api from './axios';

export const createPost = (data) => api.post('/posts', data);
export const getPost = (postId) => api.get(`/posts/${postId}`);
export const getUserPosts = (userId, page = 0, size = 12) =>
  api.get(`/posts/user/${userId}`, { params: { page, size } });
export const getFeed = (page = 0, size = 10) =>
  api.get('/posts/feed', { params: { page, size } });
export const deletePost = (postId) => api.delete(`/posts/${postId}`);
export const likePost = (postId) => api.post(`/posts/${postId}/like`);
export const unlikePost = (postId) => api.delete(`/posts/${postId}/like`);
export const getLikeStatus = (postId) => api.get(`/posts/${postId}/likes`);
export const searchPosts = (query, sort = 'relevance', page = 0, size = 10) =>
  api.get('/posts/search', { params: { q: query, sort, page, size } });

export const postApi = { createPost, getPost, getUserPosts, getFeed, deletePost, likePost, unlikePost, getLikeStatus, searchPosts };
