import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';
import AuthLayout from '../../components/layout/AuthLayout';
import { authApi } from '../../api/authApi';

// Validation helpers
function isFullNameValid(name) {
  if (!name) return false;
  // English letters and spaces only, each word starts with uppercase
  const words = name.trim().split(/\s+/);
  return words.every((word) => /^[A-Z][a-zA-Z]*$/.test(word));
}

function isEmailDomainValid(email) {
  if (!email) return false;
  const domainMatch = email.match(/@[^@]+\.(\w+)$/);
  if (!domainMatch) return false;
  return ['com', 'org', 'in'].includes(domainMatch[1].toLowerCase());
}

function getPasswordStrength(password) {
  if (!password) return { score: 0, label: '', color: '' };
  let score = 0;
  if (password.length >= 8) score++;
  if (/[a-z]/.test(password)) score++;
  if (/[A-Z]/.test(password)) score++;
  if (/[0-9]/.test(password)) score++;
  if (/[^a-zA-Z0-9]/.test(password)) score++;

  if (score <= 2) return { score, label: 'Weak', color: 'bg-red-500' };
  if (score <= 3) return { score, label: 'Fair', color: 'bg-yellow-500' };
  if (score <= 4) return { score, label: 'Good', color: 'bg-blue-500' };
  return { score, label: 'Strong', color: 'bg-green-500' };
}

export default function RegisterPage() {
  const navigate = useNavigate();
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors, isValid },
  } = useForm({ mode: 'onChange' });

  const password = watch('password', '');
  const confirmPassword = watch('confirmPassword', '');
  const strength = getPasswordStrength(password);
  const passwordsMatch = !confirmPassword || password === confirmPassword;

  const onSubmit = async (data) => {
    setError('');
    setIsLoading(true);

    try {
      await authApi.register({
        fullName: data.fullName,
        email: data.email,
        username: data.username,
        password: data.password,
        confirmPassword: data.confirmPassword,
      });
      toast.success('Account created! Please log in.');
      navigate('/login');
    } catch (err) {
      const message =
        err.response?.data?.message ||
        err.response?.data?.error ||
        'Registration failed. Please try again.';
      setError(message);
    } finally {
      setIsLoading(false);
    }
  };

  const footer = (
    <p className="text-gray-500">
      Have an account?{' '}
      <Link
        to="/login"
        data-cy="register-login-link"
        className="font-semibold text-[#0095F6]"
      >
        Log in
      </Link>
    </p>
  );

  return (
    <AuthLayout footer={footer}>
      <p className="mb-4 text-center text-sm font-semibold text-gray-500">
        Sign up to see photos and videos from your friends.
      </p>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-2">
        {/* Full Name */}
        <div>
          <input
            type="text"
            placeholder="Full Name"
            autoComplete="name"
            data-cy="register-fullname-input"
            className={`w-full rounded-sm border bg-gray-50 px-3 py-2 text-sm outline-none focus:border-gray-400 ${
              errors.fullName ? 'border-red-400' : 'border-gray-300'
            }`}
            {...register('fullName', {
              required: 'Full name is required',
              validate: (value) =>
                isFullNameValid(value) ||
                'Each word must start with a capital letter (English letters only)',
            })}
          />
          {errors.fullName && (
            <p className="mt-1 text-left text-xs text-red-500">
              {errors.fullName.message}
            </p>
          )}
        </div>

        {/* Email */}
        <div>
          <input
            type="email"
            placeholder="Email"
            autoComplete="email"
            data-cy="register-email-input"
            className={`w-full rounded-sm border bg-gray-50 px-3 py-2 text-sm outline-none focus:border-gray-400 ${
              errors.email ? 'border-red-400' : 'border-gray-300'
            }`}
            {...register('email', {
              required: 'Email is required',
              pattern: {
                value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                message: 'Enter a valid email address',
              },
              validate: (value) =>
                isEmailDomainValid(value) ||
                'Email domain must be .com, .org, or .in',
            })}
          />
          {errors.email && (
            <p className="mt-1 text-left text-xs text-red-500">
              {errors.email.message}
            </p>
          )}
        </div>

        {/* Username */}
        <div>
          <input
            type="text"
            placeholder="Username"
            autoComplete="username"
            data-cy="register-username-input"
            className={`w-full rounded-sm border bg-gray-50 px-3 py-2 text-sm outline-none focus:border-gray-400 ${
              errors.username ? 'border-red-400' : 'border-gray-300'
            }`}
            {...register('username', {
              required: 'Username is required',
              pattern: {
                value: /^[a-z0-9._]+$/,
                message:
                  'Username must be lowercase letters, digits, dots, or underscores',
              },
              minLength: {
                value: 3,
                message: 'Username must be at least 3 characters',
              },
            })}
          />
          {errors.username && (
            <p className="mt-1 text-left text-xs text-red-500">
              {errors.username.message}
            </p>
          )}
        </div>

        {/* Password */}
        <div>
          <input
            type="password"
            placeholder="Password"
            autoComplete="new-password"
            data-cy="register-password-input"
            className={`w-full rounded-sm border bg-gray-50 px-3 py-2 text-sm outline-none focus:border-gray-400 ${
              errors.password ? 'border-red-400' : 'border-gray-300'
            }`}
            {...register('password', {
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
          {/* Password strength indicator */}
          {password && (
            <div className="mt-1">
              <div className="flex items-center gap-2">
                <div className="h-1.5 flex-1 rounded-full bg-gray-200">
                  <div
                    className={`h-1.5 rounded-full transition-all ${strength.color}`}
                    style={{ width: `${(strength.score / 5) * 100}%` }}
                  />
                </div>
                <span className="text-xs text-gray-500">{strength.label}</span>
              </div>
            </div>
          )}
          {errors.password && (
            <p className="mt-1 text-left text-xs text-red-500">
              {errors.password.message}
            </p>
          )}
        </div>

        {/* Confirm Password */}
        <div>
          <input
            type="password"
            placeholder="Confirm Password"
            autoComplete="new-password"
            data-cy="register-confirm-password-input"
            className={`w-full rounded-sm border bg-gray-50 px-3 py-2 text-sm outline-none focus:border-gray-400 ${
              !passwordsMatch ? 'border-red-400' : 'border-gray-300'
            }`}
            {...register('confirmPassword', {
              required: 'Please confirm your password',
              validate: (value) =>
                value === password || 'Passwords do not match',
            })}
          />
          {!passwordsMatch && confirmPassword && (
            <p
              data-cy="register-password-mismatch-error"
              className="mt-1 text-left text-xs text-red-500"
            >
              Passwords do not match
            </p>
          )}
        </div>

        {/* Error message */}
        {error && (
          <p
            data-cy="register-error-message"
            className="text-center text-sm text-red-500"
          >
            {error}
          </p>
        )}

        {/* Submit */}
        <button
          type="submit"
          disabled={!isValid || !passwordsMatch || isLoading}
          data-cy="register-submit-btn"
          className="w-full rounded-lg bg-[#0095F6] py-2 text-sm font-semibold text-white hover:bg-[#1877F2] disabled:cursor-not-allowed disabled:opacity-50"
        >
          {isLoading ? 'Signing up...' : 'Sign Up'}
        </button>
      </form>

      <p className="mt-4 text-center text-xs text-gray-500">
        By signing up, you agree to our Terms, Privacy Policy and Cookies
        Policy.
      </p>
    </AuthLayout>
  );
}
