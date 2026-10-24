package pe.deliciasdelvalle.sigeppi.dao;

import pe.deliciasdelvalle.sigeppi.modelo.HistorialEstado;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.modelo.DetallePedido;
import pe.deliciasdelvalle.sigeppi.modelo.EstadoPedido;
import pe.deliciasdelvalle.sigeppi.modelo.Pedido;

public interface PedidoDAO {

    int insertar(Connection cn, Pedido pedido) throws SQLException;
    void insertarDetalles(Connection cn, int idPedido, List<DetallePedido> detalles) throws SQLException;
    Pedido buscarPorId(int idPedido) throws SQLException;
    List<Pedido> listar(Integer idTienda, EstadoPedido estado) throws SQLException;
    void actualizarEstado(Connection cn, int idPedido, EstadoPedido nuevo) throws SQLException;
    void insertarHistorial(Connection cn, int idPedido, EstadoPedido anterior, EstadoPedido nuevo, int idUsuario) throws SQLException;

    List<DetallePedido> listarDetalles(int idPedido) throws SQLException;
    List<HistorialEstado> listarHistorial(int idPedido) throws SQLException;
}
