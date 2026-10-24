package pe.deliciasdelvalle.sigeppi.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** Consultas de resumen para el panel de inicio de cada rol (panel de control gerencial). */
public interface PanelDAO {

    /** idTienda null = todas las tiendas. Devuelve total y cantidad de ventas. */
    Map<String, Object> ventasDelDia(Integer idTienda) throws SQLException;
    Map<String, Object> ventasDelMes(Integer idTienda) throws SQLException;
    List<Map<String, Object>> pedidosPorEstado(Integer idTienda) throws SQLException;
    List<Map<String, Object>> ventasPorTiendaMes() throws SQLException;
    List<Map<String, Object>> ventasUltimosDias(int dias) throws SQLException;
    List<Map<String, Object>> productosMasVendidosMes(int limite) throws SQLException;
    List<Map<String, Object>> insumosBajoMinimo() throws SQLException;
    List<Map<String, Object>> lotesPorVencer(int dias) throws SQLException;
    int solicitudesPendientes() throws SQLException;
    List<Map<String, Object>> pedidosEspecialesProximos(Integer idTienda, int dias) throws SQLException;
}
