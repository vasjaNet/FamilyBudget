import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CreateTenantRequest, UpdateTenantRequest, Tenant } from '../models/tenant.model';
import { ApiResponse } from '../models/user.model';
import { AssignUserToTenantRequest, UserTenantResponse } from '../models/user-tenant.model';

@Injectable({
  providedIn: 'root'
})
export class TenantService {
  private readonly apiUrl = `${environment.apiBaseUrl}${environment.apiPrefix}/tenants`;

  constructor(private http: HttpClient) {}

  getAllTenants(): Observable<ApiResponse<Tenant[]>> {
    return this.http.get<ApiResponse<Tenant[]>>(this.apiUrl);
  }

  getAllTenantsBasic(): Observable<ApiResponse<Tenant[]>> {
    return this.http.get<ApiResponse<Tenant[]>>(`${this.apiUrl}/basic`);
  }

  getTenantById(id: string): Observable<ApiResponse<Tenant>> {
    return this.http.get<ApiResponse<Tenant>>(`${this.apiUrl}/${id}`);
  }

  createTenant(request: CreateTenantRequest): Observable<ApiResponse<Tenant>> {
    return this.http.post<ApiResponse<Tenant>>(this.apiUrl, request);
  }

  updateTenant(id: string, request: UpdateTenantRequest): Observable<ApiResponse<Tenant>> {
    return this.http.put<ApiResponse<Tenant>>(`${this.apiUrl}/${id}`, request);
  }

  deleteTenant(id: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }

  assignUserToTenant(request: AssignUserToTenantRequest): Observable<ApiResponse<UserTenantResponse>> {
    return this.http.post<ApiResponse<UserTenantResponse>>(`${environment.apiBaseUrl}${environment.apiPrefix}/tenants/user-tenants`, request);
  }
}
