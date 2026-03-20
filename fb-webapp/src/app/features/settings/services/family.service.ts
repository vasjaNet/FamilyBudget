import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import type {
  Family,
  FamilyMember,
  CreateFamilyRequest,
  UpdateFamilyRequest,
  AddMemberRequest,
} from '../models/family.model';

const API = `${environment.apiBaseUrl}${environment.apiPrefix}`;

@Injectable({ providedIn: 'root' })
export class FamilyService {
  private readonly http = inject(HttpClient);

  /** List families for the current user. When backend has GET /families, use it. */
  getMyFamilies(): Observable<Family[]> {
    return this.http.get<{ data: Family[] }>(`${API}/families`)
      .pipe(map(r => r.data ?? []));
  }

  /** Create a family. When backend has POST /families, use it. */
  createFamily(request: CreateFamilyRequest): Observable<Family> {
    // When FamilyController exists: return this.http.post<{ data: Family }>(`${API}/families`, request).pipe(map(r => r.data!));
    const stub: Family = {
      id: 'stub-' + Date.now(),
      name: request.name,
      description: request.description ?? '',
      ownerId: 'current-user',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      memberCount: 1,
    };
    return of(stub);
  }

  /** Update a family. When backend has PUT /families/:id, use it. */
  updateFamily(id: string, request: UpdateFamilyRequest): Observable<Family> {
    // When FamilyController exists: return this.http.put<{ data: Family }>(`${API}/families/${id}`, request).pipe(map(r => r.data!));
    return of({
      id,
      name: request.name,
      description: request.description ?? '',
      ownerId: 'current-user',
      updatedAt: new Date().toISOString(),
      memberCount: 1,
    });
  }

  /** Get members of a family. When backend has GET /families/:id/members, use it. */
  getMembers(familyId: string): Observable<FamilyMember[]> {
    return this.http.get<{ data: FamilyMember[] }>(`${API}/families/${familyId}/members`)
      .pipe(map(r => r.data ?? []));
  }

  /** Add a member to a family. When backend has POST /families/:id/members, use it. */
  addMember(familyId: string, request: AddMemberRequest): Observable<FamilyMember> {
    // When FamilyController exists: return this.http.post<{ data: FamilyMember }>(`${API}/families/${familyId}/members`, request).pipe(map(r => r.data!));
    return of({
      id: 'stub-mem-' + Date.now(),
      familyId,
      userId: request.userId,
      username: 'User ' + request.userId.slice(0, 8),
      role: request.role,
      createdAt: new Date().toISOString(),
    });
  }
}
