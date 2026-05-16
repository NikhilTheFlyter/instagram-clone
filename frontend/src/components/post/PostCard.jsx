import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Heart, MoreHorizontal, Trash2, User } from 'lucide-react';
import { timeAgo } from '../../utils/timeAgo';
import DeleteConfirmModal from './DeleteConfirmModal';

export default function PostCard({ post, onLike, onUnlike, onDelete, currentUserId }) {
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [showMenu, setShowMenu] = useState(false);

  const isOwner = post.userId === currentUserId || post.user?.id === currentUserId;
  const isLiked = post.isLiked || false;

  const handleLikeToggle = () => {
    if (isLiked) {
      onUnlike?.(post.id);
    } else {
      onLike?.(post.id);
    }
  };

  const handleDelete = () => {
    setShowDeleteModal(false);
    setShowMenu(false);
    onDelete?.(post.id);
  };

  const hashtags = post.hashtags || [];

  return (
    <div className="bg-white border border-gray-300 rounded-lg mb-4 max-w-[470px] mx-auto">
      {/* Header */}
      <div className="flex items-center justify-between px-3 py-2">
        <Link to={`/profile/${post.userId || post.user?.id}`} className="flex items-center gap-2">
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
          <span data-cy="post-card-username" className="text-sm font-semibold">
            {post.user?.username || 'unknown'}
          </span>
        </Link>
        {isOwner && (
          <div className="relative">
            <button
              onClick={() => setShowMenu(!showMenu)}
              className="text-gray-600 hover:text-gray-900"
            >
              <MoreHorizontal className="w-5 h-5" />
            </button>
            {showMenu && (
              <div className="absolute right-0 top-8 bg-white border border-gray-300 rounded-lg shadow-lg z-10 w-36">
                <button
                  data-cy="post-card-delete-btn"
                  onClick={() => {
                    setShowMenu(false);
                    setShowDeleteModal(true);
                  }}
                  className="flex items-center gap-2 w-full px-4 py-2 text-sm text-red-500 hover:bg-gray-50"
                >
                  <Trash2 className="w-4 h-4" />
                  Delete
                </button>
              </div>
            )}
          </div>
        )}
      </div>

      {/* Media */}
      <div data-cy="post-card-media" className="w-full">
        <img
          src={post.mediaUrl || post.imageUrl}
          alt="Post"
          className="w-full object-cover"
          style={{ aspectRatio: '1/1' }}
        />
      </div>

      {/* Action bar */}
      <div className="px-3 pt-2">
        <div className="flex items-center gap-3">
          <button data-cy="post-card-like-btn" onClick={handleLikeToggle}>
            <Heart
              className={`w-6 h-6 ${
                isLiked
                  ? 'text-[#ED4956] fill-[#ED4956]'
                  : 'text-gray-800 hover:text-gray-500'
              }`}
            />
          </button>
        </div>
        <p data-cy="post-card-like-count" className="text-sm font-semibold mt-1">
          {post.likeCount ?? post.likes ?? 0} {(post.likeCount ?? post.likes ?? 0) === 1 ? 'like' : 'likes'}
        </p>
      </div>

      {/* Caption */}
      <div className="px-3 pb-1">
        <p data-cy="post-card-caption" className="text-sm">
          <Link to={`/profile/${post.userId || post.user?.id}`} className="font-semibold mr-1">
            {post.user?.username || 'unknown'}
          </Link>
          {post.caption}
        </p>
      </div>

      {/* Hashtags */}
      {hashtags.length > 0 && (
        <div data-cy="post-card-hashtags" className="px-3 pb-1 flex flex-wrap gap-1">
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

      {/* Timestamp */}
      <div className="px-3 pb-3">
        <Link to={`/post/${post.id}`}>
          <p data-cy="post-card-timestamp" className="text-[10px] text-gray-400 uppercase mt-1">
            {timeAgo(post.createdAt)}
          </p>
        </Link>
      </div>

      {/* Delete confirmation modal */}
      <DeleteConfirmModal
        isOpen={showDeleteModal}
        onConfirm={handleDelete}
        onCancel={() => setShowDeleteModal(false)}
      />
    </div>
  );
}
