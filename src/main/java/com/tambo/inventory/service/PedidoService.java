package com.tambo.inventory.service;

import com.tambo.inventory.dto.*;
import com.tambo.inventory.entity.*;
import com.tambo.inventory.exception.BadRequestException;
import com.tambo.inventory.exception.ResourceNotFoundException;
import com.tambo.inventory.repository.*;
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
    private final VentaRepository ventaRepository;
    private final SucursalRepository sucursalRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    @Autowired
    public PedidoService(
            PedidoRepository pedidoRepository,
            VentaRepository ventaRepository,
            SucursalRepository sucursalRepository,
            ProductoRepository productoRepository,
            UsuarioRepository usuarioRepository,
            InventarioRepository inventarioRepository,
            MovimientoInventarioRepository movimientoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.ventaRepository = ventaRepository;
        this.sucursalRepository = sucursalRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.inventarioRepository = inventarioRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Transactional
    public PedidoResponse registrarVenta(PedidoRequest request, String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario '" + username + "' no encontrado."));

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("La sucursal con ID " + request.getSucursalId() + " no existe."));

        TipoComprobante tipoComprobante;
        try {
            tipoComprobante = TipoComprobante.valueOf(request.getTipoComprobante().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Tipo de comprobante inválido. Debe ser BOLETA o FACTURA.");
        }

        MetodoPago metodoPago;
        try {
            metodoPago = MetodoPago.valueOf(request.getMetodoPago().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Método de pago inválido. Debe ser EFECTIVO, TARJETA o YAPE_PLIN.");
        }

        // 1. Crear Pedido y generar código correlativo
        Pedido pedido = new Pedido();
        long maxId = pedidoRepository.count();
        pedido.setCodigo(String.format("#TB-%05d", maxId + 8903)); // Empieza por correlativo similar al mockup (#TB-8902)
        pedido.setCliente(request.getCliente());
        pedido.setSucursal(sucursal);
        pedido.setUsuario(usuario);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.EN_PREPARACION); // Estado inicial correlacionado al mockup

        BigDecimal totalPedido = BigDecimal.ZERO;

        // 2. Procesar detalles de venta y validar stock
        for (DetallePedidoRequest itemRequest : request.getDetalles()) {
            Producto producto = productoRepository.findById(itemRequest.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + itemRequest.getProductoId()));

            if (!producto.getActivo()) {
                throw new BadRequestException("El producto '" + producto.getNombre() + "' no se encuentra activo.");
            }

            // Buscar existencias en el inventario de la sucursal
            Inventario inventario = inventarioRepository.findBySucursalIdAndProductoId(sucursal.getId(), producto.getId())
                    .orElseThrow(() -> new BadRequestException("No existe registro de stock para el producto '" + producto.getNombre() + "' en la sucursal '" + sucursal.getNombre() + "'."));

            if (inventario.getStockActual() < itemRequest.getCantidad()) {
                throw new BadRequestException("Stock insuficiente para el producto '" + producto.getNombre() + "' en la sucursal '" + sucursal.getNombre() + "'. Disponible: " + inventario.getStockActual() + " u. Solicitado: " + itemRequest.getCantidad() + " u.");
            }

            // Descontar stock
            int stockFinal = inventario.getStockActual() - itemRequest.getCantidad();
            inventario.setStockActual(stockFinal);
            inventarioRepository.save(inventario);

            // Registrar movimiento de auditoría
            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setInventario(inventario);
            movimiento.setTipo(TipoMovimiento.SALIDA);
            movimiento.setCantidad(itemRequest.getCantidad());
            movimiento.setMotivo("Venta realizada en Pedido " + pedido.getCodigo());
            movimiento.setFecha(LocalDateTime.now());
            movimiento.setUsuario(usuario);
            movimientoRepository.save(movimiento);

            // Crear detalle
            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setCantidad(itemRequest.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(itemRequest.getCantidad()));
            detalle.setSubtotal(subtotal);

            totalPedido = totalPedido.add(subtotal);
            pedido.agregarDetalle(detalle);
        }

        pedido.setTotal(totalPedido);

        // Guardar pedido (se guardarán automáticamente los detalles en cascada)
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // 3. Generar la Venta (Comprobante y Pago)
        long maxVentaId = ventaRepository.count();
        String serie = (tipoComprobante == TipoComprobante.BOLETA) ? "B001" : "F001";
        String comprobanteCodigo = String.format("%s-%06d", serie, maxVentaId + 120);

        Venta venta = new Venta();
        venta.setPedido(pedidoGuardado);
        venta.setComprobante(comprobanteCodigo);
        venta.setTipoComprobante(tipoComprobante);
        venta.setMetodoPago(metodoPago);
        venta.setFechaPago(LocalDateTime.now());
        
        ventaRepository.save(venta);

        return mapearAResponse(pedidoGuardado);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> obtenerTodos() {
        return pedidoRepository.findAllByOrderByFechaDesc().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> obtenerPorSucursal(Long sucursalId) {
        sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("La sucursal con ID " + sucursalId + " no existe."));

        return pedidoRepository.findBySucursalIdOrderByFechaDesc(sucursalId).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PedidoResponse obtenerPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
        return mapearAResponse(pedido);
    }

    @Transactional(readOnly = true)
    public PedidoResponse obtenerPorCodigo(String codigo) {
        Pedido pedido = pedidoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con código: " + codigo));
        return mapearAResponse(pedido);
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> obtenerVentasTodas() {
        return ventaRepository.findAllByOrderByFechaPagoDesc().stream()
                .map(this::mapearAVentaResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> obtenerVentasPorSucursal(Long sucursalId) {
        sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("La sucursal con ID " + sucursalId + " no existe."));

        return ventaRepository.findByPedidoSucursalIdOrderByFechaPagoDesc(sucursalId).stream()
                .map(this::mapearAVentaResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PedidoResponse actualizarEstado(Long id, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));

        EstadoPedido estado;
        try {
            estado = EstadoPedido.valueOf(nuevoEstado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Estado de pedido inválido. Estados permitidos: PENDIENTE, EN_PREPARACION, EN_CAMINO, ENTREGADO, CANCELADO.");
        }

        pedido.setEstado(estado);
        Pedido actualizado = pedidoRepository.save(pedido);
        return mapearAResponse(actualizado);
    }

    private PedidoResponse mapearAResponse(Pedido pedido) {
        List<DetallePedidoResponse> detallesResponse = pedido.getDetalles().stream()
                .map(det -> new DetallePedidoResponse(
                        det.getId(),
                        det.getProducto().getId(),
                        det.getProducto().getSku(),
                        det.getProducto().getNombre(),
                        det.getCantidad(),
                        det.getPrecioUnitario(),
                        det.getSubtotal()
                ))
                .collect(Collectors.toList());

        return new PedidoResponse(
                pedido.getId(),
                pedido.getCodigo(),
                pedido.getFecha(),
                pedido.getCliente(),
                pedido.getSucursal().getId(),
                pedido.getSucursal().getNombre(),
                pedido.getUsuario().getUsername(),
                pedido.getEstado().name(),
                pedido.getTotal(),
                detallesResponse
        );
    }

    private VentaResponse mapearAVentaResponse(Venta venta) {
        return new VentaResponse(
                venta.getId(),
                venta.getPedido().getId(),
                venta.getPedido().getCodigo(),
                venta.getComprobante(),
                venta.getTipoComprobante().name(),
                venta.getMetodoPago().name(),
                venta.getFechaPago(),
                venta.getPedido().getTotal()
        );
    }
}
