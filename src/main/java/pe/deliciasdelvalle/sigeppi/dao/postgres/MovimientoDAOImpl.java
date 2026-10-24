package pe.deliciasdelvalle.sigeppi.dao.postgres;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.dao.MovimientoDAO;
import pe.deliciasdelvalle.sigeppi.modelo.MovimientoInsumo;
import pe.deliciasdelvalle.sigeppi.util.ConexionBD;

public class MovimientoDAOImpl implements MovimientoDAO {

    @Override
    public void insertar(Connection cn, MovimientoInsumo m) throws SQLException {
        String sql = "INSERT INTO tb_movimiento_insumo (mov_tipo, mov_cantidad, mov_motivo, lot_id, ord_id, usu_id) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, m.getTipo());
            ps.setDouble(2, m.getCantidad());
            ps.setString(3, m.getMotivo());
            ps.setInt(4, m.getIdLote());
            if (m.getIdOrden() == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, m.getIdOrden());
            }
            ps.setInt(6, m.getIdUsuario());
            ps.executeUpdate();
        }
    }

    @Override
    public List<MovimientoInsumo> listarSalidas(LocalDate desde, LocalDate hasta) throws SQLException {
        String sql = "SELECT mov_id, mov_cantidad, mov_motivo, lot_id, ord_id, usu_id, mov_fecha "
                   + "FROM tb_movimiento_insumo WHERE mov_tipo = 'SALIDA' "
                   + "AND mov_fecha >= ? AND mov_fecha < ? ORDER BY mov_fecha DESC";
        List<MovimientoInsumo> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hasta.plusDays(1)));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MovimientoInsumo m = new MovimientoInsumo();
                    m.setIdMovimiento(rs.getInt("mov_id"));
                    m.setTipo("SALIDA");
                    m.setCantidad(rs.getDouble("mov_cantidad"));
                    m.setMotivo(rs.getString("mov_motivo"));
                    m.setIdLote(rs.getInt("lot_id"));
                    int idOrden = rs.getInt("ord_id");
                    m.setIdOrden(rs.wasNull() ? null : idOrden);
                    m.setIdUsuario(rs.getInt("usu_id"));
                    m.setFecha(rs.getTimestamp("mov_fecha").toLocalDateTime());
                    lista.add(m);
                }
            }
        }
        return lista;
    }
}
