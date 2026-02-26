import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Registration, RegistrationAdminService } from '../../../core/services/registration-admin.service';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { AdminDataTableComponent } from '../../../shared/admin-data-table/admin-data-table.component';
import { MatDialog } from '@angular/material/dialog';
import { EmployeeFormComponent } from '../../../shared/employee-form/employee-form.component';

@Component({
  selector: 'app-admin-registration-list',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatTooltipModule,
    MatButtonModule,
    MatCardModule,
    AdminDataTableComponent
  ],
  templateUrl: './admin-registration-list.component.html',
  styleUrls: ['./admin-registration-list.component.scss']
})
export class AdminRegistrationListComponent implements OnInit {
  registrations: Registration[] = [];
  loading = false;
  error: string | null = null;

  columns = [
    { key: 'firstName', label: 'First Name' },
    { key: 'lastName', label: 'Last Name' },
    { key: 'email', label: 'Email' },
    { key: 'status', label: 'Status' }
  ];

  constructor(
    private regAdminService: RegistrationAdminService,
    private dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.loadPending();
  }

  loadAll(): void {
    this.loading = true;
    this.error = null;
    this.regAdminService.getAllRegistrations().subscribe({
      next: (data) => {
        this.registrations = data.content || data;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load registrations.';
        this.loading = false;
      }
    });
  }

  loadPending(): void {
    this.loading = true;
    this.error = null;
    this.regAdminService.getPendingRegistrations().subscribe({
      next: (data) => {
        this.registrations = data.content || data;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load pending registrations.';
        this.loading = false;
      }
    });
  }

  saveRow(reg: Registration) {
    if (!reg.email) {
      alert('Email is required');
      return;
    }
    console.log('Saving registration:', reg);
    // Call update logic if backend supports registration updates
  }

  cancelEdit() {}

  openAddRegistrationDialog(): void {
    const dialogRef = this.dialog.open(EmployeeFormComponent, {
      maxWidth: '600px',
      data: { mode: 'registration' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'success') {
        this.loadAll(); // reload list after a successful registration
      }
    });
  }

  approve(email: string): void {
    this.regAdminService.approveRegistration(email).subscribe(() => {
      alert(`Approved ${email}`);
      this.loadPending();
    });
  }

  decline(email: string): void {
    this.regAdminService.declineRegistration(email).subscribe(() => {
      alert(`Declined ${email}`);
      this.loadPending();
    });
  }

  deleteRegistration(email: string): void {
    this.regAdminService.deleteRegistration(email).subscribe(() => {
      alert(`Deleted ${email}`);
      this.loadPending(); // or loadAll() depending on your view
    });
  }

}
