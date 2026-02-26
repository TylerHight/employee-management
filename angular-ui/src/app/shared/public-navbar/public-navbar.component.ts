import { Component, OnDestroy, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { EmployeeFormComponent } from '../employee-form/employee-form.component';
import { MatDialog } from '@angular/material/dialog';
import { ThemeService } from '../../core/services/theme.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'public-navbar',
  standalone: true,
  imports: [RouterModule, MatIconModule],
  templateUrl: './public-navbar.component.html',
  styleUrls: ['./public-navbar.component.scss']
})
export class PublicNavbarComponent implements OnInit, OnDestroy {
  isDarkMode = false;
  private darkModeSubscription: Subscription | undefined;

  constructor(
    private dialog: MatDialog,
    private themeService: ThemeService
  ) {}

  ngOnInit(): void {
    // Subscribe to dark mode changes
    this.darkModeSubscription = this.themeService.darkMode$.subscribe(isDarkMode => {
      this.isDarkMode = isDarkMode;
    });
  }

  ngOnDestroy(): void {
    // Clean up subscription
    if (this.darkModeSubscription) {
      this.darkModeSubscription.unsubscribe();
    }
  }

  toggleTheme() {
    this.themeService.toggleDarkMode();
  }

  openRegistrationDialog() {
    this.dialog.open(EmployeeFormComponent, {
      width: '95vw',
      maxWidth: '600px',
      panelClass: 'registration-dialog',
      data: { mode: 'registration' }
    });
  }
}
