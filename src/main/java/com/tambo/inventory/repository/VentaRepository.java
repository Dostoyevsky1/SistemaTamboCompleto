package com.tambo.inventory.repository;

import com.tambo.inventory.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByPedidoSucursalIdOrderByFechaPagoDesc(Long sucursalId);

    Optional<Venta> findByComprobante(String comprobante);

    List<Venta> findAllByOrderByFechaPagoDesc();
}
