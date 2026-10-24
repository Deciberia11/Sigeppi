package pe.deliciasdelvalle.sigeppi.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import pe.deliciasdelvalle.sigeppi.servicio.InventarioService;

/** Consulta de stock de insumos y lotes (Jefe de Producción, Encargado de Almacén, Administrador). */
@WebServlet(name = "StockServlet", urlPatterns = {"/stock"})
public class StockServlet extends HttpServlet {

    private final InventarioService inventarioService = new InventarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("insumos", inventarioService.listarInsumos());
            req.setAttribute("lotes", inventarioService.listarLotesConSaldo());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher("/vista/inventario/stock.jsp").forward(req, resp);
    }
}
