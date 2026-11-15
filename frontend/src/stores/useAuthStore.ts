import { create } from "zustand";
import type { AuthUser } from "../types/auth";

interface AuthState {
  token: string | null;
  user: AuthUser | null;
  setToken: (token: string) => void;
  logout: () => void;
}

function getUserFromToken(token: string): AuthUser | null {
  try {
    const payload = token.split(".")[1];
    const decoded = JSON.parse(
      atob(payload.replace(/-/g, "+").replace(/_/g, "/")),
    );

    if (
      !decoded.sub ||
      typeof decoded.email !== "string" ||
      !["USER", "ADMIN"].includes(decoded.role) ||
      typeof decoded.exp !== "number" ||
      decoded.exp * 1000 <= Date.now()
    ) {
      return null;
    }

    const id = Number(decoded.sub);

    if (!Number.isSafeInteger(id) || id <= 0) {
      return null;
    }

    return {
      id,
      email: decoded.email,
      role: decoded.role,
    };
  } catch {
    return null;
  }
}

function getInitialAuth() {
  const token = sessionStorage.getItem("accessToken");

  if (!token) {
    return { token: null, user: null };
  }

  const user = getUserFromToken(token);

  if (!user) {
    sessionStorage.removeItem("accessToken");
    return { token: null, user: null };
  }

  return { token, user };
}

export const useAuthStore = create<AuthState>((set) => ({
  ...getInitialAuth(),

  setToken: (token) => {
    const user = getUserFromToken(token);

    if (!user) {
      sessionStorage.removeItem("accessToken");
      set({ token: null, user: null });
      return;
    }

    sessionStorage.setItem("accessToken", token);
    set({ token, user });
  },

  logout: () => {
    sessionStorage.removeItem("accessToken");
    set({ token: null, user: null });
  },
}));
