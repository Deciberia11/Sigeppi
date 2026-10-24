package pe.deliciasdelvalle.sigeppi.dao;

import java.sql.SQLException;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.modelo.Producto;

public interface ProductoDAO {

    Producto buscarPorId(int idProducto) throws SQLException;
    List<Producto> listarActivos() throws SQLException;

    List<Producto> listarTodos() throws SQLException;
    int insertar(Producto p) throws SQLException;
    void actualizar(Producto p) throws SQLException;
}
