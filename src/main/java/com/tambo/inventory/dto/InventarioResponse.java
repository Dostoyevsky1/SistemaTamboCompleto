package com.tambo.inventory.dto;

public class InventarioResponse {
    private Long id;
    private Long sucursalId;
    private String sucursalNombre;
    private Long productoId;
    private String productoSku;
    private String productoNombre;
    private Integer stockActual;
    private Integer stockMinimo;
    private Boolean alertaBajoStock;

    public InventarioResponse() {
    }

    public InventarioResponse(Long id, Long sucursalId, String sucursalNombre, Long productoId, String productoSku, String productoNombre, Integer stockActual, Integer stockMinimo, Boolean alertaBajoStock) {
        this.id = id;
        this.sucursalId = sucursalId;
        this.sucursalNombre = sucursalNombre;
        this.productoId = productoId;
        this.productoSku = productoSku;
        this.productoNombre = productoNombre;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.alertaBajoStock = alertaBajoStock;
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

    public String getProductoSku() {
        return productoSku;
    }

    public void setProductoSku(String productoSku) {
        this.productoSku = productoSku;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Boolean getAlertaBajoStock() {
        return alertaBajoStock;
    }

    public void setAlertaBajoStock(Boolean alertaBajoStock) {
        this.alertaBajoStock = alertaBajoStock;
    }
}
