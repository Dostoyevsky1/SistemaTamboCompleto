package com.tambo.inventory.repository;

import com.tambo.inventory.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findBySucursalIdAndProductoId(Long sucursalId, Long productoId);

    List<Inventario> findBySucursalId(Long sucursalId);

    List<Inventario> findByProductoId(Long productoId);

    @Query("SELECT i FROM Inventario i WHERE i.sucursal.id = :sucursalId AND i.stockActual <= i.producto.stockMinimo")
    List<Inventario> findAlertasStockMinimoPorSucursal(@Param("sucursalId") Long sucursalId);

    @Query("SELECT i FROM Inventario i WHERE i.stockActual <= i.producto.stockMinimo")
    List<Inventario> findAlertasStockMinimoGlobal();
}
