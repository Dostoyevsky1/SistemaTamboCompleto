package com.tambo.inventory.controller;

import com.tambo.inventory.dto.ApiResponse;
import com.tambo.inventory.dto.AuthResponse;
import com.tambo.inventory.dto.LoginDTO;
import com.tambo.inventory.dto.UsuarioDTO;
import com.tambo.inventory.entity.Usuario;
import com.tambo.inventory.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDTO loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registrar(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        Usuario usuario = authService.registrar(usuarioDTO);
        return new ResponseEntity<>(
                new ApiResponse(true, "Usuario '" + usuario.getUsername() + "' registrado exitosamente."),
                HttpStatus.CREATED
        );
    }
}
