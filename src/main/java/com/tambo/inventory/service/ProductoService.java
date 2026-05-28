package com.tambo.inventory.service;

import com.tambo.inventory.dto.ProductoRequest;
import com.tambo.inventory.dto.ProductoResponse;
import com.tambo.inventory.entity.Categoria;
import com.tambo.inventory.entity.Producto;
import com.tambo.inventory.exception.BadRequestException;
import com.tambo.inventory.exception.ResourceNotFoundException;
import com.tambo.inventory.repository.CategoriaRepository;
import com.tambo.inventory.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Autowired
    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> obtenerTodos() {
        return productoRepository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> obtenerActivos() {
        return productoRepository.findByActivoTrue().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return mapearAResponse(producto);
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtenerPorSku(String sku) {
        Producto producto = productoRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con SKU: " + sku));
        return mapearAResponse(producto);
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        if (productoRepository.existsBySku(request.getSku())) {
            throw new BadRequestException("El SKU '" + request.getSku() + "' ya está registrado en otro producto.");
        }

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("La categoría con ID " + request.getCategoriaId() + " no existe."));

        Producto producto = new Producto();
        producto.setSku(request.getSku());
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStockMinimo(request.getStockMinimo());
        producto.setActivo(request.getActivo() != null ? request.getActivo() : true);
        producto.setCategoria(categoria);

        Producto guardado = productoRepository.save(producto);
        return mapearAResponse(guardado);
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        Optional<Producto> existenteSku = productoRepository.findBySku(request.getSku());
        if (existenteSku.isPresent() && !existenteSku.get().getId().equals(id)) {
            throw new BadRequestException("El SKU '" + request.getSku() + "' ya está en uso por otro producto.");
        }

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("La categoría con ID " + request.getCategoriaId() + " no existe."));

        producto.setSku(request.getSku());
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStockMinimo(request.getStockMinimo());
        producto.setActivo(request.getActivo() != null ? request.getActivo() : true);
        producto.setCategoria(categoria);

        Producto actualizado = productoRepository.save(producto);
        return mapearAResponse(actualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        // En el futuro, validaremos que no existan registros de inventario o detalles de pedido asociados a este producto.
        // Por ahora, realizamos la eliminación física.
        productoRepository.delete(producto);
    }

    private ProductoResponse mapearAResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getSku(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStockMinimo(),
                producto.getActivo(),
                producto.getCategoria().getId(),
                producto.getCategoria().getNombre()
        );
    }
}
