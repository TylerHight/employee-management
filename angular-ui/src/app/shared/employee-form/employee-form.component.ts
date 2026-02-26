import { Component, Inject, Optional, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { Employee } from '../../core/models/employee.model';
import { EmployeeFormService } from '../../core/services/employee-form.service';

@Component({
  selector: 'app-employee-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatOptionModule,
    MatIconModule
  ],
  templateUrl: './employee-form.component.html',
  styleUrls: ['./employee-form.component.scss']
})
export class EmployeeFormComponent {
  @Input() data!: { mode: string, employee?: Employee };
  states: { code: string; name: string }[] = [];
  form!: FormGroup;

  constructor(
    private employeeFormService: EmployeeFormService,
    private router: Router,
    @Optional() public dialogRef?: MatDialogRef<EmployeeFormComponent>,
    @Optional() @Inject(MAT_DIALOG_DATA) public injectedData?: { mode: string, employee?: Employee }
  ) { }

  ngOnInit() {
    const mergedData = this.data || this.injectedData || { mode: 'registration' };
    this.data = mergedData;

    this.form = this.employeeFormService.createForm(mergedData);
    this.states = this.employeeFormService.states;

    if (this.data.mode === 'admin-edit' && this.data.employee) {
      this.form.patchValue(this.data.employee);
    }

    // Remove validators for non-registration fields
    if (this.data.mode === 'registration') {
      const optionalFields = ['address', 'city', 'state', 'zip', 'cellPhone', 'homePhone', 'role'];
      optionalFields.forEach(field => {
        this.form.get(field)?.clearValidators();
        this.form.get(field)?.updateValueAndValidity();
      });
    }
  }

  getFormTitle() {
    return this.employeeFormService.getFormTitle(this.data.mode);
  }

  save() {
    try {
      const save$ = this.employeeFormService.save(this.data.mode, this.form, this.data.employee?.id);
      save$.subscribe({
        next: () => {
          alert('Save successful');
          if (this.dialogRef) {
            this.dialogRef.close('success');
          } else { 
            /** TODO: Not needed anymore, can delete (registration is submitted through dialog instead of its own page*/
            this.router.navigate(['/login']);
          }
        },
        error: err => alert(err.message || 'Failed to save employee')
      });
    } catch (err: any) {
      alert(err.message);
    }
  }

  cancel() {
    if (this.dialogRef) {
      this.dialogRef.close();
    } else {
      this.router.navigate(['/']);
    }
  }
}
