import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private darkModeKey = 'darkMode';
  private darkModeSubject = new BehaviorSubject<boolean>(this.getInitialDarkModeState());
  
  constructor() {
    // Apply the initial theme state
    this.applyTheme(this.darkModeSubject.value);
  }
  
  /**
   * Get the initial dark mode state from localStorage or default to false
   */
  private getInitialDarkModeState(): boolean {
    const savedState = localStorage.getItem(this.darkModeKey);
    return savedState ? JSON.parse(savedState) : false;
  }
  
  /**
   * Get the current dark mode state as an observable
   */
  get darkMode$(): Observable<boolean> {
    return this.darkModeSubject.asObservable();
  }
  
  /**
   * Get the current dark mode state
   */
  get isDarkMode(): boolean {
    return this.darkModeSubject.value;
  }
  
  /**
   * Toggle dark mode
   */
  toggleDarkMode(): void {
    const newState = !this.darkModeSubject.value;
    this.setDarkMode(newState);
  }
  
  /**
   * Set dark mode to a specific state
   */
  setDarkMode(isDarkMode: boolean): void {
    // Save to localStorage
    localStorage.setItem(this.darkModeKey, JSON.stringify(isDarkMode));
    
    // Update the subject
    this.darkModeSubject.next(isDarkMode);
    
    // Apply the theme
    this.applyTheme(isDarkMode);
  }
  
  /**
   * Apply the theme to the document body
   */
  private applyTheme(isDarkMode: boolean): void {
    document.body.classList.toggle('dark-mode', isDarkMode);
  }
}