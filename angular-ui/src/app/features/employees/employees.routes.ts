import { Routes } from '@angular/router';
import { ProfilePage } from './profile-page/profile-page.component';
import { AdminEmployeeList } from './admin-employee-list/admin-employee-list.component';
import { AuthGuard } from '../../core/guards/auth.guard';
import { AdminGuard } from '../../core/guards/admin.guard';

export const EMPLOYEE_ROUTES: Routes = [
  {
    path: 'user',
    component: ProfilePage,
    canActivate: [AuthGuard]
  },
  {
    path: 'admin',
    component: AdminEmployeeList,
    canActivate: [AuthGuard, AdminGuard]
  }
];
