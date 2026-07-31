export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  username: string;
  role: string;
}

export interface UserInfo {
  username: string;
  role: string;
  nomComplet: string;
}

export type Role = 'ROLE_USER' | 'ROLE_MANAGER';
