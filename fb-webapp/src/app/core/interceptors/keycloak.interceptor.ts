import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';

export const keycloakInterceptor: HttpInterceptorFn = (req, next) => {
  const keycloak = inject(KeycloakService);

  const url = req.url;
  const isApiRequest =
    environment.apiBaseUrl
      ? url.startsWith(environment.apiBaseUrl)
      : url.startsWith('/api') || url.includes('/api/');

  if (!isApiRequest) {
    return next(req);
  }

  const token = keycloak.getKeycloakInstance().token;
  const userId = keycloak.getKeycloakInstance().tokenParsed?.sub;

  let cloned = req;
  if (token) {
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
        keycloak.logout();
      }
      return throwError(() => err);
    })
  );
};
