package com.tambo.inventory.repository;

import com.tambo.inventory.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findBySucursalIdOrderByFechaDesc(Long sucursalId);

    List<Pedido> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    Optional<Pedido> findByCodigo(String codigo);

    List<Pedido> findAllByOrderByFechaDesc();

    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p " +
           "WHERE p.fecha >= :inicioDia AND p.fecha <= :finDia AND p.estado <> 'CANCELADO'")
    BigDecimal calcularVentasDelDiaGlobal(@Param("inicioDia") LocalDateTime inicioDia, @Param("finDia") LocalDateTime finDia);

    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p " +
           "WHERE p.sucursal.id = :sucursalId AND p.fecha >= :inicioDia AND p.fecha <= :finDia AND p.estado <> 'CANCELADO'")
    BigDecimal calcularVentasDelDiaPorSucursal(@Param("sucursalId") Long sucursalId, @Param("inicioDia") LocalDateTime inicioDia, @Param("finDia") LocalDateTime finDia);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado IN ('PENDIENTE', 'EN_PREPARACION', 'EN_CAMINO')")
    Long contarPedidosPendientesGlobal();

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.sucursal.id = :sucursalId AND p.estado IN ('PENDIENTE', 'EN_PREPARACION', 'EN_CAMINO')")
    Long contarPedidosPendientesPorSucursal(@Param("sucursalId") Long sucursalId);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado = 'ENTREGADO' AND p.fecha >= :inicioDia AND p.fecha <= :finDia")
    Long contarEntregasHoyGlobal(@Param("inicioDia") LocalDateTime inicioDia, @Param("finDia") LocalDateTime finDia);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.sucursal.id = :sucursalId AND p.estado = 'ENTREGADO' AND p.fecha >= :inicioDia AND p.fecha <= :finDia")
    Long contarEntregasHoyPorSucursal(@Param("sucursalId") Long sucursalId, @Param("inicioDia") LocalDateTime inicioDia, @Param("finDia") LocalDateTime finDia);
}
