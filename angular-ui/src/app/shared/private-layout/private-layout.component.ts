import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { PrivateNavbarComponent } from '../private-navbar/private-navbar.component';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-private-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, PrivateNavbarComponent],
  templateUrl: './private-layout.component.html',
  styleUrls: ['./private-layout.component.scss']
})
export class PrivateLayoutComponent {
  isAdmin = false;
  userName = '';

  constructor(private authService: AuthService) {
    this.isAdmin = this.authService.isAdmin();
    this.userName = this.authService.getUserName() || 'User';
  }
}
