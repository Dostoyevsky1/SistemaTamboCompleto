package com.tambo.inventory.repository;

import com.tambo.inventory.entity.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByInventarioIdOrderByFechaDesc(Long inventarioId);

    List<MovimientoInventario> findByInventarioSucursalIdOrderByFechaDesc(Long sucursalId);

    List<MovimientoInventario> findAllByOrderByFechaDesc();
}
