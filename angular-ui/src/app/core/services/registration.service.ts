import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { Employee } from '../models/employee.model';

@Injectable({ providedIn: 'root' })
export class RegistrationService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  register(employee: Employee): Observable<Employee> {
    return this.http.post<Employee>(`${this.baseUrl}/api/registration`, employee);
  }
}
