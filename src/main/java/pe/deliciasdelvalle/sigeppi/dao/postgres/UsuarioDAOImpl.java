package pe.deliciasdelvalle.sigeppi.dao.postgres;

import java.sql.Types;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import pe.deliciasdelvalle.sigeppi.dao.UsuarioDAO;
import pe.deliciasdelvalle.sigeppi.modelo.Rol;
import pe.deliciasdelvalle.sigeppi.modelo.Tienda;
import pe.deliciasdelvalle.sigeppi.modelo.Usuario;
import pe.deliciasdelvalle.sigeppi.util.ConexionBD;

public class UsuarioDAOImpl implements UsuarioDAO {

    private static final String SQL_BUSCAR =
            "SELECT u.usu_id, u.usu_usuario, u.usu_contrasena, u.usu_nombres, u.usu_apellidos, u.usu_estado, "
          + "       u.usu_ultimo_acceso, r.rol_id, r.rol_nombre, t.tie_id, t.tie_nombre "
          + "FROM tb_usuario u "
          + "JOIN tb_rol r ON r.rol_id = u.rol_id "
          + "LEFT JOIN tb_tienda t ON t.tie_id = u.tie_id "
          + "WHERE u.usu_usuario = ?";

    @Override
    public Usuario buscarPorUsuario(String nombreUsuario) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_BUSCAR)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("usu_id"));
                u.setNombreUsuario(rs.getString("usu_usuario"));
                u.setContrasena(rs.getString("usu_contrasena"));
                u.setNombres(rs.getString("usu_nombres"));
                u.setApellidos(rs.getString("usu_apellidos"));
                u.setEstado(rs.getBoolean("usu_estado"));
                Timestamp ts = rs.getTimestamp("usu_ultimo_acceso");
                u.setUltimoAcceso(ts == null ? null : ts.toLocalDateTime());
                Rol rol = new Rol();
                rol.setIdRol(rs.getInt("rol_id"));
                rol.setNombre(rs.getString("rol_nombre"));
                u.setRol(rol);
                int idTienda = rs.getInt("tie_id");
                if (!rs.wasNull()) {
                    Tienda t = new Tienda();
                    t.setIdTienda(idTienda);
                    t.setNombre(rs.getString("tie_nombre"));
                    u.setTienda(t);
                }
                return u;
            }
        }
    }

    @Override
    public void actualizarUltimoAcceso(int idUsuario) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(
                     "UPDATE tb_usuario SET usu_ultimo_acceso = NOW() WHERE usu_id = ?")) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    private static final String SQL_LISTA =
            "SELECT u.usu_id, u.usu_usuario, u.usu_contrasena, u.usu_nombres, u.usu_apellidos, u.usu_estado, "
          + "       u.usu_ultimo_acceso, r.rol_id, r.rol_nombre, t.tie_id, t.tie_nombre "
          + "FROM tb_usuario u JOIN tb_rol r ON r.rol_id = u.rol_id LEFT JOIN tb_tienda t ON t.tie_id = u.tie_id ";

    @Override
    public Usuario buscarPorId(int idUsuario) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_LISTA + "WHERE u.usu_id = ?")) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public List<Usuario> listar() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_LISTA + "ORDER BY r.rol_id, u.usu_usuario");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario u = mapear(rs);
                u.setContrasena(null);
                lista.add(u);
            }
        }
        return lista;
    }

    @Override
    public boolean existeNombreUsuario(String nombreUsuario, int excluirId) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT 1 FROM tb_usuario WHERE LOWER(usu_usuario) = LOWER(?) AND usu_id <> ?")) {
            ps.setString(1, nombreUsuario);
            ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public int insertar(Usuario u) throws SQLException {
        String sql = "INSERT INTO tb_usuario (usu_usuario, usu_contrasena, usu_nombres, usu_apellidos, usu_estado, rol_id, tie_id) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING usu_id";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getContrasena());
            ps.setString(3, u.getNombres());
            ps.setString(4, u.getApellidos());
            ps.setBoolean(5, u.isEstado());
            ps.setInt(6, u.getRol().getIdRol());
            setTienda(ps, 7, u);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    @Override
    public void actualizar(Usuario u) throws SQLException {
        String sql = "UPDATE tb_usuario SET usu_usuario = ?, usu_nombres = ?, usu_apellidos = ?, rol_id = ?, tie_id = ? WHERE usu_id = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getNombres());
            ps.setString(3, u.getApellidos());
            ps.setInt(4, u.getRol().getIdRol());
            setTienda(ps, 5, u);
            ps.setInt(6, u.getIdUsuario());
            ps.executeUpdate();
        }
    }

    @Override
    public void cambiarEstado(int idUsuario, boolean activo) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement("UPDATE tb_usuario SET usu_estado = ? WHERE usu_id = ?")) {
            ps.setBoolean(1, activo);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizarContrasena(int idUsuario, String hash) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement("UPDATE tb_usuario SET usu_contrasena = ? WHERE usu_id = ?")) {
            ps.setString(1, hash);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }

    private void setTienda(PreparedStatement ps, int idx, Usuario u) throws SQLException {
        if (u.getTienda() == null || u.getTienda().getIdTienda() == 0) {
            ps.setNull(idx, Types.INTEGER);
        } else {
            ps.setInt(idx, u.getTienda().getIdTienda());
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("usu_id"));
        u.setNombreUsuario(rs.getString("usu_usuario"));
        u.setContrasena(rs.getString("usu_contrasena"));
        u.setNombres(rs.getString("usu_nombres"));
        u.setApellidos(rs.getString("usu_apellidos"));
        u.setEstado(rs.getBoolean("usu_estado"));
        Timestamp ts = rs.getTimestamp("usu_ultimo_acceso");
        u.setUltimoAcceso(ts == null ? null : ts.toLocalDateTime());
        Rol rol = new Rol();
        rol.setIdRol(rs.getInt("rol_id"));
        rol.setNombre(rs.getString("rol_nombre"));
        u.setRol(rol);
        int idTienda = rs.getInt("tie_id");
        if (!rs.wasNull()) {
            Tienda t = new Tienda();
            t.setIdTienda(idTienda);
            t.setNombre(rs.getString("tie_nombre"));
            u.setTienda(t);
        }
        return u;
    }
}
