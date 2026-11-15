export type UserRole = "USER" | "ADMIN";

export interface AuthUser {
  id: number;
  email: string;
  role: UserRole;
}

export interface AuthCredentials {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
}
