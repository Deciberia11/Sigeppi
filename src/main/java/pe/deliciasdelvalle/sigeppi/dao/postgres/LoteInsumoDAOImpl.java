package pe.deliciasdelvalle.sigeppi.dao.postgres;

import pe.deliciasdelvalle.sigeppi.util.ConexionBD;
import pe.deliciasdelvalle.sigeppi.modelo.Proveedor;
import pe.deliciasdelvalle.sigeppi.modelo.Insumo;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.dao.LoteInsumoDAO;
import pe.deliciasdelvalle.sigeppi.modelo.LoteInsumo;

public class LoteInsumoDAOImpl implements LoteInsumoDAO {

    @Override
    public int insertar(Connection cn, LoteInsumo l) throws SQLException {
        String sql = "INSERT INTO tb_lote_insumo (lot_codigo, ins_id, prv_id, lot_fecha_ingreso, lot_fecha_vencimiento, "
                   + "lot_cantidad_inicial, lot_cantidad_disponible, lot_costo_unitario) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING lot_id";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, l.getCodigo());
            ps.setInt(2, l.getInsumo().getIdInsumo());
            ps.setInt(3, l.getProveedor().getIdProveedor());
            ps.setDate(4, Date.valueOf(l.getFechaIngreso()));
            ps.setDate(5, Date.valueOf(l.getFechaVencimiento()));
            ps.setDouble(6, l.getCantidadInicial());
            ps.setDouble(7, l.getCantidadInicial());
            ps.setDouble(8, l.getCostoUnitario());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    @Override
    public List<LoteInsumo> listarDisponiblesPorVencimiento(Connection cn, int idInsumo) throws SQLException {
        String sql = "SELECT lot_id, lot_codigo, lot_fecha_vencimiento, lot_cantidad_disponible "
                   + "FROM tb_lote_insumo WHERE ins_id = ? AND lot_cantidad_disponible > 0 "
                   + "AND lot_fecha_vencimiento >= CURRENT_DATE "
                   + "ORDER BY lot_fecha_vencimiento ASC FOR UPDATE";
        List<LoteInsumo> lotes = new ArrayList<>();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idInsumo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoteInsumo l = new LoteInsumo();
                    l.setIdLote(rs.getInt("lot_id"));
                    l.setCodigo(rs.getString("lot_codigo"));
                    l.setFechaVencimiento(rs.getDate("lot_fecha_vencimiento").toLocalDate());
                    l.setCantidadDisponible(rs.getDouble("lot_cantidad_disponible"));
                    lotes.add(l);
                }
            }
        }
        return lotes;
    }

    @Override
    public void descontar(Connection cn, int idLote, double cantidad) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "UPDATE tb_lote_insumo SET lot_cantidad_disponible = lot_cantidad_disponible - ? WHERE lot_id = ?")) {
            ps.setDouble(1, cantidad);
            ps.setInt(2, idLote);
            ps.executeUpdate();
        }
    }

    @Override
    public List<LoteInsumo> listarConSaldo() throws SQLException {
        String sql = "SELECT l.lot_id, l.lot_codigo, l.lot_fecha_ingreso, l.lot_fecha_vencimiento, l.lot_cantidad_inicial, "
                   + "l.lot_cantidad_disponible, l.lot_costo_unitario, i.ins_id, i.ins_nombre, i.ins_unidad, p.prv_razon_social "
                   + "FROM tb_lote_insumo l JOIN tb_insumo i ON i.ins_id = l.ins_id JOIN tb_proveedor p ON p.prv_id = l.prv_id "
                   + "WHERE l.lot_cantidad_disponible > 0 ORDER BY l.lot_fecha_vencimiento, i.ins_nombre";
        List<LoteInsumo> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LoteInsumo l = new LoteInsumo();
                l.setIdLote(rs.getInt("lot_id"));
                l.setCodigo(rs.getString("lot_codigo"));
                l.setFechaIngreso(rs.getDate("lot_fecha_ingreso").toLocalDate());
                l.setFechaVencimiento(rs.getDate("lot_fecha_vencimiento").toLocalDate());
                l.setCantidadInicial(rs.getDouble("lot_cantidad_inicial"));
                l.setCantidadDisponible(rs.getDouble("lot_cantidad_disponible"));
                l.setCostoUnitario(rs.getDouble("lot_costo_unitario"));
                Insumo i = new Insumo();
                i.setIdInsumo(rs.getInt("ins_id"));
                i.setNombre(rs.getString("ins_nombre"));
                i.setUnidadMedida(rs.getString("ins_unidad"));
                l.setInsumo(i);
                Proveedor p = new Proveedor();
                p.setRazonSocial(rs.getString("prv_razon_social"));
                l.setProveedor(p);
                lista.add(l);
            }
        }
        return lista;
    }
}
