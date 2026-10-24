package pe.deliciasdelvalle.sigeppi.dao.postgres;

import pe.deliciasdelvalle.sigeppi.dao.DAOFactory;
import pe.deliciasdelvalle.sigeppi.dao.UsuarioDAO;
import pe.deliciasdelvalle.sigeppi.dao.AuditoriaDAO;
import pe.deliciasdelvalle.sigeppi.dao.ClienteDAO;
import pe.deliciasdelvalle.sigeppi.dao.ProductoDAO;
import pe.deliciasdelvalle.sigeppi.dao.PedidoDAO;
import pe.deliciasdelvalle.sigeppi.dao.ComprobanteDAO;
import pe.deliciasdelvalle.sigeppi.dao.RecetaDAO;
import pe.deliciasdelvalle.sigeppi.dao.OrdenProduccionDAO;
import pe.deliciasdelvalle.sigeppi.dao.SolicitudCompraDAO;
import pe.deliciasdelvalle.sigeppi.dao.InsumoDAO;
import pe.deliciasdelvalle.sigeppi.dao.LoteInsumoDAO;
import pe.deliciasdelvalle.sigeppi.dao.MovimientoDAO;
import pe.deliciasdelvalle.sigeppi.dao.ReporteDAO;
import pe.deliciasdelvalle.sigeppi.dao.TiendaDAO;
import pe.deliciasdelvalle.sigeppi.dao.ProveedorDAO;
import pe.deliciasdelvalle.sigeppi.dao.PanelDAO;

/** Fábrica concreta que devuelve las implementaciones para PostgreSQL. */
public class PostgresDAOFactory extends DAOFactory {

    @Override
    public UsuarioDAO getUsuarioDAO() {
        return new UsuarioDAOImpl();
    }

    @Override
    public AuditoriaDAO getAuditoriaDAO() {
        return new AuditoriaDAOImpl();
    }

    @Override
    public ClienteDAO getClienteDAO() {
        return new ClienteDAOImpl();
    }

    @Override
    public ProductoDAO getProductoDAO() {
        return new ProductoDAOImpl();
    }

    @Override
    public PedidoDAO getPedidoDAO() {
        return new PedidoDAOImpl();
    }

    @Override
    public ComprobanteDAO getComprobanteDAO() {
        return new ComprobanteDAOImpl();
    }

    @Override
    public RecetaDAO getRecetaDAO() {
        return new RecetaDAOImpl();
    }

    @Override
    public OrdenProduccionDAO getOrdenProduccionDAO() {
        return new OrdenProduccionDAOImpl();
    }

    @Override
    public SolicitudCompraDAO getSolicitudCompraDAO() {
        return new SolicitudCompraDAOImpl();
    }

    @Override
    public InsumoDAO getInsumoDAO() {
        return new InsumoDAOImpl();
    }

    @Override
    public LoteInsumoDAO getLoteInsumoDAO() {
        return new LoteInsumoDAOImpl();
    }

    @Override
    public MovimientoDAO getMovimientoDAO() {
        return new MovimientoDAOImpl();
    }

    @Override
    public ReporteDAO getReporteDAO() {
        return new ReporteDAOImpl();
    }
    @Override
    public TiendaDAO getTiendaDAO() {
        return new TiendaDAOImpl();
    }

    @Override
    public ProveedorDAO getProveedorDAO() {
        return new ProveedorDAOImpl();
    }

    @Override
    public PanelDAO getPanelDAO() {
        return new PanelDAOImpl();
    }
}
