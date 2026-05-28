package com.tambo.inventory.service;

import com.tambo.inventory.dto.DashboardKpiResponse;
import com.tambo.inventory.dto.ProductoMasVendidoResponse;
import com.tambo.inventory.exception.ResourceNotFoundException;
import com.tambo.inventory.repository.DetallePedidoRepository;
import com.tambo.inventory.repository.InventarioRepository;
import com.tambo.inventory.repository.PedidoRepository;
import com.tambo.inventory.repository.SucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detalleRepository;
    private final InventarioRepository inventarioRepository;
    private final SucursalRepository sucursalRepository;

    @Autowired
    public ReporteService(
            PedidoRepository pedidoRepository,
            DetallePedidoRepository detalleRepository,
            InventarioRepository inventarioRepository,
            SucursalRepository sucursalRepository) {
        this.pedidoRepository = pedidoRepository;
        this.detalleRepository = detalleRepository;
        this.inventarioRepository = inventarioRepository;
        this.sucursalRepository = sucursalRepository;
    }

    @Transactional(readOnly = true)
    public DashboardKpiResponse obtenerKpis(Long sucursalId) {
        LocalDateTime inicioDia = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime finDia = LocalDateTime.now().with(LocalTime.MAX);

        BigDecimal ventasDelDia;
        Long pedidosPendientes;
        Long stockCritico;
        Long entregasHoy;

        if (sucursalId != null) {
            sucursalRepository.findById(sucursalId)
                    .orElseThrow(() -> new ResourceNotFoundException("La sucursal con ID " + sucursalId + " no existe."));

            ventasDelDia = pedidoRepository.calcularVentasDelDiaPorSucursal(sucursalId, inicioDia, finDia);
            pedidosPendientes = pedidoRepository.contarPedidosPendientesPorSucursal(sucursalId);
            stockCritico = (long) inventarioRepository.findAlertasStockMinimoPorSucursal(sucursalId).size();
            entregasHoy = pedidoRepository.contarEntregasHoyPorSucursal(sucursalId, inicioDia, finDia);
        } else {
            ventasDelDia = pedidoRepository.calcularVentasDelDiaGlobal(inicioDia, finDia);
            pedidosPendientes = pedidoRepository.contarPedidosPendientesGlobal();
            stockCritico = (long) inventarioRepository.findAlertasStockMinimoGlobal().size();
            entregasHoy = pedidoRepository.contarEntregasHoyGlobal(inicioDia, finDia);
        }

        return new DashboardKpiResponse(
                ventasDelDia,
                pedidosPendientes,
                stockCritico,
                entregasHoy
        );
    }

    @Transactional(readOnly = true)
    public List<ProductoMasVendidoResponse> obtenerProductosMasVendidos(Long sucursalId, int limite) {
        List<ProductoMasVendidoResponse> lista;

        if (sucursalId != null) {
            sucursalRepository.findById(sucursalId)
                    .orElseThrow(() -> new ResourceNotFoundException("La sucursal con ID " + sucursalId + " no existe."));
            lista = detalleRepository.obtenerProductosMasVendidosPorSucursal(sucursalId);
        } else {
            lista = detalleRepository.obtenerProductosMasVendidosGlobal();
        }

        return lista.stream()
                .limit(limite)
                .collect(Collectors.toList());
    }
}
