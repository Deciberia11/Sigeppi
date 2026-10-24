package pe.deliciasdelvalle.sigeppi.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import pe.deliciasdelvalle.sigeppi.modelo.NegocioException;
import pe.deliciasdelvalle.sigeppi.modelo.SolicitudCompra;
import pe.deliciasdelvalle.sigeppi.servicio.InventarioService;

/** Gestión de abastecimiento: solicitudes de compra con los contactos de los proveedores. */
@WebServlet(name = "SolicitudServlet", urlPatterns = {"/solicitud"})
public class SolicitudServlet extends HttpServlet {

    private final InventarioService inventarioService = new InventarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String estado = Formulario.texto(req, "estado");
        try {
            req.setAttribute("solicitudes", inventarioService.listarSolicitudes(estado));
            req.setAttribute("insumos", inventarioService.listarInsumos());
            req.setAttribute("proveedores", inventarioService.listarProveedores());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher("/vista/inventario/solicitudes.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = Formulario.texto(req, "accion");
        try {
            if ("crear".equals(accion)) {
                SolicitudCompra s = new SolicitudCompra();
                s.setIdInsumo(Formulario.entero(req, "idInsumo", 0));
                int prv = Formulario.entero(req, "idProveedor", 0);
                s.setIdProveedor(prv == 0 ? null : prv);
                s.setCantidad(Formulario.decimal(req, "cantidad"));
                s.setMotivo(Formulario.texto(req, "motivo"));
                inventarioService.crearSolicitud(s, Formulario.usuario(req), req.getRemoteAddr());
                req.setAttribute("mensaje", "Solicitud de compra registrada.");
            } else {
                inventarioService.cambiarEstadoSolicitud(Formulario.entero(req, "idSolicitud", 0), accion,
                        Formulario.usuario(req), req.getRemoteAddr());
                req.setAttribute("mensaje", "Solicitud actualizada a " + accion + ".");
            }
        } catch (NegocioException e) {
            req.setAttribute("error", e.getMessage());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        doGet(req, resp);
    }
}
