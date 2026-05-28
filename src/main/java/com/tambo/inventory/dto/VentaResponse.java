package com.tambo.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VentaResponse {
    private Long id;
    private Long pedidoId;
    private String pedidoCodigo;
    private String comprobante;
    private String tipoComprobante;
    private String metodoPago;
    private LocalDateTime fechaPago;
    private BigDecimal total;

    public VentaResponse() {
    }

    public VentaResponse(Long id, Long pedidoId, String pedidoCodigo, String comprobante, String tipoComprobante, String metodoPago, LocalDateTime fechaPago, BigDecimal total) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.pedidoCodigo = pedidoCodigo;
        this.comprobante = comprobante;
        this.tipoComprobante = tipoComprobante;
        this.metodoPago = metodoPago;
        this.fechaPago = fechaPago;
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getPedidoCodigo() {
        return pedidoCodigo;
    }

    public void setPedidoCodigo(String pedidoCodigo) {
        this.pedidoCodigo = pedidoCodigo;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
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

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
