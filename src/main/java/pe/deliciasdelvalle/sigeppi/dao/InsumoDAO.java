package pe.deliciasdelvalle.sigeppi.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.modelo.Insumo;

public interface InsumoDAO {

    Insumo buscarPorId(Connection cn, int idInsumo) throws SQLException;
    void actualizarStock(Connection cn, int idInsumo, double delta) throws SQLException;
    List<Insumo> listar() throws SQLException;

    int insertar(Insumo insumo) throws SQLException;
    void actualizar(Insumo insumo) throws SQLException;
}
