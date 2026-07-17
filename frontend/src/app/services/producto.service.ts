// Comunicación: Recupera datos de productos desde la API REST. Además, expone un BehaviorSubject para permitir la comunicación reactiva de búsquedas entre DashboardComponent y ProductosComponent, filtrando por sucursal.
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { API_BASE_URL } from '../config/api-config';

export interface Producto {
  id?: number;
  nombre: string;
  descripcion: string;
  precio: number;
  stock: number;
  sucursalId?: number;
  sucursalNombre?: string;
  activo?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ProductoService {
  private apiPublicUrl = `${API_BASE_URL}/api/productos`;
  private apiAdminUrl = `${API_BASE_URL}/admin/productos`;

  private searchSubject = new BehaviorSubject<string>('');
  searchQuery$ = this.searchSubject.asObservable();

  constructor(private http: HttpClient) {}

  setSearchQuery(query: string): void {
    this.searchSubject.next(query);
  }

  listarProductos(sucursalId?: number | null): Observable<Producto[]> {
    const url = sucursalId ? `${this.apiPublicUrl}?sucursalId=${sucursalId}` : this.apiPublicUrl;
    return this.http.get<Producto[]>(url);
  }

  buscarProductos(nombre: string, sucursalId?: number | null): Observable<Producto[]> {
    let url = `${this.apiPublicUrl}/buscar?nombre=${encodeURIComponent(nombre)}`;
    if (sucursalId) {
      url += `&sucursalId=${sucursalId}`;
    }
    return this.http.get<Producto[]>(url);
  }

  obtenerProductoPorId(id: number): Observable<Producto> {
    return this.http.get<Producto>(`${this.apiPublicUrl}/${id}`);
  }

  crearProducto(producto: Producto): Observable<Producto> {
    return this.http.post<Producto>(this.apiAdminUrl, producto);
  }

  actualizarProducto(id: number, producto: Producto): Observable<Producto> {
    return this.http.put<Producto>(`${this.apiAdminUrl}/${id}`, producto);
  }

  eliminarProducto(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiAdminUrl}/${id}`);
  }
}
