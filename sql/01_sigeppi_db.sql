-- =====================================================================
-- SIGEPPI - Sistema de Gestión de Pedidos, Producción e Inventario
-- Panadería y Pastelería "Delicias del Valle" S.A.C.
-- Script de creación de la base de datos (PostgreSQL 16)
-- Grupo 02 - Ingeniería de Software 2 - USIL 2026-02
-- =====================================================================
-- Ejecutar conectado a la BD sigeppi_db:
--   CREATE DATABASE sigeppi_db ENCODING 'UTF8';

-- ---------------------------------------------------------------------
-- SEGURIDAD
-- ---------------------------------------------------------------------
CREATE TABLE tb_rol (
    rol_id          SERIAL       PRIMARY KEY,
    rol_nombre      VARCHAR(30)  NOT NULL UNIQUE,
    rol_descripcion VARCHAR(150)
);

CREATE TABLE tb_tienda (
    tie_id        SERIAL       PRIMARY KEY,
    tie_nombre    VARCHAR(60)  NOT NULL,
    tie_direccion VARCHAR(150),
    tie_distrito  VARCHAR(40),
    tie_estado    BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE tb_usuario (
    usu_id            SERIAL       PRIMARY KEY,
    usu_usuario       VARCHAR(30)  NOT NULL UNIQUE,
    usu_contrasena    VARCHAR(60)  NOT NULL,              -- hash BCrypt (RNF-01)
    usu_nombres       VARCHAR(60)  NOT NULL,
    usu_apellidos     VARCHAR(60)  NOT NULL,
    usu_estado        BOOLEAN      NOT NULL DEFAULT TRUE,
    usu_ultimo_acceso TIMESTAMP,
    rol_id            INTEGER      NOT NULL REFERENCES tb_rol (rol_id),
    tie_id            INTEGER      REFERENCES tb_tienda (tie_id)
);

-- ---------------------------------------------------------------------
-- PEDIDOS
-- ---------------------------------------------------------------------
CREATE TABLE tb_cliente (
    cli_id             SERIAL       PRIMARY KEY,
    cli_nombres        VARCHAR(100) NOT NULL,
    cli_telefono       VARCHAR(15),
    cli_tipo           VARCHAR(10)  NOT NULL DEFAULT 'FRECUENTE'
                       CHECK (cli_tipo IN ('GENERICO', 'FRECUENTE')),
    cli_fecha_registro DATE         NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE tb_producto (
    pro_id          SERIAL        PRIMARY KEY,
    pro_nombre      VARCHAR(80)   NOT NULL UNIQUE,
    pro_descripcion VARCHAR(200),
    pro_precio      NUMERIC(10,2) NOT NULL CHECK (pro_precio >= 0),          -- RN005
    pro_tipo        VARCHAR(10)   NOT NULL CHECK (pro_tipo IN ('MOSTRADOR', 'ESPECIAL')),
    pro_tiempo_prep INTEGER       CHECK (pro_tiempo_prep >= 0),
    pro_estado      BOOLEAN       NOT NULL DEFAULT TRUE
);

-- ---------------------------------------------------------------------
-- INVENTARIO (se crea antes porque producción depende de insumos)
-- ---------------------------------------------------------------------
CREATE TABLE tb_insumo (
    ins_id           SERIAL        PRIMARY KEY,
    ins_nombre       VARCHAR(60)   NOT NULL UNIQUE,
    ins_unidad       VARCHAR(10)   NOT NULL,
    ins_stock_actual NUMERIC(12,3) NOT NULL DEFAULT 0 CHECK (ins_stock_actual >= 0),   -- RN005
    ins_stock_minimo NUMERIC(12,3) NOT NULL DEFAULT 0 CHECK (ins_stock_minimo >= 0),
    ins_perecible    BOOLEAN       NOT NULL DEFAULT FALSE
);

CREATE TABLE tb_proveedor (
    prv_id           SERIAL       PRIMARY KEY,
    prv_razon_social VARCHAR(100) NOT NULL,
    prv_ruc          CHAR(11)     NOT NULL UNIQUE CHECK (prv_ruc ~ '^[0-9]{11}$'),
    prv_telefono     VARCHAR(15),
    prv_correo       VARCHAR(80),
    prv_tipo         VARCHAR(12)  CHECK (prv_tipo IN ('SECOS', 'PERECIBLES'))
);

-- ---------------------------------------------------------------------
-- PRODUCCIÓN
-- ---------------------------------------------------------------------
CREATE TABLE tb_receta (
    rec_id       SERIAL        PRIMARY KEY,
    pro_id       INTEGER       NOT NULL REFERENCES tb_producto (pro_id),
    ins_id       INTEGER       NOT NULL REFERENCES tb_insumo (ins_id),
    rec_cantidad NUMERIC(10,3) NOT NULL CHECK (rec_cantidad > 0),
    UNIQUE (pro_id, ins_id)
);

CREATE TABLE tb_orden_produccion (
    ord_id               SERIAL       PRIMARY KEY,
    ord_fecha_produccion DATE         NOT NULL,
    ord_estado           VARCHAR(12)  NOT NULL DEFAULT 'EN_PROCESO'
                         CHECK (ord_estado IN ('EN_PROCESO', 'TERMINADA')),
    ord_observacion      VARCHAR(250),
    ord_fecha_registro   TIMESTAMP    NOT NULL DEFAULT NOW(),
    usu_id               INTEGER      NOT NULL REFERENCES tb_usuario (usu_id)
);

CREATE TABLE tb_detalle_orden (
    dor_id       SERIAL  PRIMARY KEY,
    ord_id       INTEGER NOT NULL REFERENCES tb_orden_produccion (ord_id),
    pro_id       INTEGER NOT NULL REFERENCES tb_producto (pro_id),
    dor_cantidad INTEGER NOT NULL CHECK (dor_cantidad > 0),
    UNIQUE (ord_id, pro_id)
);

-- ---------------------------------------------------------------------
-- PEDIDOS (cabecera, detalle, comprobante, historial)
-- ---------------------------------------------------------------------
CREATE TABLE tb_pedido (
    ped_id             SERIAL        PRIMARY KEY,
    ped_fecha_registro TIMESTAMP     NOT NULL DEFAULT NOW(),
    ped_fecha_entrega  DATE,
    ped_tipo           VARCHAR(10)   NOT NULL CHECK (ped_tipo IN ('MOSTRADOR', 'ESPECIAL')),
    ped_estado         VARCHAR(20)   NOT NULL DEFAULT 'REGISTRADO'
                       CHECK (ped_estado IN ('REGISTRADO', 'EN_PRODUCCION', 'LISTO_ENTREGA', 'ENTREGADO')),  -- RN007
    ped_subtotal       NUMERIC(10,2) NOT NULL CHECK (ped_subtotal >= 0),
    ped_igv            NUMERIC(10,2) NOT NULL CHECK (ped_igv >= 0),
    ped_total          NUMERIC(10,2) NOT NULL CHECK (ped_total >= 0),
    ped_observacion    VARCHAR(250),
    cli_id             INTEGER       NOT NULL REFERENCES tb_cliente (cli_id),     -- RN001
    tie_id             INTEGER       NOT NULL REFERENCES tb_tienda (tie_id),      -- RN001
    usu_id             INTEGER       NOT NULL REFERENCES tb_usuario (usu_id),
    ord_id             INTEGER       REFERENCES tb_orden_produccion (ord_id)
);

CREATE TABLE tb_detalle_pedido (
    dpe_id              SERIAL        PRIMARY KEY,
    ped_id              INTEGER       NOT NULL REFERENCES tb_pedido (ped_id),
    pro_id              INTEGER       NOT NULL REFERENCES tb_producto (pro_id),
    dpe_cantidad        INTEGER       NOT NULL CHECK (dpe_cantidad > 0),          -- RN005
    dpe_precio_unitario NUMERIC(10,2) NOT NULL CHECK (dpe_precio_unitario >= 0),  -- RN005
    dpe_subtotal        NUMERIC(10,2) NOT NULL
);

CREATE TABLE tb_comprobante (
    com_id            SERIAL        PRIMARY KEY,
    ped_id            INTEGER       NOT NULL UNIQUE REFERENCES tb_pedido (ped_id),
    com_tipo          VARCHAR(7)    NOT NULL DEFAULT 'BOLETA' CHECK (com_tipo IN ('BOLETA', 'FACTURA')),
    com_serie         CHAR(4)       NOT NULL,
    com_numero        INTEGER       NOT NULL,
    com_fecha_emision TIMESTAMP     NOT NULL DEFAULT NOW(),
    com_metodo_pago   VARCHAR(15)   NOT NULL CHECK (com_metodo_pago IN ('EFECTIVO', 'TARJETA', 'YAPE', 'PLIN')),
    com_total         NUMERIC(10,2) NOT NULL CHECK (com_total >= 0),
    UNIQUE (com_serie, com_numero)
);

-- ---------------------------------------------------------------------
-- INVENTARIO (lotes, movimientos, solicitudes)
-- ---------------------------------------------------------------------
CREATE TABLE tb_lote_insumo (
    lot_id                  SERIAL        PRIMARY KEY,
    lot_codigo              VARCHAR(20)   NOT NULL UNIQUE,
    ins_id                  INTEGER       NOT NULL REFERENCES tb_insumo (ins_id),
    prv_id                  INTEGER       NOT NULL REFERENCES tb_proveedor (prv_id),
    lot_fecha_ingreso       DATE          NOT NULL DEFAULT CURRENT_DATE,
    lot_fecha_vencimiento   DATE          NOT NULL,
    lot_cantidad_inicial    NUMERIC(12,3) NOT NULL CHECK (lot_cantidad_inicial > 0),
    lot_cantidad_disponible NUMERIC(12,3) NOT NULL CHECK (lot_cantidad_disponible >= 0),
    lot_costo_unitario      NUMERIC(10,2) NOT NULL CHECK (lot_costo_unitario >= 0),
    CONSTRAINT ck_lote_vencimiento CHECK (lot_fecha_vencimiento > lot_fecha_ingreso)    -- RN006
);

CREATE TABLE tb_movimiento_insumo (
    mov_id       SERIAL        PRIMARY KEY,
    mov_tipo     VARCHAR(7)    NOT NULL CHECK (mov_tipo IN ('INGRESO', 'SALIDA')),
    mov_cantidad NUMERIC(12,3) NOT NULL CHECK (mov_cantidad > 0),
    mov_fecha    TIMESTAMP     NOT NULL DEFAULT NOW(),
    mov_motivo   VARCHAR(100),
    lot_id       INTEGER       NOT NULL REFERENCES tb_lote_insumo (lot_id),
    ord_id       INTEGER       REFERENCES tb_orden_produccion (ord_id),
    usu_id       INTEGER       NOT NULL REFERENCES tb_usuario (usu_id)
);

CREATE TABLE tb_solicitud_compra (
    sol_id       SERIAL        PRIMARY KEY,
    sol_fecha    TIMESTAMP     NOT NULL DEFAULT NOW(),
    ins_id       INTEGER       NOT NULL REFERENCES tb_insumo (ins_id),
    prv_id       INTEGER       REFERENCES tb_proveedor (prv_id),
    sol_cantidad NUMERIC(12,3) NOT NULL CHECK (sol_cantidad > 0),
    sol_estado   VARCHAR(10)   NOT NULL DEFAULT 'PENDIENTE'
                 CHECK (sol_estado IN ('PENDIENTE', 'ATENDIDA', 'ANULADA')),
    sol_motivo   VARCHAR(150),
    usu_id       INTEGER       NOT NULL REFERENCES tb_usuario (usu_id)
);

-- ---------------------------------------------------------------------
-- AUDITORÍA Y TRAZABILIDAD (RNF-03)
-- ---------------------------------------------------------------------
CREATE TABLE tb_historial_estado_pedido (
    hep_id              SERIAL      PRIMARY KEY,
    ped_id              INTEGER     NOT NULL REFERENCES tb_pedido (ped_id),
    hep_estado_anterior VARCHAR(20),
    hep_estado_nuevo    VARCHAR(20) NOT NULL,
    hep_fecha           TIMESTAMP   NOT NULL DEFAULT NOW(),
    usu_id              INTEGER     NOT NULL REFERENCES tb_usuario (usu_id)
);

CREATE TABLE tb_auditoria (
    aud_id      BIGSERIAL   PRIMARY KEY,
    usu_id      INTEGER     REFERENCES tb_usuario (usu_id),
    aud_fecha   TIMESTAMP   NOT NULL DEFAULT NOW(),
    aud_ip      VARCHAR(45),
    aud_modulo  VARCHAR(30) NOT NULL,
    aud_accion  VARCHAR(60) NOT NULL,
    aud_detalle TEXT
);

-- Índices para las consultas más usadas
CREATE INDEX ix_pedido_estado      ON tb_pedido (ped_estado);
CREATE INDEX ix_pedido_tienda      ON tb_pedido (tie_id, ped_fecha_registro);
CREATE INDEX ix_comprobante_fecha  ON tb_comprobante (com_fecha_emision);
CREATE INDEX ix_lote_vencimiento   ON tb_lote_insumo (ins_id, lot_fecha_vencimiento);
CREATE INDEX ix_auditoria_fecha    ON tb_auditoria (aud_fecha);

-- ---------------------------------------------------------------------
-- Usuario de la aplicación: en auditoría e historial solo puede insertar y leer
-- (los registros no se pueden modificar ni borrar)
-- ---------------------------------------------------------------------
-- CREATE ROLE sigeppi_app LOGIN PASSWORD 'cambiar_esta_clave';
-- GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA public TO sigeppi_app;
-- GRANT USAGE ON ALL SEQUENCES IN SCHEMA public TO sigeppi_app;
-- REVOKE UPDATE, DELETE ON tb_auditoria, tb_historial_estado_pedido FROM sigeppi_app;
