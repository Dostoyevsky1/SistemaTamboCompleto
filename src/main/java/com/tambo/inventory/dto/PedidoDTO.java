package com.tambo.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PedidoDTO {

    private Long id;

    private Long usuarioId;
    private String usuarioNombre;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;
    private String productoNombre;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima debe ser 1")
    private Integer cantidad;

    private LocalDateTime fecha;
    private BigDecimal total;

    public PedidoDTO() {
    }

    public PedidoDTO(Long id, Long usuarioId, String usuarioNombre, Long productoId, String productoNombre, Integer cantidad, LocalDateTime fecha, BigDecimal total) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
