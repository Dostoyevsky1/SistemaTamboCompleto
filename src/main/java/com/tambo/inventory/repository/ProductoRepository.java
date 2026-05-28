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

    @Query("SELECT p FROM Producto p WHERE p.nombre LIKE %:nombre%")
    List<Producto> buscarPorNombre(@Param("nombre") String nombre);
}
