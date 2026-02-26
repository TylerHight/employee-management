import { Component } from '@angular/core';
import { PublicNavbarComponent } from '../../shared/public-navbar/public-navbar.component';
import { RouterModule } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { EmployeeFormComponent } from '../../shared/employee-form/employee-form.component';

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [RouterModule, PublicNavbarComponent],
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.scss']
})
export class LandingPageComponent {
  constructor(private dialog: MatDialog) { }

  openRegistrationDialog() {
    this.dialog.open(EmployeeFormComponent, {
      maxWidth: '600px',
      panelClass: 'registration-dialog',
      data: { mode: 'registration', testId: 'registration-dialog' }
    });
  }
}
