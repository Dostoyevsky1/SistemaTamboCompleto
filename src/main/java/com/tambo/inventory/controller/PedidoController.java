package com.tambo.inventory.controller;

import com.tambo.inventory.dto.*;
import com.tambo.inventory.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PedidoController {

    private final PedidoService pedidoService;

    @Autowired
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // --- Endpoints de Pedidos ---

    @GetMapping("/pedidos")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<List<PedidoResponse>> obtenerTodos() {
        List<PedidoResponse> pedidos = pedidoService.obtenerTodos();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/pedidos/sucursal/{sucursalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<List<PedidoResponse>> obtenerPorSucursal(@PathVariable Long sucursalId) {
        List<PedidoResponse> pedidos = pedidoService.obtenerPorSucursal(sucursalId);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/pedidos/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<PedidoResponse> obtenerPorId(@PathVariable Long id) {
        PedidoResponse pedido = pedidoService.obtenerPorId(id);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/pedidos/codigo/{codigo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<PedidoResponse> obtenerPorCodigo(@PathVariable String codigo) {
        // En HTTP GET, el carácter '#' viaja codificado o puede ser ignorado. 
        // Si el cliente envía "#TB-8902", en la ruta llegará como "%23TB-8902" o simplemente "TB-8902".
        // Le damos soporte a ambos.
        String codigoBuscado = codigo.startsWith("#") ? codigo : "#" + codigo;
        PedidoResponse pedido = pedidoService.obtenerPorCodigo(codigoBuscado);
        return ResponseEntity.ok(pedido);
    }

    @PostMapping("/pedidos")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<PedidoResponse> registrarVenta(
            @Valid @RequestBody PedidoRequest request,
            Principal principal) {
        PedidoResponse response = pedidoService.registrarVenta(request, principal.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/pedidos/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<PedidoResponse> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String nuevoEstado) {
        PedidoResponse response = pedidoService.actualizarEstado(id, nuevoEstado);
        return ResponseEntity.ok(response);
    }

    // --- Endpoints de Ventas/Comprobantes ---

    @GetMapping("/ventas")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<List<VentaResponse>> obtenerVentasTodas() {
        List<VentaResponse> ventas = pedidoService.obtenerVentasTodas();
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/ventas/sucursal/{sucursalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<List<VentaResponse>> obtenerVentasPorSucursal(@PathVariable Long sucursalId) {
        List<VentaResponse> ventas = pedidoService.obtenerVentasPorSucursal(sucursalId);
        return ResponseEntity.ok(ventas);
    }
}
