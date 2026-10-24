package pe.deliciasdelvalle.sigeppi.dao;

import java.sql.SQLException;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.modelo.Tienda;

public interface TiendaDAO {

    List<Tienda> listarActivas() throws SQLException;

    List<Tienda> listarTodas() throws SQLException;
    int insertar(Tienda t) throws SQLException;
    void actualizar(Tienda t) throws SQLException;
}
