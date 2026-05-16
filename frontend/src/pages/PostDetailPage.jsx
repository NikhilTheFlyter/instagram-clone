import { useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Heart, Trash2, User } from 'lucide-react';
import { postApi } from '../api/postApi';
import useAuthStore from '../store/useAuthStore';
import DeleteConfirmModal from '../components/post/DeleteConfirmModal';
import { timeAgo } from '../utils/timeAgo';
import toast from 'react-hot-toast';

export default function PostDetailPage() {
  const { postId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuthStore();
  const queryClient = useQueryClient();
  const [showDeleteModal, setShowDeleteModal] = useState(false);

  const { data, isLoading, isError } = useQuery({
    queryKey: ['post', postId],
    queryFn: () => postApi.getPost(postId),
    enabled: !!postId,
  });

  const post = data?.data || data || {};
  const isOwner = post.userId === user?.id || post.user?.id === user?.id;
  const isLiked = post.isLiked || false;
  const hashtags = post.hashtags || [];

  const likeMutation = useMutation({
    mutationFn: () => postApi.likePost(postId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['post', postId] }),
  });

  const unlikeMutation = useMutation({
    mutationFn: () => postApi.unlikePost(postId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['post', postId] }),
  });

  const deleteMutation = useMutation({
    mutationFn: () => postApi.deletePost(postId),
    onSuccess: () => {
      toast.success('Post deleted');
      navigate('/');
    },
    onError: () => toast.error('Failed to delete post'),
  });

  if (isLoading) {
    return (
      <div className="py-8 flex justify-center">
        <div className="w-8 h-8 border-2 border-gray-300 border-t-gray-800 rounded-full animate-spin" />
      </div>
    );
  }

  if (isError || !post.id) {
    return (
      <div className="py-16 text-center text-gray-500">
        Post not found.
      </div>
    );
  }

  return (
    <div className="py-8 max-w-[935px] mx-auto">
      <div className="bg-white border border-gray-300 rounded-lg overflow-hidden flex flex-col md:flex-row">
        {/* Media */}
        <div className="md:w-[60%] bg-black flex items-center justify-center">
          <img
            data-cy="post-detail-media"
            src={post.mediaUrl || post.imageUrl}
            alt="Post"
            className="w-full object-contain max-h-[600px]"
          />
        </div>

        {/* Details */}
        <div className="md:w-[40%] flex flex-col">
          {/* Author header */}
          <div className="flex items-center justify-between px-4 py-3 border-b border-gray-300">
            <Link
              to={`/profile/${post.userId || post.user?.id}`}
              data-cy="post-detail-author"
              className="flex items-center gap-3"
            >
              {post.user?.profilePicture ? (
                <img
                  src={post.user.profilePicture}
                  alt={post.user.username}
                  className="w-8 h-8 rounded-full object-cover"
                />
              ) : (
                <div className="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center">
                  <User className="w-4 h-4 text-gray-500" />
                </div>
              )}
              <span className="text-sm font-semibold">{post.user?.username || 'unknown'}</span>
            </Link>
            {isOwner && (
              <button
                data-cy="post-detail-delete-btn"
                onClick={() => setShowDeleteModal(true)}
                className="text-red-500 hover:text-red-600"
              >
                <Trash2 className="w-5 h-5" />
              </button>
            )}
          </div>

          {/* Caption & Hashtags */}
          <div className="flex-1 overflow-y-auto px-4 py-3">
            <div data-cy="post-detail-caption" className="text-sm">
              <Link
                to={`/profile/${post.userId || post.user?.id}`}
                className="font-semibold mr-1"
              >
                {post.user?.username || 'unknown'}
              </Link>
              {post.caption}
            </div>
            {hashtags.length > 0 && (
              <div className="mt-2 flex flex-wrap gap-1">
                {hashtags.map((tag, idx) => {
                  const cleanTag = tag.startsWith('#') ? tag.slice(1) : tag;
                  return (
                    <Link
                      key={idx}
                      to={`/search?q=${encodeURIComponent('#' + cleanTag)}`}
                      className="text-sm text-[#00376B] hover:underline"
                    >
                      #{cleanTag}
                    </Link>
                  );
                })}
              </div>
            )}
            <p className="text-[10px] text-gray-400 uppercase mt-3">
              {timeAgo(post.createdAt)}
            </p>
          </div>

          {/* Action bar */}
          <div className="border-t border-gray-300 px-4 py-3">
            <div className="flex items-center gap-3">
              <button
                data-cy="post-detail-like-btn"
                onClick={() =>
                  isLiked ? unlikeMutation.mutate() : likeMutation.mutate()
                }
              >
                <Heart
                  className={`w-6 h-6 ${
                    isLiked
                      ? 'text-[#ED4956] fill-[#ED4956]'
                      : 'text-gray-800 hover:text-gray-500'
                  }`}
                />
              </button>
            </div>
            <p data-cy="post-detail-like-count" className="text-sm font-semibold mt-1">
              {post.likeCount ?? post.likes ?? 0} {(post.likeCount ?? post.likes ?? 0) === 1 ? 'like' : 'likes'}
            </p>
          </div>
        </div>
      </div>

      {/* Delete modal */}
      <DeleteConfirmModal
        isOpen={showDeleteModal}
        onConfirm={() => {
          setShowDeleteModal(false);
          deleteMutation.mutate();
        }}
        onCancel={() => setShowDeleteModal(false)}
      />
    </div>
  );
}
