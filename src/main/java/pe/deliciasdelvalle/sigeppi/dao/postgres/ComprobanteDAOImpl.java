package pe.deliciasdelvalle.sigeppi.dao.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import pe.deliciasdelvalle.sigeppi.dao.ComprobanteDAO;
import pe.deliciasdelvalle.sigeppi.modelo.ComprobanteVenta;

public class ComprobanteDAOImpl implements ComprobanteDAO {

    @Override
    public int siguienteNumero(Connection cn, String serie) throws SQLException {
        // Se bloquea la serie para que dos vendedores no saquen el mismo número
        try (PreparedStatement lock = cn.prepareStatement("SELECT pg_advisory_xact_lock(hashtext(?))")) {
            lock.setString(1, serie);
            lock.execute();
        }
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT COALESCE(MAX(com_numero), 0) + 1 FROM tb_comprobante WHERE com_serie = ?")) {
            ps.setString(1, serie);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    @Override
    public int insertar(Connection cn, ComprobanteVenta c) throws SQLException {
        String sql = "INSERT INTO tb_comprobante (ped_id, com_tipo, com_serie, com_numero, com_metodo_pago, com_total) "
                   + "VALUES (?, ?, ?, ?, ?, ?) RETURNING com_id";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, c.getIdPedido());
            ps.setString(2, c.getTipo());
            ps.setString(3, c.getSerie());
            ps.setInt(4, c.getNumero());
            ps.setString(5, c.getMetodoPago());
            ps.setDouble(6, c.getTotal());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
