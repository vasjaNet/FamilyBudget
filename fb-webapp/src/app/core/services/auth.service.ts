import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, catchError, of, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import type { User, LoginRequest, RegisterRequest, AuthResponse } from '../models/user.model';

const TOKEN_KEY = 'fb_token';
const USER_KEY = 'fb_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly apiUrl = `${environment.apiBaseUrl}${environment.apiPrefix}`;

  private currentUser: User | null = null;
  private token: string | null = null;

  constructor() {
    this.loadStoredAuth();
  }

  private loadStoredAuth(): void {
    try {
      this.token = sessionStorage.getItem(TOKEN_KEY);
      const userJson = sessionStorage.getItem(USER_KEY);
      if (userJson) {
        this.currentUser = JSON.parse(userJson) as User;
      }
    } catch {
      this.token = null;
      this.currentUser = null;
    }
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    // When backend has /auth/login, use: return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, credentials)...
    // Until then, stub: try to get user by username and return fake token for template
    return this.http.get<{ data: User[] }>(`${this.apiUrl}/users`).pipe(
      map((res) => {
        const users = res?.data ?? (Array.isArray(res) ? res : []);
        const user = users.find((u: User) => u.username === credentials.username);
        if (!user) {
          throw new Error('Invalid username or password');
        }
        // Stub: no password check until backend has auth; accept any password for demo
        const stubResponse: AuthResponse = {
          token: `stub-jwt-${user.id}`,
          user,
        };
        this.setSession(stubResponse);
        return stubResponse;
      }),
      catchError((err) => {
        if (err.status === 0 || err.error) {
          // Backend not available: allow demo login with any username
          const stubUser: User = {
            id: 'demo-id',
            username: credentials.username,
            email: credentials.username + '@demo.local',
          };
          const stubResponse: AuthResponse = {
            token: 'stub-jwt-demo',
            user: stubUser,
          };
          this.setSession(stubResponse);
          return of(stubResponse);
        }
        throw err;
      })
    );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    // When backend has /auth/register, use that. Else use POST /users (backend CreateUserRequest has no password).
    const body = {
      username: request.username,
      email: request.email,
      firstName: request.firstName ?? '',
      lastName: request.lastName ?? '',
    };
    return this.http.post<{ data: User }>(`${this.apiUrl}/users`, body).pipe(
      map((res) => {
        const user = (res && (res as { data?: User }).data) ?? (res as unknown as User);
        const token = `stub-jwt-${user.id}`;
        const authResponse: AuthResponse = { token, user };
        this.setSession(authResponse);
        return authResponse;
      }),
      catchError((err) => {
        if (err.status === 0) {
          // Backend not available: stub registration
          const stubUser: User = {
            id: 'demo-' + Date.now(),
            username: request.username,
            email: request.email,
            firstName: request.firstName,
            lastName: request.lastName,
          };
          const authResponse: AuthResponse = {
            token: 'stub-jwt-demo',
            user: stubUser,
          };
          this.setSession(authResponse);
          return of(authResponse);
        }
        throw err;
      })
    );
  }

  private setSession(auth: AuthResponse): void {
    this.token = auth.token;
    this.currentUser = auth.user;
    sessionStorage.setItem(TOKEN_KEY, auth.token);
    sessionStorage.setItem(USER_KEY, JSON.stringify(auth.user));
  }

  logout(): void {
    this.token = null;
    this.currentUser = null;
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(USER_KEY);
    this.router.navigate(['/']);
  }

  getToken(): string | null {
    return this.token;
  }

  getCurrentUser(): User | null {
    return this.currentUser;
  }

  getCurrentUserId(): string | null {
    return this.currentUser?.id ?? null;
  }

  isAuthenticated(): boolean {
    return !!this.token;
  }
}
