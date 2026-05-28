package com.tambo.inventory.repository;

import com.tambo.inventory.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findBySku(String sku);
    Boolean existsBySku(String sku);
    List<Producto> findByCategoriaId(Long categoriaId);
    List<Producto> findByActivoTrue();
}
