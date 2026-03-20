import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { environment } from '../../../environments/environment';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.getToken();
  const userId = auth.getCurrentUserId();

  let cloned = req;

  // Only add auth headers for requests to our API (relative /api or same host when using proxy)
  const url = req.url;
  const isApiRequest =
    environment.apiBaseUrl
      ? url.startsWith(environment.apiBaseUrl)
      : url.startsWith('/api') || url.includes('/api/');
  if (isApiRequest && token) {
    cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
        ...(userId ? { 'X-User-Id': userId } : {}),
      },
    });
  }

  return next(cloned).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 401) {
        auth.logout();
        router.navigate(['/login'], { queryParams: { returnUrl: router.url } });
      }
      return throwError(() => err);
    })
  );
};
