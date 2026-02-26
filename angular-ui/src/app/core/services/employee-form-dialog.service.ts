import { Injectable } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { EmployeeFormComponent } from '../../shared/employee-form/employee-form.component';
import { Employee } from '../../core/models/employee.model';

@Injectable({ providedIn: 'root' })
export class EmployeeFormDialogService {
  constructor(private dialog: MatDialog) {}

  open(mode: string, employee?: Employee) {
    const config: MatDialogConfig = {
      width: '800px',
      maxHeight: '90vh',
      autoFocus: false,
      data: { mode, employee }
    };

    return this.dialog.open(EmployeeFormComponent, config);
  }
}
