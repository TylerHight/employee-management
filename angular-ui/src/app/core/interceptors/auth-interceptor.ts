import { inject } from '@angular/core';
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ApiError } from '../models/api-error.model';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const snackBar = inject(MatSnackBar);
  const token = authService.getToken();

  // Add Authorization header if token exists
  const authReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Handle Unauthorized
      if (error.status === 401) {
        authService.logout();
        router.navigate(['/login']);
        snackBar.open('Session expired. Please log in again.', 'Close', { duration: 5000 });
      }

      let apiError: ApiError | null = null;

      // Try to parse backend ApiError
      if (error.error && typeof error.error === 'object' && 'code' in error.error) {
        apiError = error.error as ApiError;

        // Global snackbar for general errors (skip validation errors here)
        if (apiError.code !== 'VALIDATION_ERROR') {
          snackBar.open(apiError.message, 'Close', { duration: 5000 });
        }
      } else {
        // Fallback for unexpected errors
        snackBar.open('An unexpected error occurred.', 'Close', { duration: 5000 });
      }

      // Pass ApiError (or original error) along so components can handle if needed
      return throwError(() => apiError || error);
    })
  );
};
