-- ==========================================
-- SCRIPT DE CREACIÓN DE BASE DE DATOS - TAMBO OPS
-- ==========================================

-- 1. Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS tambo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tambo_db;

-- 2. Eliminar tablas en orden inverso de dependencias para evitar conflictos de claves foráneas
DROP TABLE IF EXISTS ventas;
DROP TABLE IF EXISTS detalles_pedido;
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS movimientos_inventario;
DROP TABLE IF EXISTS usuario_rol;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS inventarios;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS sucursales;
DROP TABLE IF EXISTS categorias;

-- ==========================================
-- CREACIÓN DE TABLAS MAESTRAS (Sin Dependencias)
-- ==========================================

-- Tabla: Categorías de productos
CREATE TABLE categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NULL
) ENGINE=InnoDB;

-- Tabla: Sucursales de Tambo
CREATE TABLE sucursales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    direccion VARCHAR(255) NULL,
    telefono VARCHAR(20) NULL
) ENGINE=InnoDB;

-- Tabla: Roles de usuarios
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE -- Ej: 'ROLE_ADMIN', 'ROLE_EMPLEADO'
) ENGINE=InnoDB;

-- ==========================================
-- CREACIÓN DE TABLAS SECUNDARIAS (Con Dependencias)
-- ==========================================

-- Tabla: Productos
CREATE TABLE productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(255) NULL,
    precio DECIMAL(10, 2) NOT NULL,
    stock_minimo INT NOT NULL DEFAULT 0,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    categoria_id BIGINT NOT NULL,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Tabla: Inventarios por Sucursal
CREATE TABLE inventarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sucursal_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    stock_actual INT NOT NULL DEFAULT 0,
    UNIQUE KEY uq_sucursal_producto (sucursal_id, producto_id),
    FOREIGN KEY (sucursal_id) REFERENCES sucursales(id) ON DELETE RESTRICT,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Tabla: Usuarios
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    sucursal_id BIGINT NULL,
    FOREIGN KEY (sucursal_id) REFERENCES sucursales(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Tabla de relación: Roles de usuario (Many-To-Many)
CREATE TABLE usuario_rol (
    usuario_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Tabla: Auditoría y Movimientos de Inventario
CREATE TABLE movimientos_inventario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inventario_id BIGINT NOT NULL,
    tipo VARCHAR(20) NOT NULL, -- 'ENTRADA', 'SALIDA', 'AJUSTE', 'TRASLADO'
    cantidad INT NOT NULL,
    motivo VARCHAR(255) NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_id BIGINT NOT NULL,
    FOREIGN KEY (inventario_id) REFERENCES inventarios(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Tabla: Pedidos / Ventas Base
CREATE TABLE pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE, -- Ej: PED-2026-0001
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cliente VARCHAR(100) NOT NULL,
    sucursal_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE', -- 'PENDIENTE', 'EN_PREPARACION', 'EN_CAMINO', 'ENTREGADO', 'CANCELADO'
    total DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (sucursal_id) REFERENCES sucursales(id) ON DELETE RESTRICT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Tabla: Detalles del Pedido
CREATE TABLE detalles_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Tabla: Ventas (Comprobantes de Pago)
CREATE TABLE ventas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL UNIQUE,
    comprobante VARCHAR(50) NOT NULL UNIQUE, -- Código de serie boleta/factura
    tipo_comprobante VARCHAR(20) NOT NULL, -- 'BOLETA', 'FACTURA'
    metodo_pago VARCHAR(30) NOT NULL, -- 'EFECTIVO', 'TARJETA', 'YAPE_PLIN'
    fecha_pago DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ==========================================
-- REGISTROS INICIALES OBLIGATORIOS (Seeding)
-- ==========================================

-- Insertar roles básicos
INSERT INTO roles (nombre) VALUES ('ROLE_ADMIN');
INSERT INTO roles (nombre) VALUES ('ROLE_EMPLEADO');

-- Insertar sucursal por defecto (Sede Central)
INSERT INTO sucursales (nombre, direccion, telefono) 
VALUES ('Sede Central', 'Av. Javier Prado Este 1230, San Isidro', '01-4444444');

-- Insertar usuario Administrador por defecto
-- El password es: admin123 (encriptado con BCrypt de forma obligatoria)
INSERT INTO usuarios (username, password, nombre, email, activo, sucursal_id) 
VALUES ('admin', '$2a$10$wM02mO3M2k7xWJ.D4J4G2e8N6N3Z/6RrnkLph2XqgPzDsnYtA7Y2i', 'Admin Tambo', 'admin@tambo.pe', 1, 1);

-- Relacionar el usuario admin con el rol ROLE_ADMIN (rol_id = 1)
INSERT INTO usuario_rol (usuario_id, rol_id) 
VALUES (1, 1);
