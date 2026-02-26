import { Component } from '@angular/core';
import { EmployeeFormComponent } from '../../../shared/employee-form/employee-form.component';
import { PublicNavbarComponent } from '../../../shared/public-navbar/public-navbar.component';

@Component({
  selector: 'app-registration-page',
  standalone: true,
  imports: [EmployeeFormComponent, PublicNavbarComponent],
  templateUrl: './registration-form.component.html',
  styleUrls: ['./registration-form.component.scss']
})
export class RegistrationPageComponent { }
