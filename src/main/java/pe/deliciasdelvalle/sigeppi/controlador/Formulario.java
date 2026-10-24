package pe.deliciasdelvalle.sigeppi.controlador;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import pe.deliciasdelvalle.sigeppi.modelo.NegocioException;
import pe.deliciasdelvalle.sigeppi.modelo.Usuario;

/** Lectura segura de parámetros de los formularios (evita NumberFormatException en los servlets). */
final class Formulario {

    private Formulario() {
    }

    static String texto(HttpServletRequest req, String nombre) {
        String v = req.getParameter(nombre);
        return v == null ? "" : v.trim();
    }

    static int entero(HttpServletRequest req, String nombre, int porDefecto) {
        try {
            return Integer.parseInt(texto(req, nombre));
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }

    static double decimal(HttpServletRequest req, String nombre) throws NegocioException {
        try {
            return Double.parseDouble(texto(req, nombre).replace(",", "."));
        } catch (NumberFormatException e) {
            throw new NegocioException("Ingrese un número válido en el campo " + nombre + ".");
        }
    }

    static LocalDate fecha(HttpServletRequest req, String nombre, LocalDate porDefecto) {
        try {
            String v = texto(req, nombre);
            return v.isEmpty() ? porDefecto : LocalDate.parse(v);
        } catch (Exception e) {
            return porDefecto;
        }
    }

    static boolean marcado(HttpServletRequest req, String nombre) {
        String v = req.getParameter(nombre);
        return v != null && (v.equals("on") || v.equals("true") || v.equals("1"));
    }

    static Usuario usuario(HttpServletRequest req) {
        return (Usuario) req.getSession().getAttribute("usuario");
    }
}
