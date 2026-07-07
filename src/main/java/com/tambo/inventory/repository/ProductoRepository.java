package com.tambo.inventory.repository;

import com.tambo.inventory.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivoTrue();

    List<Producto> findBySucursalIdAndActivoTrue(Long sucursalId);

    @Query("SELECT p FROM Producto p WHERE p.nombre LIKE %:nombre%")
    List<Producto> buscarPorNombre(@Param("nombre") String nombre);

    @Query("SELECT p FROM Producto p WHERE p.nombre LIKE %:nombre% AND (:sucursalId IS NULL OR p.sucursal.id = :sucursalId) AND p.activo = true")
    List<Producto> buscarPorNombreYSucursal(@Param("nombre") String nombre, @Param("sucursalId") Long sucursalId);

    long countBySucursalIdAndActivoTrue(Long sucursalId);
    long countByActivoTrue();

    long countBySucursalIdAndStockLessThanEqualAndActivoTrue(Long sucursalId, Integer stock);
    long countByStockLessThanEqualAndActivoTrue(Integer stock);
}
