package pe.deliciasdelvalle.sigeppi.dao.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.dao.ProveedorDAO;
import pe.deliciasdelvalle.sigeppi.modelo.Proveedor;
import pe.deliciasdelvalle.sigeppi.util.ConexionBD;

public class ProveedorDAOImpl implements ProveedorDAO {

    @Override
    public List<Proveedor> listar() throws SQLException {
        String sql = "SELECT prv_id, prv_razon_social, prv_ruc, prv_telefono, prv_correo, prv_tipo FROM tb_proveedor ORDER BY prv_razon_social";
        List<Proveedor> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Proveedor p = new Proveedor();
                p.setIdProveedor(rs.getInt("prv_id"));
                p.setRazonSocial(rs.getString("prv_razon_social"));
                p.setRuc(rs.getString("prv_ruc"));
                p.setTelefono(rs.getString("prv_telefono"));
                p.setCorreo(rs.getString("prv_correo"));
                p.setTipoProveedor(rs.getString("prv_tipo"));
                lista.add(p);
            }
        }
        return lista;
    }

    @Override
    public int insertar(Proveedor p) throws SQLException {
        String sql = "INSERT INTO tb_proveedor (prv_razon_social, prv_ruc, prv_telefono, prv_correo, prv_tipo) "
                   + "VALUES (?, ?, ?, ?, ?) RETURNING prv_id";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            llenar(ps, p);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    @Override
    public void actualizar(Proveedor p) throws SQLException {
        String sql = "UPDATE tb_proveedor SET prv_razon_social = ?, prv_ruc = ?, prv_telefono = ?, prv_correo = ?, prv_tipo = ? "
                   + "WHERE prv_id = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            llenar(ps, p);
            ps.setInt(6, p.getIdProveedor());
            ps.executeUpdate();
        }
    }

    private void llenar(PreparedStatement ps, Proveedor p) throws SQLException {
        ps.setString(1, p.getRazonSocial());
        ps.setString(2, p.getRuc());
        ps.setString(3, p.getTelefono());
        ps.setString(4, p.getCorreo());
        ps.setString(5, p.getTipoProveedor());
    }
}
