import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { Employee } from '../../../core/models/employee.model';
import { EmployeeService } from '../../../core/services/employee.service';
import { EmployeeFormComponent } from '../../../shared/employee-form/employee-form.component';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule, MatButtonModule],
  templateUrl: './profile-page.component.html',
  styleUrls: ['./profile-page.component.scss']
})
export class ProfilePage implements OnInit {
  employee?: Employee;
  loading = false;
  error: string | null = null;

  constructor(
    private employeeService: EmployeeService,
    private dialog: MatDialog
  ) { }

  ngOnInit() {
    this.fetchProfile();
  }

  fetchProfile() {
    this.loading = true;
    this.error = null;

    this.employeeService.getSelf().subscribe({
      next: (data: Employee) => {
        this.employee = data;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load profile';
        this.loading = false;
      }
    });
  }


  editProfile() {
    const dialogRef = this.dialog.open(EmployeeFormComponent, {
      width: '100vw',
      maxWidth: '1000px',
      maxHeight: '100vh',
      data: { mode: 'user-edit', employee: this.employee }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'success') {
        this.fetchProfile();
      }
    });
  }
}
