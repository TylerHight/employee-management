import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { Employee } from '../models/employee.model';
import { Page } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  getEmployees(
    page: number,
    size: number,
    search?: string,
    sort?: string
  ): Observable<Page<Employee>> {

    const params: any = {
      page: page,
      size: size
    };

    if (search) params.search = search;
    if (sort) params.sort = sort;

    return this.http.get<Page<Employee>>(`${this.baseUrl}/api/employees`, { params });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/api/employees/${id}`);
  }

  update(id: number, partial: Partial<Employee>): Observable<Employee> {
    return this.http.patch<Employee>(`${this.baseUrl}/api/employees/${id}`, partial);
  }

  addEmployee(employee: Employee) {
    return this.http.post<Employee>(`${this.baseUrl}/api/employees`, employee);
  }

  getByUserId(userId: string): Observable<Employee> {
    return this.http.get<Employee>(`${this.baseUrl}/api/employees/user/${userId}`);
  }

  getById(id: number): Observable<Employee> {
    return this.http.get<Employee>(`${this.baseUrl}/api/employees/${id}`);
  }

  getSelf(): Observable<Employee> {
    return this.http.get<Employee>(`${this.baseUrl}/api/employees/me`);
  }

}
