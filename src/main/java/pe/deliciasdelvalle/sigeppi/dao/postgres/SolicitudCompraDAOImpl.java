package pe.deliciasdelvalle.sigeppi.dao.postgres;

import pe.deliciasdelvalle.sigeppi.util.ConexionBD;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import pe.deliciasdelvalle.sigeppi.dao.SolicitudCompraDAO;
import pe.deliciasdelvalle.sigeppi.modelo.SolicitudCompra;

public class SolicitudCompraDAOImpl implements SolicitudCompraDAO {

    @Override
    public boolean existePendiente(Connection cn, int idInsumo) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT 1 FROM tb_solicitud_compra WHERE ins_id = ? AND sol_estado = 'PENDIENTE' LIMIT 1")) {
            ps.setInt(1, idInsumo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public int insertar(Connection cn, SolicitudCompra s) throws SQLException {
        String sql = "INSERT INTO tb_solicitud_compra (ins_id, prv_id, sol_cantidad, sol_estado, sol_motivo, usu_id) "
                   + "VALUES (?, ?, ?, ?, ?, ?) RETURNING sol_id";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, s.getIdInsumo());
            if (s.getIdProveedor() == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, s.getIdProveedor());
            }
            ps.setDouble(3, s.getCantidad());
            ps.setString(4, s.getEstado());
            ps.setString(5, s.getMotivo());
            ps.setInt(6, s.getIdUsuario());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    @Override
    public void marcarAtendidas(Connection cn, int idInsumo) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "UPDATE tb_solicitud_compra SET sol_estado = 'ATENDIDA' WHERE ins_id = ? AND sol_estado = 'PENDIENTE'")) {
            ps.setInt(1, idInsumo);
            ps.executeUpdate();
        }
    }

    @Override
    public List<SolicitudCompra> listar(String estado) throws SQLException {
        String sql = "SELECT s.sol_id, s.sol_fecha, s.ins_id, s.prv_id, s.sol_cantidad, s.sol_estado, s.sol_motivo, s.usu_id, "
                   + "i.ins_nombre, i.ins_unidad, p.prv_razon_social, p.prv_telefono, p.prv_correo, u.usu_usuario "
                   + "FROM tb_solicitud_compra s JOIN tb_insumo i ON i.ins_id = s.ins_id "
                   + "LEFT JOIN tb_proveedor p ON p.prv_id = s.prv_id JOIN tb_usuario u ON u.usu_id = s.usu_id "
                   + "WHERE (? = '' OR s.sol_estado = ?) ORDER BY (s.sol_estado = 'PENDIENTE') DESC, s.sol_fecha DESC";
        String e = estado == null ? "" : estado;
        List<SolicitudCompra> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, e);
            ps.setString(2, e);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SolicitudCompra s = new SolicitudCompra();
                    s.setIdSolicitud(rs.getInt("sol_id"));
                    s.setFecha(rs.getTimestamp("sol_fecha").toLocalDateTime());
                    s.setIdInsumo(rs.getInt("ins_id"));
                    int prv = rs.getInt("prv_id");
                    s.setIdProveedor(rs.wasNull() ? null : prv);
                    s.setCantidad(rs.getDouble("sol_cantidad"));
                    s.setEstado(rs.getString("sol_estado"));
                    s.setMotivo(rs.getString("sol_motivo"));
                    s.setIdUsuario(rs.getInt("usu_id"));
                    s.setNombreInsumo(rs.getString("ins_nombre"));
                    s.setUnidad(rs.getString("ins_unidad"));
                    s.setNombreProveedor(rs.getString("prv_razon_social"));
                    s.setTelefonoProveedor(rs.getString("prv_telefono"));
                    s.setCorreoProveedor(rs.getString("prv_correo"));
                    s.setNombreUsuario(rs.getString("usu_usuario"));
                    lista.add(s);
                }
            }
        }
        return lista;
    }

    @Override
    public void cambiarEstado(int idSolicitud, String estado) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(
                     "UPDATE tb_solicitud_compra SET sol_estado = ? WHERE sol_id = ? AND sol_estado = 'PENDIENTE'")) {
            ps.setString(1, estado);
            ps.setInt(2, idSolicitud);
            ps.executeUpdate();
        }
    }
}
