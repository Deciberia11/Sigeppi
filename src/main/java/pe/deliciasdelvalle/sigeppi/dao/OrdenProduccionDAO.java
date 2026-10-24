package pe.deliciasdelvalle.sigeppi.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import pe.deliciasdelvalle.sigeppi.modelo.OrdenProduccion;

public interface OrdenProduccionDAO {

    /** Suma las cantidades por producto de los pedidos que siguen en estado REGISTRADO. */
    Map<Integer, Integer> consolidarCantidades(Connection cn, List<Integer> idsPedidos) throws SQLException;
    int insertar(Connection cn, OrdenProduccion orden) throws SQLException;
    void asignarPedidos(Connection cn, int idOrden, List<Integer> idsPedidos) throws SQLException;

    List<OrdenProduccion> listar(int limite) throws SQLException;
    OrdenProduccion buscarPorId(int idOrden) throws SQLException;
    List<Integer> pedidosEnProduccion(Connection cn, int idOrden) throws SQLException;
    void terminar(Connection cn, int idOrden) throws SQLException;
}
