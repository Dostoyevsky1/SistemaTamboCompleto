package com.tambo.inventory.controller;

import com.tambo.inventory.dto.ApiResponse;
import com.tambo.inventory.dto.UsuarioDTO;
import com.tambo.inventory.entity.Usuario;
import com.tambo.inventory.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final AuthService authService;

    @Autowired
    public UsuarioController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        List<UsuarioDTO> usuarios = authService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> registrarUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        Usuario usuario = authService.registrar(usuarioDTO);
        return new ResponseEntity<>(
                new ApiResponse(true, "Usuario '" + usuario.getUsername() + "' registrado exitosamente."),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO actualizado = authService.actualizar(id, usuarioDTO);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> eliminarUsuario(@PathVariable Long id) {
        authService.eliminar(id);
        return ResponseEntity.ok(new ApiResponse(true, "Usuario eliminado exitosamente."));
    }
}
