import { UserTenantResponse } from './user-tenant.model';

export type TenantType = 'PERSONAL' | 'FAMILY' | 'BUSINESS';

export interface Tenant {
  id: string;
  name: string;
  description?: string;
  type: TenantType;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  updatedBy?: string;
  userTenants?: UserTenantResponse[];
}

export interface CreateTenantRequest {
  name: string;
  description?: string;
  type: TenantType;
}

export interface UpdateTenantRequest {
  name: string;
  description?: string;
  type: TenantType;
}