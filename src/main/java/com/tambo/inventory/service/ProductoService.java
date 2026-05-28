package com.tambo.inventory.service;

import com.tambo.inventory.dto.ProductoDTO;
import com.tambo.inventory.entity.Producto;
import com.tambo.inventory.exception.ResourceNotFoundException;
import com.tambo.inventory.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Autowired
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional
    public ProductoDTO crear(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setActivo(true);

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
    public ProductoDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));
        return mapearADto(producto);
    }

    @Transactional
    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));
        producto.setActivo(false); // Eliminación lógica
        productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarPorNombre(String nombre) {
        return productoRepository.buscarPorNombre(nombre).stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    private ProductoDTO mapearADto(Producto producto) {
        return new ProductoDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion() == null ? "" : producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock()
        );
    }
}
