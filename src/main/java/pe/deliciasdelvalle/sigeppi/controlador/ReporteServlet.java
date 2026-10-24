package pe.deliciasdelvalle.sigeppi.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.modelo.ConsumoInsumoDTO;
import pe.deliciasdelvalle.sigeppi.modelo.NegocioException;
import pe.deliciasdelvalle.sigeppi.modelo.ReporteVentaDTO;
import pe.deliciasdelvalle.sigeppi.modelo.ReporteVentas;
import pe.deliciasdelvalle.sigeppi.servicio.ReporteService;

/** CUS07 - Generar Reporte de Ventas y reporte de consumo histórico de insumos (PAQ05). Exporta a CSV. */
@WebServlet(name = "ReporteServlet", urlPatterns = {"/reporte"})
public class ReporteServlet extends HttpServlet {

    private final ReporteService reporteService = new ReporteService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean consumo = "consumo".equals(req.getParameter("tipo"));
        boolean csv = "csv".equals(req.getParameter("formato"));
        LocalDate hoy = LocalDate.now();
        LocalDate desde = Formulario.fecha(req, "desde", hoy.withDayOfMonth(1));
        LocalDate hasta = Formulario.fecha(req, "hasta", hoy);
        req.setAttribute("desde", desde);
        req.setAttribute("hasta", hasta);
        boolean consultar = req.getParameter("desde") != null;
        try {
            req.setAttribute("tiendas", reporteService.listarTiendas());
            if (consumo) {
                if (consultar) {
                    List<ConsumoInsumoDTO> filas = reporteService.consumoInsumos(desde, hasta);
                    if (csv) {
                        exportarConsumo(resp, filas, desde, hasta);
                        return;
                    }
                    req.setAttribute("filas", filas);
                    if (filas.isEmpty()) {
                        req.setAttribute("mensaje", "No hubo salidas de insumos en el periodo seleccionado.");
                    }
                }
                req.getRequestDispatcher("/vista/reportes/consumoInsumos.jsp").forward(req, resp);
                return;
            }
            if (consultar) {
                int idTienda = Formulario.entero(req, "tienda", 0);   // 0 = todas
                ReporteVentas reporte = reporteService.generarReporteVentas(idTienda, desde, hasta);
                if (csv) {
                    exportarVentas(resp, reporte, desde, hasta);
                    return;
                }
                req.setAttribute("reporte", reporte);
                if (reporte.estaVacio()) {
                    req.setAttribute("mensaje", "No se encontraron ventas en el periodo seleccionado.");   // ESC19
                }
            }
        } catch (NegocioException e) {
            req.setAttribute("error", e.getMessage());                                             // ESC18
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher(consumo ? "/vista/reportes/consumoInsumos.jsp" : "/vista/reportes/reporteVentas.jsp")
                .forward(req, resp);
    }

    private PrintWriter abrirCsv(HttpServletResponse resp, String nombre) throws IOException {
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + nombre + "\"");
        PrintWriter out = resp.getWriter();
        out.write('﻿');   // BOM para que Excel lea bien las tildes
        return out;
    }

    private void exportarVentas(HttpServletResponse resp, ReporteVentas r, LocalDate desde, LocalDate hasta) throws IOException {
        PrintWriter out = abrirCsv(resp, "ventas_" + desde + "_" + hasta + ".csv");
        out.println("Fecha;Tienda;N° ventas;Total vendido (S/)");
        for (ReporteVentaDTO f : r.getFilas()) {
            out.println(f.getFecha() + ";" + f.getTienda() + ";" + f.getNroPedidos() + ";" + String.format("%.2f", f.getTotalVendido()));
        }
        out.println(";;Total general;" + String.format("%.2f", r.getTotalGeneral()));
    }

    private void exportarConsumo(HttpServletResponse resp, List<ConsumoInsumoDTO> filas, LocalDate desde, LocalDate hasta)
            throws IOException {
        PrintWriter out = abrirCsv(resp, "consumo_insumos_" + desde + "_" + hasta + ".csv");
        out.println("Insumo;Unidad;Cantidad consumida;N° órdenes");
        for (ConsumoInsumoDTO c : filas) {
            out.println(c.getInsumo() + ";" + c.getUnidad() + ";" + String.format("%.3f", c.getCantidad()) + ";" + c.getNroOrdenes());
        }
    }
}
