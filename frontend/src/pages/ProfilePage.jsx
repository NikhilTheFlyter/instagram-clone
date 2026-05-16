import { useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { User, Grid3X3 } from 'lucide-react';
import { authApi } from '../api/authApi';
import { postApi } from '../api/postApi';
import { followApi } from '../api/followApi';
import useAuthStore from '../store/useAuthStore';
import FollowButton from '../components/follow/FollowButton';
import FollowListModal from '../components/follow/FollowListModal';

export default function ProfilePage() {
  const { userId } = useParams();
  const navigate = useNavigate();
  const { user: currentUser } = useAuthStore();
  const queryClient = useQueryClient();
  const [followModal, setFollowModal] = useState(null); // 'followers' | 'following' | null

  const isOwnProfile = currentUser?.id === userId;

  // Fetch profile
  const { data: profileRes, isLoading: profileLoading } = useQuery({
    queryKey: ['profile', userId],
    queryFn: () => authApi.getProfile(userId),
    enabled: !!userId,
  });
  const profile = profileRes?.data || profileRes || {};

  // Fetch user posts
  const { data: postsRes, isLoading: postsLoading } = useQuery({
    queryKey: ['userPosts', userId],
    queryFn: () => postApi.getUserPosts(userId),
    enabled: !!userId,
  });
  const posts = postsRes?.data?.posts || postsRes?.data || postsRes?.posts || postsRes || [];

  // Fetch follow stats
  const { data: statsRes } = useQuery({
    queryKey: ['followStats', userId],
    queryFn: () => followApi.getStats(userId),
    enabled: !!userId,
  });
  const stats = statsRes?.data || statsRes || {};

  // Check if following
  const { data: followingRes } = useQuery({
    queryKey: ['isFollowing', userId],
    queryFn: () => followApi.isFollowing(userId),
    enabled: !!userId && !isOwnProfile,
  });
  const isFollowingUser = followingRes?.data?.isFollowing ?? followingRes?.isFollowing ?? false;

  const handleFollowChange = () => {
    queryClient.invalidateQueries({ queryKey: ['followStats', userId] });
    queryClient.invalidateQueries({ queryKey: ['isFollowing', userId] });
  };

  if (profileLoading) {
    return (
      <div className="py-8 flex justify-center">
        <div className="w-8 h-8 border-2 border-gray-300 border-t-gray-800 rounded-full animate-spin" />
      </div>
    );
  }

  return (
    <div className="py-8">
      {/* Profile header */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center gap-8 mb-10 px-4">
        {/* Avatar */}
        <div className="flex-shrink-0">
          {profile.profilePicture ? (
            <img
              data-cy="profile-avatar"
              src={profile.profilePicture}
              alt={profile.username}
              className="w-[150px] h-[150px] rounded-full object-cover border border-gray-300"
            />
          ) : (
            <div
              data-cy="profile-avatar"
              className="w-[150px] h-[150px] rounded-full bg-gray-200 flex items-center justify-center border border-gray-300"
            >
              <User className="w-16 h-16 text-gray-400" />
            </div>
          )}
        </div>

        {/* Info */}
        <div className="flex-1">
          <div className="flex items-center gap-4 mb-4">
            <h1 data-cy="profile-username" className="text-xl font-normal">
              {profile.username}
            </h1>
            {isOwnProfile ? (
              <button
                data-cy="profile-edit-btn"
                onClick={() => navigate('/profile/edit')}
                className="px-4 py-1.5 text-sm font-semibold bg-gray-100 hover:bg-gray-200 rounded-lg border border-gray-300"
              >
                Edit Profile
              </button>
            ) : (
              <div data-cy="profile-follow-btn">
                <FollowButton
                  targetUserId={userId}
                  isFollowing={isFollowingUser}
                  onFollowChange={handleFollowChange}
                />
              </div>
            )}
          </div>

          {/* Stats */}
          <div className="flex flex-col sm:flex-row gap-2 sm:gap-8 mb-4">
            <div>
              <span data-cy="profile-post-count" className="font-semibold">
                {Array.isArray(posts) ? posts.length : 0}
              </span>{' '}
              <span className="text-gray-500">posts</span>
            </div>
            <button onClick={() => setFollowModal('followers')} className="hover:opacity-70">
              <span data-cy="profile-followers-count" className="font-semibold">
                {stats.followersCount ?? stats.followers ?? 0}
              </span>{' '}
              <span className="text-gray-500">followers</span>
            </button>
            <button onClick={() => setFollowModal('following')} className="hover:opacity-70">
              <span data-cy="profile-following-count" className="font-semibold">
                {stats.followingCount ?? stats.following ?? 0}
              </span>{' '}
              <span className="text-gray-500">following</span>
            </button>
          </div>

          {/* Bio */}
          <div>
            <p data-cy="profile-fullname" className="font-semibold text-sm">
              {profile.fullName}
            </p>
            {profile.bio && (
              <p data-cy="profile-bio" className="text-sm mt-1 whitespace-pre-wrap">
                {profile.bio}
              </p>
            )}
          </div>
        </div>
      </div>

      {/* Posts grid */}
      <div className="border-t border-gray-300 pt-4">
        <div className="flex justify-center gap-1 mb-4">
          <div className="flex items-center gap-1 text-xs font-semibold uppercase tracking-wider border-t border-gray-800 pt-2 -mt-[17px]">
            <Grid3X3 className="w-3 h-3" />
            Posts
          </div>
        </div>

        {postsLoading ? (
          <div className="grid grid-cols-3 gap-0.5 sm:gap-1">
            {[1, 2, 3, 4, 5, 6].map((i) => (
              <div key={i} className="aspect-square bg-gray-200 animate-pulse" />
            ))}
          </div>
        ) : !Array.isArray(posts) || posts.length === 0 ? (
          <div className="text-center py-16 text-gray-400">
            <Grid3X3 className="w-12 h-12 mx-auto mb-2" />
            <p className="text-lg font-light">No Posts Yet</p>
          </div>
        ) : (
          <div data-cy="profile-posts-grid" className="grid grid-cols-3 gap-0.5 sm:gap-1">
            {posts.map((post) => (
              <Link
                key={post.id}
                to={`/post/${post.id}`}
                data-cy="profile-post-item"
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

      {/* Follow modals */}
      {followModal && (
        <div data-cy={followModal === 'followers' ? 'profile-followers-modal' : 'profile-following-modal'}>
          <FollowListModal
            userId={userId}
            type={followModal}
            currentUserId={currentUser?.id}
            onClose={() => setFollowModal(null)}
          />
        </div>
      )}
    </div>
  );
}
