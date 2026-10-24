package pe.deliciasdelvalle.sigeppi.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import pe.deliciasdelvalle.sigeppi.modelo.EstadoPedido;
import pe.deliciasdelvalle.sigeppi.modelo.NegocioException;
import pe.deliciasdelvalle.sigeppi.modelo.Pedido;
import pe.deliciasdelvalle.sigeppi.modelo.Rol;
import pe.deliciasdelvalle.sigeppi.modelo.Usuario;
import pe.deliciasdelvalle.sigeppi.servicio.PedidoService;

/** CUS03 - Actualizar Estado de Pedido. */
@WebServlet(name = "EstadoPedidoServlet", urlPatterns = {"/estadoPedido"})
public class EstadoPedidoServlet extends HttpServlet {

    private final PedidoService pedidoService = new PedidoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario u = (Usuario) req.getSession().getAttribute("usuario");
        if ("ver".equals(req.getParameter("accion"))) {
            verDetalle(req, resp, u);
            return;
        }
        String estado = req.getParameter("estado");
        // el vendedor solo ve los pedidos de su tienda; el jefe ve todos
        Integer idTienda = u.tieneRol(Rol.VENDEDOR) ? u.getTienda().getIdTienda() : null;
        try {
            req.setAttribute("pedidos", pedidoService.listarPorTiendaYEstado(idTienda,
                    estado == null || estado.isBlank() ? null : EstadoPedido.valueOf(estado)));
            req.getRequestDispatcher("/vista/pedidos/listaPedidos.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void verDetalle(HttpServletRequest req, HttpServletResponse resp, Usuario u) throws ServletException, IOException {
        try {
            int id = Formulario.entero(req, "id", 0);
            Pedido p = pedidoService.buscarPedido(id);
            // el vendedor solo puede ver pedidos de su tienda
            if (p == null || (u.tieneRol(Rol.VENDEDOR) && p.getTienda().getIdTienda() != u.getTienda().getIdTienda())) {
                resp.sendRedirect(req.getContextPath() + "/estadoPedido");
                return;
            }
            req.setAttribute("pedido", p);
            req.setAttribute("historial", pedidoService.listarHistorial(id));
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher("/vista/pedidos/detallePedido.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario u = (Usuario) req.getSession().getAttribute("usuario");
        String mensaje;
        try {
            EstadoPedido nuevo = pedidoService.avanzarEstado(Integer.parseInt(req.getParameter("idPedido")), u,
                    req.getRemoteAddr());
            mensaje = "Estado actualizado a " + nuevo;                        // ESC07
        } catch (NegocioException e) {
            mensaje = e.getMessage();                                         // ESC08
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        resp.sendRedirect(req.getContextPath() + "/estadoPedido?msg="
                + URLEncoder.encode(mensaje, StandardCharsets.UTF_8));
    }
}
