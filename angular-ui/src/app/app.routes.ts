import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { LandingPageComponent } from './features/landing/landing-page.component';
import { RegistrationPageComponent } from './features/registration/registration-form/registration-form.component';
import { PrivateLayoutComponent } from './shared/private-layout/private-layout.component';
import { HomeComponent } from './features/home/home.component';
import { AdminRegistrationListComponent } from './features/registration/admin-registration-list/admin-registration-list.component';
import { SetPasswordComponent } from './features/auth/set-password/set-password.component';

export const routes: Routes = [
  // Public routes
  { path: '', component: LandingPageComponent },
  {
    path: 'login',
    loadChildren: () =>
      import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  { path: 'register', component: RegistrationPageComponent },

  { path: 'set-password', component: SetPasswordComponent },

  // Private routes
  {
    path: '',
    component: PrivateLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'home', component: HomeComponent },
      {
        path: 'employees',
        loadChildren: () =>
          import('./features/employees/employees.routes').then(m => m.EMPLOYEE_ROUTES)
      },
      { path: 'admin/registrations', component: AdminRegistrationListComponent },
      {
        path: 'settings',
        loadChildren: () =>
          import('./features/settings/settings.routes').then(m => m.SETTINGS_ROUTES)
      }
    ]
  },

  // Fallback
  { path: '**', redirectTo: '' }
];
