package pe.deliciasdelvalle.sigeppi.dao;

import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;
import pe.deliciasdelvalle.sigeppi.modelo.SolicitudCompra;

public interface SolicitudCompraDAO {

    boolean existePendiente(Connection cn, int idInsumo) throws SQLException;
    int insertar(Connection cn, SolicitudCompra solicitud) throws SQLException;
    void marcarAtendidas(Connection cn, int idInsumo) throws SQLException;

    List<SolicitudCompra> listar(String estado) throws SQLException;
    void cambiarEstado(int idSolicitud, String estado) throws SQLException;
}
