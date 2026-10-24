package pe.deliciasdelvalle.sigeppi.dao.postgres;

import pe.deliciasdelvalle.sigeppi.modelo.RegistroAuditoria;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import pe.deliciasdelvalle.sigeppi.dao.AuditoriaDAO;
import pe.deliciasdelvalle.sigeppi.util.ConexionBD;

/** RNF-03: cada acción crítica deja registro con usuario, fecha, hora, IP y acción. */
public class AuditoriaDAOImpl implements AuditoriaDAO {

    private static final String SQL =
            "INSERT INTO tb_auditoria (usu_id, aud_ip, aud_modulo, aud_accion, aud_detalle) VALUES (?, ?, ?, ?, ?)";

    @Override
    public void registrar(Integer idUsuario, String ip, String modulo, String accion, String detalle) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion()) {
            registrar(cn, idUsuario, ip, modulo, accion, detalle);
        }
    }

    @Override
    public void registrar(Connection cn, Integer idUsuario, String ip, String modulo, String accion, String detalle) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(SQL)) {
            if (idUsuario == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, idUsuario);
            }
            ps.setString(2, ip);
            ps.setString(3, modulo);
            ps.setString(4, accion);
            ps.setString(5, detalle);
            ps.executeUpdate();
        }
    }

    @Override
    public List<RegistroAuditoria> listar(String modulo, String texto, int limite) throws SQLException {
        String sql = "SELECT a.aud_id, a.aud_fecha, COALESCE(u.usu_usuario, '-') AS usuario, a.aud_ip, a.aud_modulo, "
                   + "a.aud_accion, a.aud_detalle FROM tb_auditoria a LEFT JOIN tb_usuario u ON u.usu_id = a.usu_id "
                   + "WHERE (? = '' OR a.aud_modulo = ?) "
                   + "AND (? = '' OR a.aud_accion ILIKE ? OR a.aud_detalle ILIKE ? OR u.usu_usuario ILIKE ?) "
                   + "ORDER BY a.aud_fecha DESC LIMIT ?";
        String m = modulo == null ? "" : modulo;
        String t = texto == null ? "" : texto.trim();
        String like = "%" + t + "%";
        List<RegistroAuditoria> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, m);
            ps.setString(2, m);
            ps.setString(3, t);
            ps.setString(4, like);
            ps.setString(5, like);
            ps.setString(6, like);
            ps.setInt(7, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RegistroAuditoria r = new RegistroAuditoria();
                    r.setIdAuditoria(rs.getLong("aud_id"));
                    r.setFecha(rs.getTimestamp("aud_fecha").toLocalDateTime());
                    r.setUsuario(rs.getString("usuario"));
                    r.setIp(rs.getString("aud_ip"));
                    r.setModulo(rs.getString("aud_modulo"));
                    r.setAccion(rs.getString("aud_accion"));
                    r.setDetalle(rs.getString("aud_detalle"));
                    lista.add(r);
                }
            }
        }
        return lista;
    }
}
