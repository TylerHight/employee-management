import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { PublicNavbarComponent } from '../../../shared/public-navbar/public-navbar.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [MatFormFieldModule, MatInputModule, MatButtonModule, CommonModule, FormsModule, PublicNavbarComponent],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class Login {
  email = '';
  password = '';
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) { }

  isValidInput(): boolean {
    const alphanumeric = /^[a-zA-Z0-9@.]+$/; // allow alphanumeric + @ and . for email
    return (
      this.email.length >= 8 &&
      this.email.length <= 35 &&
      this.password.length >= 8 &&
      this.password.length <= 35 &&
      alphanumeric.test(this.email) &&
      /^[a-zA-Z0-9]+$/.test(this.password)
    );
  }

  onSubmit() {
    this.errorMessage = '';

    if (!this.isValidInput()) {
      this.errorMessage = 'Invalid email or password format';
      return;
    }

    this.authService.login(this.email, this.password).subscribe({
      next: (response) => {
        // Decode the token to get role & userId
        const payload: any = this.authService.decodeToken(response.token);
        this.authService.saveToken(response.token);
        sessionStorage.setItem('role', payload.role);
        sessionStorage.setItem('userId', payload.userId);

        // Navigate to home page
        this.router.navigate(['/home']);
      },
      error: (err) => {
        console.error('Login error:', err);
        this.errorMessage = err.error?.message || 'Sign in failed';
      }
    });
  }

  sendPasswordSetLink() {
    this.errorMessage = '';

    if (!this.email || !/^[a-zA-Z0-9@.]+$/.test(this.email)) {
      this.errorMessage = 'Please enter a valid email before requesting a password set/reset.';
      return;
    }

    this.authService.requestPasswordReset(this.email).subscribe({
      next: () => {
        // Show confirmation message to user
        alert('If an account exists for this email, a password reset link has been sent.');
      },
      error: (err) => {
        console.error('Password reset request error:', err);
        this.errorMessage = err.error?.message || 'Unable to request password reset';
      }
    });
  }

}
