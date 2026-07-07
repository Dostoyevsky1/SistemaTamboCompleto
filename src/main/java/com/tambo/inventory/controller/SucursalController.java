package com.tambo.inventory.controller;

import com.tambo.inventory.dto.SucursalDTO;
import com.tambo.inventory.entity.Sucursal;
import com.tambo.inventory.repository.SucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SucursalController {

    private final SucursalRepository sucursalRepository;

    @Autowired
    public SucursalController(SucursalRepository sucursalRepository) {
        this.sucursalRepository = sucursalRepository;
    }

    @GetMapping("/api/sucursales")
    public ResponseEntity<List<SucursalDTO>> listarSucursales() {
        List<SucursalDTO> sucursales = sucursalRepository.findAll().stream()
                .map(s -> new SucursalDTO(s.getId(), s.getNombre(), s.getDireccion()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(sucursales);
    }
}
