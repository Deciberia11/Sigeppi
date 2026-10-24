package pe.deliciasdelvalle.sigeppi.servicio;

import pe.deliciasdelvalle.sigeppi.modelo.SolicitudCompra;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import pe.deliciasdelvalle.sigeppi.dao.AuditoriaDAO;
import pe.deliciasdelvalle.sigeppi.dao.DAOFactory;
import pe.deliciasdelvalle.sigeppi.dao.InsumoDAO;
import pe.deliciasdelvalle.sigeppi.dao.LoteInsumoDAO;
import pe.deliciasdelvalle.sigeppi.dao.MovimientoDAO;
import pe.deliciasdelvalle.sigeppi.dao.ProveedorDAO;
import pe.deliciasdelvalle.sigeppi.dao.SolicitudCompraDAO;
import pe.deliciasdelvalle.sigeppi.modelo.Faltante;
import pe.deliciasdelvalle.sigeppi.modelo.Insumo;
import pe.deliciasdelvalle.sigeppi.modelo.LoteInsumo;
import pe.deliciasdelvalle.sigeppi.modelo.MovimientoInsumo;
import pe.deliciasdelvalle.sigeppi.modelo.NegocioException;
import pe.deliciasdelvalle.sigeppi.modelo.Proveedor;
import pe.deliciasdelvalle.sigeppi.modelo.Usuario;
import pe.deliciasdelvalle.sigeppi.util.ConexionBD;

/** CUS05 - Registrar Salida de Insumos por Producción y CUS06 - Registrar Ingreso de Insumos. */
public class InventarioService {

    private final InsumoDAO insumoDAO;
    private final LoteInsumoDAO loteDAO;
    private final MovimientoDAO movimientoDAO;
    private final SolicitudCompraDAO solicitudDAO;
    private final AuditoriaDAO auditoriaDAO;
    private final ProveedorDAO proveedorDAO;

    public InventarioService() {
        DAOFactory f = DAOFactory.getDAOFactory(DAOFactory.POSTGRES);
        this.insumoDAO = f.getInsumoDAO();
        this.loteDAO = f.getLoteInsumoDAO();
        this.movimientoDAO = f.getMovimientoDAO();
        this.solicitudDAO = f.getSolicitudCompraDAO();
        this.auditoriaDAO = f.getAuditoriaDAO();
        this.proveedorDAO = f.getProveedorDAO();
    }

    public List<Insumo> listarInsumos() throws SQLException {
        return insumoDAO.listar();
    }

    public List<Proveedor> listarProveedores() throws SQLException {
        return proveedorDAO.listar();
    }

    public List<Faltante> obtenerFaltantes(Connection cn, Map<Integer, Double> consumo) throws SQLException {
        List<Faltante> faltantes = new ArrayList<>();
        for (Map.Entry<Integer, Double> e : consumo.entrySet()) {
            Insumo insumo = insumoDAO.buscarPorId(cn, e.getKey());
            if (insumo.getStockActual() < e.getValue()) {
                Faltante f = new Faltante();
                f.setInsumo(insumo);
                f.setRequerido(e.getValue());
                f.setDisponible(insumo.getStockActual());
                faltantes.add(f);
            }
        }
        return faltantes;
    }

    // ------------------------------------------------------------------ CUS05
    /** Se ejecuta dentro de la transacción de la orden. Descuenta por lotes FEFO (primero vence, primero sale). */
    public List<String> registrarSalidaPorProduccion(Connection cn, int idOrden, Map<Integer, Double> consumo,
                                                     Usuario usuario) throws SQLException {
        List<String> alertas = new ArrayList<>();
        for (Map.Entry<Integer, Double> e : consumo.entrySet()) {
            int idInsumo = e.getKey();
            double pendiente = e.getValue();
            double descontado = 0;
            for (LoteInsumo lote : loteDAO.listarDisponiblesPorVencimiento(cn, idInsumo)) {
                if (pendiente <= 0) {
                    break;
                }
                double aDescontar = Math.min(pendiente, lote.getCantidadDisponible());
                loteDAO.descontar(cn, lote.getIdLote(), aDescontar);

                MovimientoInsumo mov = new MovimientoInsumo();
                mov.setTipo("SALIDA");
                mov.setIdLote(lote.getIdLote());
                mov.setIdOrden(idOrden);
                mov.setIdUsuario(usuario.getIdUsuario());
                mov.setCantidad(aDescontar);
                mov.setMotivo("Producción - orden " + idOrden);
                movimientoDAO.insertar(cn, mov);

                pendiente -= aDescontar;
                descontado += aDescontar;
            }
            // ESC10: si no alcanzó, se descuenta solo lo disponible (la compra ya está pendiente)
            insumoDAO.actualizarStock(cn, idInsumo, -descontado);

            Insumo insumo = insumoDAO.buscarPorId(cn, idInsumo);
            if (insumo.estaBajoMinimo()) {                                             // ESC13
                alertas.add("El insumo " + insumo.getNombre() + " quedó por debajo del stock mínimo ("
                        + insumo.getStockActual() + " " + insumo.getUnidadMedida() + ").");
            }
        }
        return alertas;
    }

    public List<MovimientoInsumo> listarSalidas(LocalDate desde, LocalDate hasta) throws SQLException {
        return movimientoDAO.listarSalidas(desde, hasta);
    }

    // ------------------------------------------------------------------ CUS06
    public int registrarIngreso(LoteInsumo lote, Usuario usuario, String ip) throws NegocioException, SQLException {
        validar(lote);
        try (Connection cn = ConexionBD.getInstancia().getConexion()) {
            cn.setAutoCommit(false);
            try {
                int idLote = loteDAO.insertar(cn, lote);

                MovimientoInsumo mov = new MovimientoInsumo();
                mov.setTipo("INGRESO");
                mov.setIdLote(idLote);
                mov.setIdUsuario(usuario.getIdUsuario());
                mov.setCantidad(lote.getCantidadInicial());
                mov.setMotivo("Compra a proveedor");
                movimientoDAO.insertar(cn, mov);

                insumoDAO.actualizarStock(cn, lote.getInsumo().getIdInsumo(), lote.getCantidadInicial());
                solicitudDAO.marcarAtendidas(cn, lote.getInsumo().getIdInsumo());
                auditoriaDAO.registrar(cn, usuario.getIdUsuario(), ip, "INVENTARIO", "INGRESO_STOCK", "lot_id=" + idLote);
                cn.commit();
                return idLote;
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    void validar(LoteInsumo lote) throws NegocioException {
        if (lote.getInsumo() == null || lote.getProveedor() == null) {
            throw new NegocioException("Seleccione el insumo y el proveedor.");
        }
        if (lote.getCantidadInicial() <= 0 || lote.getCostoUnitario() < 0) {                   // RN005 - ESC16
            throw new NegocioException("La cantidad debe ser mayor a 0 y el costo no puede ser negativo (RN005).");
        }
        if (lote.getFechaIngreso() == null || lote.getFechaVencimiento() == null
                || !lote.getFechaVencimiento().isAfter(lote.getFechaIngreso())) {             // RN006 - ESC15
            throw new NegocioException("La fecha de vencimiento debe ser posterior a la fecha de ingreso (RN006).");
        }
    }

    // ------------------------------------------------------------------ stock y lotes (control de stock)
    public List<LoteInsumo> listarLotesConSaldo() throws SQLException {
        return loteDAO.listarConSaldo();
    }

    // ------------------------------------------------------------------ solicitudes de compra (gestión de abastecimiento)
    public List<SolicitudCompra> listarSolicitudes(String estado) throws SQLException {
        return solicitudDAO.listar(estado);
    }

    public void crearSolicitud(SolicitudCompra s, Usuario usuario, String ip) throws NegocioException, SQLException {
        if (s.getCantidad() <= 0) {
            throw new NegocioException("La cantidad a solicitar debe ser mayor a 0 (RN005).");
        }
        s.setIdUsuario(usuario.getIdUsuario());
        if (s.getMotivo() == null || s.getMotivo().isBlank()) {
            s.setMotivo("Reposición de stock");
        }
        try (Connection cn = ConexionBD.getInstancia().getConexion()) {
            int id = solicitudDAO.insertar(cn, s);
            auditoriaDAO.registrar(cn, usuario.getIdUsuario(), ip, "INVENTARIO", "SOLICITUD_CREADA", "sol_id=" + id);
        }
    }

    public void cambiarEstadoSolicitud(int idSolicitud, String estado, Usuario usuario, String ip)
            throws NegocioException, SQLException {
        if (!"ATENDIDA".equals(estado) && !"ANULADA".equals(estado)) {
            throw new NegocioException("Estado de solicitud no válido.");
        }
        solicitudDAO.cambiarEstado(idSolicitud, estado);
        auditoriaDAO.registrar(usuario.getIdUsuario(), ip, "INVENTARIO", "SOLICITUD_" + estado, "sol_id=" + idSolicitud);
    }
}
