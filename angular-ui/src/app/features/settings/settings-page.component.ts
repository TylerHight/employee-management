import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ThemeService } from '../../core/services/theme.service';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-settings-page',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSlideToggleModule,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './settings-page.component.html',
  styleUrls: ['./settings-page.component.scss']
})
export class SettingsPageComponent implements OnInit, OnDestroy {
  // Settings properties
  notificationsEnabled = true;
  darkModeEnabled = false;
  
  // Initial state properties
  private initialNotificationsEnabled = true;
  private initialDarkModeEnabled = false;
  
  private darkModeSubscription: Subscription | undefined;
  
  constructor(
    private themeService: ThemeService,
    private location: Location
  ) {}
  
  ngOnInit(): void {
    // Subscribe to dark mode changes
    this.darkModeSubscription = this.themeService.darkMode$.subscribe(isDarkMode => {
      this.darkModeEnabled = isDarkMode;
    });
    
    // Store initial state
    this.initialDarkModeEnabled = this.darkModeEnabled;
    this.initialNotificationsEnabled = this.notificationsEnabled;
  }
  
  ngOnDestroy(): void {
    // Clean up subscription
    if (this.darkModeSubscription) {
      this.darkModeSubscription.unsubscribe();
    }
  }
  
  // Methods to handle settings changes
  toggleNotifications() {
    this.notificationsEnabled = !this.notificationsEnabled;
    // Here you would typically save this setting to a service or localStorage
    console.log('Notifications enabled:', this.notificationsEnabled);
  }
  
  toggleDarkMode() {
    this.themeService.toggleDarkMode();
  }
  
  saveSettings() {
    // Save notification settings (dark mode is already saved by the theme service)
    console.log('Settings saved');
    
    // Navigate back to the previous page
    this.location.back();
  }
  
  cancelSettings() {
    // Revert changes
    if (this.darkModeEnabled !== this.initialDarkModeEnabled) {
      this.themeService.setDarkMode(this.initialDarkModeEnabled);
    }
    
    if (this.notificationsEnabled !== this.initialNotificationsEnabled) {
      this.notificationsEnabled = this.initialNotificationsEnabled;
      // If you have a notifications service, you would revert the change there as well
    }
    
    // Navigate back
    this.location.back();
  }
}