import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { Lock } from 'lucide-react';
import AuthLayout from '../../components/layout/AuthLayout';
import { authApi } from '../../api/authApi';

export default function ForgotPasswordPage() {
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { isValid },
  } = useForm({ mode: 'onChange' });

  const onSubmit = async (data) => {
    setError('');
    setSuccessMessage('');
    setIsLoading(true);

    try {
      const res = await authApi.forgotPassword({ email: data.email });
      const token = res.data?.token || res.data?.resetToken || '';
      setSuccessMessage(
        token
          ? `Reset token: ${token}`
          : 'If the email exists, a reset link has been sent.'
      );
    } catch (err) {
      const message =
        err.response?.data?.message ||
        err.response?.data?.error ||
        'Something went wrong. Please try again.';
      setError(message);
    } finally {
      setIsLoading(false);
    }
  };

  const footer = (
    <p className="text-gray-500">
      <Link
        to="/login"
        data-cy="forgot-login-link"
        className="font-semibold text-[#0095F6]"
      >
        Back to Login
      </Link>
    </p>
  );

  return (
    <AuthLayout footer={footer}>
      <div className="mb-4 flex justify-center">
        <div className="flex h-24 w-24 items-center justify-center rounded-full border-2 border-black">
          <Lock size={48} strokeWidth={1.5} />
        </div>
      </div>

      <h2 className="mb-2 text-base font-semibold">Trouble logging in?</h2>
      <p className="mb-4 text-sm text-gray-500">
        Enter your email and we&apos;ll send you a link to get back into your
        account.
      </p>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
        <input
          type="email"
          placeholder="Email"
          autoComplete="email"
          data-cy="forgot-email-input"
          className="w-full rounded-sm border border-gray-300 bg-gray-50 px-3 py-2 text-sm outline-none focus:border-gray-400"
          {...register('email', {
            required: true,
            pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
          })}
        />

        <button
          type="submit"
          disabled={!isValid || isLoading}
          data-cy="forgot-submit-btn"
          className="w-full rounded-lg bg-[#0095F6] py-2 text-sm font-semibold text-white hover:bg-[#1877F2] disabled:cursor-not-allowed disabled:opacity-50"
        >
          {isLoading ? 'Sending...' : 'Send Reset Link'}
        </button>
      </form>

      {/* Success message */}
      {successMessage && (
        <div
          data-cy="forgot-success-message"
          className="mt-4 rounded-md bg-green-50 border border-green-200 p-3 text-sm text-green-700"
        >
          {successMessage}
        </div>
      )}

      {/* Error message */}
      {error && (
        <p className="mt-4 text-center text-sm text-red-500">{error}</p>
      )}

      {/* Divider */}
      <div className="my-5 flex items-center gap-4">
        <div className="h-px flex-1 bg-gray-300" />
        <span className="text-xs font-semibold uppercase text-gray-500">or</span>
        <div className="h-px flex-1 bg-gray-300" />
      </div>

      <Link
        to="/register"
        className="text-sm font-semibold text-[#00376B]"
      >
        Create new account
      </Link>
    </AuthLayout>
  );
}
