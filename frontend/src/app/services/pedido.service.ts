// Comunicación: Es inyectado en PedidosComponent y ProductosComponent para registrar ventas y recuperar el historial de pedidos desde el backend mediante la API REST (/api/pedidos).
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api-config';

export interface Pedido {
  id?: number;
  usuarioId?: number;
  usuarioNombre?: string;
  productoId: number;
  productoNombre?: string;
  cantidad: number;
  fecha?: string;
  total?: number;
}

@Injectable({
  providedIn: 'root'
})
export class PedidoService {
  private apiUrl = `${API_BASE_URL}/api/pedidos`;

  constructor(private http: HttpClient) {}

  registrarPedido(pedido: Pedido): Observable<Pedido> {
    return this.http.post<Pedido>(this.apiUrl, pedido);
  }

  listarPedidos(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(this.apiUrl);
  }

  listarMisPedidos(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.apiUrl}/mis-pedidos`);
  }
}
