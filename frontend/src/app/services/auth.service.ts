// Comunicación: Es inyectado en LoginComponent, AuthGuard, AdminGuard, AuthInterceptor y DashboardComponent para verificar la sesión, obtener roles del JWT y realizar el login con la API (/api/auth). Usa sessionStorage para expirar la sesión al cerrar la pestaña.
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { API_BASE_URL } from '../config/api-config';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = `${API_BASE_URL}/api/auth`;

  constructor(private http: HttpClient) {}

  login(credentials: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/login`, credentials).pipe(
      tap(res => {
        if (res && res.token) {
          sessionStorage.setItem('jwt_token', res.token);
          sessionStorage.setItem('jwt_username', res.username);
          sessionStorage.setItem('jwt_nombre', res.nombre);
          sessionStorage.setItem('jwt_roles', JSON.stringify(res.roles));
        }
      })
    );
  }

  logout(): void {
    sessionStorage.removeItem('jwt_token');
    sessionStorage.removeItem('jwt_username');
    sessionStorage.removeItem('jwt_nombre');
    sessionStorage.removeItem('jwt_roles');
  }

  getToken(): string | null {
    return sessionStorage.getItem('jwt_token');
  }

  getUsername(): string | null {
    return sessionStorage.getItem('jwt_username');
  }

  getNombre(): string | null {
    return sessionStorage.getItem('jwt_nombre');
  }

  getRoles(): string[] {
    const roles = sessionStorage.getItem('jwt_roles');
    return roles ? JSON.parse(roles) : [];
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  isAdmin(): boolean {
    return this.getRoles().includes('ROLE_ADMIN');
  }
}
