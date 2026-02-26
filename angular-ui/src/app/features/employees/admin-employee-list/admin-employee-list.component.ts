import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EmployeeService } from '../../../core/services/employee.service';
import { Employee } from '../../../core/models/employee.model';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { AdminDataTableComponent } from '../../../shared/admin-data-table/admin-data-table.component';
import { EmployeeFormComponent } from '../../../shared/employee-form/employee-form.component';
import { MatFormFieldModule, MatLabel } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-admin-employee-list',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatTooltipModule,
    MatButtonModule,
    MatCardModule,
    AdminDataTableComponent,
    FormsModule,
    MatFormFieldModule,
    MatInputModule
  ],
  templateUrl: './admin-employee-list.component.html',
  styleUrls: ['./admin-employee-list.component.scss']
})
export class AdminEmployeeList implements OnInit {
  employees: Employee[] = [];
  pageIndex = 0;
  pageSize = 10;
  totalItems = 0;
  pageSizeOptions = [5, 10, 25, 50]
  searchTerm = '';
  loading = false;
  error: string | null = null;

  columns = [
    { key: 'id', label: 'ID' },
    { key: 'firstName', label: 'First Name' },
    { key: 'lastName', label: 'Last Name' },
    { key: 'email', label: 'Email' },
    { key: 'role', label: 'Role' },
    { key: 'city', label: 'City' },
    { key: 'state', label: 'State' }
  ];

  constructor(
    private employeeService: EmployeeService,
    private dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.fetchEmployees();
  }

  fetchEmployees(): void {
    this.loading = true;
    this.error = null;

    this.employeeService.getEmployees(
      this.pageIndex,
      this.pageSize,
      this.searchTerm
    ).subscribe({
      next: (page) => {
        this.employees = page.content;
        this.totalItems = page.totalElements;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load employees.';
        this.loading = false;
      }
    });
  }

  onSearchChange() {
    this.pageIndex = 0; // reset to the first page when searching
    this.fetchEmployees();
  }

  openAddEmployeeDialog(): void {
    const dialogRef = this.dialog.open(EmployeeFormComponent, {
      width: '95vw',
      maxWidth: '1000px',
      maxHeight: '90vh',
      data: { mode: 'admin-add' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'success') {
        this.fetchEmployees();
      }
    });
  }

  openEditEmployeeDialog(employee: Employee): void {
    const dialogRef = this.dialog.open(EmployeeFormComponent, {
      width: '95vw',
      maxWidth: '1000px',
      maxHeight: '90vh',
      data: { mode: 'admin-edit', employee }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'success') {
        this.fetchEmployees();
      }
    });
  }

  deleteEmployee(id: number): void {
    if (confirm('Are you sure you want to delete this employee?')) {
      this.employeeService.delete(id).subscribe({
        next: () => {
          alert('Employee deleted');
          this.fetchEmployees();
        },
        error: () => alert('Failed to delete employee.')
      });
    }
  }

  onPageChange(event: { page: number; size: number }) {
    this.pageIndex = event.page;
    this.pageSize = event.size;
    this.fetchEmployees();
  }
}
