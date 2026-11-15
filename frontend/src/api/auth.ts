import type { AuthCredentials, AuthUser, LoginResponse } from "../types/auth";

export async function register(
  credentials: AuthCredentials,
): Promise<AuthUser> {
  const response = await fetch("/api/v1/auth/register", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(credentials),
  });

  if (response.status === 409) {
    throw new Error("An account with this email already exists");
  }

  if (!response.ok) {
    throw new Error(`Registration failed: ${response.status}`);
  }

  return response.json() as Promise<AuthUser>;
}

export async function login(
  credentials: AuthCredentials,
): Promise<LoginResponse> {
  const response = await fetch("/api/v1/auth/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(credentials),
  });

  if (response.status === 401) {
    throw new Error("Invalid email or password");
  }

  if (!response.ok) {
    throw new Error(`Login failed: ${response.status}`);
  }

  return response.json() as Promise<LoginResponse>;
}
