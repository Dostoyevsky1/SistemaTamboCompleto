package com.tambo.inventory.repository;

import com.tambo.inventory.dto.ProductoMasVendidoResponse;
import com.tambo.inventory.entity.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    @Query("SELECT new com.tambo.inventory.dto.ProductoMasVendidoResponse(" +
           "d.producto.id, d.producto.nombre, d.producto.sku, SUM(CAST(d.cantidad AS long)), SUM(d.subtotal)) " +
           "FROM DetallePedido d " +
           "GROUP BY d.producto.id, d.producto.nombre, d.producto.sku " +
           "ORDER BY SUM(d.cantidad) DESC")
    List<ProductoMasVendidoResponse> obtenerProductosMasVendidosGlobal();

    @Query("SELECT new com.tambo.inventory.dto.ProductoMasVendidoResponse(" +
           "d.producto.id, d.producto.nombre, d.producto.sku, SUM(CAST(d.cantidad AS long)), SUM(d.subtotal)) " +
           "FROM DetallePedido d " +
           "WHERE d.pedido.sucursal.id = :sucursalId " +
           "GROUP BY d.producto.id, d.producto.nombre, d.producto.sku " +
           "ORDER BY SUM(d.cantidad) DESC")
    List<ProductoMasVendidoResponse> obtenerProductosMasVendidosPorSucursal(@Param("sucursalId") Long sucursalId);
}
