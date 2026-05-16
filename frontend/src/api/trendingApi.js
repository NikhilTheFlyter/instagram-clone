import api from './axios';

export const getTrendingPosts = (filter = 'popular', page = 0, size = 20) =>
  api.get('/trending/posts', { params: { filter, page, size } });
export const getTrendingHashtags = (limit = 10) =>
  api.get('/trending/hashtags', { params: { limit } });

export const trendingApi = { getTrendingPosts, getTrendingHashtags };
