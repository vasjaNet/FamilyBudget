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