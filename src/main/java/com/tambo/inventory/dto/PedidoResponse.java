package com.tambo.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponse {
    private Long id;
    private String codigo;
    private LocalDateTime fecha;
    private String cliente;
    private Long sucursalId;
    private String sucursalNombre;
    private String username;
    private String estado;
    private BigDecimal total;
    private List<DetallePedidoResponse> detalles;

    public PedidoResponse() {
    }

    public PedidoResponse(Long id, String codigo, LocalDateTime fecha, String cliente, Long sucursalId, String sucursalNombre, String username, String estado, BigDecimal total, List<DetallePedidoResponse> detalles) {
        this.id = id;
        this.codigo = codigo;
        this.fecha = fecha;
        this.cliente = cliente;
        this.sucursalId = sucursalId;
        this.sucursalNombre = sucursalNombre;
        this.username = username;
        this.estado = estado;
        this.total = total;
        this.detalles = detalles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public Long getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Long sucursalId) {
        this.sucursalId = sucursalId;
    }

    public String getSucursalNombre() {
        return sucursalNombre;
    }

    public void setSucursalNombre(String sucursalNombre) {
        this.sucursalNombre = sucursalNombre;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<DetallePedidoResponse> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedidoResponse> detalles) {
        this.detalles = detalles;
    }
}
