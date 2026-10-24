package pe.deliciasdelvalle.sigeppi.dao.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.dao.InsumoDAO;
import pe.deliciasdelvalle.sigeppi.modelo.Insumo;
import pe.deliciasdelvalle.sigeppi.util.ConexionBD;

public class InsumoDAOImpl implements InsumoDAO {

    @Override
    public Insumo buscarPorId(Connection cn, int idInsumo) throws SQLException {
        String sql = "SELECT ins_id, ins_nombre, ins_unidad, ins_stock_actual, ins_stock_minimo, ins_perecible "
                   + "FROM tb_insumo WHERE ins_id = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idInsumo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Insumo i = new Insumo();
                i.setIdInsumo(rs.getInt("ins_id"));
                i.setNombre(rs.getString("ins_nombre"));
                i.setUnidadMedida(rs.getString("ins_unidad"));
                i.setStockActual(rs.getDouble("ins_stock_actual"));
                i.setStockMinimo(rs.getDouble("ins_stock_minimo"));
                i.setPerecible(rs.getBoolean("ins_perecible"));
                return i;
            }
        }
    }

    @Override
    public void actualizarStock(Connection cn, int idInsumo, double delta) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "UPDATE tb_insumo SET ins_stock_actual = ins_stock_actual + ? WHERE ins_id = ?")) {
            ps.setDouble(1, delta);
            ps.setInt(2, idInsumo);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Insumo> listar() throws SQLException {
        String sql = "SELECT ins_id, ins_nombre, ins_unidad, ins_stock_actual, ins_stock_minimo, ins_perecible "
                   + "FROM tb_insumo ORDER BY ins_nombre";
        List<Insumo> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Insumo i = new Insumo();
                i.setIdInsumo(rs.getInt("ins_id"));
                i.setNombre(rs.getString("ins_nombre"));
                i.setUnidadMedida(rs.getString("ins_unidad"));
                i.setStockActual(rs.getDouble("ins_stock_actual"));
                i.setStockMinimo(rs.getDouble("ins_stock_minimo"));
                i.setPerecible(rs.getBoolean("ins_perecible"));
                lista.add(i);
            }
        }
        return lista;
    }

    @Override
    public int insertar(Insumo i) throws SQLException {
        String sql = "INSERT INTO tb_insumo (ins_nombre, ins_unidad, ins_stock_actual, ins_stock_minimo, ins_perecible) "
                   + "VALUES (?, ?, 0, ?, ?) RETURNING ins_id";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, i.getNombre());
            ps.setString(2, i.getUnidadMedida());
            ps.setDouble(3, i.getStockMinimo());
            ps.setBoolean(4, i.isPerecible());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    @Override
    public void actualizar(Insumo i) throws SQLException {
        // el stock actual NO se edita aquí: solo cambia con ingresos y salidas (kardex)
        String sql = "UPDATE tb_insumo SET ins_nombre = ?, ins_unidad = ?, ins_stock_minimo = ?, ins_perecible = ? WHERE ins_id = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, i.getNombre());
            ps.setString(2, i.getUnidadMedida());
            ps.setDouble(3, i.getStockMinimo());
            ps.setBoolean(4, i.isPerecible());
            ps.setInt(5, i.getIdInsumo());
            ps.executeUpdate();
        }
    }
}
