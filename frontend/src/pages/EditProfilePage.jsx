import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { User } from 'lucide-react';
import { authApi } from '../api/authApi';
import useAuthStore from '../store/useAuthStore';
import toast from 'react-hot-toast';

export default function EditProfilePage() {
  const navigate = useNavigate();
  const { user, updateUser } = useAuthStore();

  const [fullName, setFullName] = useState(user?.fullName || '');
  const [bio, setBio] = useState(user?.bio || '');
  const [profilePicture, setProfilePicture] = useState(user?.profilePicture || '');
  const [saving, setSaving] = useState(false);

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      const res = await authApi.updateProfile(user?.id, { fullName, bio, profilePicture });
      const updatedUser = res?.data || res;
      updateUser?.({ ...user, fullName, bio, profilePicture, ...updatedUser });
      toast.success('Profile updated');
      navigate(`/profile/${user?.id}`);
    } catch (err) {
      toast.error(err?.response?.data?.message || 'Failed to update profile');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="py-8 max-w-[470px] mx-auto">
      <h1 className="text-xl font-semibold mb-6">Edit Profile</h1>

      <form onSubmit={handleSave} className="space-y-6">
        {/* Avatar preview */}
        <div className="flex items-center gap-4">
          {profilePicture ? (
            <img
              data-cy="edit-profile-avatar"
              src={profilePicture}
              alt="Profile"
              className="w-16 h-16 rounded-full object-cover border border-gray-300"
            />
          ) : (
            <div
              data-cy="edit-profile-avatar"
              className="w-16 h-16 rounded-full bg-gray-200 flex items-center justify-center border border-gray-300"
            >
              <User className="w-8 h-8 text-gray-400" />
            </div>
          )}
          <div>
            <p className="font-semibold text-sm">{user?.username}</p>
            <p className="text-xs text-gray-500">{user?.email}</p>
          </div>
        </div>

        {/* Full name */}
        <div>
          <label className="block text-sm font-semibold mb-1">Full Name</label>
          <input
            data-cy="edit-profile-fullname-input"
            type="text"
            value={fullName}
            onChange={(e) => setFullName(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-gray-500"
            placeholder="Full name"
          />
        </div>

        {/* Bio */}
        <div>
          <label className="block text-sm font-semibold mb-1">Bio</label>
          <textarea
            data-cy="edit-profile-bio-input"
            value={bio}
            onChange={(e) => setBio(e.target.value)}
            rows={3}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm resize-none focus:outline-none focus:border-gray-500"
            placeholder="Bio"
          />
        </div>

        {/* Profile picture URL */}
        <div>
          <label className="block text-sm font-semibold mb-1">Profile Picture URL</label>
          <input
            data-cy="edit-profile-picture-input"
            type="text"
            value={profilePicture}
            onChange={(e) => setProfilePicture(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-gray-500"
            placeholder="https://example.com/photo.jpg"
          />
        </div>

        {/* Buttons */}
        <div className="flex gap-3">
          <button
            data-cy="edit-profile-save-btn"
            type="submit"
            disabled={saving}
            className="px-6 py-2 text-sm font-semibold rounded-lg bg-[#0095F6] hover:bg-[#1877F2] text-white disabled:opacity-50"
          >
            {saving ? 'Saving...' : 'Save'}
          </button>
          <button
            data-cy="edit-profile-cancel-btn"
            type="button"
            onClick={() => navigate(`/profile/${user?.id}`)}
            className="px-6 py-2 text-sm font-semibold rounded-lg bg-gray-100 hover:bg-gray-200 border border-gray-300"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
}
