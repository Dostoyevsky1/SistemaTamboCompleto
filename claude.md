# Proyecto Tambo - Sistema de Gestión de Inventario

## Tecnologías

* Spring Boot
* Spring Security
* JPA/Hibernate
* MySQL
* Maven

## Arquitectura

* controller
* service
* repository
* entity
* dto
* security
* config

## Objetivo

Sistema centralizado de gestión de inventario y pedidos para múltiples sucursales de Tambo.

## Reglas importantes

* No permitir stock negativo
* Actualizar inventario en tiempo real
* Roles ADMIN y EMPLEADO
* BCrypt para passwords
* Validar stock antes de ventas

## Entidades principales

* Producto
* Usuario
* Rol
* Inventario
* Sucursal
* Pedido
* Venta
* MovimientoInventario

## Buenas prácticas

* Usar DTOs
* Controllers delgados
* Lógica en Services
* JpaRepository
* Validaciones
* ResponseEntity
* Manejo global de excepciones
