import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Registration {
  firstName: string;
  lastName: string;
  email: string;
  status: string;
}

@Injectable({ providedIn: 'root' })
export class RegistrationAdminService {
  private baseUrl = (`${environment.apiUrl}/api/registration`);

  constructor(private http: HttpClient) {}

  getAllRegistrations(): Observable<any> {
    return this.http.get(`${this.baseUrl}`);
  }

  getPendingRegistrations(): Observable<any> {
    return this.http.get(`${this.baseUrl}/pending`);
  }

  approveRegistration(email: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/${email}/approve`, {});
  }

  declineRegistration(email: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/${email}/decline`, {});
  }

  deleteRegistration(email: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${email}`, {});
  }
}
