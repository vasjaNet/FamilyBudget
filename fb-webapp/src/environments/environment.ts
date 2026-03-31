export const environment = {
  production: false,
  /** Use '' with proxy (ng serve) so /api is forwarded to backend; or set to http://localhost:8080 for direct calls */
  apiBaseUrl: 'http://localhost:8081',
  apiPrefix: '/api/v1',
  keycloak: {
    url: 'http://localhost:8080',
    realm: 'my-realm',
    clientId: 'fb-webapp-client',
  },
};
