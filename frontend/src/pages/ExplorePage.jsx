import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { trendingApi } from '../api/trendingApi';

export default function ExplorePage() {
  const navigate = useNavigate();
  const [filter, setFilter] = useState('popular'); // 'popular' | 'recent'

  const { data: hashtagsRes, isLoading: hashtagsLoading } = useQuery({
    queryKey: ['trendingHashtags'],
    queryFn: () => trendingApi.getTrendingHashtags(),
  });

  const { data: postsRes, isLoading: postsLoading } = useQuery({
    queryKey: ['trendingPosts', filter],
    queryFn: () => trendingApi.getTrendingPosts(filter),
  });

  const hashtags = hashtagsRes?.data || hashtagsRes || [];
  const posts = postsRes?.data?.posts || postsRes?.data || postsRes?.posts || postsRes || [];

  return (
    <div className="py-8">
      {/* Trending hashtags */}
      <div data-cy="explore-trending-hashtags" className="mb-6">
        <h2 className="text-sm font-semibold text-gray-500 uppercase mb-3">Trending Hashtags</h2>
        <div className="flex flex-wrap gap-2">
          {hashtagsLoading ? (
            Array.from({ length: 6 }).map((_, i) => (
              <div key={i} className="h-8 w-20 bg-gray-200 rounded-full animate-pulse" />
            ))
          ) : Array.isArray(hashtags) && hashtags.length > 0 ? (
            hashtags.map((tag, idx) => {
              const tagName = typeof tag === 'string' ? tag : tag.name || tag.hashtag;
              const cleanTag = tagName?.startsWith('#') ? tagName.slice(1) : tagName;
              return (
                <button
                  key={idx}
                  data-cy="explore-hashtag-chip"
                  onClick={() => navigate(`/search?q=${encodeURIComponent('#' + cleanTag)}`)}
                  className="px-3 py-1.5 text-sm bg-gray-100 hover:bg-gray-200 rounded-full text-[#00376B] font-medium transition-colors"
                >
                  #{cleanTag}
                </button>
              );
            })
          ) : (
            <p className="text-sm text-gray-400">No trending hashtags yet</p>
          )}
        </div>
      </div>

      {/* Filter buttons */}
      <div className="flex gap-2 mb-6">
        <button
          data-cy="explore-filter-popular"
          onClick={() => setFilter('popular')}
          className={`px-4 py-1.5 text-sm font-semibold rounded-lg transition-colors ${
            filter === 'popular'
              ? 'bg-gray-900 text-white'
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
          }`}
        >
          Popular
        </button>
        <button
          data-cy="explore-filter-recent"
          onClick={() => setFilter('recent')}
          className={`px-4 py-1.5 text-sm font-semibold rounded-lg transition-colors ${
            filter === 'recent'
              ? 'bg-gray-900 text-white'
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
          }`}
        >
          Recent
        </button>
      </div>

      {/* Posts grid */}
      {postsLoading ? (
        <div className="grid grid-cols-3 gap-1">
          {Array.from({ length: 9 }).map((_, i) => (
            <div key={i} className="aspect-square bg-gray-200 animate-pulse" />
          ))}
        </div>
      ) : !Array.isArray(posts) || posts.length === 0 ? (
        <div className="text-center py-16 text-gray-400">
          <p className="text-lg font-light">No posts to explore yet</p>
        </div>
      ) : (
        <div data-cy="explore-posts-grid" className="grid grid-cols-3 gap-1">
          {posts.map((post) => (
            <Link
              key={post.id}
              to={`/post/${post.id}`}
              className="aspect-square relative group"
            >
              <img
                src={post.mediaUrl || post.imageUrl}
                alt=""
                className="w-full h-full object-cover"
              />
              <div className="absolute inset-0 bg-black/30 opacity-0 group-hover:opacity-100 flex items-center justify-center transition-opacity">
                <span className="text-white font-semibold text-sm">
                  {post.likeCount ?? post.likes ?? 0} likes
                </span>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
