package pe.deliciasdelvalle.sigeppi.dao.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.dao.TiendaDAO;
import pe.deliciasdelvalle.sigeppi.modelo.Tienda;
import pe.deliciasdelvalle.sigeppi.util.ConexionBD;

public class TiendaDAOImpl implements TiendaDAO {

    private static final String SQL_SELECT = "SELECT tie_id, tie_nombre, tie_direccion, tie_distrito, tie_estado FROM tb_tienda ";

    @Override
    public List<Tienda> listarActivas() throws SQLException {
        return listar(SQL_SELECT + "WHERE tie_estado ORDER BY tie_nombre");
    }

    @Override
    public List<Tienda> listarTodas() throws SQLException {
        return listar(SQL_SELECT + "ORDER BY tie_estado DESC, tie_nombre");
    }

    @Override
    public int insertar(Tienda t) throws SQLException {
        String sql = "INSERT INTO tb_tienda (tie_nombre, tie_direccion, tie_distrito, tie_estado) VALUES (?, ?, ?, ?) RETURNING tie_id";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, t.getNombre());
            ps.setString(2, t.getDireccion());
            ps.setString(3, t.getDistrito());
            ps.setBoolean(4, t.isEstado());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    @Override
    public void actualizar(Tienda t) throws SQLException {
        String sql = "UPDATE tb_tienda SET tie_nombre = ?, tie_direccion = ?, tie_distrito = ?, tie_estado = ? WHERE tie_id = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, t.getNombre());
            ps.setString(2, t.getDireccion());
            ps.setString(3, t.getDistrito());
            ps.setBoolean(4, t.isEstado());
            ps.setInt(5, t.getIdTienda());
            ps.executeUpdate();
        }
    }

    private List<Tienda> listar(String sql) throws SQLException {
        List<Tienda> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Tienda t = new Tienda();
                t.setIdTienda(rs.getInt("tie_id"));
                t.setNombre(rs.getString("tie_nombre"));
                t.setDireccion(rs.getString("tie_direccion"));
                t.setDistrito(rs.getString("tie_distrito"));
                t.setEstado(rs.getBoolean("tie_estado"));
                lista.add(t);
            }
        }
        return lista;
    }
}
