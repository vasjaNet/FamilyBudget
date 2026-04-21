import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';
import type { User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly keycloak = inject(KeycloakService);
  private readonly router = inject(Router);

  getCurrentUser(): User | null {
    const tokenParsed = this.keycloak.getKeycloakInstance().tokenParsed;
    if (!tokenParsed) {
      return null;
    }

    return {
      id: tokenParsed['sub'] || '',
      username: tokenParsed['preferred_username'] || tokenParsed['sub'] || '',
      email: tokenParsed['email'] || '',
      firstName: tokenParsed['given_name'] || '',
      lastName: tokenParsed['family_name'] || '',
      status: 'ACTIVE',
    };
  }

  getCurrentUserId(): string | null {
    return this.keycloak.getKeycloakInstance().tokenParsed?.['sub'] || null;
  }

  isAuthenticated(): boolean {
    return this.keycloak.isLoggedIn();
  }

  getToken(): string | undefined {
    return this.keycloak.getKeycloakInstance().token;
  }

  async login(redirectUri?: string): Promise<void> {
    await this.keycloak.login({
      redirectUri: redirectUri || window.location.origin + '/',
    });
  }

  async register(redirectUri?: string): Promise<void> {
    await this.keycloak.register({
      redirectUri: redirectUri || window.location.origin + '/',
    });
  }

  async logout(): Promise<void> {
    await this.keycloak.logout(window.location.origin + '/');
  }

  getUserRoles(): string[] {
    return this.keycloak.getUserRoles();
  }

  hasRole(role: string): boolean {
    return this.keycloak.isUserInRole(role);
  }
}
