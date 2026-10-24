package pe.deliciasdelvalle.sigeppi.dao.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pe.deliciasdelvalle.sigeppi.dao.ClienteDAO;
import pe.deliciasdelvalle.sigeppi.modelo.Cliente;
import pe.deliciasdelvalle.sigeppi.util.ConexionBD;

public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public Cliente buscarPorTelefono(String telefono) throws SQLException {
        String sql = "SELECT cli_id, cli_nombres, cli_telefono, cli_tipo FROM tb_cliente WHERE cli_telefono = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, telefono);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Cliente c = new Cliente();
                c.setIdCliente(rs.getInt("cli_id"));
                c.setNombres(rs.getString("cli_nombres"));
                c.setTelefono(rs.getString("cli_telefono"));
                c.setTipoCliente(rs.getString("cli_tipo"));
                return c;
            }
        }
    }

    @Override
    public int insertar(Connection cn, Cliente cliente) throws SQLException {
        String sql = "INSERT INTO tb_cliente (cli_nombres, cli_telefono, cli_tipo) VALUES (?, ?, ?) RETURNING cli_id";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombres());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getTipoCliente());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    @Override
    public List<Cliente> listar() throws SQLException {
        String sql = "SELECT cli_id, cli_nombres, cli_telefono, cli_tipo FROM tb_cliente ORDER BY cli_tipo, cli_nombres";
        List<Cliente> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setIdCliente(rs.getInt("cli_id"));
                c.setNombres(rs.getString("cli_nombres"));
                c.setTelefono(rs.getString("cli_telefono"));
                c.setTipoCliente(rs.getString("cli_tipo"));
                lista.add(c);
            }
        }
        return lista;
    }

    @Override
    public void actualizar(Cliente c) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(
                     "UPDATE tb_cliente SET cli_nombres = ?, cli_telefono = ?, cli_tipo = ? WHERE cli_id = ?")) {
            ps.setString(1, c.getNombres());
            ps.setString(2, c.getTelefono());
            ps.setString(3, c.getTipoCliente());
            ps.setInt(4, c.getIdCliente());
            ps.executeUpdate();
        }
    }
}
