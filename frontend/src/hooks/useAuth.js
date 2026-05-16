import { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import useAuthStore from '../store/useAuthStore';
import { authApi } from '../api/authApi';

export function useAuth() {
  const navigate = useNavigate();
  const { login: storeLogin, logout: storeLogout, user, isAuthenticated } = useAuthStore();

  const decodeJwt = (token) => {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch {
      return null;
    }
  };

  const login = useCallback(
    async (credentials) => {
      const response = await authApi.login(credentials);
      const token = response.data.token || response.data;

      const decoded = decodeJwt(token);
      const userId = decoded?.userId || decoded?.sub || decoded?.id;

      let userProfile = { userId };
      try {
        const profileRes = await authApi.getProfile(userId);
        userProfile = profileRes.data;
      } catch {
        // Profile fetch failed, use basic info from token
      }

      storeLogin(token, userProfile);
      navigate('/');
      return response;
    },
    [navigate, storeLogin]
  );

  const logout = useCallback(() => {
    storeLogout();
    navigate('/login');
  }, [navigate, storeLogout]);

  return { login, logout, user, isAuthenticated, decodeJwt };
}
