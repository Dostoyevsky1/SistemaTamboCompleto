package com.tambo.inventory.service;

import com.tambo.inventory.dto.ProductoDTO;
import com.tambo.inventory.entity.Producto;
import com.tambo.inventory.entity.Sucursal;
import com.tambo.inventory.exception.ResourceNotFoundException;
import com.tambo.inventory.repository.ProductoRepository;
import com.tambo.inventory.repository.SucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final SucursalRepository sucursalRepository;

    @Autowired
    public ProductoService(ProductoRepository productoRepository, SucursalRepository sucursalRepository) {
        this.productoRepository = productoRepository;
        this.sucursalRepository = sucursalRepository;
    }

    @Transactional
    public ProductoDTO crear(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setActivo(true);

        if (dto.getSucursalId() != null) {
            Sucursal sucursal = sucursalRepository.findById(dto.getSucursalId()).orElse(null);
            producto.setSucursal(sucursal);
        }

        Producto guardado = productoRepository.save(producto);
        return mapearADto(guardado);
    }

    @Transactional
    public ProductoDTO actualizar(Long id, ProductoDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));

        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());

        if (dto.getSucursalId() != null) {
            Sucursal sucursal = sucursalRepository.findById(dto.getSucursalId()).orElse(null);
            producto.setSucursal(sucursal);
        } else {
            producto.setSucursal(null);
        }

        Producto guardado = productoRepository.save(producto);
        return mapearADto(guardado);
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAll().stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> listarActivos() {
        return productoRepository.findByActivoTrue().stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> listarPorSucursal(Long sucursalId) {
        return productoRepository.findBySucursalIdAndActivoTrue(sucursalId).stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));
        return mapearADto(producto);
    }

    @Transactional
    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarPorNombre(String nombre) {
        return productoRepository.buscarPorNombre(nombre).stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarPorNombreYSucursal(String nombre, Long sucursalId) {
        return productoRepository.buscarPorNombreYSucursal(nombre, sucursalId).stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    private ProductoDTO mapearADto(Producto producto) {
        return new ProductoDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion() == null ? "" : producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getSucursal() == null ? null : producto.getSucursal().getId(),
                producto.getSucursal() == null ? "Sin Sucursal" : producto.getSucursal().getNombre()
        );
    }
}
