import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { postApi } from '../api/postApi';
import useAuthStore from '../store/useAuthStore';
import PostCard from '../components/post/PostCard';
import toast from 'react-hot-toast';
import { useState } from 'react';

export default function FeedPage() {
  const { user } = useAuthStore();
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);

  const { data, isLoading, isError } = useQuery({
    queryKey: ['feed', page],
    queryFn: () => postApi.getFeed(page, 10),
  });

  const posts = data?.data?.posts || data?.data || data?.posts || [];
  const hasMore = data?.data?.hasMore ?? data?.hasMore ?? (Array.isArray(posts) && posts.length === 10);

  const likeMutation = useMutation({
    mutationFn: (postId) => postApi.likePost(postId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['feed'] }),
  });

  const unlikeMutation = useMutation({
    mutationFn: (postId) => postApi.unlikePost(postId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['feed'] }),
  });

  const deleteMutation = useMutation({
    mutationFn: (postId) => postApi.deletePost(postId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['feed'] });
      toast.success('Post deleted');
    },
    onError: () => toast.error('Failed to delete post'),
  });

  if (isLoading) {
    return (
      <div data-cy="feed-loading" className="py-8 space-y-4 w-full sm:max-w-[470px] mx-auto px-0 sm:px-4">
        {[1, 2, 3].map((i) => (
          <div key={i} className="bg-white border border-gray-300 rounded-lg animate-pulse">
            <div className="flex items-center gap-2 p-3">
              <div className="w-8 h-8 rounded-full bg-gray-200" />
              <div className="h-3 w-24 bg-gray-200 rounded" />
            </div>
            <div className="w-full aspect-square bg-gray-200" />
            <div className="p-3 space-y-2">
              <div className="h-3 w-20 bg-gray-200 rounded" />
              <div className="h-3 w-48 bg-gray-200 rounded" />
            </div>
          </div>
        ))}
      </div>
    );
  }

  if (isError) {
    return (
      <div className="py-8 text-center text-red-500">
        Failed to load feed. Please try again.
      </div>
    );
  }

  if (!Array.isArray(posts) || posts.length === 0) {
    return (
      <div data-cy="feed-empty-state" className="py-16 text-center w-full sm:max-w-[470px] mx-auto px-0 sm:px-4">
        <p className="text-gray-500 text-lg">Follow some users to see their posts here</p>
      </div>
    );
  }

  return (
    <div className="py-8 w-full sm:max-w-[470px] mx-auto px-0 sm:px-4">
      {posts.map((post) => (
        <PostCard
          key={post.id}
          post={post}
          currentUserId={user?.id}
          onLike={(id) => likeMutation.mutate(id)}
          onUnlike={(id) => unlikeMutation.mutate(id)}
          onDelete={(id) => deleteMutation.mutate(id)}
        />
      ))}
      {hasMore && (
        <div className="flex justify-center py-4">
          <button
            data-cy="feed-load-more-btn"
            onClick={() => setPage((p) => p + 1)}
            className="px-6 py-2 text-sm font-semibold text-[#0095F6] hover:text-[#1877F2]"
          >
            Load more
          </button>
        </div>
      )}
    </div>
  );
}
