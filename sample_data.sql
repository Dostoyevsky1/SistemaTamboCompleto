USE tambo_db;

INSERT INTO roles (nombre) VALUES 
('ROLE_ADMIN'),
('ROLE_USER')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

INSERT INTO usuarios (id, username, password, nombre, correo, activo) VALUES 
(1, 'admin', '$2a$10$wM02mO3M2k7xWJ.D4J4G2e8N6N3Z/6RrnkLph2XqgPzDsnYtA7Y2i', 'Administrador Tambo', 'admin@tambo.pe', 1)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), correo = VALUES(correo);

INSERT INTO usuarios (id, username, password, nombre, correo, activo) VALUES 
(2, 'empleado', '$2a$10$U.yQeFq6B2L.XzQ5U58X0uhB/2hSefo/m1B8gP/O3aMek9hQfV1rW', 'Empleado Tambo', 'empleado@tambo.pe', 1)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), correo = VALUES(correo);

INSERT INTO usuario_rol (usuario_id, rol_id) VALUES 
(1, 1), -- admin -> ROLE_ADMIN
(2, 2)  -- empleado -> ROLE_USER
ON DUPLICATE KEY UPDATE usuario_id = VALUES(usuario_id);

INSERT INTO productos (id, nombre, descripcion, precio, stock, activo) VALUES
(1, 'Coca Cola 1.5L', 'Gaseosa Coca Cola original familiar', 6.50, 48, 1),
(2, 'Inka Cola 1.5L', 'Gaseosa Inka Cola original familiar', 6.50, 12, 1),
(3, 'Papas Lays Clásicas 160g', 'Papas fritas clásicas Lays', 4.50, 38, 1),
(4, 'Galletas Oreo Familiar 6pk', 'Paquete familiar de galletas Oreo', 4.20, 7, 1),
(5, 'Leche Evaporada Gloria 400g', 'Leche entera evaporada lata azul', 4.80, 80, 1),
(6, 'Yogurt Gloria Fresa 1L', 'Yogurt batido sabor fresa botella', 6.90, 25, 1)
ON DUPLICATE KEY UPDATE precio = VALUES(precio), stock = VALUES(stock);

INSERT INTO pedidos (id, usuario_id, producto_id, cantidad, fecha, total) VALUES
(1, 2, 1, 2, NOW() - INTERVAL 2 HOUR, 13.00), -- 2 Coca Cola (13.00) por empleado
(2, 2, 3, 2, NOW() - INTERVAL 1 HOUR, 9.00),  -- 2 Papas Lays (9.00) por empleado
(3, 2, 4, 1, NOW() - INTERVAL 30 MINUTE, 4.20) -- 1 Galleta Oreo (4.20) por empleado
ON DUPLICATE KEY UPDATE total = VALUES(total);
