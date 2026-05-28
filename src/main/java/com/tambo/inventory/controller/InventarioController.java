package com.tambo.inventory.controller;

import com.tambo.inventory.dto.*;
import com.tambo.inventory.service.InventarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    @Autowired
    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping("/sucursal/{sucursalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<List<InventarioResponse>> obtenerInventarioPorSucursal(@PathVariable Long sucursalId) {
        List<InventarioResponse> inventario = inventarioService.obtenerInventarioPorSucursal(sucursalId);
        return ResponseEntity.ok(inventario);
    }

    @GetMapping("/alertas")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<List<InventarioResponse>> obtenerAlertasGlobales() {
        List<InventarioResponse> alertas = inventarioService.obtenerAlertasStockMinimo(null);
        return ResponseEntity.ok(alertas);
    }

    @GetMapping("/alertas/sucursal/{sucursalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<List<InventarioResponse>> obtenerAlertasPorSucursal(@PathVariable Long sucursalId) {
        List<InventarioResponse> alertas = inventarioService.obtenerAlertasStockMinimo(sucursalId);
        return ResponseEntity.ok(alertas);
    }

    @GetMapping("/movimientos")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<List<MovimientoResponse>> obtenerMovimientosGlobales() {
        List<MovimientoResponse> movimientos = inventarioService.obtenerHistorialMovimientos(null);
        return ResponseEntity.ok(movimientos);
    }

    @GetMapping("/movimientos/sucursal/{sucursalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<List<MovimientoResponse>> obtenerMovimientosPorSucursal(@PathVariable Long sucursalId) {
        List<MovimientoResponse> movimientos = inventarioService.obtenerHistorialMovimientos(sucursalId);
        return ResponseEntity.ok(movimientos);
    }

    @PostMapping("/movimientos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovimientoResponse> registrarMovimientoManual(
            @Valid @RequestBody MovimientoRequest request,
            Principal principal) {
        MovimientoResponse response = inventarioService.registrarMovimientoManual(request, principal.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/traslados")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MovimientoResponse>> registrarTraslado(
            @Valid @RequestBody TrasladoRequest request,
            Principal principal) {
        List<MovimientoResponse> response = inventarioService.registrarTraslado(request, principal.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
