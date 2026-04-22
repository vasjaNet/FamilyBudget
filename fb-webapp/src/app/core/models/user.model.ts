import { UserTenantResponse } from './user-tenant.model';

export interface User {
  id: string;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  status: 'ACTIVE' | 'DISABLED' | 'PASSWORD_EXPIRED';
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  updatedBy?: string;
  userTenants?: UserTenantResponse[];
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface CreateUserRequest {
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  status: 'ACTIVE' | 'DISABLED' | 'PASSWORD_EXPIRED';
}

export interface UpdateUserRequest {
  email: string;
  firstName?: string;
  lastName?: string;
  status: 'ACTIVE' | 'DISABLED' | 'PASSWORD_EXPIRED';
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}
