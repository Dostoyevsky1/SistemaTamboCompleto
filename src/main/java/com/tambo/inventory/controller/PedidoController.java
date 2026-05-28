package com.tambo.inventory.controller;

import com.tambo.inventory.dto.PedidoDTO;
import com.tambo.inventory.security.UserPrincipal;
import com.tambo.inventory.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @Autowired
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> registrarPedido(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody PedidoDTO pedidoDTO) {
        PedidoDTO creado = pedidoService.registrar(userPrincipal.getId(), pedidoDTO);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarPedidos() {
        List<PedidoDTO> pedidos = pedidoService.listarTodos();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/mis-pedidos")
    public ResponseEntity<List<PedidoDTO>> listarMisPedidos(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<PedidoDTO> pedidos = pedidoService.listarPorUsuario(userPrincipal.getId());
        return ResponseEntity.ok(pedidos);
    }
}
