package pe.deliciasdelvalle.sigeppi.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.modelo.MovimientoInsumo;

public interface MovimientoDAO {

    void insertar(Connection cn, MovimientoInsumo movimiento) throws SQLException;
    List<MovimientoInsumo> listarSalidas(LocalDate desde, LocalDate hasta) throws SQLException;
}
