package com.tambo.inventory.service;

import com.tambo.inventory.dto.PedidoDTO;
import com.tambo.inventory.entity.Pedido;
import com.tambo.inventory.entity.Producto;
import com.tambo.inventory.entity.Usuario;
import com.tambo.inventory.exception.BadRequestException;
import com.tambo.inventory.exception.ResourceNotFoundException;
import com.tambo.inventory.repository.PedidoRepository;
import com.tambo.inventory.repository.ProductoRepository;
import com.tambo.inventory.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public PedidoService(
            PedidoRepository pedidoRepository,
            ProductoRepository productoRepository,
            UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public PedidoDTO registrar(Long usuarioId, PedidoDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + dto.getProductoId()));

        if (!producto.getActivo()) {
            throw new BadRequestException("El producto seleccionado no está activo.");
        }

        if (producto.getStock() < dto.getCantidad()) {
            throw new BadRequestException("Stock insuficiente para el producto '" + producto.getNombre() + "'. Stock disponible: " + producto.getStock());
        }

        producto.setStock(producto.getStock() - dto.getCantidad());
        productoRepository.save(producto);

        BigDecimal total = producto.getPrecio().multiply(BigDecimal.valueOf(dto.getCantidad()));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setProducto(producto);
        pedido.setCantidad(dto.getCantidad());
        pedido.setFecha(LocalDateTime.now());
        pedido.setTotal(total);

        Pedido guardado = pedidoRepository.save(pedido);
        return mapearADto(guardado);
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> listarTodos() {
        return pedidoRepository.findAllByOrderByFechaDesc().stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> listarPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    private PedidoDTO mapearADto(Pedido pedido) {
        return new PedidoDTO(
                pedido.getId(),
                pedido.getUsuario().getId(),
                pedido.getUsuario().getNombre(),
                pedido.getProducto().getId(),
                pedido.getProducto().getNombre(),
                pedido.getCantidad(),
                pedido.getFecha(),
                pedido.getTotal()
        );
    }
}
