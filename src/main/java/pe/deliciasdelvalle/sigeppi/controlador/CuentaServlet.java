package pe.deliciasdelvalle.sigeppi.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import pe.deliciasdelvalle.sigeppi.modelo.NegocioException;
import pe.deliciasdelvalle.sigeppi.servicio.UsuarioService;

/** Mi cuenta: cambiar contraseña (todos los roles). */
@WebServlet(name = "CuentaServlet", urlPatterns = {"/cuenta"})
public class CuentaServlet extends HttpServlet {

    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/vista/seguridad/cuenta.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            usuarioService.cambiarContrasena(Formulario.usuario(req), req.getParameter("actual"),
                    req.getParameter("nueva"), req.getParameter("confirmar"), req.getRemoteAddr());
            req.setAttribute("mensaje", "Su contraseña se cambió correctamente.");
        } catch (NegocioException e) {
            req.setAttribute("error", e.getMessage());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        doGet(req, resp);
    }
}
