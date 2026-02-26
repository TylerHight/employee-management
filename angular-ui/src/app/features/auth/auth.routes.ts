import { Routes } from '@angular/router';
import { Login } from './login/login.component';
import { SetPasswordComponent } from './set-password/set-password.component';

export const AUTH_ROUTES: Routes = [
  { path: '', component: Login }
];
