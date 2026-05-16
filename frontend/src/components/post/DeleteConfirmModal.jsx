export default function DeleteConfirmModal({ isOpen, onConfirm, onCancel }) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/65">
      <div className="bg-white rounded-xl w-[400px] overflow-hidden">
        <div className="pt-8 pb-4 px-8 text-center">
          <h2 data-cy="delete-modal-title" className="text-xl font-semibold mb-2">
            Delete post?
          </h2>
          <p className="text-sm text-gray-500">
            This can&apos;t be undone and it will be removed from your profile.
          </p>
        </div>
        <div className="border-t border-gray-300">
          <button
            data-cy="delete-modal-confirm-btn"
            onClick={onConfirm}
            className="w-full py-3 text-sm font-bold text-red-500 hover:bg-gray-50 border-b border-gray-300"
          >
            Delete
          </button>
          <button
            data-cy="delete-modal-cancel-btn"
            onClick={onCancel}
            className="w-full py-3 text-sm hover:bg-gray-50"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
}
