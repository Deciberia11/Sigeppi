package pe.deliciasdelvalle.sigeppi.dao.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.dao.ProductoDAO;
import pe.deliciasdelvalle.sigeppi.modelo.Producto;
import pe.deliciasdelvalle.sigeppi.util.ConexionBD;

public class ProductoDAOImpl implements ProductoDAO {

    private static final String SQL_SELECT =
            "SELECT pro_id, pro_nombre, pro_descripcion, pro_precio, pro_tipo, pro_tiempo_prep, pro_estado FROM tb_producto ";

    @Override
    public Producto buscarPorId(int idProducto) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_SELECT + "WHERE pro_id = ?")) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public List<Producto> listarActivos() throws SQLException {
        return listar(SQL_SELECT + "WHERE pro_estado ORDER BY pro_nombre");
    }

    @Override
    public List<Producto> listarTodos() throws SQLException {
        return listar(SQL_SELECT + "ORDER BY pro_estado DESC, pro_nombre");
    }

    @Override
    public int insertar(Producto p) throws SQLException {
        String sql = "INSERT INTO tb_producto (pro_nombre, pro_descripcion, pro_precio, pro_tipo, pro_tiempo_prep, pro_estado) "
                   + "VALUES (?, ?, ?, ?, ?, ?) RETURNING pro_id";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            llenar(ps, p);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    @Override
    public void actualizar(Producto p) throws SQLException {
        String sql = "UPDATE tb_producto SET pro_nombre = ?, pro_descripcion = ?, pro_precio = ?, pro_tipo = ?, "
                   + "pro_tiempo_prep = ?, pro_estado = ? WHERE pro_id = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            llenar(ps, p);
            ps.setInt(7, p.getIdProducto());
            ps.executeUpdate();
        }
    }

    private void llenar(PreparedStatement ps, Producto p) throws SQLException {
        ps.setString(1, p.getNombre());
        ps.setString(2, p.getDescripcion());
        ps.setDouble(3, p.getPrecioUnitario());
        ps.setString(4, p.getTipoProducto());
        ps.setInt(5, p.getTiempoPreparacion());
        ps.setBoolean(6, p.isEstado());
    }

    private List<Producto> listar(String sql) throws SQLException {
        List<Producto> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setIdProducto(rs.getInt("pro_id"));
        p.setNombre(rs.getString("pro_nombre"));
        p.setDescripcion(rs.getString("pro_descripcion"));
        p.setPrecioUnitario(rs.getDouble("pro_precio"));
        p.setTipoProducto(rs.getString("pro_tipo"));
        p.setTiempoPreparacion(rs.getInt("pro_tiempo_prep"));
        p.setEstado(rs.getBoolean("pro_estado"));
        return p;
    }
}
