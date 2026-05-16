import { useState } from 'react';
import { followApi } from '../../api/followApi';

export default function FollowButton({ targetUserId, isFollowing: initialIsFollowing, onFollowChange }) {
  const [isFollowing, setIsFollowing] = useState(initialIsFollowing);
  const [isHovered, setIsHovered] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleClick = async () => {
    if (loading) return;
    setLoading(true);
    try {
      if (isFollowing) {
        await followApi.unfollowUser(targetUserId);
        setIsFollowing(false);
        onFollowChange?.(false);
      } else {
        await followApi.followUser(targetUserId);
        setIsFollowing(true);
        onFollowChange?.(true);
      }
    } catch (err) {
      console.error('Follow action failed:', err);
    } finally {
      setLoading(false);
    }
  };

  if (isFollowing) {
    return (
      <button
        data-cy="unfollow-btn"
        onClick={handleClick}
        disabled={loading}
        onMouseEnter={() => setIsHovered(true)}
        onMouseLeave={() => setIsHovered(false)}
        className={`px-4 py-1.5 text-sm font-semibold rounded-lg border transition-colors ${
          isHovered
            ? 'border-red-400 text-red-500 bg-red-50'
            : 'border-gray-300 text-gray-800 bg-white'
        } disabled:opacity-50`}
      >
        {isHovered ? 'Unfollow' : 'Following'}
      </button>
    );
  }

  return (
    <button
      data-cy="follow-btn"
      onClick={handleClick}
      disabled={loading}
      className="px-4 py-1.5 text-sm font-semibold rounded-lg bg-[#0095F6] hover:bg-[#1877F2] text-white disabled:opacity-50"
    >
      Follow
    </button>
  );
}
