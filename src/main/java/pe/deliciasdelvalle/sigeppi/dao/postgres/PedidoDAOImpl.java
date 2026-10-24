package pe.deliciasdelvalle.sigeppi.dao.postgres;

import pe.deliciasdelvalle.sigeppi.modelo.Producto;
import pe.deliciasdelvalle.sigeppi.modelo.HistorialEstado;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.dao.PedidoDAO;
import pe.deliciasdelvalle.sigeppi.modelo.Cliente;
import pe.deliciasdelvalle.sigeppi.modelo.DetallePedido;
import pe.deliciasdelvalle.sigeppi.modelo.EstadoPedido;
import pe.deliciasdelvalle.sigeppi.modelo.Pedido;
import pe.deliciasdelvalle.sigeppi.modelo.Tienda;
import pe.deliciasdelvalle.sigeppi.util.ConexionBD;

public class PedidoDAOImpl implements PedidoDAO {

    private static final String SQL_SELECT =
            "SELECT p.ped_id, p.ped_tipo, p.ped_estado, p.ped_fecha_registro, p.ped_fecha_entrega, p.ped_total, "
          + "       c.cli_id, c.cli_nombres, t.tie_id, t.tie_nombre "
          + "FROM tb_pedido p JOIN tb_cliente c ON c.cli_id = p.cli_id JOIN tb_tienda t ON t.tie_id = p.tie_id ";

    @Override
    public int insertar(Connection cn, Pedido p) throws SQLException {
        String sql = "INSERT INTO tb_pedido (ped_fecha_entrega, ped_tipo, ped_estado, ped_subtotal, ped_igv, ped_total, "
                   + "ped_observacion, cli_id, tie_id, usu_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING ped_id";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            if (p.getFechaEntrega() == null) {
                ps.setNull(1, Types.DATE);
            } else {
                ps.setDate(1, Date.valueOf(p.getFechaEntrega()));
            }
            ps.setString(2, p.getTipoPedido());
            ps.setString(3, p.getEstado().name());
            ps.setDouble(4, p.getSubtotal());
            ps.setDouble(5, p.getIgv());
            ps.setDouble(6, p.getTotal());
            ps.setString(7, p.getObservaciones());
            ps.setInt(8, p.getCliente().getIdCliente());
            ps.setInt(9, p.getTienda().getIdTienda());
            ps.setInt(10, p.getVendedor().getIdUsuario());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    @Override
    public void insertarDetalles(Connection cn, int idPedido, List<DetallePedido> detalles) throws SQLException {
        String sql = "INSERT INTO tb_detalle_pedido (ped_id, pro_id, dpe_cantidad, dpe_precio_unitario, dpe_subtotal) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            for (DetallePedido d : detalles) {
                ps.setInt(1, idPedido);
                ps.setInt(2, d.getProducto().getIdProducto());
                ps.setInt(3, d.getCantidad());
                ps.setDouble(4, d.getPrecioUnitario());
                ps.setDouble(5, d.getSubtotal());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    @Override
    public Pedido buscarPorId(int idPedido) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_SELECT + "WHERE p.ped_id = ?")) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public List<Pedido> listar(Integer idTienda, EstadoPedido estado) throws SQLException {
        StringBuilder sql = new StringBuilder(SQL_SELECT).append("WHERE 1 = 1 ");
        List<Object> params = new ArrayList<>();
        if (idTienda != null) {
            sql.append("AND p.tie_id = ? ");
            params.add(idTienda);
        }
        if (estado != null) {
            sql.append("AND p.ped_estado = ? ");
            params.add(estado.name());
        }
        sql.append("ORDER BY p.ped_fecha_registro DESC");
        List<Pedido> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public void actualizarEstado(Connection cn, int idPedido, EstadoPedido nuevo) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement("UPDATE tb_pedido SET ped_estado = ? WHERE ped_id = ?")) {
            ps.setString(1, nuevo.name());
            ps.setInt(2, idPedido);
            ps.executeUpdate();
        }
    }

    @Override
    public void insertarHistorial(Connection cn, int idPedido, EstadoPedido anterior, EstadoPedido nuevo, int idUsuario)
            throws SQLException {
        String sql = "INSERT INTO tb_historial_estado_pedido (ped_id, hep_estado_anterior, hep_estado_nuevo, usu_id) "
                   + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ps.setString(2, anterior == null ? null : anterior.name());
            ps.setString(3, nuevo.name());
            ps.setInt(4, idUsuario);
            ps.executeUpdate();
        }
    }

    private Pedido mapear(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setIdPedido(rs.getInt("ped_id"));
        p.setTipoPedido(rs.getString("ped_tipo"));
        p.setEstado(EstadoPedido.valueOf(rs.getString("ped_estado")));
        p.setFechaRegistro(rs.getTimestamp("ped_fecha_registro").toLocalDateTime());
        Date entrega = rs.getDate("ped_fecha_entrega");
        p.setFechaEntrega(entrega == null ? null : entrega.toLocalDate());
        p.setTotal(rs.getDouble("ped_total"));
        Cliente c = new Cliente();
        c.setIdCliente(rs.getInt("cli_id"));
        c.setNombres(rs.getString("cli_nombres"));
        p.setCliente(c);
        Tienda t = new Tienda();
        t.setIdTienda(rs.getInt("tie_id"));
        t.setNombre(rs.getString("tie_nombre"));
        p.setTienda(t);
        return p;
    }

    @Override
    public List<DetallePedido> listarDetalles(int idPedido) throws SQLException {
        String sql = "SELECT d.pro_id, p.pro_nombre, d.dpe_cantidad, d.dpe_precio_unitario FROM tb_detalle_pedido d "
                   + "JOIN tb_producto p ON p.pro_id = d.pro_id WHERE d.ped_id = ? ORDER BY d.dpe_id";
        List<DetallePedido> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto pr = new Producto();
                    pr.setIdProducto(rs.getInt("pro_id"));
                    pr.setNombre(rs.getString("pro_nombre"));
                    DetallePedido d = new DetallePedido();
                    d.setProducto(pr);
                    d.setCantidad(rs.getInt("dpe_cantidad"));
                    d.setPrecioUnitario(rs.getDouble("dpe_precio_unitario"));
                    lista.add(d);
                }
            }
        }
        return lista;
    }

    @Override
    public List<HistorialEstado> listarHistorial(int idPedido) throws SQLException {
        String sql = "SELECT h.hep_estado_anterior, h.hep_estado_nuevo, h.hep_fecha, u.usu_nombres || ' ' || u.usu_apellidos AS usuario "
                   + "FROM tb_historial_estado_pedido h JOIN tb_usuario u ON u.usu_id = h.usu_id "
                   + "WHERE h.ped_id = ? ORDER BY h.hep_fecha, h.hep_id";
        List<HistorialEstado> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HistorialEstado h = new HistorialEstado();
                    h.setEstadoAnterior(rs.getString("hep_estado_anterior"));
                    h.setEstadoNuevo(rs.getString("hep_estado_nuevo"));
                    h.setFecha(rs.getTimestamp("hep_fecha").toLocalDateTime());
                    h.setUsuario(rs.getString("usuario"));
                    lista.add(h);
                }
            }
        }
        return lista;
    }
}
