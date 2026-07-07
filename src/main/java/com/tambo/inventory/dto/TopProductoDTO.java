package com.tambo.inventory.dto;

public class TopProductoDTO {
    private String nombre;
    private Long cantidadVendida;

    public TopProductoDTO() {
    }

    public TopProductoDTO(String nombre, Long cantidadVendida) {
        this.nombre = nombre;
        this.cantidadVendida = cantidadVendida;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(Long cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }
}
