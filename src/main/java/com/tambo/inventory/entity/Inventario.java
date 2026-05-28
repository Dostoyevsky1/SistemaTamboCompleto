package com.tambo.inventory.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "inventarios",
    uniqueConstraints = @UniqueConstraint(columnNames = {"sucursal_id", "producto_id"})
)
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual = 0;

    public Inventario() {
    }

    public Inventario(Long id, Sucursal sucursal, Producto producto, Integer stockActual) {
        this.id = id;
        this.sucursal = sucursal;
        this.producto = producto;
        this.stockActual = stockActual;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        if (stockActual < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        this.stockActual = stockActual;
    }
}
