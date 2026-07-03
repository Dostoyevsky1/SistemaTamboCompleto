// Comunicación: Inyecta ProductoService (para CRUD y suscripción a búsquedas del Dashboard), PedidoService (para registrar pedidos/ventas), AuthService (para control de roles) y ChangeDetectorRef para forzar la actualización de la UI.
import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subscription, debounceTime, distinctUntilChanged } from 'rxjs';
import { ProductoService, Producto } from '../../services/producto.service';
import { PedidoService } from '../../services/pedido.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-productos',
  templateUrl: './productos.component.html',
  styleUrls: ['./productos.component.css'],
  standalone: false
})
export class ProductosComponent implements OnInit, OnDestroy {
  productos: Producto[] = [];
  isAdmin: boolean = false;
  searchSub!: Subscription;

  showProductModal: boolean = false;
  showOrderModal: boolean = false;
  selectedProducto: Producto | null = null;
  productoForm!: FormGroup;
  pedidoForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private productoService: ProductoService,
    private pedidoService: PedidoService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin();
    this.cargarProductos();

    this.productoForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(2)]],
      descripcion: [''],
      precio: [null, [Validators.required, Validators.min(0.01)]],
      stock: [null, [Validators.required, Validators.min(0)]]
    });

    this.pedidoForm = this.fb.group({
      cantidad: [1, [Validators.required, Validators.min(1)]]
    });

    this.searchSub = this.productoService.searchQuery$
      .pipe(
        debounceTime(300),
        distinctUntilChanged()
      )
      .subscribe(query => {
        if (query.trim()) {
          this.productoService.buscarProductos(query).subscribe(prods => {
            this.productos = prods;
            this.cdr.detectChanges();
          });
        } else {
          this.cargarProductos();
        }
      });
  }

  ngOnDestroy(): void {
    if (this.searchSub) {
      this.searchSub.unsubscribe();
    }
  }

  cargarProductos(): void {
    this.productoService.listarProductos().subscribe({
      next: (prods) => {
        this.productos = prods;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
      }
    });
  }

  abrirModalProducto(producto: Producto | null = null): void {
    this.selectedProducto = producto;
    this.showProductModal = true;

    if (producto) {
      this.productoForm.patchValue({
        nombre: producto.nombre,
        descripcion: producto.descripcion,
        precio: producto.precio,
        stock: producto.stock
      });
    } else {
      this.productoForm.reset({
        nombre: '',
        descripcion: '',
        precio: null,
        stock: null
      });
    }
  }

  cerrarModalProducto(): void {
    this.showProductModal = false;
    this.selectedProducto = null;
    this.productoForm.reset();
  }

  guardarProducto(): void {
    if (this.productoForm.invalid) {
      this.productoForm.markAllAsTouched();
      return;
    }

    const data: Producto = this.productoForm.value;

    if (this.selectedProducto && this.selectedProducto.id) {
      this.productoService.actualizarProducto(this.selectedProducto.id, data).subscribe({
        next: () => {
          this.cerrarModalProducto();
          this.cargarProductos();
        },
        error: (err) => alert('Error al actualizar el producto: ' + (err.error?.message || err.message))
      });
    } else {
      this.productoService.crearProducto(data).subscribe({
        next: () => {
          this.cerrarModalProducto();
          this.cargarProductos();
        },
        error: (err) => alert('Error al crear el producto: ' + (err.error?.message || err.message))
      });
    }
  }

  eliminarProducto(id: number): void {
    if (confirm('¿Está seguro de eliminar este producto? (Se dará de baja lógica en el backend)')) {
      this.productoService.eliminarProducto(id).subscribe({
        next: () => {
          this.cargarProductos();
        },
        error: (err) => alert('Error al eliminar producto')
      });
    }
  }

  abrirModalPedido(producto: Producto): void {
    this.selectedProducto = producto;
    this.showOrderModal = true;
    this.pedidoForm.reset({
      cantidad: 1
    });

    this.pedidoForm.get('cantidad')?.setValidators([
      Validators.required,
      Validators.min(1),
      Validators.max(producto.stock)
    ]);
    this.pedidoForm.get('cantidad')?.updateValueAndValidity();
  }

  cerrarModalPedido(): void {
    this.showOrderModal = false;
    this.selectedProducto = null;
    this.pedidoForm.reset({ cantidad: 1 });
  }

  guardarPedido(): void {
    if (this.pedidoForm.invalid || !this.selectedProducto || !this.selectedProducto.id) {
      this.pedidoForm.markAllAsTouched();
      return;
    }

    const cantidad = this.pedidoForm.value.cantidad;
    const data = {
      productoId: this.selectedProducto.id,
      cantidad: cantidad
    };

    this.pedidoService.registrarPedido(data).subscribe({
      next: () => {
        this.cerrarModalPedido();
        this.cargarProductos();
      },
      error: (err) => alert('Error al registrar pedido: ' + (err.error?.message || err.message))
    });
  }
}
