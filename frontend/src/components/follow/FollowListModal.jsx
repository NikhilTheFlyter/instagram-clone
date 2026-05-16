import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { X, User } from 'lucide-react';
import { followApi } from '../../api/followApi';
import FollowButton from './FollowButton';

export default function FollowListModal({ userId, type, currentUserId, onClose }) {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUsers = async () => {
      setLoading(true);
      try {
        const res =
          type === 'followers'
            ? await followApi.getFollowers(userId)
            : await followApi.getFollowing(userId);
        setUsers(res.data || res || []);
      } catch (err) {
        console.error(`Failed to fetch ${type}:`, err);
      } finally {
        setLoading(false);
      }
    };
    fetchUsers();
  }, [userId, type]);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/65">
      <div className="bg-white rounded-xl w-[400px] max-h-[400px] flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between px-4 py-2 border-b border-gray-300">
          <div />
          <h2 data-cy="follow-modal-title" className="text-base font-semibold capitalize">
            {type}
          </h2>
          <button data-cy="follow-modal-close-btn" onClick={onClose}>
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* User list */}
        <div className="overflow-y-auto flex-1 py-2">
          {loading ? (
            <div className="flex justify-center py-8">
              <div className="w-6 h-6 border-2 border-gray-300 border-t-gray-800 rounded-full animate-spin" />
            </div>
          ) : users.length === 0 ? (
            <p className="text-center text-sm text-gray-500 py-8">
              No {type} yet.
            </p>
          ) : (
            users.map((u) => (
              <div
                key={u.id}
                data-cy="follow-modal-user-item"
                className="flex items-center justify-between px-4 py-2"
              >
                <Link to={`/profile/${u.id}`} onClick={onClose} className="flex items-center gap-3">
                  {u.profilePicture ? (
                    <img
                      src={u.profilePicture}
                      alt={u.username}
                      className="w-8 h-8 rounded-full object-cover"
                    />
                  ) : (
                    <div className="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center">
                      <User className="w-4 h-4 text-gray-500" />
                    </div>
                  )}
                  <div>
                    <p className="text-sm font-semibold">{u.username}</p>
                    <p className="text-xs text-gray-500">{u.fullName}</p>
                  </div>
                </Link>
                {u.id !== currentUserId && (
                  <FollowButton
                    targetUserId={u.id}
                    isFollowing={u.isFollowing || false}
                  />
                )}
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
