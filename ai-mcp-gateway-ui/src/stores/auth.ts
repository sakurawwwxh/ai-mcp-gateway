import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface AuthState {
  token: string | null;
  username: string | null;
  login: (u: string, p: string) => Promise<boolean>;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      username: null,
      login: async (u, p) => {
        if (u === 'admin' && p === 'password123') {
          const fakeToken = 'mock_' + Math.random().toString(36).slice(2);
          set({ token: fakeToken, username: u });
          return true;
        }
        return false;
      },
      logout: () => set({ token: null, username: null }),
    }),
    { name: 'mcp-gateway-auth' }
  )
);
