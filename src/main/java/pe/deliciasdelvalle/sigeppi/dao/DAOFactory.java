package pe.deliciasdelvalle.sigeppi.dao;

import pe.deliciasdelvalle.sigeppi.dao.postgres.PostgresDAOFactory;

/**
 * Patrón Abstract Factory (DAO Factory): los servicios piden los DAO a la fábrica
 * y no saben qué base de datos hay detrás. Si mañana se cambia de motor, solo se
 * agrega otra fábrica.
 */
public abstract class DAOFactory {

    public static final int POSTGRES = 1;

    public static DAOFactory getDAOFactory(int tipo) {
        if (tipo == POSTGRES) {
            return new PostgresDAOFactory();
        }
        throw new IllegalArgumentException("Tipo de base de datos no soportado: " + tipo);
    }

    public abstract UsuarioDAO getUsuarioDAO();
    public abstract AuditoriaDAO getAuditoriaDAO();
    public abstract ClienteDAO getClienteDAO();
    public abstract ProductoDAO getProductoDAO();
    public abstract PedidoDAO getPedidoDAO();
    public abstract ComprobanteDAO getComprobanteDAO();
    public abstract RecetaDAO getRecetaDAO();
    public abstract OrdenProduccionDAO getOrdenProduccionDAO();
    public abstract SolicitudCompraDAO getSolicitudCompraDAO();
    public abstract InsumoDAO getInsumoDAO();
    public abstract LoteInsumoDAO getLoteInsumoDAO();
    public abstract MovimientoDAO getMovimientoDAO();
    public abstract ReporteDAO getReporteDAO();
    public abstract TiendaDAO getTiendaDAO();
    public abstract ProveedorDAO getProveedorDAO();
    public abstract PanelDAO getPanelDAO();
}
