package pe.deliciasdelvalle.sigeppi.dao.postgres;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import pe.deliciasdelvalle.sigeppi.dao.PanelDAO;

/** Consultas del panel de inicio. idTienda = null significa todas las tiendas. */
public class PanelDAOImpl implements PanelDAO {

    private static final String FILTRO_TIENDA = " AND (CAST(? AS INTEGER) IS NULL OR p.tie_id = ?) ";

    @Override
    public Map<String, Object> ventasDelDia(Integer idTienda) throws SQLException {
        return JdbcUtil.uno("SELECT COALESCE(SUM(c.com_total), 0) AS total, COUNT(*) AS cantidad "
                + "FROM tb_comprobante c JOIN tb_pedido p ON p.ped_id = c.ped_id "
                + "WHERE c.com_fecha_emision >= CURRENT_DATE" + FILTRO_TIENDA, idTienda, idTienda);
    }

    @Override
    public Map<String, Object> ventasDelMes(Integer idTienda) throws SQLException {
        return JdbcUtil.uno("SELECT COALESCE(SUM(c.com_total), 0) AS total, COUNT(*) AS cantidad, "
                + "COALESCE(AVG(c.com_total), 0) AS promedio "
                + "FROM tb_comprobante c JOIN tb_pedido p ON p.ped_id = c.ped_id "
                + "WHERE c.com_fecha_emision >= DATE_TRUNC('month', CURRENT_DATE)" + FILTRO_TIENDA, idTienda, idTienda);
    }

    @Override
    public List<Map<String, Object>> pedidosPorEstado(Integer idTienda) throws SQLException {
        return JdbcUtil.lista("SELECT e.estado, COUNT(p.ped_id) AS cantidad "
                + "FROM (VALUES (1, 'REGISTRADO'), (2, 'EN_PRODUCCION'), (3, 'LISTO_ENTREGA'), (4, 'ENTREGADO')) AS e(orden, estado) "
                + "LEFT JOIN tb_pedido p ON p.ped_estado = e.estado" + FILTRO_TIENDA.replace(" AND", " AND")
                + "GROUP BY e.orden, e.estado ORDER BY e.orden", idTienda, idTienda);
    }

    @Override
    public List<Map<String, Object>> ventasPorTiendaMes() throws SQLException {
        return JdbcUtil.lista("SELECT t.tie_nombre AS tienda, COALESCE(SUM(c.com_total), 0) AS total, COUNT(c.com_id) AS cantidad "
                + "FROM tb_tienda t LEFT JOIN tb_pedido p ON p.tie_id = t.tie_id "
                + "LEFT JOIN tb_comprobante c ON c.ped_id = p.ped_id AND c.com_fecha_emision >= DATE_TRUNC('month', CURRENT_DATE) "
                + "WHERE t.tie_estado GROUP BY t.tie_nombre ORDER BY total DESC");
    }

    @Override
    public List<Map<String, Object>> ventasUltimosDias(int dias) throws SQLException {
        return JdbcUtil.lista("SELECT TO_CHAR(d.dia, 'DD/MM') AS dia, COALESCE(SUM(c.com_total), 0) AS total "
                + "FROM GENERATE_SERIES(CURRENT_DATE - (CAST(? AS INTEGER) - 1), CURRENT_DATE, INTERVAL '1 day') AS d(dia) "
                + "LEFT JOIN tb_comprobante c ON CAST(c.com_fecha_emision AS DATE) = CAST(d.dia AS DATE) "
                + "GROUP BY d.dia ORDER BY d.dia", dias);
    }

    @Override
    public List<Map<String, Object>> productosMasVendidosMes(int limite) throws SQLException {
        return JdbcUtil.lista("SELECT pr.pro_nombre AS producto, SUM(d.dpe_cantidad) AS unidades, SUM(d.dpe_subtotal) AS total "
                + "FROM tb_detalle_pedido d JOIN tb_pedido p ON p.ped_id = d.ped_id JOIN tb_producto pr ON pr.pro_id = d.pro_id "
                + "WHERE p.ped_fecha_registro >= DATE_TRUNC('month', CURRENT_DATE) "
                + "GROUP BY pr.pro_nombre ORDER BY unidades DESC LIMIT ?", limite);
    }

    @Override
    public List<Map<String, Object>> insumosBajoMinimo() throws SQLException {
        return JdbcUtil.lista("SELECT ins_nombre AS insumo, ins_unidad AS unidad, ins_stock_actual AS stock, "
                + "ins_stock_minimo AS minimo FROM tb_insumo WHERE ins_stock_actual < ins_stock_minimo ORDER BY ins_nombre");
    }

    @Override
    public List<Map<String, Object>> lotesPorVencer(int dias) throws SQLException {
        return JdbcUtil.lista("SELECT l.lot_codigo AS lote, i.ins_nombre AS insumo, i.ins_unidad AS unidad, "
                + "l.lot_cantidad_disponible AS disponible, TO_CHAR(l.lot_fecha_vencimiento, 'DD/MM/YYYY') AS vence, "
                + "(l.lot_fecha_vencimiento - CURRENT_DATE) AS dias "
                + "FROM tb_lote_insumo l JOIN tb_insumo i ON i.ins_id = l.ins_id "
                + "WHERE l.lot_cantidad_disponible > 0 AND l.lot_fecha_vencimiento <= CURRENT_DATE + CAST(? AS INTEGER) "
                + "ORDER BY l.lot_fecha_vencimiento", dias);
    }

    @Override
    public int solicitudesPendientes() throws SQLException {
        Object n = JdbcUtil.uno("SELECT COUNT(*) AS n FROM tb_solicitud_compra WHERE sol_estado = 'PENDIENTE'").get("n");
        return n == null ? 0 : ((Number) n).intValue();
    }

    @Override
    public List<Map<String, Object>> pedidosEspecialesProximos(Integer idTienda, int dias) throws SQLException {
        return JdbcUtil.lista("SELECT p.ped_id AS id, c.cli_nombres AS cliente, TO_CHAR(p.ped_fecha_entrega, 'DD/MM/YYYY') AS entrega, "
                + "p.ped_estado AS estado, t.tie_nombre AS tienda "
                + "FROM tb_pedido p JOIN tb_cliente c ON c.cli_id = p.cli_id JOIN tb_tienda t ON t.tie_id = p.tie_id "
                + "WHERE p.ped_tipo = 'ESPECIAL' AND p.ped_estado <> 'ENTREGADO' "
                + "AND p.ped_fecha_entrega <= CURRENT_DATE + CAST(? AS INTEGER)" + FILTRO_TIENDA
                + "ORDER BY p.ped_fecha_entrega", dias, idTienda, idTienda);
    }
}
