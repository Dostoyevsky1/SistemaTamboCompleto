// Comunicación: Recupera las métricas consolidadas (KPIs y datos del gráfico) desde la API REST (/api/dashboard/stats) y es inyectado en InicioComponent.
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TopProducto {
  nombre: string;
  cantidadVendida: number;
}

export interface DashboardStats {
  totalVentas: number;
  cantidadProductos: number;
  bajoStockCount: number;
  usuariosActivos: number;
  topProductos: TopProducto[];
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = 'http://localhost:8080/api/dashboard/stats';

  constructor(private http: HttpClient) {}

  obtenerStats(sucursalId?: number | null): Observable<DashboardStats> {
    const url = sucursalId ? `${this.apiUrl}?sucursalId=${sucursalId}` : this.apiUrl;
    return this.http.get<DashboardStats>(url);
  }
}
