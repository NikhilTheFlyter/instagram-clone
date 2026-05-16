import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ImagePlus } from 'lucide-react';
import { postApi } from '../api/postApi';
import useAuthStore from '../store/useAuthStore';
import toast from 'react-hot-toast';

export default function CreatePostPage() {
  const navigate = useNavigate();
  const { user } = useAuthStore();

  const [mediaUrl, setMediaUrl] = useState('');
  const [caption, setCaption] = useState('');
  const [hashtagsInput, setHashtagsInput] = useState('');
  const [privacy, setPrivacy] = useState('PUBLIC');
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!mediaUrl.trim()) {
      toast.error('Please provide a media URL');
      return;
    }

    setSubmitting(true);

    // Parse hashtags: split by comma, trim, prefix with # if missing
    const hashtags = hashtagsInput
      .split(',')
      .map((tag) => tag.trim())
      .filter((tag) => tag.length > 0)
      .map((tag) => (tag.startsWith('#') ? tag : `#${tag}`));

    try {
      await postApi.createPost({
        mediaUrl,
        caption,
        hashtags,
        privacy,
      });
      toast.success('Post created!');
      navigate(`/profile/${user?.id}`);
    } catch (err) {
      toast.error(err?.response?.data?.message || 'Failed to create post');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="py-8 w-full sm:max-w-[470px] mx-auto">
      <h1 className="text-xl font-semibold mb-6">Create Post</h1>

      <form onSubmit={handleSubmit} className="space-y-5">
        {/* Media URL input */}
        <div>
          <label className="block text-sm font-semibold mb-1">Media URL</label>
          <input
            data-cy="create-post-media-input"
            type="text"
            value={mediaUrl}
            onChange={(e) => setMediaUrl(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-gray-500"
            placeholder="https://example.com/image.jpg"
          />
        </div>

        {/* Media preview */}
        <div data-cy="create-post-media-preview" className="w-full">
          {mediaUrl ? (
            <img
              src={mediaUrl}
              alt="Preview"
              className="w-full aspect-square object-cover rounded-lg border border-gray-300"
              onError={(e) => {
                e.target.style.display = 'none';
              }}
            />
          ) : (
            <div className="w-full aspect-square rounded-lg border-2 border-dashed border-gray-300 flex flex-col items-center justify-center text-gray-400">
              <ImagePlus className="w-12 h-12 mb-2" />
              <p className="text-sm">Enter a URL above to preview</p>
            </div>
          )}
        </div>

        {/* Caption */}
        <div>
          <label className="block text-sm font-semibold mb-1">Caption</label>
          <textarea
            data-cy="create-post-caption-input"
            value={caption}
            onChange={(e) => setCaption(e.target.value)}
            rows={3}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm resize-none focus:outline-none focus:border-gray-500"
            placeholder="Write a caption..."
          />
        </div>

        {/* Hashtags */}
        <div>
          <label className="block text-sm font-semibold mb-1">Hashtags</label>
          <input
            data-cy="create-post-hashtags-input"
            type="text"
            value={hashtagsInput}
            onChange={(e) => setHashtagsInput(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-gray-500"
            placeholder="travel, nature, photography (comma separated)"
          />
          {hashtagsInput && (
            <div className="mt-2 flex flex-wrap gap-1">
              {hashtagsInput
                .split(',')
                .map((tag) => tag.trim())
                .filter(Boolean)
                .map((tag, idx) => (
                  <span
                    key={idx}
                    className="text-xs bg-gray-100 text-[#00376B] px-2 py-0.5 rounded"
                  >
                    #{tag.replace(/^#/, '')}
                  </span>
                ))}
            </div>
          )}
        </div>

        {/* Privacy */}
        <div>
          <label className="block text-sm font-semibold mb-1">Privacy</label>
          <select
            data-cy="create-post-privacy-select"
            value={privacy}
            onChange={(e) => setPrivacy(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-gray-500 bg-white"
          >
            <option value="PUBLIC">Public</option>
            <option value="FRIENDS">Friends</option>
            <option value="PRIVATE">Private</option>
          </select>
        </div>

        {/* Submit */}
        <button
          data-cy="create-post-submit-btn"
          type="submit"
          disabled={submitting}
          className="w-full py-2 text-sm font-semibold rounded-lg bg-[#0095F6] hover:bg-[#1877F2] text-white disabled:opacity-50"
        >
          {submitting ? 'Posting...' : 'Share'}
        </button>
      </form>
    </div>
  );
}
