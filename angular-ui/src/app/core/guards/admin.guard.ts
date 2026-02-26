import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const AdminGuard: CanActivateFn = (): boolean | UrlTree => {
  const authService = inject(AuthService);
  const router = inject(Router);

  console.log(
    'AdminGuard running. isLoggedIn =', authService.isLoggedIn(),
    'isAdmin =', authService.isAdmin()
  );

  if (authService.isLoggedIn() && authService.isAdmin()) {
    return true;
  }

  console.warn('AdminGuard: NOT authorized — redirecting to /login');
  return router.createUrlTree(['/login']);
};
