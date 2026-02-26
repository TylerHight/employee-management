import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

// Import the EmployeeFormComponent
import { EmployeeFormComponent } from '../shared/employee-form/employee-form.component';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,         // Needed for *ngIf, *ngFor, etc.
    ReactiveFormsModule   // Needed for [formGroup] and reactive form directives
  ],
  exports: [
    CommonModule,          // Export so other modules get *ngIf, *ngFor
    ReactiveFormsModule    // Export so other modules get [formGroup]
  ]
})
export class CoreModule { }
