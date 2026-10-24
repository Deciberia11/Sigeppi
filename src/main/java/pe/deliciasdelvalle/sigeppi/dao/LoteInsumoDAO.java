package pe.deliciasdelvalle.sigeppi.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.modelo.LoteInsumo;

public interface LoteInsumoDAO {

    int insertar(Connection cn, LoteInsumo lote) throws SQLException;
    /** Lotes con saldo ordenados por fecha de vencimiento (FEFO). Bloquea las filas con FOR UPDATE. */
    List<LoteInsumo> listarDisponiblesPorVencimiento(Connection cn, int idInsumo) throws SQLException;
    void descontar(Connection cn, int idLote, double cantidad) throws SQLException;

    /** Lotes con saldo (incluye vencidos) con nombre de insumo y proveedor, del más próximo a vencer al más lejano. */
    List<LoteInsumo> listarConSaldo() throws SQLException;
}
