package pe.deliciasdelvalle.sigeppi.servicio;

import java.sql.SQLException;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.dao.AuditoriaDAO;
import pe.deliciasdelvalle.sigeppi.dao.ClienteDAO;
import pe.deliciasdelvalle.sigeppi.dao.DAOFactory;
import pe.deliciasdelvalle.sigeppi.dao.InsumoDAO;
import pe.deliciasdelvalle.sigeppi.dao.ProductoDAO;
import pe.deliciasdelvalle.sigeppi.dao.ProveedorDAO;
import pe.deliciasdelvalle.sigeppi.dao.RecetaDAO;
import pe.deliciasdelvalle.sigeppi.dao.TiendaDAO;
import pe.deliciasdelvalle.sigeppi.modelo.Cliente;
import pe.deliciasdelvalle.sigeppi.modelo.Insumo;
import pe.deliciasdelvalle.sigeppi.modelo.NegocioException;
import pe.deliciasdelvalle.sigeppi.modelo.Producto;
import pe.deliciasdelvalle.sigeppi.modelo.Proveedor;
import pe.deliciasdelvalle.sigeppi.modelo.RecetaItem;
import pe.deliciasdelvalle.sigeppi.modelo.Tienda;
import pe.deliciasdelvalle.sigeppi.modelo.Usuario;

/**
 * RNF-10: las entidades maestras (tiendas, productos con su receta, insumos, proveedores y clientes)
 * se administran desde el panel del Administrador, sin cambiar el código.
 */
public class MantenimientoService {

    private final ProductoDAO productoDAO;
    private final RecetaDAO recetaDAO;
    private final InsumoDAO insumoDAO;
    private final TiendaDAO tiendaDAO;
    private final ProveedorDAO proveedorDAO;
    private final ClienteDAO clienteDAO;
    private final AuditoriaDAO auditoriaDAO;

    public MantenimientoService() {
        DAOFactory f = DAOFactory.getDAOFactory(DAOFactory.POSTGRES);
        this.productoDAO = f.getProductoDAO();
        this.recetaDAO = f.getRecetaDAO();
        this.insumoDAO = f.getInsumoDAO();
        this.tiendaDAO = f.getTiendaDAO();
        this.proveedorDAO = f.getProveedorDAO();
        this.clienteDAO = f.getClienteDAO();
        this.auditoriaDAO = f.getAuditoriaDAO();
    }

    // ------------------------------------------------------------------ productos y receta (RN004, RN005)
    public List<Producto> listarProductos() throws SQLException {
        return productoDAO.listarTodos();
    }

    public Producto buscarProducto(int id) throws SQLException {
        return productoDAO.buscarPorId(id);
    }

    public int guardarProducto(Producto p, Usuario admin, String ip) throws NegocioException, SQLException {
        requerido(p.getNombre(), "Ingrese el nombre del producto.");
        if (p.getPrecioUnitario() < 0 || p.getTiempoPreparacion() < 0) {
            throw new NegocioException("El precio y el tiempo de preparación no pueden ser negativos (RN005).");
        }
        try {
            if (p.getIdProducto() == 0) {
                p.setIdProducto(productoDAO.insertar(p));
                auditar(admin, ip, "PRODUCTO_CREADO", "pro_id=" + p.getIdProducto());
            } else {
                productoDAO.actualizar(p);
                auditar(admin, ip, "PRODUCTO_EDITADO", "pro_id=" + p.getIdProducto());
            }
        } catch (SQLException e) {
            throw duplicado(e, "Ya existe un producto con ese nombre.");
        }
        return p.getIdProducto();
    }

    public List<RecetaItem> listarReceta(int idProducto) throws SQLException {
        return recetaDAO.listarPorProducto(idProducto);
    }

    public void agregarInsumoReceta(int idProducto, int idInsumo, double cantidad, Usuario admin, String ip)
            throws NegocioException, SQLException {
        if (cantidad <= 0) {
            throw new NegocioException("La cantidad de insumo por unidad debe ser mayor a 0 (RN005).");
        }
        recetaDAO.guardar(idProducto, idInsumo, cantidad);
        auditar(admin, ip, "RECETA_ACTUALIZADA", "pro_id=" + idProducto + " ins_id=" + idInsumo);
    }

    public void quitarInsumoReceta(int idReceta, Usuario admin, String ip) throws SQLException {
        recetaDAO.eliminar(idReceta);
        auditar(admin, ip, "RECETA_ACTUALIZADA", "rec_id eliminado=" + idReceta);
    }

    // ------------------------------------------------------------------ insumos
    public List<Insumo> listarInsumos() throws SQLException {
        return insumoDAO.listar();
    }

    public void guardarInsumo(Insumo i, Usuario admin, String ip) throws NegocioException, SQLException {
        requerido(i.getNombre(), "Ingrese el nombre del insumo.");
        requerido(i.getUnidadMedida(), "Ingrese la unidad de medida.");
        if (i.getStockMinimo() < 0) {
            throw new NegocioException("El stock mínimo no puede ser negativo (RN005).");
        }
        try {
            if (i.getIdInsumo() == 0) {
                int id = insumoDAO.insertar(i);
                auditar(admin, ip, "INSUMO_CREADO", "ins_id=" + id);
            } else {
                insumoDAO.actualizar(i);
                auditar(admin, ip, "INSUMO_EDITADO", "ins_id=" + i.getIdInsumo());
            }
        } catch (SQLException e) {
            throw duplicado(e, "Ya existe un insumo con ese nombre.");
        }
    }

    // ------------------------------------------------------------------ tiendas
    public List<Tienda> listarTiendas() throws SQLException {
        return tiendaDAO.listarTodas();
    }

    public List<Tienda> listarTiendasActivas() throws SQLException {
        return tiendaDAO.listarActivas();
    }

    public void guardarTienda(Tienda t, Usuario admin, String ip) throws NegocioException, SQLException {
        requerido(t.getNombre(), "Ingrese el nombre de la tienda.");
        if (t.getIdTienda() == 0) {
            int id = tiendaDAO.insertar(t);
            auditar(admin, ip, "TIENDA_CREADA", "tie_id=" + id);
        } else {
            tiendaDAO.actualizar(t);
            auditar(admin, ip, "TIENDA_EDITADA", "tie_id=" + t.getIdTienda());
        }
    }

    // ------------------------------------------------------------------ proveedores
    public List<Proveedor> listarProveedores() throws SQLException {
        return proveedorDAO.listar();
    }

    public void guardarProveedor(Proveedor p, Usuario admin, String ip) throws NegocioException, SQLException {
        requerido(p.getRazonSocial(), "Ingrese la razón social del proveedor.");
        if (p.getRuc() == null || !p.getRuc().matches("\\d{11}")) {
            throw new NegocioException("El RUC debe tener 11 dígitos.");
        }
        if (p.getCorreo() != null && !p.getCorreo().isBlank() && !p.getCorreo().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new NegocioException("El correo del proveedor no es válido.");
        }
        try {
            if (p.getIdProveedor() == 0) {
                int id = proveedorDAO.insertar(p);
                auditar(admin, ip, "PROVEEDOR_CREADO", "prv_id=" + id);
            } else {
                proveedorDAO.actualizar(p);
                auditar(admin, ip, "PROVEEDOR_EDITADO", "prv_id=" + p.getIdProveedor());
            }
        } catch (SQLException e) {
            throw duplicado(e, "Ya existe un proveedor con ese RUC.");
        }
    }

    // ------------------------------------------------------------------ clientes
    public List<Cliente> listarClientes() throws SQLException {
        return clienteDAO.listar();
    }

    public void guardarCliente(Cliente c, Usuario admin, String ip) throws NegocioException, SQLException {
        requerido(c.getNombres(), "Ingrese el nombre del cliente.");
        if (c.getTelefono() != null && !c.getTelefono().isBlank() && !c.getTelefono().matches("[0-9 +()-]{6,15}")) {
            throw new NegocioException("El teléfono solo puede tener números (6 a 15 caracteres).");
        }
        clienteDAO.actualizar(c);
        auditar(admin, ip, "CLIENTE_EDITADO", "cli_id=" + c.getIdCliente());
    }

    // ------------------------------------------------------------------ utilitarios
    private void requerido(String valor, String mensaje) throws NegocioException {
        if (valor == null || valor.isBlank()) {
            throw new NegocioException(mensaje);
        }
    }

    private void auditar(Usuario admin, String ip, String accion, String detalle) throws SQLException {
        auditoriaDAO.registrar(admin.getIdUsuario(), ip, "ADMINISTRACION", accion, detalle);
    }

    /** Convierte la violación de UNIQUE (SQLState 23505) en un mensaje entendible. */
    private SQLException duplicado(SQLException e, String mensaje) throws NegocioException {
        if ("23505".equals(e.getSQLState())) {
            throw new NegocioException(mensaje);
        }
        return e;
    }
}
