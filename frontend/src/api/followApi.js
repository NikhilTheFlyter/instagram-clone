import api from './axios';

export const followUser = (userId) => api.post(`/follow/${userId}`);
export const unfollowUser = (userId) => api.delete(`/follow/${userId}`);
export const getFollowers = (userId) => api.get(`/follow/${userId}/followers`);
export const getFollowing = (userId) => api.get(`/follow/${userId}/following`);
export const getStats = (userId) => api.get(`/follow/${userId}/stats`);
export const isFollowing = (userId) => api.get(`/follow/check/${userId}`);

export const followApi = { followUser, unfollowUser, getFollowers, getFollowing, getStats, isFollowing };
