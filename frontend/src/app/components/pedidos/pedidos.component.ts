// Comunicación: Inyecta PedidoService para consultar el listado histórico general de pedidos desde la API REST (/api/pedidos) y ChangeDetectorRef para forzar la actualización de la UI.
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { PedidoService, Pedido } from '../../services/pedido.service';

@Component({
  selector: 'app-pedidos',
  templateUrl: './pedidos.component.html',
  styleUrls: ['./pedidos.component.css'],
  standalone: false
})
export class PedidosComponent implements OnInit {
  pedidos: Pedido[] = [];

  constructor(
    private pedidoService: PedidoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarPedidos();
  }

  cargarPedidos(): void {
    this.pedidoService.listarPedidos().subscribe({
      next: (data) => {
        this.pedidos = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error al cargar pedidos:', err)
    });
  }
}
