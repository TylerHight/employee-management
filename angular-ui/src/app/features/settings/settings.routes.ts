import { Routes } from '@angular/router';
import { SettingsPageComponent } from './settings-page.component';
import { AuthGuard } from '../../core/guards/auth.guard';

export const SETTINGS_ROUTES: Routes = [
  { 
    path: '',
    component: SettingsPageComponent,
    canActivate: [AuthGuard] 
  }
];