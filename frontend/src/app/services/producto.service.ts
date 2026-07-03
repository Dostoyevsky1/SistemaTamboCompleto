// Comunicación: Recupera datos de productos desde la API REST. Además, expone un BehaviorSubject para permitir la comunicación reactiva de búsquedas entre DashboardComponent y ProductosComponent.
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';

export interface Producto {
  id?: number;
  nombre: string;
  descripcion: string;
  precio: number;
  stock: number;
  activo?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ProductoService {
  private apiPublicUrl = 'http://localhost:8080/api/productos';
  private apiAdminUrl = 'http://localhost:8080/admin/productos';

  private searchSubject = new BehaviorSubject<string>('');
  searchQuery$ = this.searchSubject.asObservable();

  constructor(private http: HttpClient) {}

  setSearchQuery(query: string): void {
    this.searchSubject.next(query);
  }

  listarProductos(): Observable<Producto[]> {
    return this.http.get<Producto[]>(this.apiPublicUrl);
  }

  buscarProductos(nombre: string): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.apiPublicUrl}/buscar?nombre=${encodeURIComponent(nombre)}`);
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
