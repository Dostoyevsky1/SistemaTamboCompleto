package com.tambo.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class TrasladoRequest {

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La sucursal de origen es obligatoria")
    private Long sucursalOrigenId;

    @NotNull(message = "La sucursal de destino es obligatoria")
    private Long sucursalDestinoId;

    @NotNull(message = "La cantidad a trasladar es obligatoria")
    @Min(value = 1, message = "La cantidad a trasladar debe ser al menos 1")
    private Integer cantidad;

    private String motivo;

    public TrasladoRequest() {
    }

    public TrasladoRequest(Long productoId, Long sucursalOrigenId, Long sucursalDestinoId, Integer cantidad, String motivo) {
        this.productoId = productoId;
        this.sucursalOrigenId = sucursalOrigenId;
        this.sucursalDestinoId = sucursalDestinoId;
        this.cantidad = cantidad;
        this.motivo = motivo;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Long getSucursalOrigenId() {
        return sucursalOrigenId;
    }

    public void setSucursalOrigenId(Long sucursalOrigenId) {
        this.sucursalOrigenId = sucursalOrigenId;
    }

    public Long getSucursalDestinoId() {
        return sucursalDestinoId;
    }

    public void setSucursalDestinoId(Long sucursalDestinoId) {
        this.sucursalDestinoId = sucursalDestinoId;
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
}
