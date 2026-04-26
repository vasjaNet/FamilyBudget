import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Role, RoleApiResponse } from '../models/role.model';

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  private readonly apiUrl = `${environment.apiBaseUrl}${environment.apiPrefix}/roles`;

  constructor(private http: HttpClient) {}

  getAllRoles(): Observable<RoleApiResponse<Role[]>> {
    return this.http.get<RoleApiResponse<Role[]>>(this.apiUrl);
  }
}
