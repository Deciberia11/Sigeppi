package pe.deliciasdelvalle.sigeppi.dao.postgres;

import pe.deliciasdelvalle.sigeppi.util.ConexionBD;
import pe.deliciasdelvalle.sigeppi.modelo.Usuario;
import java.util.ArrayList;
import java.sql.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pe.deliciasdelvalle.sigeppi.dao.OrdenProduccionDAO;
import pe.deliciasdelvalle.sigeppi.modelo.DetalleOrden;
import pe.deliciasdelvalle.sigeppi.modelo.OrdenProduccion;

public class OrdenProduccionDAOImpl implements OrdenProduccionDAO {

    @Override
    public Map<Integer, Integer> consolidarCantidades(Connection cn, List<Integer> idsPedidos) throws SQLException {
        String sql = "SELECT d.pro_id, SUM(d.dpe_cantidad) AS total "
                   + "FROM tb_detalle_pedido d JOIN tb_pedido p ON p.ped_id = d.ped_id "
                   + "WHERE p.ped_id = ANY (?) AND p.ped_estado = 'REGISTRADO' "
                   + "GROUP BY d.pro_id";
        Map<Integer, Integer> cantidades = new HashMap<>();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            Array ids = cn.createArrayOf("integer", idsPedidos.toArray());
            ps.setArray(1, ids);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cantidades.put(rs.getInt("pro_id"), rs.getInt("total"));
                }
            }
        }
        return cantidades;
    }

    @Override
    public int insertar(Connection cn, OrdenProduccion orden) throws SQLException {
        int idOrden;
        String sql = "INSERT INTO tb_orden_produccion (ord_fecha_produccion, ord_estado, ord_observacion, usu_id) "
                   + "VALUES (?, ?, ?, ?) RETURNING ord_id";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(orden.getFechaProduccion()));
            ps.setString(2, orden.getEstado());
            ps.setString(3, orden.getObservacion());
            ps.setInt(4, orden.getJefe().getIdUsuario());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                idOrden = rs.getInt(1);
            }
        }
        try (PreparedStatement ps = cn.prepareStatement(
                "INSERT INTO tb_detalle_orden (ord_id, pro_id, dor_cantidad) VALUES (?, ?, ?)")) {
            for (DetalleOrden d : orden.getDetalles()) {
                ps.setInt(1, idOrden);
                ps.setInt(2, d.getIdProducto());
                ps.setInt(3, d.getCantidad());
                ps.addBatch();
            }
            ps.executeBatch();
        }
        return idOrden;
    }

    @Override
    public void asignarPedidos(Connection cn, int idOrden, List<Integer> idsPedidos) throws SQLException {
        String sql = "UPDATE tb_pedido SET ord_id = ?, ped_estado = 'EN_PRODUCCION' "
                   + "WHERE ped_id = ANY (?) AND ped_estado = 'REGISTRADO'";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idOrden);
            ps.setArray(2, cn.createArrayOf("integer", idsPedidos.toArray()));
            ps.executeUpdate();
        }
    }

    private static final String SQL_ORDEN =
            "SELECT o.ord_id, o.ord_fecha_produccion, o.ord_estado, o.ord_observacion, u.usu_nombres, u.usu_apellidos, "
          + "(SELECT COUNT(*) FROM tb_pedido p WHERE p.ord_id = o.ord_id) AS nro_pedidos "
          + "FROM tb_orden_produccion o JOIN tb_usuario u ON u.usu_id = o.usu_id ";

    @Override
    public List<OrdenProduccion> listar(int limite) throws SQLException {
        List<OrdenProduccion> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_ORDEN
                     + "ORDER BY (o.ord_estado = 'EN_PROCESO') DESC, o.ord_id DESC LIMIT ?")) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
            for (OrdenProduccion o : lista) {
                cargarDetalle(cn, o);
            }
        }
        return lista;
    }

    @Override
    public OrdenProduccion buscarPorId(int idOrden) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_ORDEN + "WHERE o.ord_id = ?")) {
            ps.setInt(1, idOrden);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                OrdenProduccion o = mapear(rs);
                cargarDetalle(cn, o);
                return o;
            }
        }
    }

    @Override
    public List<Integer> pedidosEnProduccion(Connection cn, int idOrden) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT ped_id FROM tb_pedido WHERE ord_id = ? AND ped_estado = 'EN_PRODUCCION' FOR UPDATE")) {
            ps.setInt(1, idOrden);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
            }
        }
        return ids;
    }

    @Override
    public void terminar(Connection cn, int idOrden) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "UPDATE tb_orden_produccion SET ord_estado = 'TERMINADA' WHERE ord_id = ?")) {
            ps.setInt(1, idOrden);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = cn.prepareStatement(
                "UPDATE tb_pedido SET ped_estado = 'LISTO_ENTREGA' WHERE ord_id = ? AND ped_estado = 'EN_PRODUCCION'")) {
            ps.setInt(1, idOrden);
            ps.executeUpdate();
        }
    }

    private OrdenProduccion mapear(ResultSet rs) throws SQLException {
        OrdenProduccion o = new OrdenProduccion();
        o.setIdOrden(rs.getInt("ord_id"));
        o.setFechaProduccion(rs.getDate("ord_fecha_produccion").toLocalDate());
        o.setEstado(rs.getString("ord_estado"));
        o.setObservacion(rs.getString("ord_observacion"));
        o.setNroPedidos(rs.getInt("nro_pedidos"));
        Usuario jefe = new Usuario();
        jefe.setNombres(rs.getString("usu_nombres"));
        jefe.setApellidos(rs.getString("usu_apellidos"));
        o.setJefe(jefe);
        return o;
    }

    private void cargarDetalle(Connection cn, OrdenProduccion o) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT d.pro_id, p.pro_nombre, d.dor_cantidad FROM tb_detalle_orden d "
              + "JOIN tb_producto p ON p.pro_id = d.pro_id WHERE d.ord_id = ? ORDER BY p.pro_nombre")) {
            ps.setInt(1, o.getIdOrden());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetalleOrden d = new DetalleOrden();
                    d.setIdProducto(rs.getInt("pro_id"));
                    d.setNombreProducto(rs.getString("pro_nombre"));
                    d.setCantidad(rs.getInt("dor_cantidad"));
                    o.getDetalles().add(d);
                }
            }
        }
    }
}
