import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ThemeService } from './core/services/theme.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('angular-ui');

  constructor(private themeService: ThemeService) {
    // ThemeService is injected here to ensure it's initialized on app startup
    // This ensures dark mode preference is loaded from localStorage immediately
  }
}
