import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, AssignUserToTenantRequest, UserTenantResponse } from '../models/user-tenant.model';

@Injectable({
  providedIn: 'root'
})
export class UserTenantService {
  private readonly apiUrl = `${environment.apiBaseUrl}${environment.apiPrefix}/user-tenants`;

  constructor(private http: HttpClient) {}

  getAllUserTenants(): Observable<ApiResponse<UserTenantResponse[]>> {
    return this.http.get<ApiResponse<UserTenantResponse[]>>(this.apiUrl);
  }

  getUserTenantById(id: string): Observable<ApiResponse<UserTenantResponse>> {
    return this.http.get<ApiResponse<UserTenantResponse>>(`${this.apiUrl}/${id}`);
  }

  assignUserToTenant(request: AssignUserToTenantRequest): Observable<ApiResponse<UserTenantResponse>> {
    return this.http.post<ApiResponse<UserTenantResponse>>(`${environment.apiBaseUrl}${environment.apiPrefix}/users/user-tenants`, request);
  }

  removeUserFromTenant(userId: string, tenantId: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${environment.apiBaseUrl}${environment.apiPrefix}/users/${userId}/tenant/${tenantId}`);
  }

  getUserTenantsByUserId(userId: string): Observable<ApiResponse<UserTenantResponse[]>> {
    return this.http.get<ApiResponse<UserTenantResponse[]>>(`${environment.apiBaseUrl}${environment.apiPrefix}/users/${userId}/tenants`);
  }
}