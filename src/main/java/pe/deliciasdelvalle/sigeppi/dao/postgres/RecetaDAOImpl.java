package pe.deliciasdelvalle.sigeppi.dao.postgres;

import pe.deliciasdelvalle.sigeppi.util.ConexionBD;
import pe.deliciasdelvalle.sigeppi.modelo.RecetaItem;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import pe.deliciasdelvalle.sigeppi.dao.RecetaDAO;

public class RecetaDAOImpl implements RecetaDAO {

    @Override
    public Map<Integer, Double> calcularConsumo(Connection cn, Map<Integer, Integer> cantidadesPorProducto) throws SQLException {
        Map<Integer, Double> consumo = new HashMap<>();
        String sql = "SELECT ins_id, rec_cantidad FROM tb_receta WHERE pro_id = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            for (Map.Entry<Integer, Integer> e : cantidadesPorProducto.entrySet()) {
                ps.setInt(1, e.getKey());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        // RN004: receta estándar x cantidad a producir
                        double requerido = rs.getDouble("rec_cantidad") * e.getValue();
                        consumo.merge(rs.getInt("ins_id"), requerido, Double::sum);
                    }
                }
            }
        }
        return consumo;
    }

    @Override
    public List<RecetaItem> listarPorProducto(int idProducto) throws SQLException {
        String sql = "SELECT r.rec_id, i.ins_id, i.ins_nombre, i.ins_unidad, r.rec_cantidad FROM tb_receta r "
                   + "JOIN tb_insumo i ON i.ins_id = r.ins_id WHERE r.pro_id = ? ORDER BY i.ins_nombre";
        List<RecetaItem> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RecetaItem r = new RecetaItem();
                    r.setIdReceta(rs.getInt("rec_id"));
                    r.setIdInsumo(rs.getInt("ins_id"));
                    r.setInsumo(rs.getString("ins_nombre"));
                    r.setUnidad(rs.getString("ins_unidad"));
                    r.setCantidad(rs.getDouble("rec_cantidad"));
                    lista.add(r);
                }
            }
        }
        return lista;
    }

    @Override
    public void guardar(int idProducto, int idInsumo, double cantidad) throws SQLException {
        // si el insumo ya está en la receta, se actualiza la cantidad
        String sql = "INSERT INTO tb_receta (pro_id, ins_id, rec_cantidad) VALUES (?, ?, ?) "
                   + "ON CONFLICT (pro_id, ins_id) DO UPDATE SET rec_cantidad = EXCLUDED.rec_cantidad";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.setInt(2, idInsumo);
            ps.setDouble(3, cantidad);
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int idReceta) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM tb_receta WHERE rec_id = ?")) {
            ps.setInt(1, idReceta);
            ps.executeUpdate();
        }
    }
}
