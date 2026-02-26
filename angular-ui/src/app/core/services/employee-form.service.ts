// employee-form.service.ts
import { Injectable } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Employee } from '../models/employee.model';
import { US_STATES } from '../models/states';
import { EmployeeService } from './employee.service';
import { RegistrationService } from './registration.service';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class EmployeeFormService {
    // Expose states to components
    readonly states = US_STATES;

    constructor(
        private fb: FormBuilder,
        private employeeService: EmployeeService,
        private registrationService: RegistrationService
    ) { }

    createForm(data?: { mode: string; employee?: Employee }): FormGroup {
        const form = this.fb.group({
            firstName: ['', [
                Validators.required,
                Validators.pattern(/^[A-Za-z\s.'-]{2,35}$/) // letters, spaces, apostrophes, hyphens, periods
            ]],
            lastName: ['', [
                Validators.required,
                Validators.pattern(/^[A-Za-z\s.'-]{2,35}$/)
            ]],
            address: ['', [
                Validators.required,
                Validators.pattern(/^[A-Za-z0-9\s.,'-]{10,50}$/) // letters, numbers, spaces, commas, periods, hyphens, apostrophes
            ]],
            city: ['', [
                Validators.required,
                Validators.pattern(/^[A-Za-z\s'-]{2,50}$/) // letters, spaces, hyphens, apostrophes
            ]],
            state: ['', Validators.required],
            zip: ['', [
                Validators.required,
                Validators.pattern(/^\d{5}(-\d{4})?$/) // 12345 or 12345-6789
            ]],
            cellPhone: ['', [
                Validators.required,
                Validators.pattern(/^\d{10}$/) // strictly 10 digits
            ]],
            homePhone: ['', [
                Validators.required,
                Validators.pattern(/^\d{10}$/)
            ]],
            email: ['', [
                Validators.required,
                Validators.email,
                Validators.minLength(5),
                Validators.maxLength(50)
            ]],
            role: ['']

        });

        if (data?.employee) {
            form.patchValue(data.employee);
        }

        // Role handling
        if (data?.mode === 'admin-add' || data?.mode === 'admin-edit') {
            form.get('role')?.setValidators([Validators.required]);
        } else if (data?.mode === 'registration') {
            form.get('role')?.setValue('USER');
            form.get('role')?.disable();
        } else {
            form.get('role')?.clearValidators();
            form.get('role')?.disable();
        }
        form.get('role')?.updateValueAndValidity();

        return form;
    }

    getFormTitle(mode: string): string {
        switch (mode) {
            case 'admin-add': return 'Add New Employee';
            case 'admin-edit': return 'Edit Employee';
            case 'user-edit': return 'Edit Your Profile';
            case 'registration': return 'Registration Form';
            default: return 'Employee Form';
        }
    }

    save(mode: string, form: FormGroup, employeeId?: number): Observable<any> {
        if (form.invalid) {
            throw new Error('Form is invalid');
        }
        const employeeData = form.value as Employee;

        switch (mode) {
            case 'admin-add':
                return this.employeeService.addEmployee(employeeData);
            case 'admin-edit':
            case 'user-edit':
                if (!employeeId) {
                    throw new Error('Cannot update: ID is missing.');
                }
                return this.employeeService.update(employeeId, employeeData);
            case 'registration':
                return this.registrationService.register(employeeData);
            default:
                throw new Error('Unknown mode');
        }
    }
}
