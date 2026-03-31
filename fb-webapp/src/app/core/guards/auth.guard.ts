import { inject } from '@angular/core';
import { type CanActivateFn } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';

export const authGuard: CanActivateFn = async (route, state) => {
  const keycloak = inject(KeycloakService);

  const isAuthenticated = await keycloak.isLoggedIn();

  if (isAuthenticated) {
    return true;
  }

  await keycloak.login({
    redirectUri: window.location.origin + state.url,
  });
  return false;
};
