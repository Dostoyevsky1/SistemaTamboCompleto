package com.tambo.inventory.dto;

import java.time.LocalDateTime;

public class MovimientoResponse {
    private Long id;
    private Long sucursalId;
    private String sucursalNombre;
    private Long productoId;
    private String productSku;
    private String productoNombre;
    private String tipo;
    private Integer cantidad;
    private String motivo;
    private LocalDateTime fecha;
    private String username;

    public MovimientoResponse() {
    }

    public MovimientoResponse(Long id, Long sucursalId, String sucursalNombre, Long productoId, String productSku, String productoNombre, String tipo, Integer cantidad, String motivo, LocalDateTime fecha, String username) {
        this.id = id;
        this.sucursalId = sucursalId;
        this.sucursalNombre = sucursalNombre;
        this.productoId = productoId;
        this.productSku = productSku;
        this.productoNombre = productoNombre;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.fecha = fecha;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
