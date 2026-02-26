import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { jwtDecode } from 'jwt-decode';
import { environment } from '../../../environments/environment';

interface JwtPayload {
  role: string;
  sub: string;
  exp: number;
  userId: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  login(email: string, password: string) {
    return this.http.post<{ id: number; email: string; role: string; token: string }>(
      `${this.baseUrl}/api/login/authenticate`,
      { email, password }
    );
  }


  saveToken(token: string) {
    sessionStorage.setItem('token', token);
  }

  getToken(): string | null {
    return sessionStorage.getItem('token');
  }

  decodeToken(token: string): JwtPayload {
    return jwtDecode<JwtPayload>(token);
  }

  logout() {
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('role');
    sessionStorage.removeItem('userId');
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) return false;
    try {
      const payload = this.decodeToken(token);
      return Date.now() / 1000 < payload.exp;
    } catch {
      return false;
    }
  }

  isAdmin(): boolean {
    const token = this.getToken();
    if (!token) return false;
    try {
      const payload = this.decodeToken(token);
      return payload.role === 'ADMIN';
    } catch {
      return false;
    }
  }

  getUserName(): string {
    const payload = this.getDecodedPayload();
    return payload?.sub || '';
  }

  private getDecodedPayload(): JwtPayload | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      return this.decodeToken(token);
    } catch {
      return null;
    }
  }

  getUserId(): string {
    const token = this.getToken();
    if (!token) return '';
    const payload: any = this.decodeToken(token);
    return payload.userId;
  }

  requestPasswordReset(email: string) {
    return this.http.post<void>(
      `${this.baseUrl}/api/login/request-password-reset`,
      { email }
    );
  }

  updatePasswordWithToken(token: string, newPassword: string) {
    return this.http.post<void>(
      `${this.baseUrl}/api/login/set-password`,
      { token, newPassword }
    );
  }

}
