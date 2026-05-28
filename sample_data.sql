-- ========================================================
-- DATA DE PRUEBA PARA TAMBO OPS (INVENTARIO Y HISTORIAL DE VENTAS)
-- ========================================================

USE tambo_db;

-- 1. Insertar Categorías adicionales
INSERT INTO categorias (nombre, descripcion) VALUES
('Bebidas', 'Bebidas gaseosas, aguas, jugos y rehidratantes'),
('Snacks', 'Papas fritas, galletas, chocolates y bocaditos salados'),
('Lácteos', 'Leches, yogures, quesos y mantequillas'),
('Abarrotes', 'Fideos, arroz, aceites, conservas y salsas')
ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion);

-- 2. Insertar Sucursales adicionales
INSERT INTO sucursales (nombre, direccion, telefono) VALUES
('Sede Miraflores', 'Av. Larco 456, Miraflores', '01-2223333'),
('Sede Lince', 'Av. Arequipa 1820, Lince', '01-5556666')
ON DUPLICATE KEY UPDATE direccion = VALUES(direccion), telefono = VALUES(telefono);

-- 3. Insertar Productos de prueba
INSERT INTO productos (sku, nombre, descripcion, precio, stock_minimo, activo, categoria_id) VALUES
('BEB-001', 'Coca Cola 1.5L', 'Gaseosa Coca Cola original familiar', 6.50, 20, 1, 1),
('BEB-002', 'Inka Cola 1.5L', 'Gaseosa Inka Cola original familiar', 6.50, 20, 1, 1),
('SNA-001', 'Papas Lays Clásicas 160g', 'Papas fritas clásicas Lays', 4.50, 15, 1, 2),
('SNA-002', 'Galletas Oreo Familiar 6pk', 'Paquete familiar de galletas Oreo', 4.20, 10, 1, 2),
('LAC-001', 'Leche Evaporada Gloria 400g', 'Leche entera evaporada lata azul', 4.80, 30, 1, 3),
('LAC-002', 'Yogurt Gloria Fresa 1L', 'Yogurt batido sabor fresa botella', 6.90, 10, 1, 3),
('ABA-001', 'Arroz Costeño Extra 1kg', 'Arroz extra Costeño bolsa', 4.90, 25, 1, 4),
('ABA-002', 'Aceite Vegetal Primor 1L', 'Aceite vegetal premium botella', 9.50, 15, 1, 4)
ON DUPLICATE KEY UPDATE precio = VALUES(precio), stock_minimo = VALUES(stock_minimo);

-- 4. Registrar stock inicial en Inventario (Sede Central - sucursal_id = 1)
-- IMPORTANTE: Para simular Alertas de Stock Bajo, ponemos algunos productos con stock inferior a su stock_minimo.
INSERT INTO inventarios (sucursal_id, producto_id, stock_actual) VALUES
(1, 1, 50), -- Coca Cola: 50 (mínimo 20) -> Estable
(1, 2, 12), -- Inka Cola: 12 (mínimo 20) -> ALERTA STOCK CRÍTICO
(1, 3, 40), -- Papas Lays: 40 (mínimo 15) -> Estable
(1, 4, 8),  -- Galletas Oreo: 8 (mínimo 10) -> ALERTA STOCK CRÍTICO
(1, 5, 80), -- Leche Gloria: 80 (mínimo 30) -> Estable
(1, 6, 25), -- Yogurt Gloria: 25 (mínimo 10) -> Estable
(1, 7, 5),  -- Arroz Costeño: 5 (mínimo 25)  -> ALERTA STOCK CRÍTICO
(1, 8, 30)  -- Aceite Primor: 30 (mínimo 15) -> Estable
ON DUPLICATE KEY UPDATE stock_actual = VALUES(stock_actual);

-- Registrar stock inicial en Sede Miraflores (sucursal_id = 2)
INSERT INTO inventarios (sucursal_id, producto_id, stock_actual) VALUES
(2, 1, 30), -- Coca Cola: 30
(2, 2, 25), -- Inka Cola: 25
(2, 3, 5),  -- Papas Lays: 5 (mínimo 15) -> ALERTA STOCK CRÍTICO
(2, 5, 45)  -- Leche Gloria: 45
ON DUPLICATE KEY UPDATE stock_actual = VALUES(stock_actual);

-- 5. Registrar historial de auditoría en Movimientos de Inventario
-- Las transacciones de entrada iniciales para poblar los almacenes
INSERT INTO movimientos_inventario (inventario_id, tipo, cantidad, motivo, fecha, usuario_id) VALUES
(1, 'ENTRADA', 60, 'Carga inicial de almacén - Apertura', NOW() - INTERVAL 5 DAY, 1),
(2, 'ENTRADA', 15, 'Carga inicial de almacén - Apertura', NOW() - INTERVAL 5 DAY, 1),
(3, 'ENTRADA', 50, 'Carga inicial de almacén - Apertura', NOW() - INTERVAL 5 DAY, 1),
(4, 'ENTRADA', 12, 'Compra a proveedor distribuidor', NOW() - INTERVAL 3 DAY, 1),
(5, 'ENTRADA', 90, 'Carga inicial de almacén - Apertura', NOW() - INTERVAL 5 DAY, 1),
(6, 'ENTRADA', 30, 'Abastecimiento de planta', NOW() - INTERVAL 2 DAY, 1),
(7, 'ENTRADA', 10, 'Carga inicial de almacén - Apertura', NOW() - INTERVAL 5 DAY, 1),
(8, 'ENTRADA', 35, 'Compra a distribuidora Alicante', NOW() - INTERVAL 3 DAY, 1);

-- 6. Insertar Pedidos y Ventas de prueba (Historial de ingresos)
-- Pedido 1: Entregado (Venta cobrada con Boleta en Sede Central)
INSERT INTO pedidos (id, codigo, fecha, cliente, sucursal_id, usuario_id, estado, total) VALUES
(1, 'PED-2026-0001', NOW() - INTERVAL 2 HOUR, 'Carlos Mendoza', 1, 1, 'ENTREGADO', 26.20);

INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 2, 6.50, 13.00), -- 2 Coca Cola (13.00)
(1, 3, 2, 4.50, 9.00),  -- 2 Papas Lays (9.00)
(1, 4, 1, 4.20, 4.20);  -- 1 Galleta Oreo (4.20)

INSERT INTO ventas (pedido_id, comprobante, tipo_comprobante, metodo_pago, fecha_pago) VALUES
(1, 'B001-00000001', 'BOLETA', 'EFECTIVO', NOW() - INTERVAL 2 HOUR);

-- Descontar stock de inventario por la venta del Pedido 1 (Salidas de auditoría)
UPDATE inventarios SET stock_actual = stock_actual - 2 WHERE sucursal_id = 1 AND producto_id = 1;
UPDATE inventarios SET stock_actual = stock_actual - 2 WHERE sucursal_id = 1 AND producto_id = 3;
UPDATE inventarios SET stock_actual = stock_actual - 1 WHERE sucursal_id = 1 AND producto_id = 4;

INSERT INTO movimientos_inventario (inventario_id, tipo, cantidad, motivo, fecha, usuario_id) VALUES
(1, 'SALIDA', 2, 'Venta emitida Boleta B001-00000001', NOW() - INTERVAL 2 HOUR, 1),
(3, 'SALIDA', 2, 'Venta emitida Boleta B001-00000001', NOW() - INTERVAL 2 HOUR, 1),
(4, 'SALIDA', 1, 'Venta emitida Boleta B001-00000001', NOW() - INTERVAL 2 HOUR, 1);


-- Pedido 2: Pendiente de entrega en Sede Central
INSERT INTO pedidos (id, codigo, fecha, cliente, sucursal_id, usuario_id, estado, total) VALUES
(2, 'PED-2026-0002', NOW() - INTERVAL 1 HOUR, 'Ana Gómez', 1, 1, 'PENDIENTE', 20.80);

INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(2, 5, 2, 4.80, 9.60),  -- 2 Leches Gloria (9.60)
(2, 7, 1, 4.90, 4.90),  -- 1 Arroz Costeño (4.90)
(2, 2, 1, 6.50, 6.50);  -- 1 Inka Cola (6.50)


-- Pedido 3: Entregado (Venta cobrada con Factura en Sede Miraflores)
INSERT INTO pedidos (id, codigo, fecha, cliente, sucursal_id, usuario_id, estado, total) VALUES
(3, 'PED-2026-0003', NOW() - INTERVAL 30 MINUTE, 'Inversiones Larco S.A.C.', 2, 1, 'ENTREGADO', 60.10);

INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(3, 5, 5, 4.80, 24.00), -- 5 Leches Gloria (24.00)
(3, 1, 4, 6.50, 26.00), -- 4 Coca Cola (26.00)
(3, 3, 2, 4.50, 9.00),  -- 2 Papas Lays (9.00)
(3, 4, 0, 4.20, 0.00);  -- Sin oreos

INSERT INTO ventas (pedido_id, comprobante, tipo_comprobante, metodo_pago, fecha_pago) VALUES
(3, 'F001-00000001', 'FACTURA', 'TARJETA', NOW() - INTERVAL 30 MINUTE);

-- Descontar stock de inventario por la venta del Pedido 3
UPDATE inventarios SET stock_actual = stock_actual - 5 WHERE sucursal_id = 2 AND producto_id = 5;
UPDATE inventarios SET stock_actual = stock_actual - 4 WHERE sucursal_id = 2 AND producto_id = 1;
UPDATE inventarios SET stock_actual = stock_actual - 2 WHERE sucursal_id = 2 AND producto_id = 3;

INSERT INTO movimientos_inventario (inventario_id, tipo, cantidad, motivo, fecha, usuario_id) VALUES
(11, 'SALIDA', 5, 'Venta emitida Factura F001-00000001', NOW() - INTERVAL 30 MINUTE, 1),
(9,  'SALIDA', 4, 'Venta emitida Factura F001-00000001', NOW() - INTERVAL 30 MINUTE, 1),
(10, 'SALIDA', 2, 'Venta emitida Factura F001-00000001', NOW() - INTERVAL 30 MINUTE, 1);
