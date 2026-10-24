package pe.deliciasdelvalle.sigeppi.dao;

import pe.deliciasdelvalle.sigeppi.modelo.ConsumoInsumoDTO;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.modelo.ReporteVentaDTO;

public interface ReporteDAO {

    /** idTienda = 0 significa todas las tiendas. */
    List<ReporteVentaDTO> ventasPorTiendaYFechas(int idTienda, LocalDate desde, LocalDate hasta) throws SQLException;

    List<ConsumoInsumoDTO> consumoInsumos(LocalDate desde, LocalDate hasta) throws SQLException;
}
