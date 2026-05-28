package com.tambo.inventory.controller;

import com.tambo.inventory.dto.ApiResponse;
import com.tambo.inventory.dto.SucursalRequest;
import com.tambo.inventory.dto.SucursalResponse;
import com.tambo.inventory.service.SucursalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
public class SucursalController {

    private final SucursalService sucursalService;

    @Autowired
    public SucursalController(SucursalService sucursalService) {
        this.sucursalService = sucursalService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<List<SucursalResponse>> obtenerTodas() {
        List<SucursalResponse> sucursales = sucursalService.obtenerTodas();
        return ResponseEntity.ok(sucursales);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<SucursalResponse> obtenerPorId(@PathVariable Long id) {
        SucursalResponse sucursal = sucursalService.obtenerPorId(id);
        return ResponseEntity.ok(sucursal);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SucursalResponse> crear(@Valid @RequestBody SucursalRequest request) {
        SucursalResponse creada = sucursalService.crear(request);
        return new ResponseEntity<>(creada, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SucursalResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody SucursalRequest request) {
        SucursalResponse actualizada = sucursalService.actualizar(id, request);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> eliminar(@PathVariable Long id) {
        sucursalService.eliminar(id);
        return ResponseEntity.ok(new ApiResponse(true, "Sucursal eliminada exitosamente."));
    }
}
