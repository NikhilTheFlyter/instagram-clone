import { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';
import AuthLayout from '../../components/layout/AuthLayout';
import { authApi } from '../../api/authApi';
import { useAuth } from '../../hooks/useAuth';

export default function LoginPage() {
  const { login } = useAuth();
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [failCount, setFailCount] = useState(0);
  const [circuitBreakerSeconds, setCircuitBreakerSeconds] = useState(0);
  const [isCircuitOpen, setIsCircuitOpen] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { isValid },
  } = useForm({ mode: 'onChange' });

  // Poll login status when circuit breaker is active
  const checkLoginStatus = useCallback(async () => {
    try {
      const res = await authApi.getLoginStatus();
      const data = res.data;
      if (data.state === 'OPEN' || data.locked) {
        setIsCircuitOpen(true);
        const waitSeconds = data.waitSeconds || data.retryAfterSeconds || 30;
        setCircuitBreakerSeconds(waitSeconds);
      } else {
        setIsCircuitOpen(false);
        setCircuitBreakerSeconds(0);
      }
    } catch {
      // Status endpoint not available, ignore
    }
  }, []);

  // Countdown timer for circuit breaker
  useEffect(() => {
    if (circuitBreakerSeconds <= 0) {
      setIsCircuitOpen(false);
      return;
    }

    const interval = setInterval(() => {
      setCircuitBreakerSeconds((prev) => {
        if (prev <= 1) {
          setIsCircuitOpen(false);
          setFailCount(0);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(interval);
  }, [circuitBreakerSeconds]);

  const onSubmit = async (data) => {
    if (isCircuitOpen) return;

    setError('');
    setIsLoading(true);

    try {
      await login({ username: data.username, password: data.password });
      toast.success('Logged in successfully!');
    } catch (err) {
      const newFailCount = failCount + 1;
      setFailCount(newFailCount);

      const message =
        err.response?.data?.message ||
        err.response?.data?.error ||
        'Invalid username or password';
      setError(message);

      // Check circuit breaker after 3 failures
      if (newFailCount >= 3) {
        await checkLoginStatus();
      }
    } finally {
      setIsLoading(false);
    }
  };

  const footer = (
    <p className="text-gray-500">
      Don&apos;t have an account?{' '}
      <Link
        to="/register"
        data-cy="login-register-link"
        className="font-semibold text-[#0095F6]"
      >
        Sign up
      </Link>
    </p>
  );

  return (
    <AuthLayout footer={footer}>
      <form onSubmit={handleSubmit(onSubmit)} className="mt-4 space-y-2">
        <input
          type="text"
          placeholder="Username"
          autoComplete="username"
          data-cy="login-username-input"
          className="w-full rounded-sm border border-gray-300 bg-gray-50 px-3 py-2 text-sm outline-none focus:border-gray-400"
          {...register('username', { required: true })}
        />

        <input
          type="password"
          placeholder="Password"
          autoComplete="current-password"
          data-cy="login-password-input"
          className="w-full rounded-sm border border-gray-300 bg-gray-50 px-3 py-2 text-sm outline-none focus:border-gray-400"
          {...register('password', { required: true })}
        />

        <button
          type="submit"
          disabled={!isValid || isLoading || isCircuitOpen}
          data-cy="login-submit-btn"
          className="w-full rounded-lg bg-[#0095F6] py-2 text-sm font-semibold text-white hover:bg-[#1877F2] disabled:cursor-not-allowed disabled:opacity-50"
        >
          {isLoading ? 'Logging in...' : 'Log In'}
        </button>
      </form>

      {/* Divider */}
      <div className="my-4 flex items-center gap-4">
        <div className="h-px flex-1 bg-gray-300" />
        <span className="text-xs font-semibold uppercase text-gray-500">or</span>
        <div className="h-px flex-1 bg-gray-300" />
      </div>

      {/* Error message */}
      {error && (
        <p
          data-cy="login-error-message"
          className="mb-3 text-center text-sm text-red-500"
        >
          {error}
        </p>
      )}

      {/* Circuit breaker timer */}
      {isCircuitOpen && circuitBreakerSeconds > 0 && (
        <div
          data-cy="login-circuit-breaker-timer"
          className="mb-3 rounded-md bg-yellow-50 border border-yellow-200 p-3 text-center text-sm text-yellow-700"
        >
          Too many failed attempts. Please try again in{' '}
          <span className="font-bold">{circuitBreakerSeconds}s</span>
        </div>
      )}

      {/* Forgot password link */}
      <Link
        to="/forgot-password"
        data-cy="login-forgot-password-link"
        className="text-xs text-[#00376B]"
      >
        Forgot password?
      </Link>
    </AuthLayout>
  );
}
