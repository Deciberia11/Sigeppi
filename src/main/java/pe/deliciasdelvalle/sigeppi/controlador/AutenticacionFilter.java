package pe.deliciasdelvalle.sigeppi.controlador;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import pe.deliciasdelvalle.sigeppi.modelo.Rol;
import pe.deliciasdelvalle.sigeppi.modelo.Usuario;

/**
 * Patrón Intercepting Filter: revisa que haya sesión activa y que el rol tenga
 * permiso sobre el módulo (esquema de seguridad del Avance 1, sección 11).
 */
@WebFilter(urlPatterns = {"/*"})
public class AutenticacionFilter implements Filter {

    private static final Map<String, Set<String>> PERMISOS = Map.of(
            "/pedido", Set.of(Rol.VENDEDOR),
            "/estadoPedido", Set.of(Rol.VENDEDOR, Rol.JEFE_PRODUCCION),
            "/orden", Set.of(Rol.JEFE_PRODUCCION),
            "/insumo", Set.of(Rol.ENCARGADO_ALMACEN),
            "/reporte", Set.of(Rol.ADMINISTRADOR),
            "/admin", Set.of(Rol.ADMINISTRADOR),
            "/stock", Set.of(Rol.JEFE_PRODUCCION, Rol.ENCARGADO_ALMACEN, Rol.ADMINISTRADOR),
            "/solicitud", Set.of(Rol.ENCARGADO_ALMACEN, Rol.ADMINISTRADOR),
            "/catalogo", Set.of(Rol.VENDEDOR, Rol.JEFE_PRODUCCION, Rol.ADMINISTRADOR));

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        req.setCharacterEncoding("UTF-8");     // para que las tildes y la ñ lleguen bien
        resp.setCharacterEncoding("UTF-8");
        String ruta = req.getRequestURI().substring(req.getContextPath().length());

        if (ruta.equals("/login") || ruta.equals("/logout") || ruta.startsWith("/recursos/")
                || ruta.equals("/vista/seguridad/login.jsp") || ruta.equals("/vista/error.jsp")) {
            chain.doFilter(request, response);
            return;
        }
        HttpSession sesion = req.getSession(false);
        Usuario usuario = sesion == null ? null : (Usuario) sesion.getAttribute("usuario");
        if (usuario == null) {                       // no hay sesión o se cerró por los 15 minutos (RNF-02)
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (!tienePermiso(usuario.getRol().getNombre(), ruta)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "No tiene permiso para este módulo.");
            return;
        }
        // mensajes "flash" que vienen de un redirect (patrón Post/Redirect/Get)
        if (sesion.getAttribute("flashMensaje") != null) {
            req.setAttribute("mensaje", sesion.getAttribute("flashMensaje"));
            sesion.removeAttribute("flashMensaje");
        }
        if (sesion.getAttribute("flashError") != null) {
            req.setAttribute("error", sesion.getAttribute("flashError"));
            sesion.removeAttribute("flashError");
        }
        // evita que el navegador muestre páginas privadas con el botón Atrás después de salir
        resp.setHeader("Cache-Control", "no-store");
        chain.doFilter(request, response);
    }

    private boolean tienePermiso(String rol, String ruta) {
        for (Map.Entry<String, Set<String>> e : PERMISOS.entrySet()) {
            if (ruta.startsWith(e.getKey())) {
                return e.getValue().contains(rol);
            }
        }
        return true;   // páginas comunes (menú, logout)
    }
}
