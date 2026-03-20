import { type CanActivateFn } from '@angular/router';

// Temporarily disabled for anonymous access
// import { inject } from '@angular/core';
// import { Router, type CanActivateFn } from '@angular/router';
// import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  // Temporarily allow all access - authentication disabled
  return true;

  /* Original auth guard:
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.isAuthenticated()) {
    return true;
  }
  router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
  return false;
  */
};
