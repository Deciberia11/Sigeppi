package pe.deliciasdelvalle.sigeppi.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.modelo.Cliente;

public interface ClienteDAO {

    Cliente buscarPorTelefono(String telefono) throws SQLException;
    int insertar(Connection cn, Cliente cliente) throws SQLException;
    List<Cliente> listar() throws SQLException;

    void actualizar(Cliente c) throws SQLException;
}
