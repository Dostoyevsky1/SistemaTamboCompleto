// Comunicación: Sirve como layout principal. Inyecta AuthService para datos de perfil/cierre de sesión, y ProductoService para notificar búsquedas en tiempo real a ProductosComponent.
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ProductoService } from '../../services/producto.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  standalone: false
})
export class DashboardComponent implements OnInit {
  nombreUsuario: string = '';
  rolUsuario: string = '';
  avatarLetra: string = 'U';
  isAdmin: boolean = false;

  constructor(
    private authService: AuthService,
    private productoService: ProductoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.nombreUsuario = this.authService.getNombre() || 'Usuario';
    this.isAdmin = this.authService.isAdmin();
    this.rolUsuario = this.isAdmin ? 'Administrador' : 'Empleado';
    this.avatarLetra = this.nombreUsuario.charAt(0).toUpperCase();
  }

  isRouteActive(route: string): boolean {
    return this.router.url.includes(route);
  }

  onSearch(event: any): void {
    const query = event.target.value;
    this.productoService.setSearchQuery(query);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
