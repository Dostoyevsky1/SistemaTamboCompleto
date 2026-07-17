// Comunicación: Recupera la lista de sucursales desde la API REST (/api/sucursales) y expone un BehaviorSubject para propagar el cambio de sucursal activa a todos los componentes del sistema.
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { API_BASE_URL } from '../config/api-config';

export interface Sucursal {
  id: number;
  nombre: string;
  direccion: string;
}

@Injectable({
  providedIn: 'root'
})
export class SucursalService {
  private apiUrl = `${API_BASE_URL}/api/sucursales`;

  private sucursalSeleccionadaSubject = new BehaviorSubject<number | null>(null);
  sucursalSeleccionada$ = this.sucursalSeleccionadaSubject.asObservable();

  constructor(private http: HttpClient) {}

  listarSucursales(): Observable<Sucursal[]> {
    return this.http.get<Sucursal[]>(this.apiUrl);
  }

  setSucursalSeleccionada(id: number | null): void {
    this.sucursalSeleccionadaSubject.next(id);
  }

  getSucursalSeleccionada(): number | null {
    return this.sucursalSeleccionadaSubject.value;
  }
}
