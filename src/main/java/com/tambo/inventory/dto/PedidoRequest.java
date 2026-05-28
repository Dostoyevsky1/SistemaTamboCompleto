package com.tambo.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class PedidoRequest {

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String cliente;

    @NotNull(message = "La sucursal es obligatoria")
    private Long sucursalId;

    @NotEmpty(message = "El pedido debe contener al menos un producto")
    @Valid
    private List<DetallePedidoRequest> detalles;

    @NotBlank(message = "El tipo de comprobante es obligatorio (BOLETA, FACTURA)")
    private String tipoComprobante;

    @NotBlank(message = "El método de pago es obligatorio (EFECTIVO, TARJETA, YAPE_PLIN)")
    private String metodoPago;

    public PedidoRequest() {
    }

    public PedidoRequest(String cliente, Long sucursalId, List<DetallePedidoRequest> detalles, String tipoComprobante, String metodoPago) {
        this.cliente = cliente;
        this.sucursalId = sucursalId;
        this.detalles = detalles;
        this.tipoComprobante = tipoComprobante;
        this.metodoPago = metodoPago;
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

    public List<DetallePedidoRequest> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedidoRequest> detalles) {
        this.detalles = detalles;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
}
