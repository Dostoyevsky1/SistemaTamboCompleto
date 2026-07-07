package com.tambo.inventory.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardStatsDTO {
    private BigDecimal totalVentas;
    private Long cantidadProductos;
    private Long bajoStockCount;
    private Long usuariosActivos;
    private List<TopProductoDTO> topProductos;

    public DashboardStatsDTO() {
    }

    public DashboardStatsDTO(BigDecimal totalVentas, Long cantidadProductos, Long bajoStockCount, Long usuariosActivos, List<TopProductoDTO> topProductos) {
        this.totalVentas = totalVentas;
        this.cantidadProductos = cantidadProductos;
        this.bajoStockCount = bajoStockCount;
        this.usuariosActivos = usuariosActivos;
        this.topProductos = topProductos;
    }

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
    }

    public Long getCantidadProductos() {
        return cantidadProductos;
    }

    public void setCantidadProductos(Long cantidadProductos) {
        this.cantidadProductos = cantidadProductos;
    }

    public Long getBajoStockCount() {
        return bajoStockCount;
    }

    public void setBajoStockCount(Long bajoStockCount) {
        this.bajoStockCount = bajoStockCount;
    }

    public Long getUsuariosActivos() {
        return usuariosActivos;
    }

    public void setUsuariosActivos(Long usuariosActivos) {
        this.usuariosActivos = usuariosActivos;
    }

    public List<TopProductoDTO> getTopProductos() {
        return topProductos;
    }

    public void setTopProductos(List<TopProductoDTO> topProductos) {
        this.topProductos = topProductos;
    }
}
