import { useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { User } from 'lucide-react';
import { authApi } from '../api/authApi';
import { postApi } from '../api/postApi';
import useAuthStore from '../store/useAuthStore';
import FollowButton from '../components/follow/FollowButton';

export default function SearchPage() {
  const [searchParams] = useSearchParams();
  const query = searchParams.get('q') || '';
  const [activeTab, setActiveTab] = useState('users');
  const [sort, setSort] = useState('relevance');
  const { user: currentUser } = useAuthStore();

  // Search users
  const { data: usersRes, isLoading: usersLoading } = useQuery({
    queryKey: ['searchUsers', query],
    queryFn: () => authApi.searchUsers(query),
    enabled: activeTab === 'users' && !!query,
  });

  // Search posts
  const { data: postsRes, isLoading: postsLoading } = useQuery({
    queryKey: ['searchPosts', query, sort],
    queryFn: () => postApi.searchPosts(query, sort),
    enabled: activeTab === 'posts' && !!query,
  });

  const users = usersRes?.data?.users || usersRes?.data || usersRes?.users || usersRes || [];
  const posts = postsRes?.data?.posts || postsRes?.data || postsRes?.posts || postsRes || [];

  if (!query) {
    return (
      <div className="py-16 text-center text-gray-400">
        <p className="text-lg">Search for users or posts</p>
      </div>
    );
  }

  return (
    <div className="py-8">
      <h1 className="text-lg font-semibold mb-4">
        Results for &ldquo;{query}&rdquo;
      </h1>

      {/* Tab switcher */}
      <div className="flex border-b border-gray-300 mb-6">
        <button
          data-cy="search-tab-users"
          onClick={() => setActiveTab('users')}
          className={`px-6 py-2 text-sm font-semibold border-b-2 transition-colors ${
            activeTab === 'users'
              ? 'border-gray-800 text-gray-800'
              : 'border-transparent text-gray-400 hover:text-gray-600'
          }`}
        >
          Users
        </button>
        <button
          data-cy="search-tab-posts"
          onClick={() => setActiveTab('posts')}
          className={`px-6 py-2 text-sm font-semibold border-b-2 transition-colors ${
            activeTab === 'posts'
              ? 'border-gray-800 text-gray-800'
              : 'border-transparent text-gray-400 hover:text-gray-600'
          }`}
        >
          Posts
        </button>
      </div>

      {/* Users tab */}
      {activeTab === 'users' && (
        <div data-cy="search-user-results">
          {usersLoading ? (
            <div className="space-y-3">
              {[1, 2, 3].map((i) => (
                <div key={i} className="flex items-center gap-3 animate-pulse">
                  <div className="w-10 h-10 rounded-full bg-gray-200" />
                  <div className="flex-1 space-y-1">
                    <div className="h-3 w-28 bg-gray-200 rounded" />
                    <div className="h-3 w-20 bg-gray-200 rounded" />
                  </div>
                </div>
              ))}
            </div>
          ) : !Array.isArray(users) || users.length === 0 ? (
            <p className="text-center text-gray-400 py-8">No users found</p>
          ) : (
            <div className="space-y-3">
              {users.map((u) => (
                <div
                  key={u.id}
                  data-cy="search-user-item"
                  className="flex items-center justify-between py-2 w-full"
                >
                  <Link to={`/profile/${u.id}`} className="flex items-center gap-3">
                    {u.profilePicture ? (
                      <img
                        src={u.profilePicture}
                        alt={u.username}
                        className="w-10 h-10 rounded-full object-cover"
                      />
                    ) : (
                      <div className="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center">
                        <User className="w-5 h-5 text-gray-500" />
                      </div>
                    )}
                    <div>
                      <p className="text-sm font-semibold">{u.username}</p>
                      <p className="text-xs text-gray-500">{u.fullName}</p>
                    </div>
                  </Link>
                  {u.id !== currentUser?.id && (
                    <FollowButton
                      targetUserId={u.id}
                      isFollowing={u.isFollowing || false}
                    />
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Posts tab */}
      {activeTab === 'posts' && (
        <div>
          {/* Sort dropdown */}
          <div className="flex justify-end mb-4">
            <select
              data-cy="search-sort-select"
              value={sort}
              onChange={(e) => setSort(e.target.value)}
              className="border border-gray-300 rounded-lg px-3 py-1.5 text-sm bg-white focus:outline-none"
            >
              <option value="relevance">Relevance</option>
              <option value="popular">Popular</option>
              <option value="recent">Recent</option>
            </select>
          </div>

          <div data-cy="search-post-results">
            {postsLoading ? (
              <div className="grid grid-cols-2 sm:grid-cols-3 gap-1">
                {[1, 2, 3, 4, 5, 6].map((i) => (
                  <div key={i} className="aspect-square bg-gray-200 animate-pulse" />
                ))}
              </div>
            ) : !Array.isArray(posts) || posts.length === 0 ? (
              <p className="text-center text-gray-400 py-8">No posts found</p>
            ) : (
              <div className="grid grid-cols-2 sm:grid-cols-3 gap-1">
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
        </div>
      )}
    </div>
  );
}
