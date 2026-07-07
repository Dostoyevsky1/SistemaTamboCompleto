package com.tambo.inventory.controller;

import com.tambo.inventory.dto.DashboardStatsDTO;
import com.tambo.inventory.dto.TopProductoDTO;
import com.tambo.inventory.entity.Pedido;
import com.tambo.inventory.repository.PedidoRepository;
import com.tambo.inventory.repository.ProductoRepository;
import com.tambo.inventory.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class DashboardController {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public DashboardController(PedidoRepository pedidoRepository,
                               ProductoRepository productoRepository,
                               UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/api/dashboard/stats")
    public ResponseEntity<DashboardStatsDTO> obtenerEstadisticas(@RequestParam(value = "sucursalId", required = false) Long sucursalId) {
        List<Pedido> pedidos = (sucursalId != null) ?
                pedidoRepository.findByProductoSucursalId(sucursalId) :
                pedidoRepository.findAll();

        BigDecimal totalVentas = pedidos.stream()
                .map(Pedido::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long cantidadProductos = (sucursalId != null) ?
                productoRepository.countBySucursalIdAndActivoTrue(sucursalId) :
                productoRepository.countByActivoTrue();

        long bajoStockCount = (sucursalId != null) ?
                productoRepository.countBySucursalIdAndStockLessThanEqualAndActivoTrue(sucursalId, 10) :
                productoRepository.countByStockLessThanEqualAndActivoTrue(10);

        long usuariosActivos = usuarioRepository.countByActivoTrue();

        Map<String, Long> topMap = pedidos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getProducto().getNombre(),
                        Collectors.summingLong(Pedido::getCantidad)
                ));

        List<TopProductoDTO> topProductos = topMap.entrySet().stream()
                .map(entry -> new TopProductoDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> b.getCantidadVendida().compareTo(a.getCantidadVendida()))
                .limit(5)
                .collect(Collectors.toList());

        DashboardStatsDTO stats = new DashboardStatsDTO(
                totalVentas,
                cantidadProductos,
                bajoStockCount,
                usuariosActivos,
                topProductos
        );

        return ResponseEntity.ok(stats);
    }
}
