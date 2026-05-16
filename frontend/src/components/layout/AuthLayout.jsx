export default function AuthLayout({ children, footer }) {
  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4 py-8">
      <div className="w-full max-w-[350px]">
        {/* Main card */}
        <div className="border border-gray-300 bg-white px-10 py-8 text-center">
          {/* Instagram logo */}
          <h1
            className="mb-4 text-4xl"
            style={{ fontFamily: "'Grand Hotel', cursive" }}
          >
            Instagram
          </h1>
          {children}
        </div>

        {/* Footer card (e.g., "Don't have an account? Sign up") */}
        {footer && (
          <div className="mt-3 border border-gray-300 bg-white px-10 py-5 text-center text-sm">
            {footer}
          </div>
        )}
      </div>
    </div>
  );
}
