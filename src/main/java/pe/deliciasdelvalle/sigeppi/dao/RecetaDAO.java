package pe.deliciasdelvalle.sigeppi.dao;

import pe.deliciasdelvalle.sigeppi.modelo.RecetaItem;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public interface RecetaDAO {

    /** RN004: devuelve idInsumo -> cantidad requerida para producir las cantidades indicadas (idProducto -> cantidad). */
    Map<Integer, Double> calcularConsumo(Connection cn, Map<Integer, Integer> cantidadesPorProducto) throws SQLException;

    List<RecetaItem> listarPorProducto(int idProducto) throws SQLException;
    void guardar(int idProducto, int idInsumo, double cantidad) throws SQLException;
    void eliminar(int idReceta) throws SQLException;
}
