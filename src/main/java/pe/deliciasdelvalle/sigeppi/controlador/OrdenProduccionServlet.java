package pe.deliciasdelvalle.sigeppi.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.modelo.InsumosInsuficientesException;
import pe.deliciasdelvalle.sigeppi.modelo.NegocioException;
import pe.deliciasdelvalle.sigeppi.modelo.Usuario;
import pe.deliciasdelvalle.sigeppi.servicio.ProduccionService;

/** CUS04 - Registrar Orden de Producción, y seguimiento/terminación de órdenes. */
@WebServlet(name = "OrdenProduccionServlet", urlPatterns = {"/orden"})
public class OrdenProduccionServlet extends HttpServlet {

    private static final String VISTA = "/vista/produccion/planificarOrden.jsp";
    private final ProduccionService produccionService = new ProduccionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            if ("ordenes".equals(req.getParameter("accion"))) {
                req.setAttribute("ordenes", produccionService.listarOrdenes());
                req.getRequestDispatcher("/vista/produccion/ordenes.jsp").forward(req, resp);
                return;
            }
            req.setAttribute("pedidos", produccionService.listarPedidosPendientes());
            req.getRequestDispatcher(VISTA).forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario jefe = Formulario.usuario(req);
        String accion = Formulario.texto(req, "accion");
        if ("terminar".equals(accion)) {
            terminar(req, resp, jefe);
            return;
        }
        try {
            if ("solicitud".equals(accion)) {        // ESC11: el jefe genera la solicitud de compra
                produccionService.generarSolicitud(Formulario.entero(req, "idInsumo", 0),
                        Formulario.decimal(req, "cantidad"), jefe);
                req.setAttribute("mensaje", "Solicitud de compra generada. Ya puede registrar la orden.");
            } else {
                List<Integer> ids = new ArrayList<>();
                String[] seleccion = req.getParameterValues("idPedido");
                if (seleccion != null) {
                    for (String s : seleccion) {
                        ids.add(Integer.parseInt(s));
                    }
                }
                List<String> alertas = produccionService.registrarOrden(ids, jefe, req.getRemoteAddr());
                req.setAttribute("mensaje", "Orden de producción registrada correctamente.");
                req.setAttribute("alertas", alertas);
            }
        } catch (InsumosInsuficientesException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("faltantes", e.getFaltantes());
        } catch (NegocioException e) {
            req.setAttribute("error", e.getMessage());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        doGet(req, resp);
    }

    /** Al terminar la producción los pedidos pasan a LISTO_ENTREGA (RN007). */
    private void terminar(HttpServletRequest req, HttpServletResponse resp, Usuario jefe) throws ServletException, IOException {
        try {
            int n = produccionService.terminarOrden(Formulario.entero(req, "idOrden", 0), jefe, req.getRemoteAddr());
            req.getSession().setAttribute("flashMensaje", "Orden terminada: " + n + " pedido(s) pasaron a LISTO PARA ENTREGA.");
        } catch (NegocioException e) {
            req.getSession().setAttribute("flashError", e.getMessage());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        resp.sendRedirect(req.getContextPath() + "/orden?accion=ordenes");
    }
}
