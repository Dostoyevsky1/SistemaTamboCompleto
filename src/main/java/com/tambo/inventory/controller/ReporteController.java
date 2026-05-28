package com.tambo.inventory.controller;

import com.tambo.inventory.dto.DashboardKpiResponse;
import com.tambo.inventory.dto.ProductoMasVendidoResponse;
import com.tambo.inventory.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')") // Los reportes son legibles por ambos
public class ReporteController {

    private final ReporteService reporteService;

    @Autowired
    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/kpis")
    public ResponseEntity<DashboardKpiResponse> obtenerKpisGlobales() {
        DashboardKpiResponse kpis = reporteService.obtenerKpis(null);
        return ResponseEntity.ok(kpis);
    }

    @GetMapping("/kpis/sucursal/{sucursalId}")
    public ResponseEntity<DashboardKpiResponse> obtenerKpisPorSucursal(@PathVariable Long sucursalId) {
        DashboardKpiResponse kpis = reporteService.obtenerKpis(sucursalId);
        return ResponseEntity.ok(kpis);
    }

    @GetMapping("/mas-vendidos")
    public ResponseEntity<List<ProductoMasVendidoResponse>> obtenerMasVendidosGlobal(
            @RequestParam(defaultValue = "5") int limite) {
        List<ProductoMasVendidoResponse> masVendidos = reporteService.obtenerProductosMasVendidos(null, limite);
        return ResponseEntity.ok(masVendidos);
    }

    @GetMapping("/mas-vendidos/sucursal/{sucursalId}")
    public ResponseEntity<List<ProductoMasVendidoResponse>> obtenerMasVendidosPorSucursal(
            @PathVariable Long sucursalId,
            @RequestParam(defaultValue = "5") int limite) {
        List<ProductoMasVendidoResponse> masVendidos = reporteService.obtenerProductosMasVendidos(sucursalId, limite);
        return ResponseEntity.ok(masVendidos);
    }
}
