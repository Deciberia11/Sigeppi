package pe.deliciasdelvalle.sigeppi.servicio;

import pe.deliciasdelvalle.sigeppi.modelo.HistorialEstado;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.dao.AuditoriaDAO;
import pe.deliciasdelvalle.sigeppi.dao.ClienteDAO;
import pe.deliciasdelvalle.sigeppi.dao.ComprobanteDAO;
import pe.deliciasdelvalle.sigeppi.dao.DAOFactory;
import pe.deliciasdelvalle.sigeppi.dao.PedidoDAO;
import pe.deliciasdelvalle.sigeppi.dao.ProductoDAO;
import pe.deliciasdelvalle.sigeppi.modelo.Cliente;
import pe.deliciasdelvalle.sigeppi.modelo.ComprobanteVenta;
import pe.deliciasdelvalle.sigeppi.modelo.DetallePedido;
import pe.deliciasdelvalle.sigeppi.modelo.EstadoPedido;
import pe.deliciasdelvalle.sigeppi.modelo.NegocioException;
import pe.deliciasdelvalle.sigeppi.modelo.Pedido;
import pe.deliciasdelvalle.sigeppi.modelo.Producto;
import pe.deliciasdelvalle.sigeppi.modelo.Rol;
import pe.deliciasdelvalle.sigeppi.modelo.Usuario;
import pe.deliciasdelvalle.sigeppi.util.ConexionBD;

/** CUS02 - Registrar Pedido y CUS03 - Actualizar Estado de Pedido. */
public class PedidoService {

    public static final double IGV = 0.18;

    private final PedidoDAO pedidoDAO;
    private final ClienteDAO clienteDAO;
    private final ProductoDAO productoDAO;
    private final ComprobanteDAO comprobanteDAO;
    private final AuditoriaDAO auditoriaDAO;

    public PedidoService() {
        DAOFactory f = DAOFactory.getDAOFactory(DAOFactory.POSTGRES);
        this.pedidoDAO = f.getPedidoDAO();
        this.clienteDAO = f.getClienteDAO();
        this.productoDAO = f.getProductoDAO();
        this.comprobanteDAO = f.getComprobanteDAO();
        this.auditoriaDAO = f.getAuditoriaDAO();
    }

    // ------------------------------------------------------------------ listas para la vista
    public List<Producto> listarProductos() throws SQLException {
        return productoDAO.listarActivos();
    }

    public List<Cliente> listarClientes() throws SQLException {
        return clienteDAO.listar();
    }

    // ------------------------------------------------------------------ CUS02
    public ComprobanteVenta registrarPedido(Pedido pedido, String metodoPago, String ip) throws NegocioException, SQLException {
        cargarPrecios(pedido);       // el precio sale de la BD, no del formulario
        validar(pedido);             // RN001, RN002, RN005
        calcularTotales(pedido);     // RN003

        try (Connection cn = ConexionBD.getInstancia().getConexion()) {
            cn.setAutoCommit(false); // RNF-08: todo o nada
            try {
                if (pedido.getCliente().getIdCliente() == 0) {   // cliente nuevo
                    int idCliente = clienteDAO.insertar(cn, pedido.getCliente());
                    pedido.getCliente().setIdCliente(idCliente);
                }
                int idPedido = pedidoDAO.insertar(cn, pedido);
                pedido.setIdPedido(idPedido);
                pedidoDAO.insertarDetalles(cn, idPedido, pedido.getDetalles());
                pedidoDAO.insertarHistorial(cn, idPedido, null, EstadoPedido.REGISTRADO,
                        pedido.getVendedor().getIdUsuario());

                ComprobanteVenta comp = new ComprobanteVenta();
                comp.setIdPedido(idPedido);
                comp.setSerie(String.format("B%03d", pedido.getTienda().getIdTienda()));
                comp.setNumero(comprobanteDAO.siguienteNumero(cn, comp.getSerie()));
                comp.setMetodoPago(metodoPago);
                comp.setTotal(pedido.getTotal());
                comp.setIdComprobante(comprobanteDAO.insertar(cn, comp));

                auditoriaDAO.registrar(cn, pedido.getVendedor().getIdUsuario(), ip, "PEDIDOS",
                        "PEDIDO_REGISTRADO", "ped_id=" + idPedido);
                cn.commit();
                return comp;
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    private void cargarPrecios(Pedido pedido) throws NegocioException, SQLException {
        for (DetallePedido d : pedido.getDetalles()) {
            Producto p = productoDAO.buscarPorId(d.getProducto().getIdProducto());
            if (p == null || !p.isEstado()) {
                throw new NegocioException("El producto seleccionado no está disponible.");
            }
            d.setProducto(p);
            d.setPrecioUnitario(p.getPrecioUnitario());
        }
    }

    void validar(Pedido pedido) throws NegocioException {
        if (pedido.getCliente() == null || pedido.getTienda() == null) {
            throw new NegocioException("El pedido debe tener un cliente y una tienda (RN001).");
        }
        if (pedido.getCliente().getIdCliente() == 0
                && (pedido.getCliente().getNombres() == null || pedido.getCliente().getNombres().isBlank())) {
            throw new NegocioException("Ingrese el nombre del cliente nuevo o elija un cliente de la lista (RN001).");
        }
        if (pedido.getDetalles().isEmpty()) {
            throw new NegocioException("Agregue al menos un producto al pedido (RN002).");
        }
        for (DetallePedido d : pedido.getDetalles()) {
            if (d.getCantidad() <= 0 || d.getPrecioUnitario() < 0) {
                throw new NegocioException("La cantidad debe ser mayor a 0 y el precio no puede ser negativo (RN005).");
            }
        }
    }

    void calcularTotales(Pedido pedido) {
        double subtotal = 0;
        for (DetallePedido d : pedido.getDetalles()) {
            subtotal += d.getSubtotal();
        }
        double igv = Math.round(subtotal * IGV * 100.0) / 100.0;
        pedido.setSubtotal(subtotal);
        pedido.setIgv(igv);
        pedido.setTotal(subtotal + igv);
    }

    // ------------------------------------------------------------------ CUS03
    public List<Pedido> listarPorTiendaYEstado(Integer idTienda, EstadoPedido estado) throws SQLException {
        return pedidoDAO.listar(idTienda, estado);
    }

    public EstadoPedido avanzarEstado(int idPedido, Usuario usuario, String ip) throws NegocioException, SQLException {
        Pedido pedido = pedidoDAO.buscarPorId(idPedido);
        if (pedido == null) {
            throw new NegocioException("El pedido no existe.");
        }
        EstadoPedido actual = pedido.getEstado();
        EstadoPedido siguiente = actual.siguiente();   // RN007: solo el siguiente, sin saltos ni retrocesos
        validarPermiso(usuario, pedido, siguiente);

        try (Connection cn = ConexionBD.getInstancia().getConexion()) {
            cn.setAutoCommit(false);
            try {
                pedidoDAO.actualizarEstado(cn, idPedido, siguiente);
                pedidoDAO.insertarHistorial(cn, idPedido, actual, siguiente, usuario.getIdUsuario());
                auditoriaDAO.registrar(cn, usuario.getIdUsuario(), ip, "PEDIDOS", "CAMBIO_ESTADO",
                        "ped_id=" + idPedido + " " + actual + "->" + siguiente);
                cn.commit();
                return siguiente;
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    void validarPermiso(Usuario usuario, Pedido pedido, EstadoPedido siguiente) throws NegocioException {
        if (siguiente == null) {
            throw new NegocioException("El pedido ya fue entregado, no tiene más estados.");
        }
        switch (siguiente) {
            case EN_PRODUCCION:
                // RN008: este cambio solo se hace al registrar la orden de producción (CUS04)
                throw new NegocioException("Para pasar a 'En producción' el Jefe de Producción debe registrar la orden de producción.");
            case LISTO_ENTREGA:
                if (!usuario.tieneRol(Rol.JEFE_PRODUCCION)) {
                    throw new NegocioException("Solo el Jefe de Producción puede marcar el pedido como 'Listo para entrega'.");
                }
                break;
            case ENTREGADO:
                if (!usuario.tieneRol(Rol.VENDEDOR) || usuario.getTienda() == null
                        || usuario.getTienda().getIdTienda() != pedido.getTienda().getIdTienda()) {
                    throw new NegocioException("Solo el vendedor de la tienda del pedido puede marcarlo como 'Entregado'.");
                }
                break;
            default:
                throw new NegocioException("Cambio de estado no permitido.");
        }
    }

    // ------------------------------------------------------------------ detalle del pedido (consulta)
    public Pedido buscarPedido(int idPedido) throws SQLException {
        Pedido p = pedidoDAO.buscarPorId(idPedido);
        if (p != null) {
            for (DetallePedido d : pedidoDAO.listarDetalles(idPedido)) {
                p.agregarDetalle(d);
            }
            calcularTotales(p);
        }
        return p;
    }

    public List<HistorialEstado> listarHistorial(int idPedido) throws SQLException {
        return pedidoDAO.listarHistorial(idPedido);
    }
}
