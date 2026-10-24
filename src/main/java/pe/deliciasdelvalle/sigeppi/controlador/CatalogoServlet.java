package pe.deliciasdelvalle.sigeppi.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import pe.deliciasdelvalle.sigeppi.servicio.PedidoService;

/** Consulta de disponibilidad de productos (vendedor). */
@WebServlet(name = "CatalogoServlet", urlPatterns = {"/catalogo"})
public class CatalogoServlet extends HttpServlet {

    private final PedidoService pedidoService = new PedidoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("productos", pedidoService.listarProductos());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher("/vista/pedidos/catalogo.jsp").forward(req, resp);
    }
}
