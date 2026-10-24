package pe.deliciasdelvalle.sigeppi.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import pe.deliciasdelvalle.sigeppi.modelo.NegocioException;
import pe.deliciasdelvalle.sigeppi.modelo.Usuario;
import pe.deliciasdelvalle.sigeppi.servicio.UsuarioService;

/** CUS01 - Iniciar Sesión. */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private static final int QUINCE_MINUTOS = 15 * 60;   // RNF-02
    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession actual = req.getSession(false);
        if (actual != null && actual.getAttribute("usuario") != null) {
            resp.sendRedirect(req.getContextPath() + paginaInicio((Usuario) actual.getAttribute("usuario")));
            return;
        }
        req.getRequestDispatcher("/vista/seguridad/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String usuario = req.getParameter("usuario");
        String contrasena = req.getParameter("contrasena");
        try {
            Usuario u = usuarioService.autenticar(usuario, contrasena, req.getRemoteAddr());
            req.getSession().invalidate();                // nueva sesión para evitar fijación de sesión
            HttpSession sesion = req.getSession(true);
            sesion.setAttribute("usuario", u);
            sesion.setMaxInactiveInterval(QUINCE_MINUTOS);
            resp.sendRedirect(req.getContextPath() + paginaInicio(u));
        } catch (NegocioException e) {                    // ESC02 y ESC03
            req.setAttribute("error", e.getMessage());
            req.setAttribute("usuario", usuario);
            req.getRequestDispatcher("/vista/seguridad/login.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Error de base de datos al iniciar sesión", e);
        }
    }

    private String paginaInicio(Usuario u) {
        // todos los roles entran a su panel de inicio
        return u.getRol() == null ? "/login" : "/inicio";
    }
}
