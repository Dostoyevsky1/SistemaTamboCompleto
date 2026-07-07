// Comunicación: Inyecta DashboardService y SucursalService para obtener las estadísticas consolidadas según la sucursal activa y renderizar los KPIs y gráficos SVG.
import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { Subscription } from 'rxjs';
import { DashboardService, DashboardStats } from '../../services/dashboard.service';
import { SucursalService } from '../../services/sucursal.service';

@Component({
  selector: 'app-inicio',
  templateUrl: './inicio.component.html',
  styleUrls: ['./inicio.component.css'],
  standalone: false
})
export class InicioComponent implements OnInit, OnDestroy {
  stats: DashboardStats = {
    totalVentas: 0,
    cantidadProductos: 0,
    bajoStockCount: 0,
    usuariosActivos: 0,
    topProductos: []
  };
  
  sucursalSub!: Subscription;
  maxVendido: number = 1;

  constructor(
    private dashboardService: DashboardService,
    private sucursalService: SucursalService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.sucursalSub = this.sucursalService.sucursalSeleccionada$.subscribe(id => {
      this.cargarStats(id);
    });
  }

  ngOnDestroy(): void {
    if (this.sucursalSub) {
      this.sucursalSub.unsubscribe();
    }
  }

  cargarStats(sucursalId: number | null): void {
    this.dashboardService.obtenerStats(sucursalId).subscribe({
      next: (data) => {
        this.stats = data;
        if (data.topProductos && data.topProductos.length > 0) {
          this.maxVendido = Math.max(...data.topProductos.map(p => p.cantidadVendida));
        } else {
          this.maxVendido = 1;
        }
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  getPorcentajeBarra(cantidad: number): number {
    return (cantidad / this.maxVendido) * 100;
  }
}
