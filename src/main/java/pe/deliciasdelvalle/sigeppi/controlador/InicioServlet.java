package pe.deliciasdelvalle.sigeppi.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import pe.deliciasdelvalle.sigeppi.servicio.ReporteService;

/** Panel de inicio de cada rol (panel de control gerencial para el Administrador). */
@WebServlet(name = "InicioServlet", urlPatterns = {"/inicio"})
public class InicioServlet extends HttpServlet {

    private final ReporteService reporteService = new ReporteService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("panel", reporteService.panel(Formulario.usuario(req)));
        } catch (SQLException e) {
            throw new ServletException("No se pudo cargar el panel", e);
        }
        req.getRequestDispatcher("/vista/inicio.jsp").forward(req, resp);
    }
}
