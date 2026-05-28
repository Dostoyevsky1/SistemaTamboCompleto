package com.tambo.inventory.dto;

import java.math.BigDecimal;

public class ProductoMasVendidoResponse {
    private Long productoId;
    private String productoNombre;
    private String productoSku;
    private Long cantidadVendida;
    private BigDecimal totalRecaudado;

    public ProductoMasVendidoResponse() {
    }

    public ProductoMasVendidoResponse(Long productoId, String productoNombre, String productoSku, Long cantidadVendida, BigDecimal totalRecaudado) {
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.productoSku = productoSku;
        this.cantidadVendida = cantidadVendida;
        this.totalRecaudado = totalRecaudado;
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

    public String getProductoSku() {
        return productoSku;
    }

    public void setProductoSku(String productoSku) {
        this.productoSku = productoSku;
    }

    public Long getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(Long cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public BigDecimal getTotalRecaudado() {
        return totalRecaudado;
    }

    public void setTotalRecaudado(BigDecimal totalRecaudado) {
        this.totalRecaudado = totalRecaudado;
    }
}
