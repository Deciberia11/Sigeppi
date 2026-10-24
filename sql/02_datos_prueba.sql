-- =====================================================================
-- SIGEPPI - Datos de prueba
-- Contraseña de todos los usuarios de prueba: Sigeppi2026
-- (hash BCrypt generado con PasswordUtil.encriptar)
-- =====================================================================
INSERT INTO tb_rol (rol_nombre, rol_descripcion) VALUES
 ('VENDEDOR', 'Atiende clientes y registra pedidos en tienda'),
 ('JEFE_PRODUCCION', 'Planifica y autoriza las órdenes de producción'),
 ('ENCARGADO_ALMACEN', 'Controla el stock y los lotes de insumos'),
 ('ADMINISTRADOR', 'Revisa reportes y administra el sistema');

INSERT INTO tb_tienda (tie_nombre, tie_direccion, tie_distrito) VALUES
 ('Delicias San Borja', 'Av. San Borja Norte 1234', 'San Borja'),
 ('Delicias Surco', 'Av. Caminos del Inca 567', 'Santiago de Surco'),
 ('Delicias La Molina', 'Av. La Molina 890', 'La Molina');

INSERT INTO tb_usuario (usu_usuario, usu_contrasena, usu_nombres, usu_apellidos, rol_id, tie_id) VALUES
 ('vendedor1', '$2a$10$B98zJXjfGWQdJ9JIc2s9o.L6o5nFQrCjNLjruZjKqWLQk08mA6B/u', 'Rosa', 'Quispe', 1, 1),
 ('vendedor2', '$2a$10$B98zJXjfGWQdJ9JIc2s9o.L6o5nFQrCjNLjruZjKqWLQk08mA6B/u', 'Jorge', 'Ramos', 1, 2),
 ('jefeprod',  '$2a$10$B98zJXjfGWQdJ9JIc2s9o.L6o5nFQrCjNLjruZjKqWLQk08mA6B/u', 'Carlos', 'Mendoza', 2, NULL),
 ('almacen',   '$2a$10$B98zJXjfGWQdJ9JIc2s9o.L6o5nFQrCjNLjruZjKqWLQk08mA6B/u', 'Lucía', 'Torres', 3, NULL),
 ('admin',     '$2a$10$B98zJXjfGWQdJ9JIc2s9o.L6o5nFQrCjNLjruZjKqWLQk08mA6B/u', 'Ana', 'Valle', 4, NULL);

INSERT INTO tb_cliente (cli_nombres, cli_telefono, cli_tipo) VALUES
 ('CLIENTE GENÉRICO MOSTRADOR', NULL, 'GENERICO'),
 ('María Fernández', '987654321', 'FRECUENTE');

INSERT INTO tb_producto (pro_nombre, pro_descripcion, pro_precio, pro_tipo, pro_tiempo_prep) VALUES
 ('Pan francés (unidad)', 'Pan de mostrador', 0.30, 'MOSTRADOR', 90),
 ('Torta de chocolate 20 cm', 'Torta por encargo', 55.00, 'ESPECIAL', 240),
 ('Pie de manzana', 'Porción familiar', 32.00, 'MOSTRADOR', 120);

INSERT INTO tb_insumo (ins_nombre, ins_unidad, ins_stock_actual, ins_stock_minimo, ins_perecible) VALUES
 ('Harina preparada', 'kg', 0, 50, FALSE),
 ('Mantequilla', 'kg', 0, 10, TRUE),
 ('Huevos', 'und', 0, 120, TRUE),
 ('Cacao en polvo', 'kg', 0, 2, FALSE),
 ('Manzana', 'kg', 0, 5, TRUE);

INSERT INTO tb_proveedor (prv_razon_social, prv_ruc, prv_telefono, prv_correo, prv_tipo) VALUES
 ('Molinos del Sur S.A.C.', '20512345678', '014445566', 'ventas@molinosdelsur.pe', 'SECOS'),
 ('Granja Los Andes E.I.R.L.', '20598765432', '016667788', 'pedidos@granjalosandes.pe', 'PERECIBLES');

-- Receta estándar (cantidad de insumo por UNA unidad de producto) - RN004
INSERT INTO tb_receta (pro_id, ins_id, rec_cantidad) VALUES
 (1, 1, 0.040), (1, 2, 0.002),
 (2, 1, 0.500), (2, 2, 0.250), (2, 3, 6), (2, 4, 0.150),
 (3, 1, 0.400), (3, 2, 0.150), (3, 5, 0.800);
