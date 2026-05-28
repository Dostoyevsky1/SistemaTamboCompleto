package com.tambo.inventory.service;

import com.tambo.inventory.dto.CategoriaRequest;
import com.tambo.inventory.dto.CategoriaResponse;
import com.tambo.inventory.entity.Categoria;
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
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Autowired
    public CategoriaService(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> obtenerTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaResponse obtenerPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
        return mapearAResponse(categoria);
    }

    @Transactional
    public CategoriaResponse crear(CategoriaRequest request) {
        if (categoriaRepository.findByNombre(request.getNombre()).isPresent()) {
            throw new BadRequestException("Ya existe una categoría con el nombre: " + request.getNombre());
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescription());

        Categoria guardada = categoriaRepository.save(categoria);
        return mapearAResponse(guardada);
    }

    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        Optional<Categoria> existente = categoriaRepository.findByNombre(request.getNombre());
        if (existente.isPresent() && !existente.get().getId().equals(id)) {
            throw new BadRequestException("Ya existe otra categoría con el nombre: " + request.getNombre());
        }

        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescription());

        Categoria actualizada = categoriaRepository.save(categoria);
        return mapearAResponse(actualizada);
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        // Validación de integridad empresarial: No eliminar si tiene productos asociados
        boolean tieneProductos = !productoRepository.findByCategoriaId(id).isEmpty();
        if (tieneProductos) {
            throw new BadRequestException("No se puede eliminar la categoría '" + categoria.getNombre() + "' porque contiene productos asociados.");
        }

        categoriaRepository.delete(categoria);
    }

    private CategoriaResponse mapearAResponse(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion()
        );
    }
}
