package com.tambo.inventory.repository;

import com.tambo.inventory.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioId(Long usuarioId);
    List<Pedido> findAllByOrderByFechaDesc();
    List<Pedido> findByProductoSucursalId(Long sucursalId);
}
