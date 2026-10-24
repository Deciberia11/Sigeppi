package pe.deliciasdelvalle.sigeppi.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import pe.deliciasdelvalle.sigeppi.modelo.Insumo;
import pe.deliciasdelvalle.sigeppi.modelo.LoteInsumo;
import pe.deliciasdelvalle.sigeppi.modelo.NegocioException;
import pe.deliciasdelvalle.sigeppi.modelo.Proveedor;
import pe.deliciasdelvalle.sigeppi.modelo.Usuario;
import pe.deliciasdelvalle.sigeppi.servicio.InventarioService;

/** CUS06 - Registrar Ingreso de Insumos y consulta de salidas (CUS05). */
@WebServlet(name = "InsumoServlet", urlPatterns = {"/insumo"})
public class InsumoServlet extends HttpServlet {

    private final InventarioService inventarioService = new InventarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("salidas".equals(req.getParameter("accion"))) {
            try {
                LocalDate hoy = LocalDate.now();
                req.setAttribute("salidas", inventarioService.listarSalidas(hoy.minusDays(7), hoy));
                req.getRequestDispatcher("/vista/inventario/salidasInsumo.jsp").forward(req, resp);
            } catch (SQLException e) {
                throw new ServletException(e);
            }
            return;
        }
        mostrarFormulario(req, resp);
    }

    private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("insumos", inventarioService.listarInsumos());
            req.setAttribute("proveedores", inventarioService.listarProveedores());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        mostrarFormulario(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario u = (Usuario) req.getSession().getAttribute("usuario");
        try {
            inventarioService.registrarIngreso(armarLote(req), u, req.getRemoteAddr());
            req.setAttribute("mensaje", "Ingreso registrado y stock actualizado.");      // ESC14
        } catch (NegocioException e) {                                                   // ESC15 y ESC16
            req.setAttribute("error", e.getMessage());
        } catch (NumberFormatException | DateTimeParseException e) {
            req.setAttribute("error", "Revise que las cantidades y fechas tengan un formato válido.");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher("/vista/inventario/registrarIngreso.jsp").forward(req, resp);
    }

    private LoteInsumo armarLote(HttpServletRequest req) {
        LoteInsumo l = new LoteInsumo();
        Insumo i = new Insumo();
        i.setIdInsumo(Integer.parseInt(req.getParameter("idInsumo")));
        Proveedor p = new Proveedor();
        p.setIdProveedor(Integer.parseInt(req.getParameter("idProveedor")));
        l.setInsumo(i);
        l.setProveedor(p);
        l.setCodigo(req.getParameter("codigoLote"));
        l.setCantidadInicial(Double.parseDouble(req.getParameter("cantidad")));
        l.setCostoUnitario(Double.parseDouble(req.getParameter("costoUnitario")));
        l.setFechaIngreso(LocalDate.parse(req.getParameter("fechaIngreso")));
        l.setFechaVencimiento(LocalDate.parse(req.getParameter("fechaVencimiento")));
        return l;
    }
}
