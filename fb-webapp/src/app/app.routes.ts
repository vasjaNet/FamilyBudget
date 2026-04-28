import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { LandingComponent } from './features/landing/landing.component';
import { LoginComponent } from './features/auth/login/login.component';
import { SignupComponent } from './features/auth/signup/signup.component';

export const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  {
    path: 'settings',
    loadComponent: () =>
      import('./features/settings/settings.component').then((m) => m.SettingsComponent),
    canActivate: [authGuard],
  },
  {
    path: 'chat',
    loadComponent: () =>
      import('./features/chat/chat.component').then((m) => m.ChatComponent),
    canActivate: [authGuard],
  },
  {
    path: 'users',
    loadComponent: () =>
      import('./features/users/users.component').then((m) => m.UsersComponent),
    canActivate: [authGuard],
  },
  {
    path: 'users/new',
    loadComponent: () =>
      import('./features/users/user-edit/user-edit.component').then((m) => m.UserEditComponent),
    canActivate: [authGuard],
  },
  {
    path: 'users/:id/edit',
    loadComponent: () =>
      import('./features/users/user-edit/user-edit.component').then((m) => m.UserEditComponent),
    canActivate: [authGuard],
  },
  {
    path: 'tenants',
    loadComponent: () =>
      import('./features/tenants/tenants.component').then((m) => m.TenantsComponent),
    canActivate: [authGuard],
  },
  { path: '**', redirectTo: '' },
];
