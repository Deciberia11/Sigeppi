package pe.deliciasdelvalle.sigeppi.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import pe.deliciasdelvalle.sigeppi.modelo.Cliente;
import pe.deliciasdelvalle.sigeppi.modelo.ComprobanteVenta;
import pe.deliciasdelvalle.sigeppi.modelo.DetallePedido;
import pe.deliciasdelvalle.sigeppi.modelo.NegocioException;
import pe.deliciasdelvalle.sigeppi.modelo.Pedido;
import pe.deliciasdelvalle.sigeppi.modelo.Producto;
import pe.deliciasdelvalle.sigeppi.modelo.Usuario;
import pe.deliciasdelvalle.sigeppi.servicio.PedidoService;

/** CUS02 - Registrar Pedido. */
@WebServlet(name = "PedidoServlet", urlPatterns = {"/pedido"})
public class PedidoServlet extends HttpServlet {

    private final PedidoService pedidoService = new PedidoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        mostrarFormulario(req, resp);
    }

    private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("productos", pedidoService.listarProductos());
            req.setAttribute("clientes", pedidoService.listarClientes());
        } catch (SQLException e) {
            throw new ServletException("No se pudo cargar el catálogo de productos", e);
        }
        req.getRequestDispatcher("/vista/pedidos/registrarPedido.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Pedido pedido = armarPedido(req);
            ComprobanteVenta comp = pedidoService.registrarPedido(pedido, req.getParameter("metodoPago"), req.getRemoteAddr());
            req.getSession().setAttribute("comprobante", comp);
            resp.sendRedirect(req.getContextPath() + "/vista/pedidos/comprobante.jsp");
        } catch (NegocioException | NumberFormatException e) {      // ESC05 y ESC06
            req.setAttribute("error", e instanceof NumberFormatException
                    ? "Ingrese cantidades numéricas válidas." : e.getMessage());
            mostrarFormulario(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Error al registrar el pedido", e);
        }
    }

    private Pedido armarPedido(HttpServletRequest req) {
        Usuario vendedor = (Usuario) req.getSession().getAttribute("usuario");
        Pedido p = new Pedido();
        p.setVendedor(vendedor);
        p.setTienda(vendedor.getTienda());                         // RN001: tienda del vendedor
        p.setTipoPedido(req.getParameter("tipoPedido"));
        p.setObservaciones(req.getParameter("observaciones"));
        String entrega = req.getParameter("fechaEntrega");
        if (entrega != null && !entrega.isBlank()) {
            p.setFechaEntrega(LocalDate.parse(entrega));
        }
        Cliente c = new Cliente();
        String idCliente = req.getParameter("idCliente");
        if (idCliente != null && !idCliente.isBlank()) {
            c.setIdCliente(Integer.parseInt(idCliente));
        } else {                                                   // cliente nuevo
            c.setNombres(req.getParameter("nombreCliente"));
            c.setTelefono(req.getParameter("telefonoCliente"));
        }
        p.setCliente(c);

        String[] productos = req.getParameterValues("idProducto");
        String[] cantidades = req.getParameterValues("cantidad");
        if (productos != null) {
            for (int i = 0; i < productos.length; i++) {
                Producto prod = new Producto();
                prod.setIdProducto(Integer.parseInt(productos[i]));
                DetallePedido d = new DetallePedido();
                d.setProducto(prod);
                d.setCantidad(Integer.parseInt(cantidades[i]));
                p.agregarDetalle(d);
            }
        }
        return p;
    }
}
