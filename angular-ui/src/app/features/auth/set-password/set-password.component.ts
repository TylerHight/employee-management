import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../../core/services/auth.service';
import { PublicNavbarComponent } from '../../../shared/public-navbar/public-navbar.component';

@Component({
  selector: 'app-set-password',
  standalone: true,
  imports: [CommonModule, FormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, PublicNavbarComponent],
  templateUrl: './set-password.component.html',
  styleUrls: ['./set-password.component.scss']
})
export class SetPasswordComponent implements OnInit {
  newPassword = '';
  confirmPassword = '';
  errorMessage = '';
  successMessage = '';
  token: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) { }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.token = params['token'] || null;
    });
  }

  isValidInput(): boolean {
    return (
      this.newPassword.length >= 8 &&
      this.newPassword.length <= 35 &&
      this.confirmPassword === this.newPassword &&
      /^[a-zA-Z0-9]+$/.test(this.newPassword)
    );
  }

  onSubmit() {
    if (!this.token) {
      this.errorMessage = 'No reset token found. Please use the password reset link.';
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    this.authService.updatePasswordWithToken(this.token, this.newPassword).subscribe({
      next: () => {
        this.successMessage = 'Password updated successfully.';
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: err => {
        this.errorMessage = err.error?.message || 'Failed to update password';
      }
    });
  }
}
