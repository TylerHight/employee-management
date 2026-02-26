import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const AuthGuard: CanActivateFn = (): boolean | UrlTree => {
  const authService = inject(AuthService);
  const router = inject(Router);

  console.log('AuthGuard running. isLoggedIn =', authService.isLoggedIn());

  if (authService.isLoggedIn()) {
    return true;
  }

  console.warn('AuthGuard: NOT logged in — redirecting to /login');
  return router.createUrlTree(['/login']);
};
