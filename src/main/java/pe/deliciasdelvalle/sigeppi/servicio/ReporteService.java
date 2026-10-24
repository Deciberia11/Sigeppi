package pe.deliciasdelvalle.sigeppi.servicio;

import pe.deliciasdelvalle.sigeppi.modelo.Usuario;
import pe.deliciasdelvalle.sigeppi.modelo.Rol;
import pe.deliciasdelvalle.sigeppi.modelo.RegistroAuditoria;
import pe.deliciasdelvalle.sigeppi.modelo.ConsumoInsumoDTO;
import pe.deliciasdelvalle.sigeppi.dao.PanelDAO;
import pe.deliciasdelvalle.sigeppi.dao.AuditoriaDAO;
import java.util.Map;
import java.util.LinkedHashMap;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.dao.DAOFactory;
import pe.deliciasdelvalle.sigeppi.dao.ReporteDAO;
import pe.deliciasdelvalle.sigeppi.dao.TiendaDAO;
import pe.deliciasdelvalle.sigeppi.modelo.Tienda;
import pe.deliciasdelvalle.sigeppi.modelo.NegocioException;
import pe.deliciasdelvalle.sigeppi.modelo.ReporteVentaDTO;
import pe.deliciasdelvalle.sigeppi.modelo.ReporteVentas;

/** CUS07 - Generar Reporte de Ventas. */
public class ReporteService {

    private final ReporteDAO reporteDAO;
    private final TiendaDAO tiendaDAO;

    public ReporteService() {
        DAOFactory f = DAOFactory.getDAOFactory(DAOFactory.POSTGRES);
        this.reporteDAO = f.getReporteDAO();
        this.tiendaDAO = f.getTiendaDAO();
    }

    public List<Tienda> listarTiendas() throws SQLException {
        return tiendaDAO.listarActivas();
    }

    public ReporteVentas generarReporteVentas(int idTienda, LocalDate desde, LocalDate hasta)
            throws NegocioException, SQLException {
        validarRango(desde, hasta);
        List<ReporteVentaDTO> filas = reporteDAO.ventasPorTiendaYFechas(idTienda, desde, hasta);
        ReporteVentas reporte = new ReporteVentas();
        reporte.setFilas(filas);
        reporte.setTotalGeneral(filas.stream().mapToDouble(ReporteVentaDTO::getTotalVendido).sum());
        return reporte;   // si está vacío la vista muestra "No se encontraron ventas" (ESC19)
    }

    void validarRango(LocalDate desde, LocalDate hasta) throws NegocioException {
        if (desde == null || hasta == null) {
            throw new NegocioException("Seleccione la fecha inicial y la fecha final.");
        }
        if (desde.isAfter(hasta)) {                                                    // ESC18
            throw new NegocioException("La fecha inicial no puede ser mayor a la fecha final.");
        }
    }

    // ------------------------------------------------------------------ consumo histórico de insumos (PAQ05)
    public List<ConsumoInsumoDTO> consumoInsumos(LocalDate desde, LocalDate hasta) throws NegocioException, SQLException {
        validarRango(desde, hasta);
        List<ConsumoInsumoDTO> filas = reporteDAO.consumoInsumos(desde, hasta);
        double max = filas.stream().mapToDouble(ConsumoInsumoDTO::getCantidad).max().orElse(0);
        for (ConsumoInsumoDTO c : filas) {
            c.setPorcentaje(max == 0 ? 0 : Math.round(c.getCantidad() * 100 / max));
        }
        return filas;
    }

    // ------------------------------------------------------------------ auditoría (RNF-03)
    public List<RegistroAuditoria> auditoria(String modulo, String texto) throws SQLException {
        AuditoriaDAO dao = DAOFactory.getDAOFactory(DAOFactory.POSTGRES).getAuditoriaDAO();
        return dao.listar(modulo, texto, 300);
    }

    // ------------------------------------------------------------------ panel de inicio (panel de control gerencial)
    public Map<String, Object> panel(Usuario u) throws SQLException {
        PanelDAO panel = DAOFactory.getDAOFactory(DAOFactory.POSTGRES).getPanelDAO();
        Map<String, Object> datos = new LinkedHashMap<>();
        String rol = u.getRol().getNombre();
        Integer tienda = Rol.VENDEDOR.equals(rol) && u.getTienda() != null ? u.getTienda().getIdTienda() : null;

        if (Rol.ADMINISTRADOR.equals(rol) || Rol.VENDEDOR.equals(rol)) {
            datos.put("ventasDia", panel.ventasDelDia(tienda));
            datos.put("ventasMes", panel.ventasDelMes(tienda));
            datos.put("estados", panel.pedidosPorEstado(tienda));
            datos.put("especiales", panel.pedidosEspecialesProximos(tienda, 7));
        }
        if (Rol.ADMINISTRADOR.equals(rol)) {
            datos.put("porTienda", conPorcentaje(panel.ventasPorTiendaMes(), "total"));
            datos.put("ultimosDias", conPorcentaje(panel.ventasUltimosDias(7), "total"));
            datos.put("topProductos", panel.productosMasVendidosMes(5));
        }
        if (Rol.JEFE_PRODUCCION.equals(rol)) {
            datos.put("estados", panel.pedidosPorEstado(null));
            datos.put("especiales", panel.pedidosEspecialesProximos(null, 3));
        }
        if (!Rol.VENDEDOR.equals(rol)) {
            datos.put("bajoMinimo", panel.insumosBajoMinimo());
        }
        if (Rol.ENCARGADO_ALMACEN.equals(rol) || Rol.ADMINISTRADOR.equals(rol) || Rol.JEFE_PRODUCCION.equals(rol)) {
            datos.put("porVencer", panel.lotesPorVencer(7));
            datos.put("solicitudesPendientes", panel.solicitudesPendientes());
        }
        return datos;
    }

    /** Agrega la columna "pct" (0-100) para dibujar barras en la vista. */
    private List<Map<String, Object>> conPorcentaje(List<Map<String, Object>> filas, String columna) {
        double max = 0;
        for (Map<String, Object> f : filas) {
            max = Math.max(max, ((Number) f.get(columna)).doubleValue());
        }
        for (Map<String, Object> f : filas) {
            double v = ((Number) f.get(columna)).doubleValue();
            f.put("pct", max == 0 ? 0 : Math.round(v * 100 / max));
        }
        return filas;
    }
}
