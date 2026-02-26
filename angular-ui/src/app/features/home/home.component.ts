import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { PrivateNavbarComponent } from '../../shared/private-navbar/private-navbar.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {
  isAdmin = false;
  userName = '';

  constructor(private authService: AuthService) {
    this.isAdmin = this.authService.isAdmin();
    this.userName = this.authService.getUserName() || 'User';
  }
}
