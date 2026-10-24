package pe.deliciasdelvalle.sigeppi.dao.postgres;

import pe.deliciasdelvalle.sigeppi.modelo.ConsumoInsumoDTO;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.dao.ReporteDAO;
import pe.deliciasdelvalle.sigeppi.modelo.ReporteVentaDTO;
import pe.deliciasdelvalle.sigeppi.util.ConexionBD;

public class ReporteDAOImpl implements ReporteDAO {

    private static final String SQL =
            "SELECT t.tie_nombre, CAST(c.com_fecha_emision AS DATE) AS fecha, COUNT(*) AS nro, SUM(c.com_total) AS total "
          + "FROM tb_pedido p "
          + "JOIN tb_comprobante c ON c.ped_id = p.ped_id "
          + "JOIN tb_tienda t ON t.tie_id = p.tie_id "
          + "WHERE (? = 0 OR p.tie_id = ?) AND c.com_fecha_emision >= ? AND c.com_fecha_emision < ? "
          + "GROUP BY t.tie_nombre, CAST(c.com_fecha_emision AS DATE) "
          + "ORDER BY fecha, t.tie_nombre";

    @Override
    public List<ReporteVentaDTO> ventasPorTiendaYFechas(int idTienda, LocalDate desde, LocalDate hasta) throws SQLException {
        List<ReporteVentaDTO> filas = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL)) {
            ps.setInt(1, idTienda);
            ps.setInt(2, idTienda);
            ps.setDate(3, Date.valueOf(desde));
            ps.setDate(4, Date.valueOf(hasta.plusDays(1))); // incluye todo el día final
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReporteVentaDTO f = new ReporteVentaDTO();
                    f.setTienda(rs.getString("tie_nombre"));
                    f.setFecha(rs.getDate("fecha").toLocalDate());
                    f.setNroPedidos(rs.getInt("nro"));
                    f.setTotalVendido(rs.getDouble("total"));
                    filas.add(f);
                }
            }
        }
        return filas;
    }

    @Override
    public List<ConsumoInsumoDTO> consumoInsumos(LocalDate desde, LocalDate hasta) throws SQLException {
        String sql = "SELECT i.ins_nombre, i.ins_unidad, SUM(m.mov_cantidad) AS cantidad, COUNT(DISTINCT m.ord_id) AS ordenes "
                   + "FROM tb_movimiento_insumo m JOIN tb_lote_insumo l ON l.lot_id = m.lot_id "
                   + "JOIN tb_insumo i ON i.ins_id = l.ins_id "
                   + "WHERE m.mov_tipo = 'SALIDA' AND m.mov_fecha >= ? AND m.mov_fecha < ? "
                   + "GROUP BY i.ins_nombre, i.ins_unidad ORDER BY cantidad DESC";
        List<ConsumoInsumoDTO> filas = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hasta.plusDays(1)));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ConsumoInsumoDTO c = new ConsumoInsumoDTO();
                    c.setInsumo(rs.getString("ins_nombre"));
                    c.setUnidad(rs.getString("ins_unidad"));
                    c.setCantidad(rs.getDouble("cantidad"));
                    c.setNroOrdenes(rs.getInt("ordenes"));
                    filas.add(c);
                }
            }
        }
        return filas;
    }
}
