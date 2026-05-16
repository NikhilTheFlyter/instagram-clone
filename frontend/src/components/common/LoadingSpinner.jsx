export default function LoadingSpinner() {
  return (
    <div
      data-cy="loading-spinner"
      className="flex items-center justify-center min-h-[200px]"
    >
      <div className="h-8 w-8 animate-spin rounded-full border-4 border-gray-300 border-t-[#0095F6]" />
    </div>
  );
}
