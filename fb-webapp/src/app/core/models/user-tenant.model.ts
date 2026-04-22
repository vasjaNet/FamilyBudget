export interface UserTenantResponse {
  id: string;
  userId: string;
  userUsername: string;
  tenantId: string;
  tenantName: string;
  roleId: string;
  roleName: string;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  updatedBy?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface AssignUserToTenantRequest {
  userId: string;
  tenantId: string;
  roleId: string;
}