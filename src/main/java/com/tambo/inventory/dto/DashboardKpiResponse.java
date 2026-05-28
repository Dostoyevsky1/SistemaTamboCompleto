package com.tambo.inventory.dto;

import java.math.BigDecimal;

public class DashboardKpiResponse {
    private BigDecimal ventasDelDia;
    private Long pedidosPendientes;
    private Long stockCritico;
    private Long entregasHoy;

    public DashboardKpiResponse() {
    }

    public DashboardKpiResponse(BigDecimal ventasDelDia, Long pedidosPendientes, Long stockCritico, Long entregasHoy) {
        this.ventasDelDia = ventasDelDia;
        this.pedidosPendientes = pedidosPendientes;
        this.stockCritico = stockCritico;
        this.entregasHoy = entregasHoy;
    }

    public BigDecimal getVentasDelDia() {
        return ventasDelDia;
    }

    public void setVentasDelDia(BigDecimal ventasDelDia) {
        this.ventasDelDia = ventasDelDia;
    }

    public Long getPedidosPendientes() {
        return pedidosPendientes;
    }

    public void setPedidosPendientes(Long pedidosPendientes) {
        this.pedidosPendientes = pedidosPendientes;
    }

    public Long getStockCritico() {
        return stockCritico;
    }

    public void setStockCritico(Long stockCritico) {
        this.stockCritico = stockCritico;
    }

    public Long getEntregasHoy() {
        return entregasHoy;
    }

    public void setEntregasHoy(Long entregasHoy) {
        this.entregasHoy = entregasHoy;
    }
}
