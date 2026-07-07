// Comunicación: Sirve como layout principal. Inyecta AuthService, SucursalService para el dropdown de sucursales, y ProductoService para cargar alertas de stock crítico y buscador general.
import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { ProductoService, Producto } from '../../services/producto.service';
import { SucursalService, Sucursal } from '../../services/sucursal.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  standalone: false
})
export class DashboardComponent implements OnInit, OnDestroy {
  nombreUsuario: string = '';
  rolUsuario: string = '';
  avatarLetra: string = 'U';
  isAdmin: boolean = false;

  sucursales: Sucursal[] = [];
  sucursalSeleccionadaId: number | null = null;
  alertasStock: Producto[] = [];
  mostrarAlertasDropdown: boolean = false;
  
  sucursalSub!: Subscription;

  constructor(
    private authService: AuthService,
    private productoService: ProductoService,
    private sucursalService: SucursalService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.nombreUsuario = this.authService.getNombre() || 'Usuario';
    this.isAdmin = this.authService.isAdmin();
    this.rolUsuario = this.isAdmin ? 'Administrador' : 'Empleado';
    this.avatarLetra = this.nombreUsuario.charAt(0).toUpperCase();

    this.sucursalService.listarSucursales().subscribe({
      next: (data) => {
        this.sucursales = data;
        if (data.length > 0) {
          const defaultId = data[0].id;
          this.sucursalSeleccionadaId = defaultId;
          this.sucursalService.setSucursalSeleccionada(defaultId);
        }
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });

    this.sucursalSub = this.sucursalService.sucursalSeleccionada$.subscribe(id => {
      if (id !== null) {
        this.cargarAlertasStock(id);
      }
    });
  }

  ngOnDestroy(): void {
    if (this.sucursalSub) {
      this.sucursalSub.unsubscribe();
    }
  }

  cargarAlertasStock(sucursalId: number): void {
    this.productoService.listarProductos(sucursalId).subscribe({
      next: (prods) => {
        this.alertasStock = prods.filter(p => p.stock <= 10);
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  onSucursalChange(event: any): void {
    const sucursalId = +event.target.value;
    this.sucursalSeleccionadaId = sucursalId;
    this.sucursalService.setSucursalSeleccionada(sucursalId);
  }

  toggleAlertasDropdown(): void {
    this.mostrarAlertasDropdown = !this.mostrarAlertasDropdown;
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
