package pe.deliciasdelvalle.sigeppi.dao;

import java.sql.SQLException;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.modelo.Proveedor;

public interface ProveedorDAO {

    List<Proveedor> listar() throws SQLException;

    int insertar(Proveedor p) throws SQLException;
    void actualizar(Proveedor p) throws SQLException;
}
