package pe.deliciasdelvalle.sigeppi.dao;

import java.sql.Connection;
import java.sql.SQLException;
import pe.deliciasdelvalle.sigeppi.modelo.ComprobanteVenta;

public interface ComprobanteDAO {

    int siguienteNumero(Connection cn, String serie) throws SQLException;
    int insertar(Connection cn, ComprobanteVenta comprobante) throws SQLException;
}
