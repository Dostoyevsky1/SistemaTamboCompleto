package com.tambo.inventory.service;

import com.tambo.inventory.dto.*;
import com.tambo.inventory.entity.*;
import com.tambo.inventory.exception.BadRequestException;
import com.tambo.inventory.exception.ResourceNotFoundException;
import com.tambo.inventory.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final SucursalRepository sucursalRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public InventarioService(
            InventarioRepository inventarioRepository,
            MovimientoInventarioRepository movimientoRepository,
            SucursalRepository sucursalRepository,
            ProductoRepository productoRepository,
            UsuarioRepository usuarioRepository) {
        this.inventarioRepository = inventarioRepository;
        this.movimientoRepository = movimientoRepository;
        this.sucursalRepository = sucursalRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<InventarioResponse> obtenerInventarioPorSucursal(Long sucursalId) {
        // Verificar que la sucursal exista
        sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("La sucursal con ID " + sucursalId + " no existe."));

        return inventarioRepository.findBySucursalId(sucursalId).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InventarioResponse> obtenerAlertasStockMinimo(Long sucursalId) {
        List<Inventario> inventarios;
        if (sucursalId != null) {
            sucursalRepository.findById(sucursalId)
                    .orElseThrow(() -> new ResourceNotFoundException("La sucursal con ID " + sucursalId + " no existe."));
            inventarios = inventarioRepository.findAlertasStockMinimoPorSucursal(sucursalId);
        } else {
            inventarios = inventarioRepository.findAlertasStockMinimoGlobal();
        }
        return inventarios.stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> obtenerHistorialMovimientos(Long sucursalId) {
        List<MovimientoInventario> movimientos;
        if (sucursalId != null) {
            sucursalRepository.findById(sucursalId)
                    .orElseThrow(() -> new ResourceNotFoundException("La sucursal con ID " + sucursalId + " no existe."));
            movimientos = movimientoRepository.findByInventarioSucursalIdOrderByFechaDesc(sucursalId);
        } else {
            movimientos = movimientoRepository.findAllByOrderByFechaDesc();
        }
        return movimientos.stream()
                .map(this::mapearAMovimientoResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MovimientoResponse registrarMovimientoManual(MovimientoRequest request, String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario '" + username + "' no encontrado."));

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + request.getSucursalId()));

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + request.getProductoId()));

        TipoMovimiento tipoMovimiento;
        try {
            tipoMovimiento = TipoMovimiento.valueOf(request.getTipo().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Tipo de movimiento inválido. Debe ser ENTRADA, SALIDA o AJUSTE.");
        }

        // El traslado se maneja por su propio método
        if (tipoMovimiento == TipoMovimiento.TRASLADO) {
            throw new BadRequestException("Los traslados se deben registrar a través del endpoint específico de traslados.");
        }

        // Buscar u obtener inventario para esta combinación sucursal-producto
        Inventario inventario = inventarioRepository.findBySucursalIdAndProductoId(sucursal.getId(), producto.getId())
                .orElseGet(() -> {
                    Inventario nuevo = new Inventario();
                    nuevo.setSucursal(sucursal);
                    nuevo.setProducto(producto);
                    nuevo.setStockActual(0);
                    return nuevo;
                });

        int stockActual = inventario.getStockActual();
        int stockFinal;

        if (tipoMovimiento == TipoMovimiento.ENTRADA) {
            stockFinal = stockActual + request.getCantidad();
        } else if (tipoMovimiento == TipoMovimiento.SALIDA) {
            stockFinal = stockActual - request.getCantidad();
        } else { // AJUSTE
            // El ajuste puede recibir un motivo específico. El stock se actualiza al valor indicado si es ajuste directo,
            // o se realiza un delta. Proponemos tratar la cantidad como el nuevo stock fijado por inventario físico en caso de ajuste manual.
            // Para mantener consistencia con "cantidad", usemos la cantidad como delta de ajuste (puede ser positivo o negativo).
            // Sin embargo, para evitar entradas negativas en "cantidad" (que tiene validación @Min(1)),
            // definamos que AJUSTE manual fijará el stock actual a la cantidad indicada, o que el ajuste manual sumará la cantidad.
            // Para mayor claridad en retail, el AJUSTE manual suele fijar el stock físico real contado.
            // Hagamos que AJUSTE establezca directamente el stockActual = request.getCantidad() y registre la diferencia.
            stockFinal = request.getCantidad();
        }

        if (stockFinal < 0) {
            throw new BadRequestException("Operación inválida: El stock final en '" + sucursal.getNombre() + "' para el producto '" + producto.getNombre() + "' no puede ser negativo (" + stockFinal + ").");
        }

        inventario.setStockActual(stockFinal);
        Inventario inventarioGuardado = inventarioRepository.save(inventario);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setInventario(inventarioGuardado);
        movimiento.setTipo(tipoMovimiento);
        // Registramos la cantidad neta del movimiento en valor absoluto
        int delta = Math.abs(stockFinal - stockActual);
        if (delta == 0 && tipoMovimiento != TipoMovimiento.AJUSTE) {
            throw new BadRequestException("La cantidad a modificar debe generar un cambio de stock real.");
        }
        movimiento.setCantidad(delta > 0 ? delta : request.getCantidad()); // Si es ajuste a 0, cantidad es 0 (no permitido por JPA setter), forzar al menos 1 o la cantidad enviada
        movimiento.setCantidad(request.getCantidad());
        movimiento.setMotivo(request.getMotivo() != null ? request.getMotivo() : "Ajuste manual de stock");
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setUsuario(usuario);

        MovimientoInventario guardadoMov = movimientoRepository.save(movimiento);
        return mapearAMovimientoResponse(guardadoMov);
    }

    @Transactional
    public List<MovimientoResponse> registrarTraslado(TrasladoRequest request, String username) {
        if (request.getSucursalOrigenId().equals(request.getSucursalDestinoId())) {
            throw new BadRequestException("La sucursal de origen y destino no pueden ser la misma.");
        }

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario '" + username + "' no encontrado."));

        Sucursal origen = sucursalRepository.findById(request.getSucursalOrigenId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal de origen no encontrada con ID: " + request.getSucursalOrigenId()));

        Sucursal destino = sucursalRepository.findById(request.getSucursalDestinoId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal de destino no encontrada con ID: " + request.getSucursalDestinoId()));

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + request.getProductoId()));

        // 1. Obtener y validar inventario de origen
        Inventario inventarioOrigen = inventarioRepository.findBySucursalIdAndProductoId(origen.getId(), producto.getId())
                .orElseThrow(() -> new BadRequestException("No existe stock registrado para el producto '" + producto.getNombre() + "' en la sucursal de origen '" + origen.getNombre() + "'."));

        if (inventarioOrigen.getStockActual() < request.getCantidad()) {
            throw new BadRequestException("Stock insuficiente en origen. Stock disponible: " + inventarioOrigen.getStockActual() + " u. Traslado solicitado: " + request.getCantidad() + " u.");
        }

        // 2. Obtener o crear inventario de destino
        Inventario inventarioDestino = inventarioRepository.findBySucursalIdAndProductoId(destino.getId(), producto.getId())
                .orElseGet(() -> {
                    Inventario nuevo = new Inventario();
                    nuevo.setSucursal(destino);
                    nuevo.setProducto(producto);
                    nuevo.setStockActual(0);
                    return nuevo;
                });

        // 3. Modificar stocks
        inventarioOrigen.setStockActual(inventarioOrigen.getStockActual() - request.getCantidad());
        inventarioDestino.setStockActual(inventarioDestino.getStockActual() + request.getCantidad());

        inventarioRepository.save(inventarioOrigen);
        inventarioRepository.save(inventarioDestino);

        String motivoTraslado = request.getMotivo() != null && !request.getMotivo().isBlank() 
                ? request.getMotivo() 
                : "Traslado de sucursal " + origen.getNombre() + " a " + destino.getNombre();

        // 4. Registrar movimientos de auditoría
        // Movimiento de Salida (Origen)
        MovimientoInventario salidaMov = new MovimientoInventario();
        salidaMov.setInventario(inventarioOrigen);
        salidaMov.setTipo(TipoMovimiento.TRASLADO);
        salidaMov.setCantidad(request.getCantidad());
        salidaMov.setMotivo(motivoTraslado + " (SALIDA)");
        salidaMov.setFecha(LocalDateTime.now());
        salidaMov.setUsuario(usuario);
        movimientoRepository.save(salidaMov);

        // Movimiento de Entrada (Destino)
        MovimientoInventario entradaMov = new MovimientoInventario();
        entradaMov.setInventario(inventarioDestino);
        entradaMov.setTipo(TipoMovimiento.TRASLADO);
        entradaMov.setCantidad(request.getCantidad());
        entradaMov.setMotivo(motivoTraslado + " (ENTRADA)");
        entradaMov.setFecha(LocalDateTime.now());
        entradaMov.setUsuario(usuario);
        movimientoRepository.save(entradaMov);

        List<MovimientoResponse> respuestas = new ArrayList<>();
        respuestas.add(mapearAMovimientoResponse(salidaMov));
        respuestas.add(mapearAMovimientoResponse(entradaMov));

        return respuestas;
    }

    private InventarioResponse mapearAResponse(Inventario inventario) {
        boolean alerta = inventario.getStockActual() <= inventario.getProducto().getStockMinimo();
        return new InventarioResponse(
                inventario.getId(),
                inventario.getSucursal().getId(),
                inventario.getSucursal().getNombre(),
                inventario.getProducto().getId(),
                inventario.getProducto().getSku(),
                inventario.getProducto().getNombre(),
                inventario.getStockActual(),
                inventario.getProducto().getStockMinimo(),
                alerta
        );
    }

    private MovimientoResponse mapearAMovimientoResponse(MovimientoInventario mov) {
        return new MovimientoResponse(
                mov.getId(),
                mov.getInventario().getSucursal().getId(),
                mov.getInventario().getSucursal().getNombre(),
                mov.getInventario().getProducto().getId(),
                mov.getInventario().getProducto().getSku(),
                mov.getInventario().getProducto().getNombre(),
                mov.getTipo().name(),
                mov.getCantidad(),
                mov.getMotivo(),
                mov.getFecha(),
                mov.getUsuario().getUsername()
        );
    }
}
