package pe.deliciasdelvalle.sigeppi.servicio;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import pe.deliciasdelvalle.sigeppi.dao.AuditoriaDAO;
import pe.deliciasdelvalle.sigeppi.dao.DAOFactory;
import pe.deliciasdelvalle.sigeppi.dao.OrdenProduccionDAO;
import pe.deliciasdelvalle.sigeppi.dao.PedidoDAO;
import pe.deliciasdelvalle.sigeppi.dao.RecetaDAO;
import pe.deliciasdelvalle.sigeppi.dao.SolicitudCompraDAO;
import pe.deliciasdelvalle.sigeppi.modelo.DetalleOrden;
import pe.deliciasdelvalle.sigeppi.modelo.EstadoPedido;
import pe.deliciasdelvalle.sigeppi.modelo.Faltante;
import pe.deliciasdelvalle.sigeppi.modelo.InsumosInsuficientesException;
import pe.deliciasdelvalle.sigeppi.modelo.NegocioException;
import pe.deliciasdelvalle.sigeppi.modelo.OrdenProduccion;
import pe.deliciasdelvalle.sigeppi.modelo.Pedido;
import pe.deliciasdelvalle.sigeppi.modelo.Rol;
import pe.deliciasdelvalle.sigeppi.modelo.SolicitudCompra;
import pe.deliciasdelvalle.sigeppi.modelo.Usuario;
import pe.deliciasdelvalle.sigeppi.util.ConexionBD;

/** CUS04 - Registrar Orden de Producción (incluye CUS05). */
public class ProduccionService {

    private final RecetaDAO recetaDAO;
    private final OrdenProduccionDAO ordenDAO;
    private final SolicitudCompraDAO solicitudDAO;
    private final PedidoDAO pedidoDAO;
    private final AuditoriaDAO auditoriaDAO;
    private final InventarioService inventarioService;

    public ProduccionService() {
        DAOFactory f = DAOFactory.getDAOFactory(DAOFactory.POSTGRES);
        this.recetaDAO = f.getRecetaDAO();
        this.ordenDAO = f.getOrdenProduccionDAO();
        this.solicitudDAO = f.getSolicitudCompraDAO();
        this.pedidoDAO = f.getPedidoDAO();
        this.auditoriaDAO = f.getAuditoriaDAO();
        this.inventarioService = new InventarioService();
    }

    public List<Pedido> listarPedidosPendientes() throws SQLException {
        return pedidoDAO.listar(null, EstadoPedido.REGISTRADO);
    }

    /** Devuelve las alertas de stock mínimo que se generaron al descontar insumos. */
    public List<String> registrarOrden(List<Integer> idsPedidos, Usuario jefe, String ip) throws NegocioException, SQLException {
        if (!jefe.tieneRol(Rol.JEFE_PRODUCCION)) {                       // RN008
            throw new NegocioException("Solo el Jefe de Producción puede registrar órdenes de producción.");
        }
        if (idsPedidos == null || idsPedidos.isEmpty()) {
            throw new NegocioException("Seleccione al menos un pedido.");
        }
        try (Connection cn = ConexionBD.getInstancia().getConexion()) {
            cn.setAutoCommit(false);
            try {
                Map<Integer, Integer> cantidades = ordenDAO.consolidarCantidades(cn, idsPedidos);
                if (cantidades.isEmpty()) {
                    throw new NegocioException("Los pedidos seleccionados ya no están en estado 'Registrado'.");
                }
                Map<Integer, Double> consumo = recetaDAO.calcularConsumo(cn, cantidades);   // RN004

                // RN008: si falta algún insumo, debe existir una solicitud de compra pendiente
                List<Faltante> sinSolicitud = new ArrayList<>();
                for (Faltante f : inventarioService.obtenerFaltantes(cn, consumo)) {
                    if (!solicitudDAO.existePendiente(cn, f.getInsumo().getIdInsumo())) {
                        sinSolicitud.add(f);
                    }
                }
                if (!sinSolicitud.isEmpty()) {
                    throw new InsumosInsuficientesException(sinSolicitud);               // ESC11
                }

                OrdenProduccion orden = new OrdenProduccion();
                orden.setFechaProduccion(LocalDate.now());
                orden.setJefe(jefe);
                cantidades.forEach((idProducto, cant) -> {
                    DetalleOrden d = new DetalleOrden();
                    d.setIdProducto(idProducto);
                    d.setCantidad(cant);
                    orden.getDetalles().add(d);
                });
                int idOrden = ordenDAO.insertar(cn, orden);
                ordenDAO.asignarPedidos(cn, idOrden, idsPedidos);
                for (Integer idPedido : idsPedidos) {
                    pedidoDAO.insertarHistorial(cn, idPedido, EstadoPedido.REGISTRADO,
                            EstadoPedido.EN_PRODUCCION, jefe.getIdUsuario());
                }
                // <<include>> CUS05 - Registrar Salida de Insumos por Producción
                List<String> alertas = inventarioService.registrarSalidaPorProduccion(cn, idOrden, consumo, jefe);
                auditoriaDAO.registrar(cn, jefe.getIdUsuario(), ip, "PRODUCCION", "ORDEN_REGISTRADA", "ord_id=" + idOrden);
                cn.commit();
                return alertas;
            } catch (SQLException | NegocioException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    /** Opción que se le ofrece al Jefe en el ESC11. */
    public void generarSolicitud(int idInsumo, double cantidad, Usuario usuario) throws NegocioException, SQLException {
        if (cantidad <= 0) {
            throw new NegocioException("La cantidad a solicitar debe ser mayor a 0 (RN005).");
        }
        try (Connection cn = ConexionBD.getInstancia().getConexion()) {
            SolicitudCompra s = new SolicitudCompra();
            s.setIdInsumo(idInsumo);
            s.setCantidad(cantidad);
            s.setMotivo("Faltante para orden de producción");
            s.setIdUsuario(usuario.getIdUsuario());
            solicitudDAO.insertar(cn, s);
        }
    }

    // ------------------------------------------------------------------ seguimiento de órdenes
    public List<OrdenProduccion> listarOrdenes() throws SQLException {
        return ordenDAO.listar(50);
    }

    /** Al terminar la producción, los pedidos de la orden pasan a LISTO_ENTREGA (RN007) con su historial. */
    public int terminarOrden(int idOrden, Usuario jefe, String ip) throws NegocioException, SQLException {
        if (!jefe.tieneRol(Rol.JEFE_PRODUCCION)) {
            throw new NegocioException("Solo el Jefe de Producción puede terminar una orden.");
        }
        OrdenProduccion orden = ordenDAO.buscarPorId(idOrden);
        if (orden == null || !"EN_PROCESO".equals(orden.getEstado())) {
            throw new NegocioException("La orden no existe o ya fue terminada.");
        }
        try (Connection cn = ConexionBD.getInstancia().getConexion()) {
            cn.setAutoCommit(false);
            try {
                List<Integer> pedidos = ordenDAO.pedidosEnProduccion(cn, idOrden);
                ordenDAO.terminar(cn, idOrden);
                for (Integer idPedido : pedidos) {
                    pedidoDAO.insertarHistorial(cn, idPedido, EstadoPedido.EN_PRODUCCION, EstadoPedido.LISTO_ENTREGA,
                            jefe.getIdUsuario());
                }
                auditoriaDAO.registrar(cn, jefe.getIdUsuario(), ip, "PRODUCCION", "ORDEN_TERMINADA", "ord_id=" + idOrden);
                cn.commit();
                return pedidos.size();
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }
}
