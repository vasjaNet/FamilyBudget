# FamilyBudget Web App (fb-webapp)

Angular 19 frontend for the FamilyBudget project. Uses Angular Material for UI and integrates with the backend services (fb-userService, fb-chatService) when their APIs are available.

## Prerequisites

- Node.js 20+
- npm (or use the project's npm scripts)

## Setup

```bash
npm install
```

## Development

```bash
npm start
```

Runs the app at `http://localhost:4200`. The dev server is configured to proxy `/api` to `http://localhost:8080`, so ensure the backend (fb-userService) is running if you want to use real user/family APIs.

## Build

```bash
npm run build
```

Production build output is in `dist/fb-webapp`.

## Pages

- **Landing** (`/`) – Hero and CTAs to Login / Sign Up
- **Login** (`/login`) – Username/password; uses backend when `/api/v1/auth/login` exists, otherwise stub
- **Sign Up** (`/signup`) – Registration; uses `POST /api/v1/users` when backend is available
- **Settings** (`/settings`) – Create/edit family groups and manage members (protected)
- **Chat** (`/chat`) – Group chat per family (protected; stub until chat API exists)

## Backend integration

- **Auth**: The app is ready for JWT auth. When you add `/api/v1/auth/login` and `/api/v1/auth/register` (and JWT validation) to fb-userService, update `AuthService` to call those endpoints; the interceptor already sends `Authorization: Bearer <token>` and `X-User-Id`.
- **Families**: Family and member UI use stub data. When you add a FamilyController in fb-userService, update `FamilyService` to call `GET/POST/PUT /api/v1/families` and `GET/POST /api/v1/families/:id/members`.
- **Chat**: The chat page uses stub messages. When fb-chatService exposes group chat endpoints, wire them in `ChatService`.

## Proxy

`proxy.conf.json` forwards `/api` to `http://localhost:8080`. With this, use `apiBaseUrl: ''` in `src/environments/environment.ts` so requests are relative and go through the proxy.
