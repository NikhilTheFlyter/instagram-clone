import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';
import AuthLayout from '../../components/layout/AuthLayout';
import { authApi } from '../../api/authApi';

export default function ResetPasswordPage() {
  const navigate = useNavigate();
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors, isValid },
  } = useForm({ mode: 'onChange' });

  const newPassword = watch('newPassword', '');
  const confirmPassword = watch('confirmPassword', '');
  const passwordsMatch = !confirmPassword || newPassword === confirmPassword;

  const onSubmit = async (data) => {
    setError('');
    setIsLoading(true);

    try {
      await authApi.resetPassword({
        email: data.email,
        token: data.token,
        newPassword: data.newPassword,
      });
      setSuccess(true);
      toast.success('Password reset successfully!');
      setTimeout(() => navigate('/login'), 2000);
    } catch (err) {
      const message =
        err.response?.data?.message ||
        err.response?.data?.error ||
        'Reset failed. Please check your token and try again.';
      setError(message);
    } finally {
      setIsLoading(false);
    }
  };

  const footer = (
    <p className="text-gray-500">
      Remember your password?{' '}
      <Link to="/login" className="font-semibold text-[#0095F6]">
        Log in
      </Link>
    </p>
  );

  return (
    <AuthLayout footer={footer}>
      <h2 className="mb-2 text-base font-semibold">Reset Your Password</h2>
      <p className="mb-4 text-sm text-gray-500">
        Enter your email, reset token, and new password.
      </p>

      {success ? (
        <div
          data-cy="reset-success-message"
          className="rounded-md bg-green-50 border border-green-200 p-4 text-sm text-green-700"
        >
          Password reset successfully! Redirecting to login...
        </div>
      ) : (
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-2">
          {/* Email */}
          <input
            type="email"
            placeholder="Email"
            autoComplete="email"
            data-cy="reset-email-input"
            className="w-full rounded-sm border border-gray-300 bg-gray-50 px-3 py-2 text-sm outline-none focus:border-gray-400"
            {...register('email', {
              required: 'Email is required',
              pattern: {
                value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                message: 'Enter a valid email address',
              },
            })}
          />

          {/* Reset Token */}
          <input
            type="text"
            placeholder="Reset Token"
            autoComplete="off"
            data-cy="reset-token-input"
            className="w-full rounded-sm border border-gray-300 bg-gray-50 px-3 py-2 text-sm outline-none focus:border-gray-400"
            {...register('token', { required: 'Reset token is required' })}
          />

          {/* New Password */}
          <div>
            <input
              type="password"
              placeholder="New Password"
              autoComplete="new-password"
              data-cy="reset-new-password-input"
              className={`w-full rounded-sm border bg-gray-50 px-3 py-2 text-sm outline-none focus:border-gray-400 ${
                errors.newPassword ? 'border-red-400' : 'border-gray-300'
              }`}
              {...register('newPassword', {
                required: 'Password is required',
                minLength: {
                  value: 8,
                  message: 'Password must be at least 8 characters',
                },
                maxLength: {
                  value: 16,
                  message: 'Password must be at most 16 characters',
                },
                validate: (value) => {
                  if (!/[a-z]/.test(value))
                    return 'Must contain a lowercase letter';
                  if (!/[A-Z]/.test(value))
                    return 'Must contain an uppercase letter';
                  if (!/[0-9]/.test(value)) return 'Must contain a digit';
                  if (!/[^a-zA-Z0-9]/.test(value))
                    return 'Must contain a special character';
                  return true;
                },
              })}
            />
            {errors.newPassword && (
              <p className="mt-1 text-left text-xs text-red-500">
                {errors.newPassword.message}
              </p>
            )}
          </div>

          {/* Confirm Password */}
          <div>
            <input
              type="password"
              placeholder="Confirm New Password"
              autoComplete="new-password"
              data-cy="reset-confirm-password-input"
              className={`w-full rounded-sm border bg-gray-50 px-3 py-2 text-sm outline-none focus:border-gray-400 ${
                !passwordsMatch ? 'border-red-400' : 'border-gray-300'
              }`}
              {...register('confirmPassword', {
                required: 'Please confirm your password',
                validate: (value) =>
                  value === newPassword || 'Passwords do not match',
              })}
            />
            {!passwordsMatch && confirmPassword && (
              <p
                data-cy="reset-password-mismatch-error"
                className="mt-1 text-left text-xs text-red-500"
              >
                Passwords do not match
              </p>
            )}
          </div>

          {/* Error message */}
          {error && (
            <p
              data-cy="reset-error-message"
              className="text-center text-sm text-red-500"
            >
              {error}
            </p>
          )}

          {/* Submit */}
          <button
            type="submit"
            disabled={!isValid || !passwordsMatch || isLoading}
            data-cy="reset-submit-btn"
            className="w-full rounded-lg bg-[#0095F6] py-2 text-sm font-semibold text-white hover:bg-[#1877F2] disabled:cursor-not-allowed disabled:opacity-50"
          >
            {isLoading ? 'Resetting...' : 'Reset Password'}
          </button>
        </form>
      )}
    </AuthLayout>
  );
}
