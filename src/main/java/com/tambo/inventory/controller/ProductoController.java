package com.tambo.inventory.controller;

import com.tambo.inventory.dto.ApiResponse;
import com.tambo.inventory.dto.ProductoDTO;
import com.tambo.inventory.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductoController {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/api/productos")
    public ResponseEntity<List<ProductoDTO>> listarProductos() {
        List<ProductoDTO> productos = productoService.listarActivos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/api/productos/buscar")
    public ResponseEntity<List<ProductoDTO>> buscarProductos(@RequestParam("nombre") String nombre) {
        List<ProductoDTO> productos = productoService.buscarPorNombre(nombre);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/api/productos/{id}")
    public ResponseEntity<ProductoDTO> obtenerProductoPorId(@PathVariable Long id) {
        ProductoDTO producto = productoService.obtenerPorId(id);
        return ResponseEntity.ok(producto);
    }

    @PostMapping("/admin/productos")
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        ProductoDTO creado = productoService.crear(productoDTO);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @PutMapping("/admin/productos/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoDTO productoDTO) {
        ProductoDTO actualizado = productoService.actualizar(id, productoDTO);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/admin/productos/{id}")
    public ResponseEntity<ApiResponse> eliminarProducto(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.ok(new ApiResponse(true, "Producto eliminado exitosamente (baja lógica)."));
    }
}
