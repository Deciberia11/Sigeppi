package pe.deliciasdelvalle.sigeppi.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import pe.deliciasdelvalle.sigeppi.modelo.Cliente;
import pe.deliciasdelvalle.sigeppi.modelo.Insumo;
import pe.deliciasdelvalle.sigeppi.modelo.NegocioException;
import pe.deliciasdelvalle.sigeppi.modelo.Producto;
import pe.deliciasdelvalle.sigeppi.modelo.Proveedor;
import pe.deliciasdelvalle.sigeppi.modelo.Rol;
import pe.deliciasdelvalle.sigeppi.modelo.Tienda;
import pe.deliciasdelvalle.sigeppi.modelo.Usuario;
import pe.deliciasdelvalle.sigeppi.servicio.MantenimientoService;
import pe.deliciasdelvalle.sigeppi.servicio.ReporteService;
import pe.deliciasdelvalle.sigeppi.servicio.UsuarioService;

/**
 * Panel de administración (RNF-10): usuarios, productos y recetas, insumos, tiendas, proveedores,
 * clientes y la bitácora de auditoría (RNF-03). Front controller del módulo: parámetro "entidad".
 */
@WebServlet(name = "AdminServlet", urlPatterns = {"/admin"})
public class AdminServlet extends HttpServlet {

    private final MantenimientoService mant = new MantenimientoService();
    private final UsuarioService usuarioService = new UsuarioService();
    private final ReporteService reporteService = new ReporteService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String entidad = Formulario.texto(req, "entidad");
        if (entidad.isEmpty()) {
            entidad = "usuarios";
        }
        int id = Formulario.entero(req, "id", 0);
        try {
            switch (entidad) {
                case "productos":
                    req.setAttribute("lista", mant.listarProductos());
                    if (id > 0) {
                        req.setAttribute("editar", mant.buscarProducto(id));
                        req.setAttribute("receta", mant.listarReceta(id));
                        req.setAttribute("insumos", mant.listarInsumos());
                    }
                    break;
                case "insumos":
                    req.setAttribute("lista", mant.listarInsumos());
                    break;
                case "tiendas":
                    req.setAttribute("lista", mant.listarTiendas());
                    break;
                case "proveedores":
                    req.setAttribute("lista", mant.listarProveedores());
                    break;
                case "clientes":
                    req.setAttribute("lista", mant.listarClientes());
                    break;
                case "auditoria":
                    req.setAttribute("lista", reporteService.auditoria(Formulario.texto(req, "modulo"), Formulario.texto(req, "q")));
                    break;
                default:
                    entidad = "usuarios";
                    req.setAttribute("lista", usuarioService.listar());
                    req.setAttribute("tiendas", mant.listarTiendasActivas());
                    if (id > 0) {
                        req.setAttribute("editar", usuarioService.buscar(id));
                    }
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.setAttribute("entidad", entidad);
        req.getRequestDispatcher("/vista/admin/" + entidad + ".jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String entidad = Formulario.texto(req, "entidad");
        String accion = Formulario.texto(req, "accion");
        Usuario admin = Formulario.usuario(req);
        String ip = req.getRemoteAddr();
        String volver = "/admin?entidad=" + entidad;
        try {
            switch (entidad + ":" + accion) {
                case "usuarios:guardar": {
                    Usuario u = new Usuario();
                    u.setIdUsuario(Formulario.entero(req, "id", 0));
                    u.setNombreUsuario(Formulario.texto(req, "nombreUsuario"));
                    u.setNombres(Formulario.texto(req, "nombres"));
                    u.setApellidos(Formulario.texto(req, "apellidos"));
                    Rol rol = new Rol();
                    rol.setIdRol(Formulario.entero(req, "idRol", 1));
                    rol.setNombre(nombreRol(rol.getIdRol()));
                    u.setRol(rol);
                    Tienda t = new Tienda();
                    t.setIdTienda(Formulario.entero(req, "idTienda", 0));
                    u.setTienda(t);
                    usuarioService.guardar(u, req.getParameter("clave"), admin, ip);
                    mensaje(req, "Usuario guardado correctamente.");
                    break;
                }
                case "usuarios:estado":
                    usuarioService.cambiarEstado(Formulario.entero(req, "id", 0), Formulario.marcado(req, "activo"), admin, ip);
                    mensaje(req, "Estado del usuario actualizado.");
                    break;
                case "usuarios:clave":
                    usuarioService.resetearContrasena(Formulario.entero(req, "id", 0), req.getParameter("clave"), admin, ip);
                    mensaje(req, "Contraseña restablecida. Comuníquesela al trabajador.");
                    break;
                case "productos:guardar": {
                    Producto p = new Producto();
                    p.setIdProducto(Formulario.entero(req, "id", 0));
                    p.setNombre(Formulario.texto(req, "nombre"));
                    p.setDescripcion(Formulario.texto(req, "descripcion"));
                    p.setPrecioUnitario(Formulario.decimal(req, "precio"));
                    p.setTipoProducto(Formulario.texto(req, "tipo"));
                    p.setTiempoPreparacion(Formulario.entero(req, "tiempo", 0));
                    p.setEstado(Formulario.marcado(req, "estado"));
                    int idProducto = mant.guardarProducto(p, admin, ip);
                    mensaje(req, "Producto guardado. Revise su receta estándar.");
                    volver += "&id=" + idProducto;
                    break;
                }
                case "productos:receta": {
                    int idProducto = Formulario.entero(req, "id", 0);
                    mant.agregarInsumoReceta(idProducto, Formulario.entero(req, "idInsumo", 0),
                            Formulario.decimal(req, "cantidad"), admin, ip);
                    mensaje(req, "Receta actualizada.");
                    volver += "&id=" + idProducto;
                    break;
                }
                case "productos:quitar": {
                    mant.quitarInsumoReceta(Formulario.entero(req, "idReceta", 0), admin, ip);
                    mensaje(req, "Insumo quitado de la receta.");
                    volver += "&id=" + Formulario.entero(req, "id", 0);
                    break;
                }
                case "insumos:guardar": {
                    Insumo i = new Insumo();
                    i.setIdInsumo(Formulario.entero(req, "id", 0));
                    i.setNombre(Formulario.texto(req, "nombre"));
                    i.setUnidadMedida(Formulario.texto(req, "unidad"));
                    i.setStockMinimo(Formulario.decimal(req, "minimo"));
                    i.setPerecible(Formulario.marcado(req, "perecible"));
                    mant.guardarInsumo(i, admin, ip);
                    mensaje(req, "Insumo guardado.");
                    break;
                }
                case "tiendas:guardar": {
                    Tienda t = new Tienda();
                    t.setIdTienda(Formulario.entero(req, "id", 0));
                    t.setNombre(Formulario.texto(req, "nombre"));
                    t.setDireccion(Formulario.texto(req, "direccion"));
                    t.setDistrito(Formulario.texto(req, "distrito"));
                    t.setEstado(Formulario.marcado(req, "estado"));
                    mant.guardarTienda(t, admin, ip);
                    mensaje(req, "Tienda guardada.");
                    break;
                }
                case "proveedores:guardar": {
                    Proveedor p = new Proveedor();
                    p.setIdProveedor(Formulario.entero(req, "id", 0));
                    p.setRazonSocial(Formulario.texto(req, "razonSocial"));
                    p.setRuc(Formulario.texto(req, "ruc"));
                    p.setTelefono(Formulario.texto(req, "telefono"));
                    p.setCorreo(Formulario.texto(req, "correo"));
                    p.setTipoProveedor(Formulario.texto(req, "tipo"));
                    mant.guardarProveedor(p, admin, ip);
                    mensaje(req, "Proveedor guardado.");
                    break;
                }
                case "clientes:guardar": {
                    Cliente c = new Cliente();
                    c.setIdCliente(Formulario.entero(req, "id", 0));
                    c.setNombres(Formulario.texto(req, "nombres"));
                    c.setTelefono(Formulario.texto(req, "telefono"));
                    c.setTipoCliente(Formulario.texto(req, "tipo"));
                    mant.guardarCliente(c, admin, ip);
                    mensaje(req, "Cliente actualizado.");
                    break;
                }
                default:
                    throw new NegocioException("Acción no reconocida.");
            }
        } catch (NegocioException e) {
            req.getSession().setAttribute("flashError", e.getMessage());
            int id = Formulario.entero(req, "id", 0);
            if (id > 0 && !volver.contains("&id=")) {
                volver += "&id=" + id;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        // patrón Post/Redirect/Get: evita que al refrescar se vuelva a enviar el formulario
        resp.sendRedirect(req.getContextPath() + volver);
    }

    private void mensaje(HttpServletRequest req, String texto) {
        req.getSession().setAttribute("flashMensaje", texto);
    }

    private String nombreRol(int idRol) {
        switch (idRol) {
            case 2: return Rol.JEFE_PRODUCCION;
            case 3: return Rol.ENCARGADO_ALMACEN;
            case 4: return Rol.ADMINISTRADOR;
            default: return Rol.VENDEDOR;
        }
    }
}
